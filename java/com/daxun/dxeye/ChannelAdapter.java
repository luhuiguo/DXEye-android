package com.daxun.dxeye;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

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
        ImageView imageView;

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);

            Point size = new Point();

            ((Activity) context).getWindowManager()
                    .getDefaultDisplay().getSize(size);


            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, size.x / 8 * 3));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(R.drawable.screen);

        return imageView;
    }






}
