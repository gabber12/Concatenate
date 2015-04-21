package com.iplay.concatenate;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ibt.ortc.extensibility.OrtcClient;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class InviteFriends extends Activity {
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
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION | SystemUiHider.FLAG_FULLSCREEN ;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    public FriendListAdapter fla;
    private WebDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_friends);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        final ListView friendList = (ListView) findViewById(R.id.friendList);
        Session session = Session.getActiveSession();
        final Activity that = this;
        final Context ctx = getApplicationContext();
        if(session != null) {
            Request friendsRequest = Request.newMyFriendsRequest(session , new Request.GraphUserListCallback()
            {

                @Override
                public void onCompleted(List<GraphUser> graphUsers, Response response) {
                    final List<FriendModel> friends = new ArrayList<FriendModel>();
                    for(GraphUser usr: graphUsers){
                        friends.add(new FriendModel(usr.getName(), usr.getId()));
                    }
                    CommonUtils.friendArrayList = friends;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fla = new FriendListAdapter(ctx,R.layout.friendlistlayout, friends);
                            ((ListView) findViewById(R.id.friendList)).setAdapter(fla);
                            fla.notifyDataSetChanged();

                        }
                    });
                }
            });
            Request.executeBatchAsync(friendsRequest);



        }
        ((EditText)findViewById(R.id.inputSearch)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                InviteFriends.this.fla.getFilter().filter(s);
                System.out.println("Changed");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showDialogWithoutNotificationBar(String action, Bundle params){
        System.out.println(Session.getActiveSession().getAccessToken());
        dialog = new WebDialog.Builder(InviteFriends.this, Session.getActiveSession(), action, params).
                setOnCompleteListener(new WebDialog.OnCompleteListener() {
                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if (error != null && !(error instanceof FacebookOperationCanceledException)) {

                        }
                        if(values != null) {
                            System.out.println("to=>," + values.toString());

                            final String opponentId = values.getString("to[0]");
                            dialog = null;
                            final OrtcClient client = ORTCUtil.getClient();
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("typeFlag", 1);
                                jsonObject.put("toUser", opponentId);
                                jsonObject.put("fromUser", CommonUtils.userId);
                                client.send(CommonUtils.getChannelNameFromUserID(opponentId), jsonObject.toString());
                            } catch (JSONException je) {
                                System.out.println("Unable to encode json: " + je.getMessage());
                            }
                            CommonUtils.waitingFor = opponentId;
                            CommonUtils.hostGameTimer = new Timer();
                            final long startTime = System.currentTimeMillis();
                            CommonUtils.hostGameTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    InviteFriends.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            long left = (30 * 1000 - System.currentTimeMillis() + startTime);
                                            if (left <= 0) {
                                                CommonUtils.hostGameTimer.cancel();
                                                try {
                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("typeFlag", 3);
                                                    jsonObject.put("toUser", opponentId);
                                                    jsonObject.put("fromUser", CommonUtils.userId);
                                                    client.send(CommonUtils.getChannelNameFromUserID(opponentId), jsonObject.toString());
                                                } catch (JSONException je) {
                                                    System.out.println("Unable to encode json: " + je.getMessage());
                                                }
                                                CommonUtils.waitingFor = null;
                                                Toast toast = Toast.makeText(getApplicationContext(), "Opponent did not join. :(", Toast.LENGTH_LONG);
                                                toast.show();
                                                Intent intent = new Intent(InviteFriends.this, HomeActivity.class);
                                                InviteFriends.this.startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }, new Long(0), new Long(200));

                        }

                    }
                }).build();

        Window dialog_window = dialog.getWindow();
        dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        dialog.show();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        ((carbon.widget.ListView)findViewById(R.id.friendList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle params = new Bundle();
                params.putString("message", "I just smashed " +
                        " friends! Can you beat it?");
                params.putString("to", fla.getItem(position).getId());
                params.putInt("max_recipients", 1);
                showDialogWithoutNotificationBar("apprequests", params);
                System.out.print("Hello");
            }
        });
    }


}
