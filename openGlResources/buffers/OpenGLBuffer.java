package openGlResources.buffers;

public abstract class OpenGLBuffer {
	
	protected int ID;
	
	protected OpenGLBuffer(int ID){
		this.ID = ID;
	}
	
	public int ID(){
		return ID;
	}
	
	public abstract void bind();
	
	public abstract void unbind();
	
	public abstract void delete();
	
}
