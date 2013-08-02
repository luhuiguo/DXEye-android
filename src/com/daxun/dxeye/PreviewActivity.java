package com.daxun.dxeye;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

/**
 * Created by luhuiguo on 13-6-28.
 */
public class PreviewActivity extends Activity {

	private static final String TAG = PreviewActivity.class.getSimpleName();

	Channel channel;

	Monitor monitor;

	ImageButton btnUp;
	ImageButton btnDown;
	ImageButton btnLeft;
	ImageButton btnRight;
	ImageButton btnZoomin;
	ImageButton btnZoomout;
	ImageButton btnCapture;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setContentView(R.layout.activity_preview);

		monitor = (Monitor) findViewById(R.id.monitor);
		btnUp = (ImageButton) findViewById(R.id.btnUp);
		btnDown = (ImageButton) findViewById(R.id.btnDown);
		btnLeft = (ImageButton) findViewById(R.id.btnLeft);
		btnRight = (ImageButton) findViewById(R.id.btnRight);
		btnZoomin = (ImageButton) findViewById(R.id.btnZoomin);
		btnZoomout = (ImageButton) findViewById(R.id.btnZoomout);
		btnCapture = (ImageButton) findViewById(R.id.btnCapture);

		channel = (Channel) getIntent().getSerializableExtra(
				Constants.CHANNEL_KEY);

		setTitle(channel.getName());

		monitor.setChannel(channel);
		monitor.setEnableOSD(true);

		btnCapture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (monitor.getBitmap() != null) {
					ContentResolver cr = getContentResolver();
					MediaStore.Images.Media.insertImage(cr,
							monitor.getBitmap(), channel.getName(), "");
				}

			}
		});
		PtzButtonListener l = new PtzButtonListener();
		btnUp.setOnTouchListener(l);
		btnDown.setOnTouchListener(l);
		btnLeft.setOnTouchListener(l);
		btnRight.setOnTouchListener(l);
		btnZoomin.setOnTouchListener(l);
		btnZoomout.setOnTouchListener(l);
	}

	class PtzButtonListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			int cmd = NumberUtils.toInt(v.getTag().toString(), 0);
			
			//Log.e(TAG,"action:"+event.getAction());
			
			PtzTask task = new PtzTask();

			if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				task.execute(channel.getId(), cmd, 0);
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				task.execute(channel.getId(), cmd, 1);
			}
			return true;
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
        if(StringUtils.isEmpty(SNVRClient.getInstance().getToken())){
        	Intent intent = new Intent();
            intent.setClass(PreviewActivity.this, LoginActivity.class);
            startActivity(intent);
            PreviewActivity.this.finish();
        }
		// monitor.play();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		// monitor.stop();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    Log.d(TAG, "onConfigurationChanged");
	    // Checks the orientation of the screen
//	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//	        Toast.makeText(this, "横屏模式", Toast.LENGTH_SHORT).show();
//	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//	        Toast.makeText(this, "竖屏模式", Toast.LENGTH_SHORT).show();
//	    }
	}

	class PtzTask extends AsyncTask<Integer, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {

			SNVRClient client = SNVRClient.getInstance();

			client.ptz(params[0].shortValue(), params[1].byteValue(),
					params[2].byteValue());

			return Boolean.TRUE;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

		}

	}

}