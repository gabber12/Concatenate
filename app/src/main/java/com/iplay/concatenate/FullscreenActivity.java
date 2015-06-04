package com.iplay.concatenate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.common.UserInfoFetcher;

import java.util.Arrays;

import ibt.ortc.extensibility.OnConnected;
import ibt.ortc.extensibility.OrtcClient;



public class FullscreenActivity extends NetworkActivity {

    private AccessToken token;

    private LoginButton loginButton;
    public static final boolean IS_SOCIAL = true;

    private UiLifecycleHelper fbUiLifecycleHelper;

    public UiLifecycleHelper getFbUiLifecycleHelper() {
        return fbUiLifecycleHelper;
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(token != null) {
//            Intent in = new Intent(getApplicationContext(), HomeActivity.class);
//            startActivity(in);
//        }
//    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        fbUiLifecycleHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        token = null;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        ListAdapterUtil.getQueue();
        ListAdapterUtil.getAdapter(getApplicationContext());

        fbUiLifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                // Add code here to accommodate session changes
                System.out.println("Hello-----------=");
                if (exception != null) {
                    Log.e("Error", "Error loggin in "+exception.getStackTrace());
                }
                if(state.isOpened()) {
                    System.out.println("session" + session.getAccessToken());
                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            System.out.println("Hello------########");
                            if (user != null) {
                                try {

                                    System.out.println("Graph Inner Json" + user.getInnerJSONObject().get("id"));
                                    final String userId = (String)user.getInnerJSONObject().get("id");
                                    CommonUtils.userId = userId;
                                    CommonUtils.name = user.getName();
                                    new BackgroundURLRequest().execute("subscribe_user/", userId);

                                    CommonUtils.fetchFriendScore(null,true);
                                    OrtcClient cli = ORTCUtil.getClient();

                                    cli.onConnected = new OnConnected() {
                                                  @Override
                                                  public void run(OrtcClient ortcClient) {
                                            System.out.println("Connected to ORTC");
                                            ortcClient.subscribe(CommonUtils.getChannelNameFromUserID(CommonUtils.userId), true,
                                                    new SubscribeCallbackHandler(getApplicationContext()));
                                }
                            };

                                    ORTCUtil.connect();

                                    UserInfoFetcher uif = new UserInfoFetcher(getApplicationContext());
                                    uif.fetchUserInfo();
                                    IntentFilter ifl = new IntentFilter();
                                    ifl.addAction("data_loaded");
                                    LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
                                        @Override
                                        public void onReceive(Context context, Intent intent) {
                                            Log.v("Info", "Brodcast Recienved for user Info");
                                            if (CommonUtils.imageResponse != null){
                                                Log.v("Info loaded", "Image loaded");
                                            }
                                            if(CommonUtils.friendsMap.size() > 0) {
                                                Log.v("Info loaded", "Friend List loaded");
                                            }
                                            if(CommonUtils.name != null) {
                                                Log.v("Info loaded", "Name loaded "+CommonUtils.name);
                                            }
                                        }
                                    }, ifl);



                                } catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }


                    });



                }
            }
        });
        fbUiLifecycleHelper.onCreate(savedInstanceState);
        CommonUtils.setFbUiLifecycleHelper(fbUiLifecycleHelper);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPublishPermissions(Arrays.asList("user_friends", "publish_actions"));
        CommonUtils.setLoginButton(loginButton);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    @Override
    protected void onResume(){
        super.onResume();
        fbUiLifecycleHelper.onResume();
        Session session = Session.getActiveSession();
        if( session.isOpened() ) {
            Intent in = new Intent(getApplicationContext(), HomeActivity.class);
            in.putExtra("userId", CommonUtils.userId);
            startActivity(in);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }


}
