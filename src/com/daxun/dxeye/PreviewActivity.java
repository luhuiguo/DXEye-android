package com.daxun.dxeye;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.mina.core.session.IoSession;

/**
 * Created by luhuiguo on 13-6-28.
 */
public class PreviewActivity extends Activity {

    private static final String TAG = ChannelActivity.class.getSimpleName();

    private Channel channel;

    private Monitor monitor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        channel = (Channel) getIntent().getSerializableExtra(Constants.CHANNEL_KEY);

        setTitle(channel.getName());

        monitor = new Monitor(channel);
        monitor.play(1);

//        PreviewTask task = new PreviewTask();
//        task.execute();


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

    }


//    class PreviewTask extends AsyncTask<Void, Void, IoSession> {
//
//        @Override
//        protected IoSession doInBackground(Void... params) {
//
//            SNVRClient client = SNVRClient.getInstance();
//            IoSession session = client.preview((short) channel.getId(), 1);
//
//
//            return session;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(IoSession result) {
//
//
//        }
//
//
//    }
}