package tools;

/**
 * A GameThread is a Thread with some extra capabilities included. It is to be
 * used for repetitive tasks, for that it measures frameTimeSeconds and
 * therefore FPS and caps them for reduction of CPU usage to a certain
 * specifiable value (standard is 120)
 * 
 * @author xaver
 * 
 */
public abstract class GameThread extends Thread {

	private static int threadcount = 1;

	private long minFTM;
	private long ftm;
	private long lastTime;
	protected boolean running = true;
	private boolean decoupled = false;

	/**
	 * for start and end override {@link GameThread#init()} and
	 * {@link GameThread#end()} respectively. For stopping this GameThread call
	 * {@link GameThread#stopThread()}. It will stop automatically if
	 * [{@link ThreadManager#running()} equals false
	 */
	public GameThread(String name, float maxFPS) {
		super(name);
		setMaxFPS(maxFPS);
	}

	/**
	 * creates a new GameThread with a standard maxFPS of 120 (equals a min loop
	 * time of 8ms). For start and end override {@link GameThread#init()} and
	 * {@link GameThread#end()} respectively. For stopping this GameThread call
	 * {@link GameThread#stopThread()}. It will stop automatically if
	 * [{@link ThreadManager#running()} equals false
	 */
	public GameThread(String name) {
		this(name, 120);
	}

	@Override
	public synchronized final void start() {
		super.start();
	}

	@Override
	public final void run() {
		ftm = minFTM;
		AppFolder.log.println("started! (ThreadCount: " + ++threadcount + ')');
		init();
		while (running && (decoupled || ThreadManager.running())) {
			lastTime = System.currentTimeMillis();
			try {
				loop();
			} catch (Exception e) {
				e.printStackTrace(AppFolder.log);
				running = false;
				break;
			}
			long delta = System.currentTimeMillis() - lastTime;
			if (delta < minFTM) {
				Meth.wartn(minFTM - delta);
			}
			ftm = System.currentTimeMillis() - lastTime;
		}
		end();
		AppFolder.log.println("stopped (ThreadCount: " + --threadcount + ')');
	}

	public void init() {

	}

	/**
	 * for start and end override {@link GameThread#init()} and
	 * {@link GameThread#end()} respectively. For stopping this GameThread call
	 * {@link GameThread#stopThread()}
	 */
	public abstract void loop();

	public void end() {

	}

	public void decoupleFromGame() {
		decoupled = true;
	}

	public void stopThread() {
		running = false;
	}

	/**
	 * @return the time between the start of this one loop() execution and this call
	 */
	protected long currentLoopTime() {
		return System.currentTimeMillis() - lastTime;
	}

	public void setMaxFPS(float fps) {
		minFTM = (long) (1000f / fps);
	}

	public long frameTimeMillis() {
		return ftm;
	}

	public float frameTimeSeconds() {
		return ftm * 0.001f;
	}

	public float fps() {
		return 1000f / frameTimeMillis();
	}

}
