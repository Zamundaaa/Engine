package tools;

public class ThreadManager {

	private static volatile boolean running = true;

	public static void goOn() {
		running = true;
	}

	public static void shutdown() {
		// Err.err.println("shutting down Threads...");
		running = false;
	}

	public static boolean running() {
		return running;
	}

}
