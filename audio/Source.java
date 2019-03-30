package audio;

import static audio.AudioMaster.sounddistmult;
import static audio.AudioMaster.soundvelmult;
import static org.lwjgl.openal.AL10.*;

import org.joml.Vector3f;

public class Source {

	private int sourceId;

	public Source() {

		sourceId = alGenSources();
		alSourcef(sourceId, AL_ROLLOFF_FACTOR, 0.7f);
		alSourcef(sourceId, AL_REFERENCE_DISTANCE, 6);
		alSourcef(sourceId, AL_MAX_DISTANCE, 50);

		alSourcef(sourceId, AL_GAIN, 0.5f);
		alSourcef(sourceId, AL_PITCH, 1);
		alSource3f(sourceId, AL_POSITION, 0, 0, 0);

		SourceManager.addSource(this);

	}

	public void pause() {
		// if (!Intraface.isServer)
		alSourcePause(sourceId);
	}

	public void continuePlaying() {
		// if (!Intraface.isServer)
		alSourcePlay(sourceId);
	}

	public void stop() {
		// if (!Intraface.isServer)
		alSourceStop(sourceId);
	}

	private Vector3f pos = new Vector3f();

	public void setPosition(Vector3f pos) {
		// if (!Intraface.isServer) {
		alSource3f(sourceId, AL_POSITION, pos.x * sounddistmult, pos.y * sounddistmult, pos.z * sounddistmult);
		this.pos.set(pos);
		// }
	}

	public void setPosition(float x, float y, float z) {
		// if (!Intraface.isServer) {
		alSource3f(sourceId, AL_POSITION, x * sounddistmult, y * sounddistmult, z * sounddistmult);
		this.pos.set(x, y, z);
		// }
	}

	public void setPitch(float pitch) {
		// if (!Intraface.isServer)
		alSourcef(sourceId, AL_PITCH, pitch);
	}

	public void setVolume(float gain) {
		// if (!Intraface.isServer)
		alSourcef(sourceId, AL_GAIN, gain);
	}

	public void setVelocity(Vector3f velo) {
		// if (!Intraface.isServer)
		alSource3f(sourceId, AL_VELOCITY, velo.x * soundvelmult, velo.y * soundvelmult, velo.z * soundvelmult);
	}

	public void setDoppler(boolean b) {
		alSourcef(sourceId, AL_DOPPLER_FACTOR, b ? 1 : 0);
	}

	public void setLooping(boolean loop) {
		// if (!Intraface.isServer) {
		alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
		playinStarted = 0;
		// }
	}

	public boolean isPlaying() {
		// if (!Intraface.isServer)
		return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
		// else
		// return true;
	}

	public void play(int buffer) {
		// if (Intraface.isServer)
		// return;
		stop();
		if (buffer <= 0)
			return;
		alSourcei(sourceId, AL_BUFFER, buffer);
		alSourcePlay(sourceId);

		int sizeInBytes = alGetBufferi(buffer, AL_SIZE);
		int channels = alGetBufferi(buffer, AL_CHANNELS);
		int bits = alGetBufferi(buffer, AL_BITS);
		long lengthInSamples = sizeInBytes * 8 / (channels * bits);
		int frequency = alGetBufferi(buffer, AL_FREQUENCY);
		playTime = (float) lengthInSamples / (float) frequency;

		playin = buffer;
		playinStarted = System.currentTimeMillis();
	}

	public void delete() {
		// if (Intraface.isServer)
		// return;
		stop();
		SourceManager.removeSource(this);
		alDeleteSources(sourceId);
	}

	private float playTime;
	private long playinStarted;
	private int playin;

	public int buffer() {
		return playin;
	}

	public Vector3f pos() {
		return pos;
	}

	public float remainingPlayTime() {
		return playTime - ((System.currentTimeMillis() - playinStarted) / 1000);
	}

	public float volume() {
		return alGetSourcef(sourceId, AL_GAIN);
	}

	/**
	 * @param relative if the source and its properties should be interpreted as
	 *                 relative to the listener (-> music backplay) or if it should
	 *                 be absolute (relative to (0|0|0), -> sounds in the world)
	 */
	public void setRelative(boolean relative) {
		alSourcei(sourceId, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
		alSourcei(sourceId, AL_SOURCE_ABSOLUTE, relative ? AL_FALSE : AL_TRUE);
	}

}
