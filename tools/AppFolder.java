package tools;

import java.io.*;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import collectionsStuff.ArrayListB;

public class AppFolder {

	public static String folder;
	public static Config prefs;
	public static Config graphicsPrefs;
	public static Config soundPrefs;
	public static LogPrinter log;

	public static void init(String folderName) {
		init(folderName, "latestlog.txt");
	}

	public static void init(String folderName, String logfilename) {
		String os = System.getProperty("os.name").toUpperCase();
		if (os.contains("WIN")) {
			folder = System.getenv("APPDATA") + "/" + folderName + '/';
			System.err.println(folder);
		} // else if(os.contains("MAC")){

		// }
		else {
			folder = System.getProperty("user.home") + "/.local/share/" + folderName + '/';
			System.err.println(folder);
		}
		File configFolder = new File(folder + "config/");
		if (!configFolder.getParentFile().exists())
			configFolder.getParentFile().mkdirs();
		if (!configFolder.exists())
			configFolder.mkdir();
		prefs = new Config("config/preferences.cfg");
		graphicsPrefs = new Config("config/graphicsPreferences.cfg");
		soundPrefs = new Config("config/soundPreferences.cfg");
		log = LogPrinter.createNewLogPrinter(folder + "/logs/" + logfilename);
	}

	public static String readTextFile(String pathInsideThingsFolder) {
		return readTextFileAbsolute(folder + pathInsideThingsFolder);
	}

	public static String readTextFileAbsolute(String absolutePath) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(absolutePath));
			StringBuilder s = new StringBuilder();
			while (r.ready()) {
				s.append(r.readLine());
				s.append('\n');
			}
			r.close();
			return s.toString();
		} catch (IOException e) {
			return null;
		} catch (Exception exceptional) {
			exceptional.printStackTrace(AppFolder.log);
			return null;
		}
	}

	public static void writeTextFile(String pathInsideThingsFolder, String content) {
		File f = new File(folder + pathInsideThingsFolder);
		try {
			f = f.getCanonicalFile();
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();

			if (!f.exists()) {
				f.createNewFile();// if(!f.createNewFile()){...}
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			AppFolder.log.println("An error ocurred while writing to File '" + f.getAbsolutePath() + "' !!!");
			e.printStackTrace(AppFolder.log);
			System.exit(-1);
		}
	}

	public static String readJarFile(String path) {
		InputStream in = AppFolder.class.getClassLoader().getResourceAsStream(path);
		if (in == null)
			return null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			String text = "";
			String line = reader.readLine();
			while (line != null) {
				text += line + "\n";
				line = reader.readLine();
			}
			reader.close();
			return text;
		} catch (Exception e) {
			e.printStackTrace(AppFolder.log);
			AppFolder.log.println("Error while reading File " + path);
			System.exit(-1);
		}
		return null;
	}

	public static boolean copyFromJarToFolder(String path, String folderPath) {
		String jarText = readJarFile(path);
		if (jarText != null)
			return false;
		writeTextFile(folderPath, jarText);
		return true;
	}

	public static String[] getFiles(String folder) {
		File f = new File(folder);
		if (f.exists()) {
			return f.list();
		}
		return null;
	}

	public static ByteBuffer readJarBytes(String fileInJar) {
		try {
			InputStream I = AppFolder.class.getClassLoader().getResourceAsStream(fileInJar);
			if(I == null)
				return null;
			ArrayListB bytes = new ArrayListB();
			byte[] input = new byte[1];
			int in = I.read(input);
			while (in != -1) {
				bytes.add(input[0]);
				in = I.read(input);
			}
			I.close();
			ByteBuffer b = BufferUtils.createByteBuffer(bytes.size());
			for (int i = 0; i < bytes.size(); i++) {
				b.put(bytes.get(i));
			}
			b.flip();
			return b;
		} catch (IOException e) {
			AppFolder.log.println("Failed to read " + fileInJar + " from the jar to a ByteBuffer!");
			e.printStackTrace(AppFolder.log);
			return null;
		} catch (Exception e) {
			AppFolder.log.println("Failed to read " + fileInJar + " from the jar to a ByteBuffer!");
			e.printStackTrace(AppFolder.log);
			return null;
		}
	}

	public static ByteBuffer readBytes(String fileInFolder) {
		try {
			InputStream I = new FileInputStream(fileInFolder);
			ArrayListB bytes = new ArrayListB();
			byte[] input = new byte[1];
			int in = I.read(input);
			while (in != -1) {
				bytes.add(input[0]);
				in = I.read(input);
			}
			I.close();
			ByteBuffer b = BufferUtils.createByteBuffer(bytes.size());
			for (int i = 0; i < bytes.size(); i++) {
				b.put(bytes.get(i));
			}
			b.flip();
			return b;
		} catch (IOException e) {
			AppFolder.log.println("Failed to read " + fileInFolder + " to a ByteBuffer!");
			e.printStackTrace(AppFolder.log);
			return null;
		} catch (Exception e) {
			AppFolder.log.println("Failed to read " + fileInFolder + " to a ByteBuffer!");
			e.printStackTrace(AppFolder.log);
			System.exit(-1);
			return null;
		}
	}

	public static void savePreferences() {
		if (prefs != null)
			prefs.save();
		if (graphicsPrefs != null)
			graphicsPrefs.save();
		if (soundPrefs != null)
			soundPrefs.save();
	}

	public static InputStream getInputStream(String path) throws FileNotFoundException {
		if (path.startsWith("/")) {
			return new FileInputStream(path);
		} else {
			return AppFolder.class.getClassLoader().getResourceAsStream(path);
		}
	}

	public static boolean absolute(String path) {
		return path.charAt(0) == '/';
	}

//	public static String getFolderOfApp(String appname) {
//		// FIXME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//		return System.getProperty("user.home") + '/' + appname;
//	}

}
