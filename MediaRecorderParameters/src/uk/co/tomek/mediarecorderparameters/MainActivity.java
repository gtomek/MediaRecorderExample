package uk.co.tomek.mediarecorderparameters;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";

	private static final String mOutputFileName = "mediaFile.amr";
	private static final String mPathName = "MediaRecorder";

	private Button mButtonPlay;
	private Button mButtonRecord;
	private Button mButtonStopPlaying;
	private Button mButtonStopeRecording;

	private MediaManager mMediaManager;

	private boolean DEVELOPER_MODE = true;

	// counter that will be displayed on the screen
	private TextView mCounterTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 if (DEVELOPER_MODE) {
	         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	                 .detectDiskReads()
	                 .detectDiskWrites()
	                 .detectNetwork()   // or .detectAll() for all detectable problems
	                 .penaltyLog()
	                 .build());
	         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	                 .detectLeakedSqlLiteObjects()
	                 .detectLeakedClosableObjects()
	                 .penaltyLog()
	                 .penaltyDeath()
	                 .build());
	     }
		
		
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
		
		mMediaManager = MediaMangerImpl.newInstance();
		
		mCounterTv = (TextView) findViewById(R.id.seconds_counter_tv);
	
		
	}
	
	private void keepScreenOn() {
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
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
			keepScreenOn();
			new RecordingTask().execute();

		}

	}

	public class PlayClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			keepScreenOn();
			new PlayingTask().execute();
		}
	}

	/**
	 * Implementation of {@link AsyncTask} for Recording.
	 * 
	 * @author Tomek Giszczak
	 * 
	 */
	public class RecordingTask extends AsyncTask<Void, Integer, Void> {
		
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "onPreExecute for recording");
			// reset counter
			mCounterTv.setText("0");
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String outputFileName = getOutputFileName();
			if (outputFileName != null && mMediaManager != null) {
				mMediaManager.recordGreeting(outputFileName);
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// Update counter
			mCounterTv.setText(values[0]);
			super.onProgressUpdate(values);
		}
		
	}
	
	
	/**
	 * Implementation of {@link AsyncTask} for Playing.
	 * 
	 * @author Tomek
	 * 
	 */
	public class PlayingTask extends AsyncTask<Void, Integer, Void> {
		
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "onPreExecute for playing");
			// reset counter
			mCounterTv.setText("0");
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String outputFileName = getOutputFileName();
			if (outputFileName != null && mMediaManager != null) {
				mMediaManager.playGreeting(outputFileName, true);
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// Update counter
			mCounterTv.setText(values[0]);
			super.onProgressUpdate(values);
		}

	}
	
	/**
	 * Creates and gets output file name
	 * @return
	 */
	private String getOutputFileName() {
		
		// create media file
		File filePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
				 File.separator + mPathName);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		
		File audioFile = new File(filePath.getAbsolutePath() + File.separator + mOutputFileName);
		try {
			if (!audioFile.exists()) {
				audioFile.createNewFile();
			}
		} catch (IOException e) {
			Log.d(TAG, "Unable to create media file!");
			e.printStackTrace();
			finish();
		}
		
		return audioFile.getAbsolutePath();
	}

}
