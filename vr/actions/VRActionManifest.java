package vr.actions;

import static org.lwjgl.openvr.VRInput.VRInput_SetActionManifestPath;

import java.io.File;
import java.util.*;

import collectionsStuff.ArrayListInt;
import tools.AppFolder;
import tools.JarResources;

public class VRActionManifest {

	public static final int BOOLEAN = 0, POSE = 1, VIBRATION = 2, FLOAT = 3, VEC2 = 4, VEC3 = 5;

	public static final int SINGLE = 0, LEFTRIGHT = 1, HIDDEN = 2;

	protected final String filePath = "config/VRActionManifest.json";
	protected Map<String, String> defaultBindingsFileNames = new HashMap<>();
	protected boolean isFile;
	protected ArrayList<String> actionPaths = new ArrayList<>();
	protected ArrayListInt types = new ArrayListInt();
	protected Map<String, Map<String, String>> localization = new HashMap<>();

	protected Map<String, VRActionSet> actionSetMap = new HashMap<>();
	protected ArrayList<VRActionSet> actionSets = new ArrayList<>();

	protected ArrayList<String> actionSetNames = new ArrayList<>();
	protected ArrayListInt actionSetUsages = new ArrayListInt();
	protected boolean defaultActions = false;

	public VRActionManifest() {

	}

	/**
	 * put the file in (AppFolder/)config/. Default file name (if fileName is null)
	 * is "default_bindings_controllerType.json"
	 */
	public void setDefaultBindings(String controllerType, String fileName) {
		defaultBindingsFileNames.put(controllerType,
				fileName == null ? "default_bindings_" + controllerType + ".json" : fileName);
	}

	/**
	 * @param jarPath the in-jar path to a folder (/package) containing the bindings
	 *                for various controllers
	 */
	public void copyDefaultBindings(String jarPath) {
		for (String f : defaultBindingsFileNames.values()) {
			String content = JarResources.loadJarTextFile(jarPath + f);
			if (content != null) {
				AppFolder.writeTextFile("config/" + f, content);
			}
		}
	}

	/**
	 * @param path
	 * @param type one of BOOLEAN, POSE, VIBRATION, FLOAT, VEC2, VEC3
	 * @return if an action with the same path already exists and thus no action was
	 *         added, false. Otherwise, true
	 */
	public boolean addAction(String path, int type, String name_en) {
		if (!actionPaths.contains(path)) {
			actionPaths.add(path);
			types.add(type);
			Map<String, String> enList = localization.get("en");
			if (enList == null) {
				localization.put("en", enList = new HashMap<>());
			}
			if (!enList.containsKey(path))
				enList.put(path, name_en);
			return true;
		}
		return false;
	}

	/**
	 * @param name    for example "/actions/main". You would then name the actions
	 *                of this action set for example "/actions/main/in/whatever"
	 * @param usage   one of SINGLE, LEFTRIGHT, HIDDEN
	 * @param name_en
	 */
	public void addActionSet(String name, int usage, String name_en) {
		if (!actionSetNames.contains(name)) {
			actionSetNames.add(name);
			actionSetUsages.add(usage);
			Map<String, String> enList = localization.get("en");
			if (enList == null) {
				localization.put("en", enList = new HashMap<>());
			}
			if (!enList.containsKey(name))
				enList.put(name, name_en);
		}
	}

	/**
	 * some actions that are often used and the respective actionset
	 * "/actions/XEngine". Makes life easier
	 */
	public void addDefaultActions() {
		String x = "/actions/xengine/";
		addAction(x + "in/hand_pose_left", POSE, "left hand pose");
		addAction(x + "in/hand_pose_right", POSE, "right hand pose");
		addAction(x + "in/grab_right", BOOLEAN, "right grab");
		addAction(x + "in/grab_left", BOOLEAN, "left grab");

		addAction(x + "in/hand_action_left", BOOLEAN, "left hand action");
		addAction(x + "in/hand_action_right", BOOLEAN, "right hand action");
		addAction(x + "in/trigger_left", FLOAT, "left trigger");
		addAction(x + "in/trigger_right", FLOAT, "right trigger");

		addAction(x + "in/touchpad_left", VEC2, "left touchpad");
		addAction(x + "in/touchpad_right", VEC2, "right touchpad");

		addAction(x + "out/vibration_left", VIBRATION, "left vibration");
		addAction(x + "out/vibration_right", VIBRATION, "right vibration");

		addActionSet(x.substring(0, x.length() - 1), LEFTRIGHT, "XEngine general actions");

		defaultActions = true;
	}

	public void createFile() {
		createFile(true);
	}

	public void createFile(boolean overwrite) {
		isFile = true;
		if (!overwrite && new File(filePath).exists()) {
			return;
		}
		JsonFile file = new JsonFile();
		if (!defaultBindingsFileNames.isEmpty()) {
			file.addSegment('[', "default_bindings");
			for (String f : defaultBindingsFileNames.keySet()) {
				file.addSegment('{');
				file.addValue("controller_type", f);
				file.addValue("binding_url", defaultBindingsFileNames.get(f));
				file.closeCurrentSegment();
			}
			file.closeCurrentSegment();
		}
		file.addSegment('[', "actions");
		for (int i = 0; i < actionPaths.size(); i++) {
			file.addSegment('{');
			file.addValue("name", actionPaths.get(i));
			file.addValue("type", getActionTypeName(types.get(i)));
			file.closeCurrentSegment();
		}
		file.closeCurrentSegment();
		file.addSegment('[', "action_sets");
		for (int i = 0; i < actionSetNames.size(); i++) {
			file.addSegment('{');
			file.addValue("name", actionSetNames.get(i));
			file.addValue("usage", getUsageName(actionSetUsages.get(i)));
			file.closeCurrentSegment();
		}
		file.closeCurrentSegment();
		file.addSegment('[', "localization");
		for (String lang : localization.keySet()) {
			file.addSegment('{');
			Map<String, String> m = localization.get(lang);
			file.addValue("language_tag", lang);
			for (String k : m.keySet()) {
				file.addValue(k, m.get(k));
			}
			file.closeCurrentSegment();
		}
		AppFolder.writeTextFile(filePath, file.closeFile());
	}

	public void submitToOpenVR() {
		submitToOpenVR(true);
	}

	/**
	 * has to be called before the first VRHandler.updateDevices() call. After this,
	 * you can retrieve action sets from this VRActionManifest! NOTE: add some
	 * default bindings before
	 */
	public void submitToOpenVR(boolean createFileIfNotDoneSo) {
		if (createFileIfNotDoneSo && !isFile) {
			createFile();
		}
		VRInput_SetActionManifestPath(AppFolder.folder + filePath);
		for (int i = 0; i < actionSetNames.size(); i++) {
			VRActionSet as = new VRActionSet(actionSetNames.get(i));
			actionSets.add(as);
			actionSetMap.put(actionSetNames.get(i), as);
		}
	}

	public int nrOfDefaultBindings() {
		return defaultBindingsFileNames.size();
	}

	public boolean hasDefaultActions() {
		return defaultActions;
	}

	public static String getActionTypeName(int actionType) {
		switch (actionType) {
		case BOOLEAN:
			return "boolean";
		case POSE:
			return "pose";
		case FLOAT:
			return "vector1";
		case VEC2:
			return "vector2";
		case VEC3:
			return "vector3";
		case VIBRATION:
			return "vibration";
		default:
			AppFolder.log.println("YOU PUT A WRONG ID IN HERE! " + actionType);
			new IllegalArgumentException().printStackTrace(AppFolder.log);
			System.exit(-1);
		}
		return null;
	}

	public static String getUsageName(int usage) {
		switch (usage) {
		case SINGLE:
			return "single";
		case LEFTRIGHT:
			return "leftright";
		case HIDDEN:
			return "hidden";
		default:
			AppFolder.log.println("wrong usage int: " + usage);
		}
		return null;
	}

}
