package graphics3D.particles;

import openGlResources.textures.Texture;

public class ParticleTexture{

	private Texture t;
	private int NOR, NORSq;
	private boolean transparent = false, td = false;
	private float brightness = 0;

	public ParticleTexture(Texture t, int nOR) {
		this.t = t;
		NOR = nOR;
		NORSq = NOR*NOR;
	}

	public ParticleTexture(Texture t, int nOR, boolean Transparent) {
		this.t = t;
		this.transparent = Transparent;
		NOR = nOR;
		NORSq = NOR*NOR;
	}
	
	public Texture getTex() {
		return t;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparency(boolean bool) {
		transparent = bool;
	}

	public int getNOR() {
		return NOR;
	}

	public void setTimeDarkening(boolean b) {
		td = b;
	}

	public boolean timeAndWeatherDarkening() {
		return td;
	}

	public void setBright(float brightness) {
		this.brightness = brightness;
	}

	public float brightness() {
		return brightness;
	}


	public int NORSquared() {
		return NORSq;
	}

}
