package com.iplay.concatenate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.iplay.concatenate.common.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import ibt.ortc.extensibility.OrtcClient;



public class InviteFriends extends NetworkActivity implements DataListener{

    public FriendListAdapter fla;
    private WebDialog dialog;
    public List<FriendModel>friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_friends);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        friends = new ArrayList<FriendModel>();
        final ListView friendList = (ListView) findViewById(R.id.friendList);
        final Session session = Session.getActiveSession();
        final Activity that = this;
        final Context ctx = getApplicationContext();
        if (CommonUtils.friendsMap != null)
        for (Map.Entry<String, FriendModel> friend: CommonUtils.friendsMap.entrySet()) {
            FriendModel f = friend.getValue();
            if(!f.getId().equalsIgnoreCase(CommonUtils.userId))
                friends.add(new FriendModel(f.getName(), f.getId(), f.getScore()));
        }


        CommonUtils.addAsSubscriber(this);

        fla = new FriendListAdapter(ctx, R.layout.friendlistlayout, friends);
        ((ListView) findViewById(R.id.friendList)).setAdapter(fla);
        fla.notifyDataSetChanged();





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
                            Intent in = new Intent(getApplicationContext(), NewHostGameActivity.class);
                            in.putExtra("id", opponentId);
                            startActivity(in);
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
//        ((carbon.widget.ListView)findViewById(R.id.friendList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Bundle params = new Bundle();
//                params.putString("message", "I just smashed " +
//                        " friends! Can you beat it?");
//                params.putString("to", fla.getItem(position).getId());
//                params.putInt("max_recipients", 1);
//                showDialogWithoutNotificationBar("apprequests", params);
//                System.out.print("Hello");
//            }
//        });
    }
    public void myfunc_invite(View v){

        Bundle params = new Bundle();
        params.putString("message", "I challenge you for a Concaty showdown!");
        params.putString("to",((CircularProfilePicView) v.findViewById(R.id.profile_pic)).getProfileId());
        params.putInt("max_recipients", 1);
        showDialogWithoutNotificationBar("apprequests", params);
        System.out.print("Hello");
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, HomeActivity.class);
        startActivity(in);
    }

    @Override
    public void onResume() {
        super.onResume();
        EditText editText = ((EditText)findViewById(R.id.inputSearch));
//        editText.clearFocus();
        editText.setFocusableInTouchMode(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
    }

    @Override
    public void dataSetAvailable() {
        if(friends.size() == 0)
        for (Map.Entry<String, FriendModel> friend: CommonUtils.friendsMap.entrySet()) {
            FriendModel f = friend.getValue();
            if(!f.getId().equalsIgnoreCase(CommonUtils.userId))
                friends.add(new FriendModel(f.getName(), f.getId(), f.getScore()));
        }
        if(fla != null)
            fla.notifyDataSetChanged();
    }
}
