package com.iplay.concatenate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.iplay.concatenate.common.CommonUtils;

import org.json.simple.JSONObject;

import java.util.concurrent.ConcurrentLinkedQueue;

import ibt.ortc.extensibility.OrtcClient;




public class JoinGameAcitvity extends NetworkActivity {

    public MyAdapter ma;



    private JoinGameAcitvity that;
    ListView inviteView;
    ConcurrentLinkedQueue<InviteModel> inviteModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        that = this;
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("invite_received"));

        setContentView(R.layout.activity_join_game_acitvity);

        ((TextView)findViewById(R.id.join_title)).setTypeface(CommonUtils.FreightSansFont);

        //Add id to channel

        final OrtcClient client = ORTCUtil.getClient();


        inviteView = (ListView)findViewById(R.id.invitesView);


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String senderId = intent.getStringExtra("sender_id");
            Log.d("receiver", "Got message: " + senderId);
            ListAdapterUtil.getAdapter(getApplicationContext()).notifyDataSetChanged();
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        inviteModels = ListAdapterUtil.getQueue();

        ListView inviteView =(ListView) findViewById(R.id.invitesView);
        try {
            ma = ListAdapterUtil.getAdapter(getApplicationContext());
            inviteView.setAdapter(ma);

            ma.notifyDataSetChanged();

        } catch(Exception e) {
            e.printStackTrace();
        }
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    public void myfunc_join(View v){
        String senderId = ((CircularProfilePicView)v.findViewById(R.id.profile_pic)).getProfileId();
        OrtcClient client = ORTCUtil.getClient();
        try {
            Intent intent = new Intent(that, NewJoinGameActivity.class);
            intent.putExtra("sender_id", senderId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("typeFlag", 2);
            jsonObject.put("fromUser", CommonUtils.userId);
            jsonObject.put("toUser", senderId);
            System.out.println(jsonObject.toString());
            client.send(CommonUtils.getChannelNameFromUserID(senderId), jsonObject.toString());
            ListAdapterUtil.removeInviteById( senderId );
            ma.notifyDataSetChanged();
            startActivity(intent);
        } catch (Exception e) {
            Log.e("json", "error while generating a json file and sending to server: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, HomeActivity.class);
        startActivity(in);
    }

}
