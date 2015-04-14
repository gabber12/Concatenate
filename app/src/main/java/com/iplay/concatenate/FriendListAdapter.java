package com.iplay.concatenate;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

import java.util.List;

/**
 * Created by gabber12 on 12/04/15.
 */
public class FriendListAdapter extends BaseAdapter {
    List<Pair<String, String>> list;
    private LayoutInflater mInflater;
    Context mContext;
    public FriendListAdapter(Context ctx,List<Pair<String, String>> list) {
        this.list = list;
        mContext = ctx;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {




        final View result;

        if (convertView == null) {

            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendlistlayout, parent, false);
        } else {
            result = convertView;
        }
        System.out.println(""+list.get(position).first);
        // TODO replace findViewById by ViewHolder
        ((CircularProfilePicView) result.findViewById(R.id.profile_pic)).setProfileId(list.get(position).second);
        ((TextView) result.findViewById(R.id.profile_name)).setText(list.get(position).first);
        return result;


    }
}

