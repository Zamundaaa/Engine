package loaders;

import static org.lwjgl.assimp.Assimp.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import collectionsStuff.ArrayListFloat;
import collectionsStuff.ArrayListInt;
import generic.Transform;
import models.ModelData;
import models.animated.*;
import openGlResources.buffers.VAO;
import openGlResources.buffers.VBO;
import openGlResources.textures.Texture;
import tools.AppFolder;
import tools.JarResources;
import tools.misc.Vects;

public class Loader {

	static {
		// Assimp.aiSetImportPropertyInteger(AIPropertyStore.create(),
		// AI_CONFIG_PP_LBW_MAX_WEIGHTS, 3);
	}

//	public static void init() {
//
//	}

	// static models

	public static VAO loadToVAO(String path) {
		return loadToVAO(path, !AppFolder.absolute(path));
	}

	public static VAO loadToVAO(String filepath, boolean relative) {
		ModelData m = loadModel(filepath, relative);
		if (m != null) {
			return m.loadToVAO();
		} else {
			AppFolder.log.println("something went wrong loading file " + filepath + "; relative: " + relative);
			return null;
		}
	}

	public static ModelData loadModel(String filepath) {
		return loadModel(filepath, !AppFolder.absolute(filepath));
	}

	/**
	 * @param filepath
	 * @param relative if the 'file' is in this jar
	 * @return
	 */
	public static ModelData loadModel(String filepath, boolean relative) {
		ByteBuffer file;
		if (relative) {
			file = JarResources.loadJarFile(filepath);
		} else {
			file = AppFolder.readBytes(filepath);
		}
		if (file == null)
			return null;
		String[] split = filepath.split("\\.");
		byte[] b = (split[split.length - 1] + "\0").getBytes();
		ByteBuffer extention = BufferUtils.createByteBuffer(b.length);
		extention.put(b).flip();
		AIScene ret = Assimp.aiImportFileFromMemory(file, aiProcess_Triangulate | aiProcess_FixInfacingNormals
				| aiProcess_GenSmoothNormals | aiProcess_GenUVCoords, extention);
		if (ret == null) {
			AppFolder.log.println(aiGetErrorString());
			return null;
		} else {
			// FIXME make this load more textures, or textures at all. This should return
			// all possible Materials in the attached ModelData!
			// ModelCache().getModel(...) (?) should return a ModelData instance with the
			// required model, and that should be possible to just pluck into the Model
			// class and get a render-ready model out (with scale & position)

			// materials, textures:
//			int numMaterials = ret.mNumMaterials();
//			PointerBuffer mats = ret.mMaterials();
//			ArrayList<Texture> texes = new ArrayList<>();
//			for (int i = 0; i < numMaterials; i++) {
//			AIMaterial mat = AIMaterial.create(mats.get());
//			Texture t = loadDiffuseTex(mat);
//			texes.add(t);
//			}
			// mesh(es):
			PointerBuffer pb = ret.mMeshes();
//			for (int i = 0; i < pb.limit(); i++) {
			AIMesh m = AIMesh.create(pb.get());
			ModelData model = loadModel(m);
			model.setName(filepath);
//			}
			aiReleaseImport(ret);
			return model;
		}
	}

	public static Texture loadDiffuseTex(AIMaterial m) {
		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(m, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
		String p = path.dataString();
		if (p != null && p.length() > 0)
			return TextureCache.get().getTexture(p);
		else
			return null;
	}

	public static ModelData loadModel(AIMesh m) {
		AIFace.Buffer b = m.mFaces();
		float[] vertices = new float[m.mNumVertices() * 3];
		float[] norms = new float[vertices.length];
		float[] textureCoords = new float[m.mNumVertices() * 2];
		int[] indices = new int[m.mNumFaces() * 3];
		int i = 0;
		AIVector3D.Buffer vs = m.mVertices();
		AIVector3D.Buffer texes = m.mTextureCoords(0);
		AIVector3D.Buffer normals = m.mNormals();

		while (vs.hasRemaining()) {
			AIVector3D v = vs.get();
			vertices[i * 3] = v.x();
			vertices[i * 3 + 1] = v.y();
			vertices[i * 3 + 2] = v.z();
			v = texes.get();
			textureCoords[i * 2] = v.x();
			textureCoords[i * 2 + 1] = v.y();
			v = normals.get();
			norms[i * 3] = v.x();
			norms[i * 3 + 1] = v.y();
			norms[i * 3 + 2] = v.z();
			i++;
		}
		i = 0;
		while (b.hasRemaining()) {
			AIFace f = b.get();
			IntBuffer ib = f.mIndices();
//			int c = f.mNumIndices(); // bei mir immer 3
			for (int j = 0; j < 3; j++) {
				indices[i++] = ib.get();
			}
		}
		return new ModelData(vertices, textureCoords, norms, indices);
	}

	// \static models
	// Animations!

	public static AnimatedModelData loadAnimatedModel(String filepath) {
		return loadAnimatedModel(filepath, !AppFolder.absolute(filepath));
	}

	/*
	 * @param filepath
	 * 
	 * @param relative if the 'file' is in this jar
	 * 
	 * @return
	 */
	public static AnimatedModelData loadAnimatedModel(String filepath, boolean relative) {
		ByteBuffer file;
		if (relative) {
			file = JarResources.loadJarFile(filepath);
		} else {
			file = AppFolder.readBytes(filepath);
		}
		String[] split = filepath.split("\\.");
		byte[] b = (split[split.length - 1] + "\0").getBytes();
		ByteBuffer extention = BufferUtils.createByteBuffer(b.length);
		extention.put(b).flip();
//		Assimp.AI_LBW_MAX_WEIGHTS = 3;
//		Assimp.aiSetImportPropertyInteger(AIPropertyStore.create(), AI_CONFIG_PP_LBW_MAX_WEIGHTS, 3);
		AIScene ret = Assimp.aiImportFileFromMemory(file,
				aiProcess_Triangulate | aiProcess_LimitBoneWeights | aiProcess_JoinIdenticalVertices, extention);
		// | aiProcess_FixInfacingNormals
		if (ret == null) {
			AppFolder.log.println(aiGetErrorString());
			return null;
		} else {
//			int flipflag = 0;// dynmisches erkennen?
			PointerBuffer pb = ret.mMeshes();
//			AppFolder.log.println("there's " + ret.mNumMeshes() + " meshes to get");
			AIMesh m = AIMesh.create(pb.get());
			Map<String, Bone> bonemap = new HashMap<>();
			Matrix4f tmat = toMatrix(ret.mRootNode().mTransformation());
			AnimatedModelData model = loadAnimatedModel(m, bonemap, null, tmat);
			model.setName(filepath);
			processNodeHierarchy(ret.mRootNode(), null, bonemap);
			applyTransformationMatrix(model, tmat);
			model.setGlobalInverseTransform(new Matrix4f());
			loadAnimations(ret, model.bones(), bonemap, model.animations(), tmat, new Matrix4f());
			aiReleaseImport(ret);
			return model;
		}
	}

	public static AnimatedModelData loadAnimatedModel(AIMesh m, Map<String, Bone> bonemap, List<Bone> dest,
			Matrix4f transmat) {
		AIFace.Buffer b = m.mFaces();
		int numofvertices = m.mNumVertices();
		float[] vertices = new float[numofvertices * 3];
		float[] norms = new float[vertices.length];
		float[] textureCoords = new float[numofvertices * 2];
		int[] indices = new int[m.mNumFaces() * 3];
		int i = 0;
		AIVector3D.Buffer vs = m.mVertices();
		AIVector3D.Buffer texes = m.mTextureCoords(0);
		AIVector3D.Buffer normals = m.mNormals();
		while (vs.hasRemaining()) {
			AIVector3D v = vs.get();
			vertices[i * 3] = v.x();
//			if (flipflag == 1) {
//				vertices[i * 3 + 1] = v.z();
//				vertices[i * 3 + 2] = v.y();
//			} else {
			vertices[i * 3 + 1] = v.y();
			vertices[i * 3 + 2] = v.z();
//			}
			v = texes.get();
			textureCoords[i * 2] = v.x();
			textureCoords[i * 2 + 1] = v.y();
			v = normals.get();
			norms[i * 3] = v.x();
//			if (flipflag == 1) {
//				norms[i * 3 + 1] = v.z();
//				norms[i * 3 + 2] = v.y();
//			} else {
			norms[i * 3 + 1] = v.y();
			norms[i * 3 + 2] = v.z();
//			}
			i++;
		}
		i = 0;
		while (b.hasRemaining()) {
			AIFace f = b.get();
			IntBuffer ib = f.mIndices();
			while (ib.hasRemaining()) {
				indices[i++] = ib.get();
			}
		}
		int[] boneIndices = new int[numofvertices * 4];
		float[] weights = new float[numofvertices * 4];
		return new AnimatedModelData(vertices, textureCoords, norms, indices, boneIndices, weights,
				loadBones(m, boneIndices, weights, dest, bonemap, transmat));
	}

	public static List<Bone> loadBones(AIMesh m, int[] boneIndices, float[] weights, List<Bone> dest,
			Map<String, Bone> bonemap, Matrix4f transmat) {
		m.mAnimMeshes();
		int num = m.mNumBones();
		PointerBuffer bones = m.mBones();
		if (dest == null)
			dest = new ArrayList<>(num);
		for (int i = 0; i < num; i++) {
			AIBone b = AIBone.create(bones.get(i));
			Matrix4f off = toMatrix(b.mOffsetMatrix()).invert();
			transmat.mul(off, off);
			Bone bone = new Bone(i, b.mName().dataString(), off.invert());
//			AppFolder.log.println("found bone with the name '" + bone.name + "' and ID " + bone.ID() + "!");
			dest.add(bone);
			bonemap.put(bone.name, bone);
			AIVertexWeight.Buffer buff = b.mWeights();
			int numweights = b.mNumWeights();
			for (int w1 = 0; w1 < numweights; w1++) {
				AIVertexWeight w = buff.get();
				// 4 ints(/indices)/floats(/weights) per vertex
				int id = w.mVertexId() * 4;
				int add = 0;
				for (; add < 4 && weights[id + add] != 0; add++)
					;
				if (add >= 4) {
					AppFolder.log.println("FAIL!");
				} else {
					boneIndices[id + add] = i;
					weights[id + add] = w.mWeight();
				}
			}
		}
		for (int i = 0; i < boneIndices.length; i++)
			if (boneIndices[i] == -1) {
				AppFolder.log.println("found a -1! " + i);
				boneIndices[i] = 0;
				weights[i] = 0;
			}
		return dest;
	}

	public static Bone processNodeHierarchy(AINode node, Bone parent, Map<String, Bone> bones) {
		String name = node.mName().dataString();
		Bone b = bones.get(name);
		if (b != null)
			b.setNodeTransform(toMatrix(node.mTransformation()));
		int numchildren = node.mNumChildren();
		PointerBuffer children = node.mChildren();
		for (int i = 0; i < numchildren; i++) {
			AINode aichild = AINode.create(children.get(i));
			Bone child = processNodeHierarchy(aichild, b, bones);
			if (b == null) {
				if (child != null) {
					b = child;
				}
			} else if (child != null) {
				b.children().add(child);
				child.setParent(b);
			}
		}
		return b;
	}

	public static ArrayList<Animation> loadAnimations(AIScene s, List<Bone> bones, Map<String, Bone> bonemap,
			ArrayList<Animation> dest, Matrix4f transmat, Matrix4f calcMat) {
		if (dest == null)
			dest = new ArrayList<>();
		PointerBuffer as = s.mAnimations();
//		AppFolder.log.println("amount of animations: " + as.limit());
		while (as.hasRemaining()) {
			ArrayList<KeyFrame> keyframes = new ArrayList<>();
			AIAnimation a = AIAnimation.create(as.get());
//			AppFolder.log.println("Animation \"" + a.mName().dataString() + "\"");
			int numchannels = a.mNumChannels();
			PointerBuffer channels = a.mChannels();
			AINodeAnim anim1 = AINodeAnim.create(channels.get(0));
			int numkeyframes = anim1.mNumPositionKeys();
			AIVectorKey.Buffer poss1 = anim1.mPositionKeys();
			for (int c = 0; c < numkeyframes; c++) {
				float t = (float) (poss1.get().mTime() * a.mTicksPerSecond());
				keyframes.add(new KeyFrame(t, new Transform[bones.size()]));
			}
			for (int c = 0; c < numchannels; c++) {
				AINodeAnim anim = AINodeAnim.create(channels.get(c));
				Bone b = bonemap.get(anim.mNodeName().dataString());
				if (b == null) {
//					AppFolder.log.println("no bone with the name '" + anim.mNodeName().dataString() + "'!");
//					return dest;
					continue;
				}
				AIVectorKey.Buffer poss = anim.mPositionKeys();
				AIQuatKey.Buffer rots = anim.mRotationKeys();
//				AppFolder.log.println("new channel for bone " + b.name);
				for (int i = 0; i < numkeyframes; i++) {
					AIVectorKey k = poss.get(i);
					AIVector3D v = k.mValue();
					AIQuatKey r = rots.get(i);
					AIQuaternion q = r.mValue();
					Transform t;
					t = new Transform(new Vector3f(v.x(), v.y(), v.z()), new Quaternionf(q.x(), q.y(), q.z(), q.w()));
					keyframes.get(i).getPose()[b.boneID()] = t;
				}
			}
			// there could be multiple root bones

			for (int i = 0; i < numkeyframes; i++) {
				for (int b = 0; b < bones.size(); b++) {
//					rootBone.transformByParentTransform(keyframes.get(i), new Matrix4f());
					if (bones.get(b).parent() == null)// parent null -> top of hierarchy
						bones.get(b).transformByParentTransform(keyframes.get(i), new Matrix4f(transmat));
				}
			}
			Animation an = new Animation(keyframes, (float) (a.mDuration() * a.mTicksPerSecond()),
					a.mName().dataString());
			dest.add(an);
//			AppFolder.log.println("found " + an.toString());
		}
		return dest;
	}

	public static void applyTransformationMatrix(ModelData d, Matrix4f m) {
		Vector3f v = Vects.calcVect();// , v2 = Vects.calcVect2();
		for (int i = 0; i < d.positions().length; i += 3) {
			v.set(d.positions()[i], d.positions()[i + 1], d.positions()[i + 2]);
			m.transformPosition(v);
			d.positions()[i] = v.x;
			d.positions()[i + 1] = v.y;
			d.positions()[i + 2] = v.z;
		}
		for (int i = 0; i < d.positions().length; i += 3) {
			v.set(d.normals()[i], d.normals()[i + 1], d.normals()[i + 2]);
			m.transformDirection(v);
			d.normals()[i] = v.x;
			d.normals()[i + 1] = v.y;
			d.normals()[i + 2] = v.z;
		}
	}

	// \Animations

	public static Matrix4f toMatrix(AIMatrix4x4 m) {
		return toMatrix(m, new Matrix4f());
	}

	public static Matrix4f toMatrix(AIMatrix4x4 m, Matrix4f dest) {
//		return dest.set(m.a1(), m.b1(), m.c1(), m.d1(), m.a2(), m.b2(), m.c2(), m.d2(), m.a3(), m.b3(), m.c3(), m.d3(),
//				m.a4(), m.b4(), m.c4(), m.d4());
		// müsste korrekt sein!
		return dest.set(m.a1(), m.a2(), m.a3(), m.a4(), m.b1(), m.b2(), m.b3(), m.b4(), m.c1(), m.c2(), m.c3(), m.c4(),
				m.d1(), m.d2(), m.d3(), m.d4()).transpose();
	}

	public static VAO loadToVAO(int[] indices, int[] dimensions, float[]... data) {
		VAO ret = new VAO();
		ret.setIndicesArray(VBO.createStaticIndicesVBO(indices));
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				ret.setVbo(i, VBO.createStaticVertexDataVBO(data[i], dimensions[i]));
			}
		}
		return ret;
	}

	public static VAO loadToVAO(ArrayListInt indices, int[] dimensions, float[]... data) {
		VAO ret = new VAO();
		ret.setIndicesArray(VBO.createStaticIndicesVBO(indices));
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				ret.setVbo(i, VBO.createStaticVertexDataVBO(data[i], dimensions[i]));
			}
		}
		return ret;
	}

	public static VAO loadToVAO(float[] vertices, int dimensions) {
		return loadToVAO(new int[] { dimensions }, vertices);
	}

	public static VAO loadToVAO(int[] dimensions, float[]... arrays) {
		VAO ret = new VAO();
		for (int i = 0; i < arrays.length; i++)
			if (arrays[i] != null)
				ret.setVbo(i, VBO.createStaticVertexDataVBO(arrays[i], dimensions[i]));
		return ret;
	}

	public static VAO loadToVAO(ArrayListInt indices, int[] dimensions, ArrayListFloat... data) {
		VAO ret = new VAO();
		ret.setIndicesArray(VBO.createStaticIndicesVBO(indices));
		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				ret.setVbo(i, VBO.createStaticVertexDataVBO(data[i], dimensions[i]));
			}
		}
		return ret;
	}

//	public static Bone processBoneHierarchy(AINode node, Bone parent, Map<String, Bone> allBones) {
//		String name = node.mName().dataString();
//		Bone b = allBones.get(name);
//		if (b == null) {
//			AppFolder.log.println("didn't find a bone with the name '" + name + "'!");
//			return null;
//		}
//		b.setParent(parent);
//		int numchildren = node.mNumChildren();
//		PointerBuffer children = node.mChildren();
//		for (int i = 0; i < numchildren; i++) {
//			AINode aichild = AINode.create(children.get(i));
//			Bone child = processBoneHierarchy(aichild, b, allBones);
//			if (child == null) {
//				
//			} else {
//				b.children().add(child);
//			}
//		}
//		return b;
//	}

//	private static class Node {
//
//		private final String name;
//		private Node parent;
//		private Matrix4f transformation;
//		private ArrayList<Node> children = new ArrayList<>();
//
//		private Node(String name, Node parent) {
//			this.parent = parent;
//			this.name = name;
//		}
//
//		private Node find(String name) {
//			if (this.name.equals(name))
//				return this;
//			Node ret = null;
//			for (int i = 0; i < children.size() && ret == null; i++) {
//				ret = children.get(i).find(name);
//			}
//			return ret;
//		}
//
//	}
//	public static Node processNodeHierarchy(AINode node, Node parent, Map<String, Bone> bones, Bone[] rootBonePut) {
//		String name = node.mName().dataString();
//		Node n = new Node(name, parent);
//		if (rootBonePut[0] == null) {
//			Bone b = bones.get(name);
//			if (b != null)
//				rootBonePut[0] = b;
//		}
//		int numchildren = node.mNumChildren();
//		PointerBuffer children = node.mChildren();
//		for (int i = 0; i < numchildren; i++) {
//			AINode aichild = AINode.create(children.get(i));
//			Node child = processNodeHierarchy(aichild, n, bones, rootBonePut);
//			n.children.add(child);
//		}
//		return n;
//	}

}
