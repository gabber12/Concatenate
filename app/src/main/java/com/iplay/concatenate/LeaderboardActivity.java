package com.iplay.concatenate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iplay.concatenate.common.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LeaderboardActivity extends Fragment implements DataListener {


    public List<FriendModel> friends;
    View myFragmentView;
    public ListView friendList;
    public FriendListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.activity_leaderboard, container, false);
        friends = new ArrayList<FriendModel>();
        if (friends.size() == 0 && CommonUtils.friendsMap != null)
            for (Map.Entry<String, FriendModel> friend: CommonUtils.friendsMap.entrySet()) {
                FriendModel f = friend.getValue();
                if(!f.getId().equalsIgnoreCase(CommonUtils.userId))
                    friends.add(new FriendModel(f.getName(), f.getId(), f.getScore()));
            }
        CommonUtils.addAsSubscriber(this);
        friendList = (ListView)myFragmentView.findViewById(R.id.friendsView1);
        adapter = new FriendListAdapter(getActivity().getApplicationContext(), R.layout.friendlistlayout_leaderboard, friends);
        friendList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        System.out.println("Reached Here");
        return myFragmentView;
    }

    @Override
    public void dataSetAvailable() {

        if (friends.size() == 0)
        for (Map.Entry<String, FriendModel> friend: CommonUtils.friendsMap.entrySet()) {
            FriendModel f = friend.getValue();
            if(!f.getId().equalsIgnoreCase(CommonUtils.userId))
                friends.add(new FriendModel(f.getName(), f.getId(), f.getScore()));
        }

        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }


    }
}
