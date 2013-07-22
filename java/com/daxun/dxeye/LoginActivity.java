package com.daxun.dxeye;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * Created by luhuiguo on 13-6-28.
 */
public class LoginActivity extends Activity {

    public static final String USER_INFO = "userInfo";

    public static final  String REMEMBER_ME = "rememberMe";

    public static final  String HOST = "host";

    public static final  String PORT = "port";

    public static final  String USERNAME = "username";

    public static final  String PASSWORD = "password";

    private EditText host;
    private EditText port;
    private EditText username;
    private EditText password;

    private CheckBox rememberMe;
    private Button login;

    private SharedPreferences sp;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);




        sp = getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);

        host = (EditText)findViewById(R.id.host);
        port = (EditText)findViewById(R.id.port);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        rememberMe = (CheckBox)findViewById(R.id.rememberMe);
        login = (Button)findViewById(R.id.login);

        host.setText(sp.getString(HOST, ""));
        port.setText(sp.getString(PORT, ""));
        username.setText(sp.getString(USERNAME, ""));

        if(sp.getBoolean(REMEMBER_ME, false)){
            rememberMe.setChecked(true);
            password.setText(sp.getString(PASSWORD, ""));
        }




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginTask task = new LoginTask();
                task.execute();
            }
        });
    }


    class LoginTask extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            SNVRClient client = SNVRClient.getInstance();

            client.setHost(host.getText().toString());
            client.setPort(NumberUtils.toInt(port.getText().toString(), 53403));
            return client.login(username.getText().toString(), password.getText().toString());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this, "", getString(R.string.logining));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result){

                ChannelTask task = new ChannelTask();
                task.execute();
            }else{
                Toast.makeText(getApplicationContext(), R.string.login_failed,
                                    Toast.LENGTH_SHORT).show();

            }



        }


    }

    class ChannelTask extends AsyncTask<Void,Integer,List<Channel>> {

        @Override
        protected List<Channel> doInBackground(Void... params) {

            SNVRClient client = SNVRClient.getInstance();

            return client.channel();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this, "", getString(R.string.retrieving_channels));
        }

        @Override
        protected void onPostExecute(List<Channel> result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result!= null && result.size()>0 ){

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(HOST, host.getText().toString());
                editor.putString(PORT,port.getText().toString());
                editor.putString(USERNAME, username.getText().toString());
                editor.putBoolean(REMEMBER_ME,rememberMe.isChecked());
                editor.commit();
                if (rememberMe.isChecked()){
                    sp.edit().putString(PASSWORD,password.getText().toString()).commit();
                }else {
                    sp.edit().remove(PASSWORD).commit();

                }

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this,MainActivity.class);

                startActivity(intent);

                LoginActivity.this.finish();

            }else{
                Toast.makeText(getApplicationContext(), R.string.retrieve_channels_failed,
                        Toast.LENGTH_SHORT).show();

            }



        }


    }


}