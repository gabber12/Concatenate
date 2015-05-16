package com.iplay.concatenate;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import ibt.ortc.extensibility.OnConnected;
import ibt.ortc.extensibility.OnMessage;
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
    private ProfilePictureView userImage;
    private TextView welcomeTextView;
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

                                    CommonUtils.fetchFriendScore();
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
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPublishPermissions(Arrays.asList("user_friends", "publish_actions"));
        String text = loginButton.getText().toString();
        System.out.println(text);
        CommonUtils.setLoginButton(loginButton);

        // Callback registration

        OrtcClient cli =  ORTCUtil.getClient();

        // Adding dictionary words to store in a static hash set

        if ( CommonUtils.words == null ) {
            CommonUtils.words = new HashSet<String>();
            InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.dict);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line = reader.readLine();
                while (line != null) {
                    CommonUtils.words.add( line.toUpperCase() );
                    line = reader.readLine();
                }
            } catch ( Exception e) {
                System.out.println("error while reading from dictionary of words.");
            }
        }
        
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
