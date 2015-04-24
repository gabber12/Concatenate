package com.iplay.concatenate;

import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.simple.JSONObject;

import java.util.concurrent.ConcurrentLinkedQueue;

import ibt.ortc.extensibility.OrtcClient;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */


public class JoinGameAcitvity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    public MyAdapter ma;
    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private JoinGameAcitvity that;

    ConcurrentLinkedQueue<InviteModel> inviteModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        that = this;
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("invite_received"));

        setContentView(R.layout.activity_join_game_acitvity);

        //Add id to channel

        final OrtcClient client = ORTCUtil.getClient();
        final ListView inviteView = (ListView)findViewById(R.id.invitesView);

        inviteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object object = inviteView.getItemAtPosition(position);
                InviteModel inviteModel = (InviteModel) object;//As you are using Default String Adapter

                try {
                    Intent intent = new Intent(that, StartingGame.class);
                    intent.putExtra("sender_id", inviteModel.getSenderId());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("typeFlag", 2);
                    jsonObject.put("fromUser", CommonUtils.userId);
                    jsonObject.put("toUser", inviteModel.getSenderId());
                    System.out.println(jsonObject.toString());
                    client.send(CommonUtils.getChannelNameFromUserID(inviteModel.getSenderId()), jsonObject.toString());
                    ListAdapterUtil.removeInviteById( inviteModel.getSenderId() );
                    ma.notifyDataSetChanged();
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("json", "error while generating a json file and sending to server: " + e.getMessage());
                }
            }
        });


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


}
