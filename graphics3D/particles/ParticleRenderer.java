package graphics3D.particles;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.lang.Math;
import java.util.List;

import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import loaders.Loader;
import openGlResources.buffers.VAO;
import openGlResources.buffers.VBO;
import tools.AppFolder;
import tools.Meth;
import tools.misc.Vects;

public class ParticleRenderer {

	// public static final boolean FI = false;
	private static final float[] VERTICES = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
	public static final int INSTANCE_DATA_LENGTH = 21;

	// private static final FloatBuffer buffer =
	// BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);

	private VAO quad;
	private static ParticleShader shader;
	public static boolean inited;

	private VBO vbo;
	private int pointer = 0;
	protected ParticleMaster pm;
	protected Vector2f o1 = new Vector2f(), o2 = new Vector2f();
	protected int MAX_INSTANCES = 20000;
	protected float[] vboData;

	protected ParticleRenderer(ParticleMaster pm) {
		this(pm, 20_000);
	}
	
	protected ParticleRenderer(ParticleMaster pm, int MAX_INSTANCES) {
		vboData = new float[MAX_INSTANCES * INSTANCE_DATA_LENGTH];
		this.vbo = VBO.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES * 4, GL_STREAM_DRAW);
		quad = Loader.loadToVAO(VERTICES, 2);
		quad.bind();
		quad.addInstancedAtribute(vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);
		quad.addInstancedAtribute(vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);
		quad.addInstancedAtribute(vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);
		quad.addInstancedAtribute(vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);
		quad.addInstancedAtribute(vbo, 5, 4, INSTANCE_DATA_LENGTH, 16);
		quad.addInstancedAtribute(vbo, 6, 1, INSTANCE_DATA_LENGTH, 20);

		shader = new ParticleShader();
		inited = true;
		this.pm = pm;

	}

	protected void render(Matrix4f viewMatrix, Matrix4f projMat, List<List<Particle>> particles, float planey,
			boolean upordownside) {
		// int gesC = 0;
		prepare(projMat);
		for (int j = 0; j < particles.size(); j++) {
			List<Particle> particleList = particles.get(j);
			if (particleList.size() > 0) {
				short t = particleList.get(0).tex;
				ParticleTexture tex = pm.get(t);
				bindTexture(tex);
				pointer = 0;
				float[] vboData = new float[particleList.size() * INSTANCE_DATA_LENGTH];
				if (tex.isTransparent()) {
					glBlendFunc(GL_SRC_ALPHA, GL_ONE);
				} else {
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				}

				// if (tex == PTM.lightning || tex == PTM.cloudy) {
				// shader.loadBright(new Vector4f(0.9f, 0.9f, 0.9f, 1.0f));
				// } else if (tex == PTM.fire) {
				// shader.loadBright(new Vector4f(0.7f, 0.5f, 0.5f, 1.0f));
				// } else {
				// shader.loadBright(new Vector4f(0, 0, 0, 1.0f));
				// }
				List<Particle> list = particles.get(t);
				// synchronized(list){
				for (Particle p : list) {
					if ((upordownside ? p.getPosition().y >= planey : p.getPosition().y <= planey)) {
						updateModelViewMatrix(p.getPosition(), p.getRotation(), p.getScale(), viewMatrix, vboData);
						updateTexCoordInfo(p, tex, vboData);
					}
				}
				// }
				vbo.update(vboData);
				GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVbo(0).count(), particleList.size());
			}
		}
		finishRendering();
	}

	protected void render(List<List<Particle>> particles, Matrix4f vm, Matrix4f projMat) {
		try {
			prepare(projMat);
			for (int j = 0; j < particles.size(); j++) {
				List<Particle> particleList = particles.get(j);
				if (particleList.size() > 0) {
					short t = particles.get(j).get(0).tex;
					ParticleTexture tex = pm.get(t);
					bindTexture(tex);
					pointer = 0;
					int partics = Math.min(MAX_INSTANCES, particleList.size());
//				vboData = new float[partics * INSTANCE_DATA_LENGTH];
					if (tex.isTransparent()) {
						glBlendFunc(GL_SRC_ALPHA, GL_ONE);
//				GLUtils.enableTranslucency();
					} else {
						glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//				GLUtils.disableTranslucency();
					}
					int count = 0;
					for (int i = 0; i < particleList.size() && i < partics && count < MAX_INSTANCES; i++) {
						Particle p = particleList.get(i);
						count++;
						updateModelViewMatrix(p.getPosition(), p.getRotation(), p.getScale(), vm, vboData);
						updateTexCoordInfo(p, tex, vboData);
					}
					vbo.update(vboData, partics * INSTANCE_DATA_LENGTH);
					GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVbo(0).count(), particleList.size());
				}
			}
			finishRendering();
		} catch (Exception e) {
			e.printStackTrace(AppFolder.log);
		}
	}

	public void bindTexture(ParticleTexture tex) {
		tex.getTex().bindAndActivateTo(0);
		shader.setTexture(0);
		shader.loadNOR(tex.getNOR());
//		if (tex.timeAndWeatherDarkening()) {
//			shader.loadColorMult(((1 - WeatherController.blendFactor()) * (1 / 0.7f)) * TM.particleColorMult()
//					* TM.particleColorMult() * TM.particleColorMult() * 0.75f + 0.25f);
//		} else {
		shader.loadColorMult(1);
//		}
		shader.loadBright(tex.brightness());
	}

	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f modelView = new Matrix4f();

	public void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix,
			float[] vboData) {
		modelMatrix.identity();
		// .x, position.y, logarithmicDistance(position.z)
		modelMatrix.translate(position);
		modelMatrix.m00(viewMatrix.m00());
		modelMatrix.m01(viewMatrix.m10());
		modelMatrix.m02(viewMatrix.m20());
		modelMatrix.m10(viewMatrix.m01());
		modelMatrix.m11(viewMatrix.m11());
		modelMatrix.m12(viewMatrix.m21());
		modelMatrix.m20(viewMatrix.m02());
		modelMatrix.m21(viewMatrix.m12());
		modelMatrix.m22(viewMatrix.m22());
		modelMatrix.rotate(rotation * Meth.angToRad, Vects.ZP);
		modelMatrix.scale(scale);
		storeMatrixData(viewMatrix.mul(modelMatrix, modelView), vboData);
	}

	private void updateTexCoordInfo(Particle p, ParticleTexture t, float[] data) {
		p.getTextureCoordInfo(t.getNOR(), t.NORSquared(), o1, o2);
		data[pointer++] = o1.x;
		data[pointer++] = o1.y;
		data[pointer++] = o2.x;
		data[pointer++] = o2.y;
		data[pointer++] = p.getBlend();
	}

	private void storeMatrixData(Matrix4f matrix, float[] vboData) {
		vboData[pointer++] = matrix.m00();
		vboData[pointer++] = matrix.m01();
		vboData[pointer++] = matrix.m02();
		vboData[pointer++] = matrix.m03();
		vboData[pointer++] = matrix.m10();
		vboData[pointer++] = matrix.m11();
		vboData[pointer++] = matrix.m12();
		vboData[pointer++] = matrix.m13();
		vboData[pointer++] = matrix.m20();
		vboData[pointer++] = matrix.m21();
		vboData[pointer++] = matrix.m22();
		vboData[pointer++] = matrix.m23();
		vboData[pointer++] = matrix.m30();
		vboData[pointer++] = matrix.m31();
		vboData[pointer++] = matrix.m32();
		vboData[pointer++] = matrix.m33();
	}

	protected void cleanUp() {
		shader.cleanUp();
	}

	private void prepare(Matrix4f projMat) {
		shader.start();
		shader.loadProjectionMatrix(projMat);
		quad.bind();
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		glEnableVertexAttribArray(5);
		glEnableVertexAttribArray(6);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(false);
	}

	private void finishRendering() {
		glDepthMask(true);
		glDisable(GL_BLEND);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);
		glDisableVertexAttribArray(5);
		glDisableVertexAttribArray(6);
		glBindVertexArray(0);
		shader.stop();
	}

	public static void setProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

}
