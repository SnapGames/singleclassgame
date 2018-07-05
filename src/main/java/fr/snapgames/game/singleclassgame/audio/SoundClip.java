/**
 * SnapGames
 * 
 * Project : SingleClassGame
 * 
 * <p>This project intends to propose a basement for **2D game development** with the `Java` language. 
 * 
 * @year 2018
 * @see http://snapgames.fr/
 */
package fr.snapgames.game.singleclassgame.audio;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Class to play and manage a sound clip from file.
 * 
 * @author Frédéric Delorme
 *
 */
public class SoundClip {

	/**
	 * Java Sound clip to be read.
	 */
	private Clip clip;
	/**
	 * Volume control.
	 */
	private FloatControl gainControl;
	/**
	 * Pan Control.
	 */
	private FloatControl panControl;

	/**
	 * Initialize the sound clip ready to play from the file at
	 * <code>path</code>.
	 * 
	 * @param path
	 *            Path to the sound clip to be read.
	 */
	public SoundClip(String path) {
		try {
			InputStream audioSrc = SoundClip.class.getResourceAsStream(path);
			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
			AudioFormat baseFormat = ais.getFormat();

			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
					baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			clip = AudioSystem.getClip();
			clip.open(dais);

			gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Start playing the clip.
	 */
	public void play() {
		if (clip == null) {
			return;
		} else {
			stop();
			clip.setFramePosition(0);
			while (!clip.isRunning()) {
				clip.start();
			}
		}

	}

	public void play(float pan, float volume) {
		if (clip == null) {
			return;
		} else {
			stop();
			clip.setFramePosition(0);
			while (!clip.isRunning()) {
				clip.start();
			}
		}
		setPan(pan);
		setVolume(volume);
	}

	/**
	 * @param pan
	 */
	public void setPan(float pan) {
		panControl.setValue(pan);
	}

	/**
	 * @param volume
	 */
	public void setVolume(float volume) {
		float min = gainControl.getMinimum() / 4;
		if (volume != 1) {
			gainControl.setValue(min * (1 - volume));
		}
	}

	/**
	 * Stop playing the clip.
	 */
	public void stop() {
		if (clip == null) {
			return;
		} else if (clip.isRunning()) {
			clip.stop();
		}
	}

	/**
	 * Loop the clip continuously
	 */
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		while (!clip.isRunning()) {
			clip.start();
		}
	}

	public void close() {
		stop();
		clip.drain();
		clip.close();
	}

	public boolean isPlaying() {
		return clip.isRunning();
	}

}
