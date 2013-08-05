package com.daxun.dxeye;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final String TAG = ChannelActivity.class.getSimpleName();

    private GridView gridView;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);

        gridView.setAdapter(new ChannelAdapter(this, SNVRClient.getInstance().getMonitors()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Channel c = (Channel) adapterView.getItemAtPosition(i);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PreviewActivity.class);
                intent.putExtra(Constants.CHANNEL_KEY, c);

                startActivity(intent);


            }
        });
        



    }
    

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if(StringUtils.isEmpty(SNVRClient.getInstance().getToken())){
        	Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
        
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	//gridView.setNumColumns(2);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        	//gridView.setNumColumns(1);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.logout)
                        .setMessage(R.string.confirm_logout)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogoutTask task = new LogoutTask();
                                task.execute();
                            }

                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;

            case R.id.action_channel:
                Intent intent = new Intent(MainActivity.this, ChannelActivity.class);

                //intent.putExtra(Constants.CHANNELS_KEY, (Serializable) channels);

                startActivityForResult(intent, Constants.CHANNEL_REQUEST);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.CHANNEL_REQUEST) {
            if (resultCode == RESULT_OK) {

                //List<Channel> channels = (List<Channel>) data.getSerializableExtra(Constants.CHANNELS_KEY);

                //SNVRClient.getInstance().setMonitors(channels);

                ((ChannelAdapter) gridView.getAdapter()).notifyDataSetChanged();
                gridView.invalidateViews();

                gridView.setAdapter(new ChannelAdapter(this, SNVRClient.getInstance().getMonitors()));


            } else if (resultCode == RESULT_CANCELED) {

            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), R.string.confirm_exit,
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            LogoutTask task = new LogoutTask();
            task.execute();
            finish();
            System.exit(0);
        }
    }


    class LogoutTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            SNVRClient client = SNVRClient.getInstance();

            client.logout();

            return Boolean.TRUE;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);


            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);

            startActivity(intent);

            MainActivity.this.finish();


        }


    }

}
