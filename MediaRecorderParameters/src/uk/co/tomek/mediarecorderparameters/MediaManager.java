package uk.co.tomek.mediarecorderparameters;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

/**
 * Interface containing media management methods.
 */
public interface MediaManager {

	/**
	 * Records a file.
	 */
	void recordGreeting();
	
	/**
	 * Plays saved greeting.
	 * 
	 * @param mIsNormalGreetingSelected
	 *            flags saying if normal greeting is selected
	 */
	void playGreeting(boolean isRestartRequired);

	/**
	 * Stops recording process.
	 */
	void stopRecording();

	/**
	 * Stops greeting playback process.
	 */
	void stopPlayback();
	
	/**
	 * Pauses greeting playback.
	 */
	void pausePlayback();
	
	/**
	 * Gets playback duration.
	 * @return ms of the file to be played. 
	 */
	int getPlaybackDuration();
	
	/**
	 * Current position of greeting being played. 
	 * @return the current position in milliseconds 
	 */
	int getCurrentPlaybackPosition();
	
	/**
	 * Sets current playback position.
	 * @param progress
	 */
	void setPlayPosition(int progress);
	
	/**
	 * Gets current media player object.
	 * @return MediaRecorder
	 */
	MediaPlayer getMediaPlayer();
	
	/**
	 * Gets current MediaRecorder object.
	 * @return MediaRecorder
	 */
	MediaRecorder getMediaRecorder();
	
}
