package com.iplay.concatenate;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.LoginButton;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class LeaderboardActivity extends Fragment implements DataListener {


    public List<FriendModel> friends;
    View myFragmentView;
    public ListView friendList;
    public FriendListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.activity_leaderboard, container, false);
        friends = new ArrayList<FriendModel>();
        CommonUtils.addAsSubscriber(this);
        friendList = (ListView)myFragmentView.findViewById(R.id.friendsView1);
        adapter = new FriendListAdapter(getActivity().getApplicationContext(), R.layout.friendlistlayout_leaderboard, friends);
        friendList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return myFragmentView;
    }

    @Override
    public void dataSetAvailable() {
        System.out.println(CommonUtils.friendsMap.size()+"================");
        if (friends.size() == 0)
        for (Map.Entry<String, FriendModel> friend: CommonUtils.friendsMap.entrySet()) {
            FriendModel f = friend.getValue();
            if(!f.getId().equalsIgnoreCase(CommonUtils.userId))
                friends.add(new FriendModel(f.getName(), f.getId(), f.getScore()));
        }

        if(adapter != null) {
            System.out.println("----=Hello");
            adapter.notifyDataSetChanged();
        }


    }
}
