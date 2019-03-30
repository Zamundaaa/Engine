package window;

/**
 * this class is not even close to be finished. Don't use yet!
 * 
 * @author xaver
 * 
 */
public class EGLWindow extends Window {

	/**
	 * create a EGL context first. Just call GLHandler.init(true or false);
	 */
	protected EGLWindow(String title, int startXPos, int startYPos, int w, int h, int openGL_version_major,
			int openGL_version_minor, String internalWindowName) {
		super(title, startXPos, startYPos, w, h, openGL_version_major, openGL_version_minor, internalWindowName);

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowResized() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPosition(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearColor(float r, float g, float b, float a) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWindowIcons(String... icons) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void resizable(boolean b) {
		
	}

	@Override
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

}
