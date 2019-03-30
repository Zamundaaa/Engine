package audio;

import java.util.ArrayDeque;
import java.util.ArrayList;

import org.joml.Vector3f;

public class SourcesManager {

	// TODO remove loading of audio files. Do that on client only. Constants
	// shall be standard here... then convertion!
	// this also should be an instance so you can define your own SourcesManager...

	/* package */static ArrayList<Source> sources = new ArrayList<>();
	private static ArrayDeque<Source> queue = new ArrayDeque<>();
	private static ArrayDeque<Source> nis = new ArrayDeque<>();

	public static int missle3;
	public static int thundersound;
	public static int boom;
	public static int blockbreak;
	public static int glassbreak;
	public static int BLASTERSOUND;
	public static int waterDrop;
	public static int fire;

	public static void loadDefaultSounds() {
		missle3 = AudioMaster.loadJarSound("missile 3.ogg");
		thundersound = AudioMaster.loadJarSound("thunder3.ogg");
		boom = AudioMaster.loadJarSound("bomb-03.ogg");
//		blockbreak = AudioMaster.loadJarSound("blockStuff/dropsound.ogg");
//		glassbreak = AudioMaster.loadJarSound("breaking_glass.ogg");
//		waterDrop = AudioMaster.loadJarSound("blockStuff/water.ogg");
//		BLASTERSOUND = AudioMaster.loadJarSound("Laser_Blaster-SoundBible.ogg");
		fire = AudioMaster.loadJarSound("fire.ogg");
	}

	public static int playingSources() {// too many. Can be 200+
		return sources.size();
	}

	public static int bufferedSources() {
		return nis.size();
	}

	public static Source getSource() {
		if (nis.isEmpty()) {
			return new Source();
		} else {
			synchronized (nis) {
				if (nis.isEmpty())
					return nis.pop();
				else
					return new Source();
			}
		}
	}

	/**
	 * has to be called from the main thread!
	 */
	public static void updateClient() {
		for (int i = 0; i < sources.size(); i++) {
			if (!sources.get(i).isPlaying()) {
				// sources.get(i).stop();
				if (nis.size() < 31)
					nis.add(sources.remove(i));
				else
					sources.remove(i).delete();
				i--;
			}
		}
		if (nis.size() > 30)
			synchronized (nis) {
				while (nis.size() > 30) {
					Source s = nis.pop();
					s.delete();
				}
			}
		while (!queue.isEmpty() && sources.size() < 300) {
			sources.add(queue.pop());
		}
		// not optimal!
		if (queue.size() > 100)
			queue.clear();
	}

	public static Source playSource(int soundbuffer, float volume, Vector3f position) {
		Source s = getSource();
		s.setPosition(position);
		s.setVolume(volume);
		s.play(soundbuffer);
		queue.add(s);
		return s;
	}

	public static void play(int soundbuffer, float volume, Vector3f position) {
		Source s = getSource();
		s.setPosition(position);
		s.setVolume(volume);
		s.play(soundbuffer);
		queue.add(s);
	}

	public static Source play(int soundbuffer, float gain, float x, float y, float z) {
		Source s = getSource();
		s.setPosition(x, y, z);
		s.setVolume(gain);
		s.play(soundbuffer);
		queue.add(s);
		return s;
	}

	public static void play(int soundbuffer, float volume, Vector3f position, Vector3f speed) {
		Source s = getSource();
		s.setPosition(position);
		s.setVolume(volume);
		s.setVelocity(speed);
		s.play(soundbuffer);
		queue.add(s);
	}

	public static void addSource(Source s) {
		if (!sources.contains(s)) {
			if (!queue.contains(s))
				queue.add(s);
		}
	}

	public static void removeSource(Source s) {
		sources.remove(s);
		queue.remove(s);
	}

	public static void cleanUp() {
		for (int i = 0; i < sources.size(); i++) {
			sources.get(i).delete();
		}
		synchronized (nis) {
			while (!nis.isEmpty()) {
				nis.pop().delete();
			}
		}
		synchronized (queue) {
			while (!queue.isEmpty()) {
				queue.pop().delete();
			}
		}
	}

}
