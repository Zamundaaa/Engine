package audio.music;

import java.util.ArrayList;
import java.util.Iterator;

import audio.AudioMaster;
import audio.Source;
import tools.*;

public class MusicManager {

	// protected static final int LOADING_IN_BACKGROUND = -126;

	private static final ArrayList<MusicTrack> list = new ArrayList<>();
	private static final ArrayList<MusicTrack> active = new ArrayList<>(), inactive = new ArrayList<>();
	public static final Config cfg = new Config("config/music.cfg");

	public static void init() {
		if (cfg.entryCount() == 0) {
			AppFolder.copyFromJarToFolder("audio/music/defaultConf", "music.conf");
			cfg.reload();
		}
		Iterator<String> it = cfg.configConfigNameSet().iterator();
		while (it.hasNext()) {
			Config c = cfg.getConfigConfig(it.next());
			// build in the possibility to "delete" configurations into a trash
			// bin (?)
			boolean a = c.getBoolConfig("active", true);
			MusicTrack m = new MusicTrack(a, c.getConfig("name", "error"), c.getConfig("path", "error"),
					c.getBoolConfig("relativePath", false), c.getIntConfig("downloadStatus", 0), c);
			if (a && m.fileExists()) {
				active.add(m);
			} else {
				m.active = false;
				inactive.add(m);
			}
			list.add(m);
		}

		loadPossibleMusicFiles();
	}

	public static final int MAX = 1;

	private static Source backplay = new Source();

	private static MusicTrack current = active.size() > 0 ? active.get(Meth.randomInt(0, active.size() - 1)) : null;
	private static boolean justpaused = false;
	private static float gain = cfg.getFloatConfig("gain", 0.5f);

	public static void play() {
		if (AudioMaster.soundEnabled && AudioMaster.musicEnabled) {
			if (!backplay.isPlaying()) {
				if (justpaused) {
					backplay.continuePlaying();
					justpaused = false;
				} else {
					next();
				}
			}
			backplay.setVolume(gain);

			backplay.setDoppler(false);
		}
	}

	public static void stop() {
		backplay.pause();
		justpaused = true;
	}

	private static GameThread thread;
	public static boolean stop = false;
	private static boolean flagToPlay = false, nextFlag = false, loadingSomeSound = false;

	public static void update() {
		if (thread == null || !thread.isAlive()) {
			thread = new GameThread("music player", 20) {
				@Override
				public void loop() {
					if (!stop) {
						stopThread();
						return;
					}
					if (flagToPlay) {
						flagToPlay = false;
						play(current);
					} else if (nextFlag) {
						nextFlag = false;
						next();
					} else {
						play();
					}
				}
			};
			thread.decoupleFromGame();
			thread.start();
		}
	}

	public static void setGain(float value) {
		gain = value;
		cfg.setConfig("gain", gain);
		cfg.save();
	}

	public static float gain() {
		return gain;
	}

	public static ArrayList<MusicTrack> getTracks() {
		return list;
	}

	public static void save() {
		cfg.save();
	}

	public static void play(MusicTrack musicTrack) {
		if (!active.contains(musicTrack))
			active.add(musicTrack);
		inactive.remove(musicTrack);
		musicTrack.active = true;
		if (Thread.currentThread() == thread) {
			loadingSomeSound = true;
			int i = musicTrack.giveLoadedIDOrLoad();
			loadingSomeSound = false;
			backplay.play(i);
			if (i == -1)
				AppFolder.log.println("format of " + musicTrack.name + " not fitting!");
			else
				AppFolder.log.println("now playing: " + musicTrack.name);
		} else {
			flagToPlay = true;
		}
		current = musicTrack;
	}

	public static void add(MusicTrack m) {
		if (!active.contains(m))
			active.add(m);
		inactive.remove(m);
		m.active = true;
		m.cfg.setConfig("active", m.active);
		if (active.size() == 1 && AudioMaster.musicEnabled && current == null) {
			current = m;
			flagToPlay = true;
		}
	}

	public static void remove(MusicTrack m) {
		if (active.remove(m)) {
			inactive.add(m);
		}
		if (m == current) {
			current = null;
			backplay.stop();
			nextFlag = true;
		}
		m.active = false;
		m.cfg.setConfig("active", m.active);
		if (active.size() == 0) {
			current = null;
			backplay.stop();
		}
	}

	public static void next() {
		MusicTrack c = current;
		if (current == null && active.size() == 1) {
			current = active.get(0);
		}
		while (current == c && active.size() > 1) {
			current = active.get(Meth.randomInt(0, active.size() - 1));
		}
		if (!AudioMaster.musicEnabled)
			return;

		if (current == null) {
			backplay.stop();
			return;
		}
		int b = current.giveLoadedIDOrLoad();
		if (b > 0) {
			backplay.play(b);
		} else {
			backplay.stop();
			current = null;
		}
		if (current == null && c != null)
			AppFolder.log.println("stopped music");
		else if (current != null)
			AppFolder.log.println("Now playing: " + current.name);
	}

	public static ArrayList<MusicTrack> enabledTracks() {
		return active;
	}

	public static ArrayList<MusicTrack> disabledTracks() {
		return inactive;
	}

	public static void checkForOnlineTracks() {
		// download config and check if items are already configured or not
	}

	private static void loadPossibleMusicFiles() {
		String[] files = AppFolder.getFiles("music/");
		ArrayList<Config> cs = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			String[] f = files[i].split("/");
			if (!f[f.length - 1].startsWith(".")) {
				// System.out.println(f[f.length-1]);
				Config c = new Config(null, "");
				String name;
				String[] names = f[f.length - 1].split("\\.");
				StringBuilder n = new StringBuilder(names[0]);
				for (int i2 = 1; i2 < names.length - 1; i2++) {
					n.append(names[i2]);
				}
				name = n.toString();
				MusicTrack m = new MusicTrack(false, name, AppFolder.folder + "music/" + files[i], false, 0, c);
				if (!list.contains(m)) {
					c.setConfig("active", false);
					c.setConfig("name", m.name);
					c.setConfig("path", m.path);
					c.setConfig("relativePath", false);
					c.setConfig("downloadStatus", 0);
					cfg.addConfigConfig(m.name, c);
					list.add(m);
					inactive.add(m);
				}
				cs.add(c);
			}
		}
		// Iterator<String> i = cfg.configConfigNameSet().iterator();
		// ArrayList<String> ss = new ArrayList<>();
		// while(i.hasNext()){
		// ss.add(i.next());
		// }
		// for(int i2 = 0; i2 < ss.size(); i2++){
		// String s = ss.get(i2);
		// Config c = cfg.getConfigConfig(s);
		// if(!c.relativePath && !cs.contains(c)){
		// cfg.removeConfigConfig(s);
		// }
		// }
	}

	public static boolean isPlaying() {
		return loadingSomeSound || backplay.isPlaying();
	}

}
