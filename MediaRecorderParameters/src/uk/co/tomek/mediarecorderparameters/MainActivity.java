package uk.co.tomek.mediarecorderparameters;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";

	public class PlayerErrorListener implements android.media.MediaPlayer.OnErrorListener {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			Log.d(TAG, "PlayerErrorListener error");
			return false;
		}

	}

	public class RecorderErrorListener implements OnErrorListener {

		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Log.d(TAG, "RecorderErrorListener error");
		}

	}

	private MediaRecorder mMediaRecorder;
	private MediaPlayer mMediaPlayer;
	private static final String mOutputFileString = "/sdcard/tempFile.arm";  
	private File mOutputFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    mOutputFile = new File(mOutputFileString);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public void recordGreeting(View view) {

		// initialise MediaRecorder
		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.setAudioEncodingBitRate(12200);
			mMediaRecorder.setOnErrorListener(new RecorderErrorListener());
		} else {
			mMediaRecorder.stop();
			mMediaRecorder.reset();
		}
		
		mMediaRecorder.setOutputFile(mOutputFileString);
		mMediaRecorder.setMaxDuration(300000);
		try {
			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mMediaRecorder.release();
			mMediaRecorder = null;
		} catch (IOException e) {
			e.printStackTrace();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	public void playGreeting(View view) {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnErrorListener(new PlayerErrorListener());
		} else {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
		}
		
		try {
			mMediaPlayer.setDataSource(mOutputFileString);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

	}

	public void stopRecording(View view) {
		if (mMediaRecorder != null) {
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	public void stopPlayback(View view) {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

}
