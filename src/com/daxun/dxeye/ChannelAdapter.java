package com.daxun.dxeye;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

/**
 * Created by luhuiguo on 13-7-12.
 */
public class ChannelAdapter extends BaseAdapter {

    private Context context;
    private  List<Channel> channels;

    public ChannelAdapter(Context context, List<Channel> channels) {
        super();
        this.context = context;
        this.channels = channels;
    }

    @Override
    public int getCount() {
        return channels.size();

    }

    @Override
    public Object getItem(int position) {
        return channels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return channels.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        Monitor monitor;

        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	monitor = new Monitor(context);
        	monitor.setChannel(channels.get(position));
        	monitor.setStream(1);
        	monitor.setEnableOSD(true);

            Point size = new Point();

            ((Activity) context).getWindowManager()
                    .getDefaultDisplay().getSize(size);


            monitor.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, size.x / 32 * 9 + 1));
            monitor.setPadding(1, 1, 1, 1);
        } else {
        	monitor = (Monitor) convertView;
        }


        return monitor;
    }






}
