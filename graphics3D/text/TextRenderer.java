package graphics3D.text;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;

import generic.Thing;
import genericRendering.Renderer;
import genericRendering.pipeline.PipelineResult;
import graphics3D.Camera;
import models.text.Text;
import openGlResources.GLUtils;

public class TextRenderer extends Renderer<Text, TextShader> {

	protected final Matrix4f projViewMat = new Matrix4f();

	public TextRenderer() {
		super(new TextShader());
		autoSortList = false;
		noWireframe = true;
	}

	@Override
	public void sortList(List<Text> toRender, Camera cam) {
		sortBackToFrontAndVAOs(toRender, cam.position());
	}

	@Override
	protected boolean startRendering(Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
//		shader.projViewMat().set(shader.calcMat.set(projMat).mul(c.viewMat()));
		projViewMat.set(projMat).mul(c.viewMat());
		shader.projViewMat().set(inverseViewportScaleMat);
		GLUtils.enableTranslucency();
		return true;
	}

	@Override
	protected boolean prepareFor(Text t, Camera c, Matrix4f projMat, Matrix4f inverseViewportScaleMat,
			Map<String, PipelineResult> pipelineResults) {
		shader.diffuse().set(t.material().diffuse(), 0);
		shader.bloomMat().set(t.material().bloomMat(), 1);
		shader.atlasTexture().set(t.getFont().getTextureAtlas(), 2);
		shader.fakeLight().set(t.fakeLight());
		shader.transMat().set(t.createTransformationMatrix(shader.calcMat));
		if (t.drawScaled())
			shader.projViewMat().set(inverseViewportScaleMat);
		else
			shader.projViewMat().set(projViewMat);
		return false;
	}

	@Override
	protected void stopRendering() {
		GLUtils.disableTranslucency();
	}

	@Override
	public int renderTiming() {
		return 1000;
	}

	@Override
	public int priority(Thing m) {
		return m instanceof Text ? 100 : 0;
	}

}
