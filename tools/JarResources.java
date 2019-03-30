package tools;

import java.io.*;
import java.nio.ByteBuffer;

public class JarResources {

	public static String loadJarTextFile(String relativePath) {
		if (relativePath.charAt(0) != '/')
			relativePath = '/' + relativePath;
		InputStream in = JarResources.class.getResourceAsStream(relativePath);
		if (in == null) {
			AppFolder.log.println("text file at \"" + relativePath + "\" isn't there!");
			return null;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		StringBuilder ret = new StringBuilder();
		try {
			while (r.ready()) {
				ret.append(r.readLine());
				ret.append('\n');
			}
			return ret.toString();
		} catch (Exception e) {
			e.printStackTrace(AppFolder.log);
			return null;
		}
	}

	public static ByteBuffer loadJarFile(String filepath) {
		return AppFolder.readJarBytes(filepath);
	}

}
