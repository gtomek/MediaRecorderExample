package uk.co.tomek.mediarecorderparameters;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
	private static final int COUNTER_UPDATE_MSG_ID = 123;
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

	private ExecutorService mThreadPool;

	private Handler mHandler;

	public boolean mIsPlaying;
	

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

		mThreadPool = Executors.newCachedThreadPool();
		
		initialiseHandler();

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
	
	/**
	 * Initialises Handler and a way to handle a message.
	 */
	private void initialiseHandler() {
		mHandler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				Log.i(TAG, String.format("Received msg:%s", msg));
				if (msg.what == COUNTER_UPDATE_MSG_ID) {
					int position = msg.arg1 / 1000;
					if (position != 0) {
						mCounterTv.setText(Integer.toString(position));
					} 
					return true;
				} else {
					return false;
				}
			}
		});
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
			mIsPlaying = false;
			mMediaManager.stopPlayback();

		}

	}

	public class RecordClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			keepScreenOn();
			mThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					String outputFileName = getOutputFileName();
					if (outputFileName != null && mMediaManager != null) {
						mMediaManager.recordGreeting(outputFileName);
					}
					
				}
			});

		}

	}

	public class PlayClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mIsPlaying = true;
			keepScreenOn();
			mThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					String outputFileName = getOutputFileName();
					if (outputFileName != null && mMediaManager != null) {
						mThreadPool.execute(new CounterUpdater());
						mMediaManager.playGreeting(outputFileName, true);
					}
				}
			});
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

	
	/**
	 * Updates the duration counter.
	 * 
	 */
	public class CounterUpdater extends Thread {
		
		private long UPDATE_PERIOD = 1000;

		@Override
		public void run() {
			try {
				while (mIsPlaying) {
					int currentPlaybackPosition = mMediaManager.getCurrentPlaybackPosition();
					Log.d(TAG, String.format("Current position:%d", currentPlaybackPosition));
					Message msg = Message.obtain(mHandler, COUNTER_UPDATE_MSG_ID, currentPlaybackPosition, 0);
					mHandler.sendMessage(msg);
					Thread.sleep(UPDATE_PERIOD);
				}
			} catch (InterruptedException e) {
				Log.w(TAG, "Sleeping Thread has ben interrupted");
				e.printStackTrace();
				// propagate the interrupt state
				Thread.currentThread().interrupt();
			}
			mHandler.sendEmptyMessage(0);
		}
	}
}
