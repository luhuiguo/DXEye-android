package com.daxun.dxeye;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luhuiguo on 13-6-28.
 */
public class ChannelActivity extends ListActivity {

    private static final String TAG = ChannelActivity.class.getSimpleName();

    private List<Channel> channels;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channels = SNVRClient.getInstance().getChannels();
        List<Channel> monitors = SNVRClient.getInstance().getMonitors();

//        List<Channel> selected = (List<Channel>)getIntent().getSerializableExtra(Constants.CHANNELS_KEY);
//
//        Log.d(TAG,"selected:" + selected);



        SimpleAdapter adapter = new SimpleAdapter(this, getData(), android.R.layout.simple_list_item_multiple_choice,new String[] {"name"}, new int[] {android.R.id.text1});

        setListAdapter(adapter);
        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if (monitors!=null&& monitors.size()>0){
            for(int i=0; i< channels.size();i++){
                Channel channel = channels.get(i);
                if (monitors.contains(channel)){
                    listView.setItemChecked(i, true);
                }

            }

        }



    }


    public List<Map<String,String>> getData(){

        List<Map<String,String>> list = new ArrayList<Map<String,String>>();

        for (Channel ch:SNVRClient.getInstance().getChannels()){
            Map<String,String> map = new HashMap<String, String>();
            map.put("id",String.valueOf(ch.getId()));
            map.put("name",ch.getName());
            list.add(map);

        }



        return list;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:


                ChannelActivity.this.setResult(RESULT_CANCELED);
                ChannelActivity.this.finish();

                return true;

            case R.id.action_ok:

                ListView listView = ChannelActivity.this.getListView();
                SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                List<Channel> selected = new ArrayList<Channel>();

                if (checkedItems != null) {
                    for (int i=0; i<checkedItems.size(); i++) {

                        if (checkedItems.valueAt(i)) {
                            selected.add(channels.get(
                                    checkedItems.keyAt(i)));
                        }
                    }
                }

                Log.d(TAG,"selected:" + selected);
                SNVRClient.getInstance().setMonitors(selected);

                //Intent intent = ChannelActivity.this.getIntent();

//                Intent intent = new Intent(ChannelActivity.this, MainActivity.class);
//
//                intent.putExtra(Constants.CHANNELS_KEY, (Serializable)selected);
                ChannelActivity.this.setResult(RESULT_OK);
                ChannelActivity.this.finish();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

}