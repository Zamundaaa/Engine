package tools;

import java.util.*;

public class Config {

	private final Map<String, String> stringConfigs = new HashMap<>();
	private final Map<String, Integer> intConfigs = new HashMap<>();
	private final Map<String, Long> longConfigs = new HashMap<>();
	private final Map<String, Float> floatConfigs = new HashMap<>();
	private final Map<String, Double> doubleConfigs = new HashMap<>();
	private final Map<String, Boolean> boolConfigs = new HashMap<>();
	private final Map<String, Config> configConfigs = new HashMap<>();

	public final String path;
	public final boolean relativePath;
	private int entrys;

	public Config(String pathInsideThingsFolder) {
		this.path = pathInsideThingsFolder;
		relativePath = false;
		load(false);
	}

	/*
	 * @param createFileIfNotPresent only works outside jars and compressed files
	 */
	public Config(String path, boolean absoluteOrNot, boolean createFileIfNotPresent) {
		this.path = path;
		relativePath = absoluteOrNot;
		load(createFileIfNotPresent);
	}

	public Config(String pathInsideThingsFolder, boolean createFileIfNotPresent) {
		this.path = pathInsideThingsFolder;
		relativePath = false;
		load(createFileIfNotPresent);
	}

	public Config(String pathInsideThingsFolder, String content) {
		this.path = pathInsideThingsFolder;
		relativePath = false;
		load(content);
	}

	public int getIntConfig(String name) {
		return intConfigs.get(name);
	}

	public int getIntConfig(String name, int defaultValue) {
		Integer i = intConfigs.get(name);
		if (i == null)
			return defaultValue;
		else
			return i;
	}

	public long getLongConfig(String name) {
		return longConfigs.get(name);
	}

	public long getLongConfig(String name, long defaultValue) {
		Long i = longConfigs.get(name);
		if (i == null)
			return defaultValue;
		else
			return i;
	}

	public float getFloatConfig(String name) {
		return floatConfigs.get(name);
	}

	public float getFloatConfig(String name, float defaultValue) {
		Float i = floatConfigs.get(name);
		if (i == null)
			return defaultValue;
		else
			return i;
	}

	public double getDoubleConfig(String name) {
		return doubleConfigs.get(name);
	}

	public double getDoubleConfig(String name, double defaultValue) {
		Double i = doubleConfigs.get(name);
		if (i == null)
			return defaultValue;
		else
			return i;
	}

	public boolean getBoolConfig(String name) {
		return boolConfigs.get(name);
	}

	public boolean getBoolConfig(String name, boolean defaultValue) {
		Boolean b = boolConfigs.get(name);
		if (b == null)
			return defaultValue;
		else
			return b;
	}

	public String getConfig(String name) {
		return stringConfigs.get(name);
	}

	public String getConfig(String name, String returnIfNotPresent) {
		if (stringConfigs.containsKey(name)) {
			return getConfig(name);
		} else {
			return returnIfNotPresent;
		}
	}

	public void setConfig(String name, String conf) {
		stringConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size()
				+ doubleConfigs.size() + boolConfigs.size();
	}

	public void setConfig(String name, int conf) {
		intConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size()
				+ doubleConfigs.size() + boolConfigs.size();
	}

	public void setConfig(String name, long conf) {
		longConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size()
				+ doubleConfigs.size() + boolConfigs.size();
	}

	public void setConfig(String name, float conf) {
		floatConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size()
				+ doubleConfigs.size() + boolConfigs.size();
	}

	public void setConfig(String name, double conf) {
		doubleConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size()
				+ doubleConfigs.size() + boolConfigs.size();
	}

	public boolean setConfig(String name, boolean conf) {
		Boolean b = boolConfigs.put(name, conf);
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size()
				+ doubleConfigs.size() + boolConfigs.size();
		return b == null || b;
	}

	public void load(boolean createFileIfNotPresent) {
		String content = AppFolder.readTextFile(path);
		if (relativePath) {
			content = AppFolder.readJarFile(path);
			// System.out.println("loaded relative! " + content);
		} else {
			content = AppFolder.readTextFile(path);
			if (content == null && createFileIfNotPresent) {
				// System.err.println("The configuration file '" +
				// Tools.getFolderPath() + "/" + pathInsideGameFolder + "'
				// doesn't
				// exist!");
				AppFolder.writeTextFile(path, "No Content Yet!");
				return;
			}
		}
		if (content != null) {
			load(content);
		} else {
			stringConfigs.clear();
			intConfigs.clear();
			longConfigs.clear();
			boolConfigs.clear();
			floatConfigs.clear();
			doubleConfigs.clear();
			configConfigs.clear();
		}
	}

	public void reload() {
		load(false);
	}

	public void load(String content) {
		stringConfigs.clear();
		intConfigs.clear();
		longConfigs.clear();
		boolConfigs.clear();
		floatConfigs.clear();
		doubleConfigs.clear();
		configConfigs.clear();
		String[] configs = content.split("\n");
		for (int i = 0; i < configs.length; i++) {
			// System.out.println(configs[i]);
			if (!configs[i].isEmpty()) {
				String trimmed = configs[i].trim();
				if (trimmed.endsWith("{")) {
					i++;
					switch (trimmed.substring(0, trimmed.length() - 1)) {
					case "Strings":
						for (; i < configs.length && !configs[i].startsWith("}"); i++) {
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							stringConfigs.put(cfgs[0], cfgs[1]);
						}
						break;
					case "Integers":
						for (; i < configs.length && !configs[i].startsWith("}"); i++) {
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							intConfigs.put(cfgs[0], Integer.parseInt(cfgs[1]));
							// System.out.println(cfgs[0] + " -- " + cfgs[1]);
						}
						break;
					case "Longs":
						for (; i < configs.length && !configs[i].startsWith("}"); i++) {
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							longConfigs.put(cfgs[0], Long.parseLong(cfgs[1]));
						}
						break;
					case "Floats":
						for (; i < configs.length && !configs[i].startsWith("}"); i++) {
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							floatConfigs.put(cfgs[0], Float.parseFloat(cfgs[1]));
						}
						break;
					case "Doubles":
						for (; i < configs.length && !configs[i].startsWith("}"); i++) {
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							doubleConfigs.put(cfgs[0], Double.parseDouble(cfgs[1]));
						}
						break;
					case "Booleans":
						for (; i < configs.length && !configs[i].startsWith("}"); i++) {
							String[] cfgs = configs[i].split("=");
							cfgs[0] = cfgs[0].trim();
							cfgs[1] = cfgs[1].trim();
							boolConfigs.put(cfgs[0], Boolean.parseBoolean(cfgs[1]));
						}
						break;
					case "Config":
						int klammern = 1;
						String name = configs[i++].trim();
						// System.out.println(name);
						StringBuilder c = new StringBuilder();
						for (; i < configs.length && klammern != 0; i++) {
							String t = configs[i].trim();
							// System.out.println(t);
							if (!t.isEmpty()) {
								if (t.endsWith("}")) {
									klammern--;
								} else if (t.endsWith("{")) {
									klammern++;
								}
								if (klammern > 0) {
									c.append(t);
									c.append('\n');
								}
							}
						}
						i--;
						Config cfg = new Config(null, c.toString());
						configConfigs.put(name, cfg);
					}
				}
			}
		}
		entrys = stringConfigs.size() + intConfigs.size() + longConfigs.size() + floatConfigs.size()
				+ doubleConfigs.size() + boolConfigs.size() + configConfigs.size();
	}

	public void save() {
		if (path != null && !relativePath)
			AppFolder.writeTextFile(path, getConfigString());
	}

	public String getConfigString() {
		StringBuilder context = new StringBuilder();
		if (stringConfigs.size() > 0) {
			context.append("Strings{\n");
			for (String key : stringConfigs.keySet()) {
				context.append(key);
				context.append(" = ");
				context.append(stringConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if (intConfigs.size() > 0) {
			context.append("Integers{\n");
			for (String key : intConfigs.keySet()) {
				context.append(key);
				context.append(" = ");
				context.append(intConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if (longConfigs.size() > 0) {
			context.append("Longs{\n");
			for (String key : longConfigs.keySet()) {
				context.append(key);
				context.append(" = ");
				context.append(longConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if (floatConfigs.size() > 0) {
			context.append("Floats{\n");
			for (String key : floatConfigs.keySet()) {
				context.append(key);
				context.append(" = ");
				context.append(floatConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if (doubleConfigs.size() > 0) {
			context.append("Doubles{\n");
			for (String key : doubleConfigs.keySet()) {
				context.append(key);
				context.append(" = ");
				context.append(doubleConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if (boolConfigs.size() > 0) {
			context.append("Booleans{\n");
			for (String key : boolConfigs.keySet()) {
				context.append(key);
				context.append(" = ");
				context.append(boolConfigs.get(key));
				context.append('\n');
			}
			context.append("}\n");
		}
		if (configConfigs.size() > 0) {
			for (String key : configConfigs.keySet()) {
				String s = configConfigs.get(key).getConfigString();
				if (s.length() > 0) {
					context.append("Config{\n");
					context.append(key);
					context.append('\n');
					context.append(s);
					context.append("}\n");
				}
			}
		}
		return context.toString();
	}

	public int entryCount() {
		return entrys;
	}

	public boolean isKey(String name) {
		return stringConfigs.keySet().contains(name) || intConfigs.keySet().contains(name)
				|| longConfigs.keySet().contains(name) || floatConfigs.keySet().contains(name)
				|| doubleConfigs.keySet().contains(name) || boolConfigs.keySet().contains(name)
				|| configConfigs.containsKey(name);
	}

	public Set<String> intKeySet() {
		return intConfigs.keySet();
	}

	public Map<String, Integer> integerMap() {
		return intConfigs;
	}

	public Set<String> boolKeySet() {
		return boolConfigs.keySet();
	}

	public Map<String, Boolean> boolMap() {
		return boolConfigs;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Config) {
			Config c = (Config) o;
			return c.entryCount() == this.entryCount()
					&& ((c.path == null && this.path == null) || c.path.equals(this.path))
					&& c.intConfigs.equals(this.intConfigs) && c.longConfigs.equals(this.longConfigs)
					&& c.floatConfigs.equals(this.floatConfigs) && c.doubleConfigs.equals(this.doubleConfigs)
					&& c.boolConfigs.equals(boolConfigs) && c.stringConfigs.equals(this.stringConfigs)
					&& c.configConfigs.equals(configConfigs);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Config of the file " + path + " with the content\n" + getConfigString();
	}

	public Set<String> configConfigNameSet() {
		return configConfigs.keySet();
	}

	public Config getConfigConfig(String next) {
		return configConfigs.get(next);
	}

	public void addConfigConfig(String name, Config c) {
		configConfigs.put(name, c);
	}

	public void removeConfigConfig(String name) {
		configConfigs.remove(name);
	}

}
