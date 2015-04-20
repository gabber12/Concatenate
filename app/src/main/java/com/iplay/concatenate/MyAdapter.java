package com.iplay.concatenate;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import java.util.concurrent.ConcurrentLinkedQueue;
import android.view.LayoutInflater;
import android.widget.TextView;


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
        System.out.println("Pos: " + position);
        final View result;

        if (convertView == null) {

            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.inviteentitylayout, parent, false);
        } else {
            result = convertView;
        }

        InviteModel item = (InviteModel)getItem(position);
        System.out.println("Item = "+ item.getSenderId() + " " + position);
        System.out.flush();
        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(R.id.listViewItem)).setText(item.getSenderId());

        return result;
    }
}
