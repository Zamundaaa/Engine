package audio;

import java.io.*;
import java.nio.ByteBuffer;

import javax.sound.sampled.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import tools.AppFolder;

public class WaveData {

	final int format;
	final int samplerate;
	final int totalBytes;
	final int bytesPerFrame;
	final ByteBuffer data;

	private final AudioInputStream audioStream;
	private final byte[] dataArray;

	private WaveData(AudioInputStream stream) {
		this.audioStream = stream;
		AudioFormat audioFormat = stream.getFormat();
		format = getOpenAlFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
		this.samplerate = (int) audioFormat.getSampleRate();
		this.bytesPerFrame = audioFormat.getFrameSize();
		this.totalBytes = (int) (stream.getFrameLength() * bytesPerFrame);
		this.data = BufferUtils.createByteBuffer(totalBytes);
		this.dataArray = new byte[totalBytes];
		loadData();
	}

	protected void dispose() {
		try {
			audioStream.close();
			data.clear();
		} catch (IOException e) {
			e.printStackTrace(AppFolder.log);
		}
	}

	private ByteBuffer loadData() {
		try {
			int bytesRead = audioStream.read(dataArray, 0, totalBytes);
			data.clear();
			data.put(dataArray, 0, bytesRead);
			data.flip();
		} catch (IOException e) {
			e.printStackTrace(AppFolder.log);
			AppFolder.log.println("Couldn't read bytes from audio stream!");
		}
		return data;
	}

	public static WaveData create(String file) {
		InputStream stream = Class.class.getResourceAsStream("/" + file);
		if (stream == null) {
			AppFolder.log.println("Couldn't find file: " + file);
			return null;
		}
		InputStream bufferedInput = new BufferedInputStream(stream);
		AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(bufferedInput);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace(AppFolder.log);
		} catch (IOException e) {
			e.printStackTrace(AppFolder.log);
		}
		WaveData wavStream = new WaveData(audioStream);
		return wavStream;
	}

	private static int getOpenAlFormat(int channels, int bitsPerSample) {
		if (channels == 1) {
			return bitsPerSample == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
		} else {
			return bitsPerSample == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
		}
	}

}