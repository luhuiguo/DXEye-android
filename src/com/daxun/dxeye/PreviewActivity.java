package com.daxun.dxeye;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by luhuiguo on 13-6-28.
 */
public class PreviewActivity extends Activity {

    private static final String TAG = ChannelActivity.class.getSimpleName();

    Channel channel;

    Monitor monitor;
    
    ImageView imageView;
    
    Timer timer = new Timer();  
    
    static class RenderHandler extends Handler {
		WeakReference<PreviewActivity> mActivity;

		RenderHandler(PreviewActivity activity) {
			mActivity = new WeakReference<PreviewActivity>(activity);
		}

		@Override  
        public void handleMessage(Message msg)  
        {  
  
			PreviewActivity theActivity = mActivity.get();
        	
			theActivity.imageView.setImageBitmap(theActivity.monitor.getBitmap());
			
			Log.d(TAG,"setImageBitmap");
            super.handleMessage(msg);  
        }  
	};
    
	private Handler handler = new RenderHandler(this);  
 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        
        imageView = (ImageView)findViewById(R.id.imageView);

        channel = (Channel) getIntent().getSerializableExtra(Constants.CHANNEL_KEY);

        setTitle(channel.getName());

        monitor = new Monitor(channel);


    }

    @Override
    public void onResume() {
        super.onResume();
        monitor.play(1);
        timer.scheduleAtFixedRate(new TimerTask()  
        {  
            @Override  
            public void run()  
            {  
            	handler.obtainMessage().sendToTarget();
            }  
        }, 0, 50); 
        
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        monitor.stop();
        timer.cancel();  
        Log.d(TAG, "onPause");

    }



}