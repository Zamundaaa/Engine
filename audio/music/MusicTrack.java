package audio.music;

import java.io.File;

import audio.AudioMaster;
import tools.Config;

public class MusicTrack {

	public static final int notDownloadable = 0, downloadable = 1, downloaded = 2;

	public boolean active;
	public String name;
	public String path;
	public boolean relativePath;
	public int downloadStatus;
	public int loadedID;
	public Config cfg;

	public MusicTrack(boolean active, String name, String path, boolean relativePath, int downloadStatus, Config cfg) {
		this.active = active;
		this.name = name;
		this.path = path;
		this.relativePath = relativePath;
		this.downloadStatus = downloadStatus;
		this.cfg = cfg;
	}

	public void play() {
		MusicManager.play(this);
	}

	public void addToList() {
		MusicManager.add(this);
	}

	public void removeFromList() {
		MusicManager.remove(this);
	}

	public void load() {
		if (relativePath)
			loadedID = AudioMaster.loadJarSound(path);
		else
			loadedID = AudioMaster.loadSoundFromOutside(path);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MusicTrack) {
			MusicTrack m = (MusicTrack) o;
			return m.name.equals(name);
		} else {
			return false;
		}
	}

	public boolean fileExists() {
		if (relativePath) {
			return MusicTrack.class.getClassLoader().getResourceAsStream("res/audioFiles/"+path) != null;
		} else {
			return new File(path).exists();
		}
	}

	public int giveLoadedIDOrLoad() {
		if(loadedID == 0){// || loadedID == MusicManager.LOADING_IN_BACKGROUND
			if(!fileExists()){
				loadedID = -1;
				return -1;
			}else{
				load();
				return loadedID;
			}
		}else{
			return loadedID;
		}
	}

}
