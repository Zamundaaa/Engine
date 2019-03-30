package vr;

import static org.lwjgl.openvr.VR.*;
import static org.lwjgl.openvr.VRCompositor.*;
import static org.lwjgl.openvr.VRSystem.*;

import java.nio.*;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.*;

import collectionsStuff.ArrayListInt;
import generic.Thing;
import graphics3D.Camera;
import graphics3D.meshRenderer._MeshRenderer;
import loaders.Loader;
import models.material.Material;
import openGlResources.buffers.Fbo;
import openGlResources.buffers.VAO;
import tools.AppFolder;
import vr.actions.VRActionManifest;
import vr.actions.VRActionSet;

public class VRHandler {

	public static final int FLAG_MULTISAMPLING = 2, FLAG_MULTITARGETS = 4;
	public static int init_bitmask = FLAG_MULTISAMPLING;

	public static int handle = -1;
	private static int counter = 0;
	public static ArrayListInt trackedDevices = new ArrayListInt();
	private static TrackedDevicePose.Buffer matrixBuffer;
	private static TrackedDevicePose.Buffer matrixBuffer_forGamePlay;
	private static TrackedDevicePose[] poses;
	private static VREvent event;
	protected static IntBuffer iBuff = BufferUtils.createIntBuffer(1), iBuff2 = BufferUtils.createIntBuffer(1);
	protected static FloatBuffer fBuff = BufferUtils.createFloatBuffer(1);
	protected static LongBuffer lBuff = BufferUtils.createLongBuffer(1);
	public static boolean running = false;
	public static boolean hasInputFocus = true;
	private static Fbo leftFbo, rightFbo, leftSubmitFbo, rightSubmitFbo;
	private static int recommendedRenderWidth = 1024, recommendedRenderHeight = 1024;
	public static Texture submitTexLeft, submitTexRight;
	private static boolean useSubmitFbos = true;

	public static VRPlayer player = new VRPlayer(new HMD(0, null), new VRController(1, true, false),
			new VRController(2, false, false));
//	public static VRController left, right;
	public static float nearClipPlane = 0.05f, farClipPlane = 1_000_000;

	private static VRActionManifest actionManifest;
	private static _MeshRenderer hiddenMeshRenderer;
	private static Thing leftHiddenMesh, rightHiddenMesh;

	/**
	 * please use addExitRequestListener or it'll just System.exit(0)!
	 */
	public static boolean init() {
		return init(false);
	}

	/**
	 * please use addExitRequestListener or it'll just System.exit(0)!
	 */
	public static boolean init(boolean printErrorMessage) {
		return init(0.05f, 1_000_000, printErrorMessage);
	}

	/**
	 * please use addExitRequestListener or it'll just System.exit(0)!
	 */
	public static boolean init(float clipPlane_near, float clipPlane_far, boolean printErrorMessage) {
		if (!VR.VR_IsRuntimeInstalled() || !VR.VR_IsHmdPresent()) {
			if (printErrorMessage) {
				AppFolder.log.println("could not init VR!");
				AppFolder.log.println("Reason: " + (VR.VR_IsRuntimeInstalled()
						? (VR.VR_IsHmdPresent() ? "Maybe SteamVR (or equivalent) isn't running?"
								: "no headset connected")
						: "runtime not installed! Please install for example SteamVR to use this VR app"));
			}
			return false;
		} else {
			handle = VR.VR_InitInternal(iBuff, VR.EVRApplicationType_VRApplication_Scene);
			if (printErrorMessage && iBuff.get(0) != EVRInitError_VRInitError_None) {
				String errorString = VR.VR_GetVRInitErrorAsEnglishDescription(iBuff.get(0));
				AppFolder.log.println("VR init error: " + errorString);
			}
			if (handle <= 0) {
				return false;
			} else {
				nearClipPlane = clipPlane_near;
				farClipPlane = clipPlane_far;
				OpenVR.create(handle);

				event = new VREvent(BufferUtils.createByteBuffer(VREvent.SIZEOF));

//				if (!(hasInputFocus = VRSystem_CaptureInputFocus())) {
//					AppFolder.log.println("could not capture input focus ?!?");
//					if (VRSystem_IsInputFocusCapturedByAnotherProcess())
//						AppFolder.log.println("it's captured by another process!");
//				}
				poses = new TrackedDevicePose[k_unMaxTrackedDeviceCount];
				matrixBuffer = TrackedDevicePose.calloc(TrackedDevicePose.SIZEOF * k_unMaxTrackedDeviceCount);
				matrixBuffer_forGamePlay = TrackedDevicePose
						.calloc(TrackedDevicePose.SIZEOF * k_unMaxTrackedDeviceCount);
				iBuff.clear();
				iBuff2.clear();
				VRSystem_GetRecommendedRenderTargetSize(iBuff, iBuff2);

				recommendedRenderWidth = iBuff.get();
				recommendedRenderHeight = iBuff2.get();
				AppFolder.log.println(
						"recommended render size: [" + recommendedRenderWidth + ", " + recommendedRenderHeight + "]");
				recreateRenderFbos(init_bitmask);
				if (!leftFbo.isComplete()) {
					AppFolder.log.println("left FBO NOT COMPLETE");
					leftFbo.unbind();
					cleanUp();
					return false;
				}

				leftSubmitFbo = new Fbo(recommendedRenderWidth, recommendedRenderHeight);
				leftSubmitFbo.createTextureAttachment(0, false);

				rightSubmitFbo = new Fbo(recommendedRenderWidth, recommendedRenderHeight);
				rightSubmitFbo.createTextureAttachment(0, false);
				rightSubmitFbo.unbind();

				submitTexLeft = new Texture(BufferUtils.createByteBuffer(Texture.SIZEOF));
				submitTexLeft.handle(leftSubmitFbo.getColorTexture(0));
				submitTexLeft.eType(ETextureType_TextureType_OpenGL);
				submitTexLeft.eColorSpace(EColorSpace_ColorSpace_Gamma);

				submitTexRight = new Texture(BufferUtils.createByteBuffer(Texture.SIZEOF));
				submitTexRight.handle(rightSubmitFbo.getColorTexture(0));
				submitTexRight.eType(ETextureType_TextureType_OpenGL);
				submitTexRight.eColorSpace(EColorSpace_ColorSpace_Gamma);

				hiddenMeshRenderer = new _MeshRenderer(false, 0, false, null);
				hiddenMeshRenderer.autoStartShader(true);
				hiddenMeshRenderer.autoStopShader(true);

				createHiddenAreaMeshes();

				VRHandler.running = true;
				return true;
			}
		}
	}

	private static void createHiddenAreaMeshes() {
		HiddenAreaMesh leftMesh = VRSystem_GetHiddenAreaMesh(EVREye_Eye_Left,
				EHiddenAreaMeshType_k_eHiddenAreaMesh_Standard, HiddenAreaMesh.calloc());
		HiddenAreaMesh rightMesh = VRSystem_GetHiddenAreaMesh(EVREye_Eye_Right,
				EHiddenAreaMeshType_k_eHiddenAreaMesh_Standard, HiddenAreaMesh.calloc());
		float[] vertices = new float[leftMesh.pVertexData().remaining() * 3];
		AppFolder.log.println("hidden area mesh has " + leftMesh.pVertexData().remaining() + " vertices");
		int i = 0, i2 = 0;
		while (i2 < leftMesh.pVertexData().limit()) {
			HmdVector2 v = leftMesh.pVertexData().get(i2++);
			vertices[i++] = v.v(0);
			vertices[i++] = 0;
			vertices[i++] = v.v(1);
		}
		VAO leftVAO = Loader.loadToVAO(new int[] { 3 }, vertices);
		if (vertices.length != rightMesh.pVertexData().remaining() * 3) {
			vertices = new float[rightMesh.pVertexData().remaining() * 3];
		}
		i = 0;
		i2 = 0;
		while (i2 < rightMesh.pVertexData().limit()) {
			HmdVector2 v = rightMesh.pVertexData().get(i2++);
			vertices[i++] = v.v(0);
			vertices[i++] = 0;
			vertices[i++] = v.v(1);
		}
		VAO rightVAO = Loader.loadToVAO(new int[] { 3 }, vertices);
		Thing l = new Thing(leftVAO, new Material(0, 0, 0, 1)), r = new Thing(rightVAO, new Material(0, 0, 0, 1));
		l.cullFaces(false);
		l.drawMode(Thing.DRAWMODE_RAW);
		r.cullFaces(false);
		r.drawMode(Thing.DRAWMODE_RAW);
		leftHiddenMesh = l;
		rightHiddenMesh = r;
		leftMesh.free();
		rightMesh.free();
	}

	public static void setActionManifest(VRActionManifest m) {
		actionManifest = m;
	}

	public static void createAndSetDefaultActionManifest() {
		actionManifest = new VRActionManifest();
		actionManifest.addDefaultActions();
		actionManifest.setDefaultBindings("vive_controller", null);
		actionManifest.submitToOpenVR(true);

//		actionManifest = new VRActionManifes();
//		actionManifest.addDefaultActions();
//		actionManifest.createFile(false);
//		actionManifest.submitToOpenVR(false);
	}

	/**
	 * also polls events
	 */
	public static void update(float frameTimeSeconds) {
		counter++;
		if (counter > 30) {
			counter = 0;
			updateDeviceList();
		}
		VRActionSet.update();
//		for (int i = 0; i < trackedDevices.size(); i++) {
//			int index = trackedDevices.get(i);
//			if (poses[index].bDeviceIsConnected() && poses[index].bPoseIsValid()) {
//				
//			}
//		}
		if (player != null)
			player.update(frameTimeSeconds);

		while (VRSystem_PollNextEvent(event)) {
			int type = event.eventType();
			int index = event.trackedDeviceIndex();
			VRController ctrl = null;
			if (player != null)
				if (player.left() != null && player.left().trackedDeviceID() == index) {
					ctrl = player.left();
				} else if (player.right() != null && player.right().trackedDeviceID() == index) {
					ctrl = player.right();
				}
			switch (type) {
			case EVREventType_VREvent_ButtonPress:
				if (ctrl != null) {
//					ctrl.update(frameTimeSeconds);
//					ctrl.vibrate(1000);
					ctrl.vibrate(0.5f, 10, 1);
//					AppFolder.log.println("button pressed. " + event.data().controller().button());
				} else if (index != 0) {
//					AppFolder.log.println("lol a button press without a button! " + index);
				}
				break;
			case EVREventType_VREvent_ButtonUnpress:
				if (ctrl != null) {
//					ctrl.update(frameTimeSeconds);
//					ctrl.vibrate(1000);
					ctrl.vibrate(0.1f, 10, 1);
//					AppFolder.log.println("button released. " + event.data().controller().button());
				} else if (index != 0) {
//					AppFolder.log.println("lol a button release without a button! " + index);
				}
				break;
			case EVREventType_VREvent_ButtonTouch:
//				AppFolder.log.println("button touched!");
				break;
			case EVREventType_VREvent_ButtonUntouch:
//				AppFolder.log.println("button untouched!");
				break;
			case EVREventType_VREvent_Quit:
				exitRequested = true;
				AppFolder.log.println("exiting!");
				VRSystem_AcknowledgeQuit_Exiting();
				if (closeListeners.size() == 0) {
					System.exit(0);
				}
				for (int i = 0; i < closeListeners.size(); i++)
					closeListeners.get(i).closeRequested();
				break;
			case EVREventType_VREvent_EnterStandbyMode:// may want to do
			case EVREventType_VREvent_LeaveStandbyMode:// something here?
			case EVREventType_VREvent_TrackedDeviceUserInteractionStarted:
			case EVREventType_VREvent_TrackedDeviceUserInteractionEnded:
			case EVREventType_VREvent_PropertyChanged:
			case EVREventType_VREvent_IpdChanged:
			case EVREventType_VREvent_Compositor_ChaperoneBoundsHidden:
			case EVREventType_VREvent_Compositor_ChaperoneBoundsShown:
			case EVREventType_VREvent_SceneApplicationChanged:
				// ignore
				break;
			case EVREventType_VREvent_Input_BindingLoadSuccessful:
//				AppFolder.log.println("BindingLoadSuccessful");
				break;
			case EVREventType_VREvent_ActionBindingReloaded:
//				AppFolder.log.println("action binding reloaded!");
				break;
			case EVREventType_VREvent_Input_HapticVibration:
//				AppFolder.log.println("haptic!");
				break;
			case EVREventType_VREvent_Input_BindingLoadFailed:
				AppFolder.log.println("BindingLoadFailed");
				break;
			case EVREventType_VREvent_Input_ActionManifestLoadFailed:
				AppFolder.log.println("ActionManifestLoadFailed");
				break;
			default:
				AppFolder.log.println(
						"some event occurred! ID: " + type + " name: " + VRSystem_GetEventTypeNameFromEnum(type));
			}
		}
	}

	/**
	 * is called automatically from time to time
	 */
	public static void updateDeviceList() {
		for (int i = k_unTrackedDeviceIndex_Hmd/* 0 */; i < k_unMaxTrackedDeviceCount; i++) {
			if (VRSystem_IsTrackedDeviceConnected(i)) {
				if (poses[i] == null) {
					poses[i] = matrixBuffer.get(i);
					iBuff.clear();
					AppFolder.log.println(
							"found connected device " + i + " with device class " + VRSystem_GetTrackedDeviceClass(i)
									+ " and the name " + VRSystem.VRSystem_GetStringTrackedDeviceProperty(i,
											VR.ETrackedDeviceProperty_Prop_RenderModelName_String, iBuff));
					trackedDevices.add(i);
					int deviceclass = VRSystem_GetTrackedDeviceClass(i);
					if (deviceclass == ETrackedDeviceClass_TrackedDeviceClass_Controller) {
						player.setLeftController(new VRController(3, true));
						player.left.active = true;
						player.setRightController(new VRController(4, false));
						player.right.active = true;
					} else if (deviceclass == ETrackedDeviceClass_TrackedDeviceClass_HMD) {
						AppFolder.log.println("hdm!");
						poses[i] = matrixBuffer.get(i);
						player.setHMD(new HMD(i, poses[i]));
						// IDs are apparently completely irrelevant now...
						player.setLeftController(new VRController(3, true));
						player.left.active = true;
						player.setRightController(new VRController(4, false));
						player.right.active = true;
					}
				}
//				if (player.left != null)
//					player.right.active = true;
//				if (player.right != null)
//					player.right.active = true;
			} else if (!VRSystem_IsTrackedDeviceConnected(i)) {
				// TODO have all that stuff initiated in the beginning (& filled with values at
				// connect like now, cleared at disconnect).
				// has the advantage that you then basically just have to check if a device is
				// active & nullptr doesn't occur as often. + disconnect is possible...

//				poses[i] = null;
//				trackedDevices.removeValue(i);
				if (player.left != null && player.left.trackedDeviceID() == i) {
//					leftCtrl.cleanUp();
					if (player.left.active) {
						player.left.active = false;
						AppFolder.log.println("disconnected the left controller");
					}
				} else if (player.right != null && player.right.trackedDeviceID() == i) {
//					rightCtrl.cleanUp();
					if (player.right.active) {
						player.right.active = false;
						AppFolder.log.println("disconnected the right controller");
					}
				} else {
//					AppFolder.log.println("Disconnected device nr. " + i);
				}
			}
		}
	}

	public static void cleanUp() {
		AppFolder.log.println("cleaning up VR!");
		VR_ShutdownInternal();

		leftFbo.unbind();
		rightFbo.unbind();
		leftSubmitFbo.unbind();
		rightSubmitFbo.unbind();
		if (matrixBuffer != null)
			matrixBuffer.close();
		if (matrixBuffer_forGamePlay != null)
			matrixBuffer_forGamePlay.close();
		if (leftFbo != null)
			leftFbo.delete();
		if (rightFbo != null)
			rightFbo.delete();
		if (submitTexLeft != null)
			submitTexLeft.close();
		if (submitTexRight != null)
			submitTexRight.close();
		if (leftSubmitFbo != null)
			leftSubmitFbo.delete();
		if (rightSubmitFbo != null)
			rightSubmitFbo.delete();

		handle = -1;
		running = false;
	}

	public static float getSecondsTillPhotons() {
		fBuff.clear();
		lBuff.clear();
		VRSystem_GetTimeSinceLastVsync(fBuff, lBuff);
		float fSecondsSinceLastVsync = fBuff.get(0);
		float fDisplayFrequency = VRSystem_GetFloatTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd,
				ETrackedDeviceProperty_Prop_DisplayFrequency_Float, null);// iBuff
		float fFrameDuration = 1.f / fDisplayFrequency;
		float fVsyncToPhotons = VRSystem_GetFloatTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd,
				ETrackedDeviceProperty_Prop_SecondsFromVsyncToPhotons_Float, null);// iBuff
		float ret = fFrameDuration - fSecondsSinceLastVsync + fVsyncToPhotons;
		return ret;
	}

	/**
	 * @param left false for right
	 */
	public static Fbo getRenderBuffer(boolean left) {
		if (left) {
			return leftFbo;
		} else {
			return rightFbo;
		}
	}

	private static Matrix4f projMat = new Matrix4f();
	private static HmdMatrix44 getMat44 = new HmdMatrix44(ByteBuffer.allocateDirect(HmdMatrix44.SIZEOF));

	public static Matrix4f getLeftProjMat() {
		VRSystem_GetProjectionMatrix(EVREye_Eye_Left, nearClipPlane, farClipPlane, getMat44);
		projMat.set(getMat44.m());
		projMat.transpose();
		return projMat;
	}

	public static Matrix4f getRightProjMat() {
		VRSystem_GetProjectionMatrix(EVREye_Eye_Right, nearClipPlane, farClipPlane, getMat44);
		projMat.set(getMat44.m());
		projMat.transpose();
		return projMat;
	}

	/**
	 * must be called before rendering both eyes! Also updates the hdms pose. So you
	 * may have to update positions after this call accordingly, or your app may
	 * appear to lag behind a little frame. Because it does.
	 */
	public static void startRendering() {
		VRCompositor_WaitGetPoses(matrixBuffer, matrixBuffer_forGamePlay);
//		updateDevices(frameTimeSeconds);
	}

	private static Fbo lastLeftFbo = leftFbo;
	private static Fbo lastRightFbo = rightFbo;

	public static void submitBothEyes() {
		submitBothEyes(leftFbo, rightFbo);
	}

	public static void submitBothEyes(Fbo leftFbo, Fbo rightFbo) {
		boolean _useSubmitFbos = leftFbo.multisampled() || leftFbo.numColorTextures() == 0;
		if (useSubmitFbos != _useSubmitFbos || lastLeftFbo != leftFbo || lastRightFbo != rightFbo) {
			useSubmitFbos = _useSubmitFbos;
			submitTexLeft.handle(useSubmitFbos ? leftSubmitFbo.getColorTexture(0) : leftFbo.getColorTexture(0));
			submitTexRight.handle(useSubmitFbos ? rightSubmitFbo.getColorTexture(0) : rightFbo.getColorTexture(0));
			lastLeftFbo = leftFbo;
			lastRightFbo = rightFbo;
		}
		if (useSubmitFbos) {
			leftFbo.blitTo(leftSubmitFbo, 0, false);
			leftSubmitFbo.bindToRead(0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, leftSubmitFbo.getColorTexture(0));
		} else {
			leftFbo.bindToRead(0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, leftFbo.getColorTexture(0));
		}
		int submitRetLeft = VRCompositor_Submit(EVREye_Eye_Left, submitTexLeft, null, EVRSubmitFlags_Submit_Default);
		if (useSubmitFbos) {
			rightFbo.blitTo(rightSubmitFbo, 0, false);
			rightSubmitFbo.bind();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, rightSubmitFbo.getColorTexture(0));
		} else {
			rightFbo.bind();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, rightFbo.getColorTexture(0));
		}
		int submitRetRight = VRCompositor_Submit(EVREye_Eye_Right, submitTexRight, null, EVRSubmitFlags_Submit_Default);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glFlush();
		if (submitRetLeft != 0 || submitRetRight != 0) {
			if (submitRetLeft == EVRCompositorError_VRCompositorError_DoNotHaveFocus
					|| submitRetRight == EVRCompositorError_VRCompositorError_DoNotHaveFocus)
				AppFolder.log.println("no focus!");
			else
				AppFolder.log.println(submitRetLeft + ", " + submitRetRight);
		}
		leftFbo.unbind();// unbinds all
	}

	/**
	 * binds & clears the left framebuffer and sets the cameras position to the left
	 * eye, updates its view matrix and sets the viewport
	 */
	public static void renderingLeft(Camera c) {
		renderingLeft(c, true);
	}

	/**
	 * binds & clears the right framebuffer and sets the cameras position to the
	 * right eye
	 */
	public static void renderingRight(Camera c) {
		renderingRight(c, true);
	}

	/**
	 * binds & clears the left framebuffer (if wanted) and sets the cameras position
	 * to the left eye, updates its view matrix and sets the viewport
	 */
	public static void renderingLeft(Camera c, boolean bindFrameBuffer) {
		if (bindFrameBuffer) {
			leftFbo.bind();
			leftFbo.clearBuffers();
		}
		if (player != null)
			player.set(c, EVREye_Eye_Left, getLeftProjMat());
	}

	/**
	 * binds & clears the right framebuffer (if wanted and sets the cameras position
	 * to the right eye
	 */
	public static void renderingRight(Camera c, boolean bindFrameBuffer) {
		if (bindFrameBuffer) {
			rightFbo.bind();
			rightFbo.clearBuffers();
		}
		if (player != null)
			player.set(c, EVREye_Eye_Right, getRightProjMat());
		GL11.glViewport(0, 0, recommendedRenderWidth, recommendedRenderHeight);
	}

	public static boolean canRenderScene() {
		return VRCompositor_CanRenderScene();
	}

	// TODO more listeners?

	public static boolean exitRequested = false;
	private static ArrayList<ExitRequestListener> closeListeners = new ArrayList<>();

	public static interface ExitRequestListener {
		public void closeRequested();
	}

	public static void addExitRequestListener(ExitRequestListener e) {
		closeListeners.add(e);
	}

	/**
	 * @param bitmask with flags like {@link VRHandler#FLAG_MULTISAMPLING}
	 *                (multisampling is enabled by default). Call if you want to
	 *                change a setting like multisampling
	 */
	public static void recreateRenderFbos(int bitmask) {
		if (leftFbo != null)
			leftFbo.delete();
		if (rightFbo != null)
			rightFbo.delete();
		boolean multisampled = (bitmask & FLAG_MULTISAMPLING) != 0;
		if ((bitmask & FLAG_MULTITARGETS) != 0) {
			leftFbo = Fbo.createMultiTargetsFbo(recommendedRenderWidth, recommendedRenderHeight, 2, false,
					multisampled);
			rightFbo = Fbo.createMultiTargetsFbo(recommendedRenderWidth, recommendedRenderHeight, 2, false,
					multisampled);
		} else {
			leftFbo = Fbo.createStandardFbo(recommendedRenderWidth, recommendedRenderHeight, false, multisampled);
			rightFbo = Fbo.createStandardFbo(recommendedRenderWidth, recommendedRenderHeight, false, multisampled);
		}
	}

	/**
	 * in case you're doing some fancy stuff not included
	 */
	public static void setFbo(boolean left, Fbo f) {
		if (left) {
			if (leftFbo != null)
				leftFbo.delete();
			leftFbo = f;
		} else {
			if (rightFbo != null)
				rightFbo.delete();
			rightFbo = f;
		}
	}

	public static void renderHiddenMesh(boolean left) {
		if (hiddenMeshRenderer != null)
			hiddenMeshRenderer.render(left ? leftHiddenMesh : rightHiddenMesh, null, null, null, null);
	}

	public static int renderWidth() {
		return recommendedRenderWidth;
	}

	public static int renderHeight() {
		return recommendedRenderHeight;
	}

	public static Fbo leftFbo() {
		return leftFbo;
	}

	public static Fbo rightFbo() {
		return rightFbo;
	}

}
