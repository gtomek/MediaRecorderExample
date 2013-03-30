package uk.co.tomek.mediarecorderparameters;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";

	private static final String mOutputFileName = "mediaFile";
	private static final String mPathName = "MediaRecorder";

	private Button mButtonPlay;
	private Button mButtonRecord;
	private Button mButtonStopPlaying;
	private Button mButtonStopeRecording;

	private MediaManager mMediaManager;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// play button
		mButtonPlay = (Button) findViewById(R.id.button_play);
		mButtonPlay.setOnClickListener(new PlayClickListener());
		
		mButtonRecord = (Button)findViewById(R.id.button_record);
		mButtonRecord.setOnClickListener(new RecordClickListener());
		
		mButtonStopPlaying = (Button)findViewById(R.id.button_stop_playback);
		mButtonStopPlaying.setOnClickListener(new StopPlayingListener());
		
		mButtonStopeRecording = (Button)findViewById(R.id.button_stop_recording);
		mButtonStopeRecording.setOnClickListener(new StopRecordingListener());
		
		// create media file
		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
				 "/" + mPathName);
		path.mkdirs();
		File audioFile = null;
		try {
			audioFile = File.createTempFile(mOutputFileName, ".arm", path);
		} catch (IOException e) {
			Log.d(TAG, "Unable to create media file!");
			e.printStackTrace();
			finish();
		}
		
		mMediaManager = MediaMangerImpl.newInstance(audioFile.getAbsolutePath());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public class StopRecordingListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mMediaManager.stopRecording();
		}

	}

	public class StopPlayingListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mMediaManager.stopPlayback();

		}

	}

	public class RecordClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mMediaManager.recordGreeting();

		}

	}

	public class PlayClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mMediaManager.playGreeting(true);
		}

	}

}
