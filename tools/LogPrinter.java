package tools;

import java.io.*;

public class LogPrinter extends PrintStream {

	/**
	 * printSystemMillis is preferred (if printHumanTime and printSystemMillis are
	 * set)
	 */
	public boolean printHumanTime = true, printSystemMillis;

	public static LogPrinter createNewLogPrinter(String absolutePath) {
		return createNewLogPrinter(absolutePath, false);
	}

	public static LogPrinter createNewLogPrinter(String absolutePath, boolean append) {
		try {
			File f = new File(absolutePath);
			if (!f.exists()) {
				File parent = f.getParentFile();
				if (!parent.exists())
					parent.mkdirs();
				f.createNewFile();
			}
			String s = null;
			LogPrinter ret;
			if (append) {
				s = AppFolder.readTextFileAbsolute(absolutePath);
				ret = new LogPrinter(f, s);
			} else {
				ret = new LogPrinter(f);
			}
			return ret;
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public LogPrinter(File f, String appendOnBeginning) throws FileNotFoundException {
		super(f);
		super.print(appendOnBeginning);
	}

	public LogPrinter(File f) throws FileNotFoundException {
		super(f);
	}

	/**
	 * @comment calls out.println() and System.err.println()
	 */
	@Override
	public synchronized void println() {
		System.err.println();
		super.println();
	}

	public synchronized void println(boolean x) {
		println(x + "");
	}

	@Override
	public synchronized void println(double x) {
		println("" + x);
	}

	@Override
	public synchronized void println(float x) {
		print(x + "\n");
	}

	@Override
	public synchronized void println(int x) {
		print(x + "\n");
	}

	@Override
	public synchronized void println(long x) {
		print(x + "\n");
	}

	@Override
	public synchronized void println(Object o) {
		println("" + o);
	}

	/**
	 * @param x
	 * @comment calls out.println(x) and System.err.println(x)
	 */
	@Override
	public synchronized void println(String x) {
		print(x + '\n');
	}

	/**
	 * @param x
	 * @comment calls out.print(x) and System.err.print(x)
	 */
	@Override
	public synchronized void print(String x) {
		if (printSystemMillis)
			x = System.currentTimeMillis() + " [" + Thread.currentThread().getName() + "]: " + x;
		else if (printHumanTime)
			x = Time.getTime(true, true, true) + " [" + Thread.currentThread().getName() + "]: " + x;
		else
			x = '[' + Thread.currentThread().getName() + "]: " + x;
		System.err.print(x);
		super.print(x);
	}

	/**
	 * @param x
	 * @comment calls out.println(x) and System.err.println(x)
	 */
	public synchronized void println(String x, boolean printThreadName) {
		print(x + '\n', printThreadName);
	}

	/**
	 * @param x
	 * @comment calls out.print(x) and System.err.print(x)
	 */
	public synchronized void print(String x, boolean printThreadName) {
		if (printSystemMillis)
			x = System.currentTimeMillis() + " [" + Thread.currentThread().getName() + "]: " + x;
		else if (printHumanTime)
			x = Time.getTime(true, true, true) + " [" + Thread.currentThread().getName() + "]: " + x;
		else if (printThreadName)
			x = '[' + Thread.currentThread().getName() + "]: " + x;
		System.err.print(x);
		super.print(x);
	}

	/**
	 * @param x
	 * @comment calls out.print(x) and System.err.print(x)
	 */
	public synchronized void printDirectToFile(String x) {
		System.err.print(x);
		super.print(x);
	}

}
