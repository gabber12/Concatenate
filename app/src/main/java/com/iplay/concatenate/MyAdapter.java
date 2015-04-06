package com.iplay.concatenate;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.concurrent.ConcurrentLinkedQueue;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.iplay.concatenate.R;


public class MyAdapter extends BaseAdapter {
    private ConcurrentLinkedQueue mData;
    private LayoutInflater mInflater;
    public MyAdapter(Context mContext,  ConcurrentLinkedQueue<String> data) {
        mData = data;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        ConcurrentLinkedQueue<String> temp = new ConcurrentLinkedQueue<String>(mData);
        while(position > 0 && temp.size() > 0) {
            temp.poll();
            position--;
        }


        return (String) temp.poll();
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {

            result = mInflater.from(parent.getContext()).inflate(R.layout.inviteentitylayout, parent, false);
        } else {
            result = convertView;
        }

        String item = getItem(position);
        System.out.println("Item = "+ item);
        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(R.id.listViewItem)).setText(item);

        return result;
    }
}
