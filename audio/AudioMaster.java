package audio;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.AL_LINEAR_DISTANCE_CLAMPED;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

import java.io.FileNotFoundException;
import java.nio.*;
import java.util.*;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.libc.LibCStdlib;

import tools.AppFolder;

public class AudioMaster {

	public static final float sounddistmult = 1f;
	public static final float soundvelmult = sounddistmult;
	public static final float defaultGain = 1f;

	private static float GAIN = AppFolder.soundPrefs.getFloatConfig("GAIN", defaultGain);

	public static boolean soundEnabled = GAIN > 0.01f;
	public static boolean musicEnabled = AppFolder.soundPrefs.getBoolConfig("music", true);
	public static boolean CREATED = false;

	private static List<Integer> buffers = new ArrayList<>();

	private static long context;
	private static long device;
	
	private static float[] orientationBuffer = new float[6];
	static{
		orientationBuffer[4] = 1;
	}

	public static void init() throws Exception {
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);
		contextAttribList.put(ALC_REFRESH);
		contextAttribList.put(60);
		contextAttribList.put(ALC_SYNC);
		contextAttribList.put(ALC_FALSE);

		// Don't worry about this for now; deals with effects count
		contextAttribList.put(ALC_MAX_AUXILIARY_SENDS);
		contextAttribList.put(2);

		contextAttribList.put(AL_DISTANCE_MODEL);
		contextAttribList.put(AL_LINEAR_DISTANCE_CLAMPED);

		contextAttribList.put(0);
		contextAttribList.flip();

		context = ALC10.alcCreateContext(device, contextAttribList);

		if (!ALC10.alcMakeContextCurrent(context)) {
			throw new Exception("Failed to make context current");
		}

		AL.createCapabilities(deviceCaps);
		CREATED = true;
		
		setGain(GAIN);

//		MusicManager.init();

		AppFolder.log.println("AudioMaster inited!");

	}

	public static void setGain(float gain) {
		GAIN = gain;
		if (GAIN > 0.01f) {
			alListenerf(AL_GAIN, gain * 0.99f);
			if (!soundEnabled) {
				enableSound();
			}
		} else {
			disableSound();
			alListenerf(AL_GAIN, 0);
			GAIN = 0;
		}
	}

	public static float gain() {
		return GAIN;
	}

	public static void enableSound() {
		for (int i = 0; i < SourceManager.sources.size(); i++) {
			SourceManager.sources.get(i).continuePlaying();
		}
		soundEnabled = true;
	}

	public static void disableSound() {
		for (int i = 0; i < SourceManager.sources.size(); i++) {
			SourceManager.sources.get(i).pause();
		}
		soundEnabled = false;
	}

	public static int loadSoundFromOutside(String file) {
		if (file.endsWith(".ogg")) {
			ByteBuffer b = AppFolder.readBytes(file);
			return loadFromBuffer(b);
		} else {
			System.out.println("file not fitting: " + file);
			return -1;
		}
	}

	public static int loadJarSound(String file) {
		file = "res/audioFiles/" + file;
		ByteBuffer b = AppFolder.readJarBytes(file);
		return loadFromBuffer(b);
	}

	public static void unloadSound(int soundbuffer) {
		AL10.alDeleteBuffers(soundbuffer);
	}

	public static void setListenerData(Vector3f pos, Vector3f velocity, Vector3f lookat) {
		AL10.alListener3f(AL10.AL_POSITION, pos.x * sounddistmult, pos.y * sounddistmult, pos.z * sounddistmult);
		AL10.alListener3f(AL10.AL_VELOCITY, velocity.x * soundvelmult, velocity.y * soundvelmult,
				velocity.z * soundvelmult);
		orientationBuffer[0] = lookat.x;
		orientationBuffer[1] = lookat.y;
		orientationBuffer[2] = lookat.z;
		AL10.alListenerfv(AL10.AL_ORIENTATION, orientationBuffer);
	}

	public static void cleanUp() {
		if (CREATED) {
			for (int buffer : buffers) {
				AL10.alDeleteBuffers(buffer);
			}
			ALC10.alcMakeContextCurrent(0);
			ALC10.alcCloseDevice(device);
			ALC.destroy();
			CREATED = false;
			AppFolder.soundPrefs.setConfig("GAIN", GAIN);
			AppFolder.soundPrefs.setConfig("music", musicEnabled);
//			MusicManager.save();
			AppFolder.log.println("AUDIO CLEANUP!");
		}
	}

	public static boolean enabled() {
		return CREATED;
	}

	public static int loadFromBuffer(ByteBuffer b) {

		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);

		IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer sampleRateBuffer = BufferUtils.createIntBuffer(1);

		ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_memory(b, channelsBuffer, sampleRateBuffer);
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();

		int format = -1;
		if (channels == 1) {
			format = AL_FORMAT_MONO16;
		} else if (channels == 2) {
			format = AL_FORMAT_STEREO16;
		}

		if (rawAudioBuffer == null) {
			new FileNotFoundException("failed to load audio file").printStackTrace(AppFolder.log);
			AppFolder.log.println("no plan what went wrong loading that audio!");
			return -1;
		}

		alBufferData(buffer, format, rawAudioBuffer, sampleRate);

		LibCStdlib.free(rawAudioBuffer);
		// LibCStdlib.free(channelsBuffer);
		// LibCStdlib.free(sampleRateBuffer);

		return buffer;
	}
	
	private static final Map<Thread, Boolean> doSounds = new HashMap<>();
	
	/**
	 * @return if this Thread may do sounds (right now)
	 */
	public static boolean doSounds(){
		if(!soundEnabled)
			return false;
		Boolean b = doSounds.get(Thread.currentThread());
		if(b == null)
			return true;
		else
			return b;
	}
	
	public static void doSounds(boolean b){
		doSounds.put(Thread.currentThread(), b);
	}
	
}
