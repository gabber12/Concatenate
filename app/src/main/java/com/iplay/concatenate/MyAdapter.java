package com.iplay.concatenate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.concurrent.ConcurrentLinkedQueue;


public class MyAdapter extends BaseAdapter {
    private ConcurrentLinkedQueue<InviteModel> mData;
    private LayoutInflater mInflater;
    public MyAdapter(Context mContext,  ConcurrentLinkedQueue<InviteModel> data) {
        mData = data;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public InviteModel getItem(int position) {

        InviteModel[] a = mData.toArray(new InviteModel[mData.size()]);


        return a[position];
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

            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendlistlayout_join, parent, false);
        } else {
            result = convertView;
        }
        System.out.println("==----"+getItem(position).getName());
        // TODO replace findViewById by ViewHolder
        ((CircularProfilePicView) result.findViewById(R.id.profile_pic)).setProfileId(getItem(position).getId());
        ((TextView) result.findViewById(R.id.profile_name)).setText(getItem(position).getName());
        ((TextView) result.findViewById(R.id.profile_score)).setText("Score "+getItem(position).getScore());

        return result;
    }
}
