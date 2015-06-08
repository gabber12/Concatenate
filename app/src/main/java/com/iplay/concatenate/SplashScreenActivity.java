package com.iplay.concatenate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;

import ibt.ortc.extensibility.OnConnected;
import ibt.ortc.extensibility.OrtcClient;


public class SplashScreenActivity extends NetworkActivity {

    private AccessToken token;
    private LoginButton loginButton;
    private UiLifecycleHelper fbUiLifecycleHelper;

    public UiLifecycleHelper getFbUiLifecycleHelper() {
        return fbUiLifecycleHelper;
    }
    UserInfoFetcher uif;
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        fbUiLifecycleHelper.onSaveInstanceState(outState);
    }

    private long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);


        CommonUtils.FreightSansFont = Typeface.createFromAsset(getAssets(), "FreightSans-BoldSC.ttf");

        setupFacebook();
        fbUiLifecycleHelper.onCreate(savedInstanceState);
        CommonUtils.setFbUiLifecycleHelper(fbUiLifecycleHelper);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPublishPermissions(Arrays.asList("user_friends", "publish_actions"));
        loginButton.setBackgroundResource(R.drawable.profile_login);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        startTime = System.currentTimeMillis();


        ImageView background_image = (ImageView) findViewById(R.id.splash_logo);
        Animation scale = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(3000);

        ((Button)loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false);
                v.setBackgroundResource(R.drawable.profile_logging);
            }
        });

        uif = new UserInfoFetcher(getApplicationContext());
        CommonUtils.uif = uif;
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // start the animation to fullscreen if not logged in otherwise just start your caching.

                uif.dataSetAvailable();
                if(!Session.getActiveSession().isOpened())
                    animateToShowLogin();


//                Intent intent = new Intent(SplashScreenActivity.this, FullscreenActivity.class);
//                SplashScreenActivity.this.startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        background_image.startAnimation(scale);
//        background_image.setColorFilter(Color.argb(255, 255, 255, 255));
        cacheData();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void animateToShowLogin() {

        RelativeLayout loginLayout = (RelativeLayout) findViewById(R.id.loginlayout);

        loginLayout.setVisibility(View.VISIBLE);
        Animation anim = new TranslateAnimation(0, 0, CommonUtils.getPixelsfromDP(getApplicationContext(), 150f), 0);
        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        loginLayout.startAnimation(anim);

        Animation animlogo = new TranslateAnimation(0, 0, 0, -1*CommonUtils.getPixelsfromDP(getApplicationContext(), 100f));
        animlogo.setDuration(1000);
        animlogo.setFillAfter(true);
        animlogo.setFillEnabled(true);
        animlogo.setInterpolator(new AccelerateDecelerateInterpolator());
        (findViewById(R.id.splash_logo)).startAnimation(animlogo);

    }

    private void cacheData() {

        // Adding dictionary words to store in a static hash set
//        System.out.println(System.currentTimeMillis());

        // Loading dictionary from a thread - assuming will be loaded till game loads

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                long currentTime = System.currentTimeMillis();

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

                System.out.println("Time - " + (System.currentTimeMillis() - currentTime)/1000);

            }
        });
        thread.start();

    }

    private void setupFacebook() {
        fbUiLifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                // Add code here to accommodate session changes

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
                                    final String userId = (String) user.getInnerJSONObject().get("id");
                                    CommonUtils.userId = userId;
                                    CommonUtils.name = user.getName();
                                    new BackgroundURLRequest().execute("subscribe_user/", userId);

                                    OrtcClient cli = ORTCUtil.getClient();

                                    cli.onConnected = new OnConnected() {
                                        @Override
                                        public void run(OrtcClient ortcClient) {
                                            System.out.println("Fetch ortc - " + System.currentTimeMillis());
                                            System.out.println("Connected to ORTC");
                                            ortcClient.subscribe(CommonUtils.getChannelNameFromUserID(CommonUtils.userId), true,
                                                    new SubscribeCallbackHandler(getApplicationContext()));
                                        }
                                    };

                                    ORTCUtil.connect();
                                    uif.dataSetAvailable();


                                    uif.fetchUserInfo();
                                    IntentFilter ifl = new IntentFilter();
                                    ifl.addAction("data_loaded");
                                    LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
                                        @Override
                                        public void onReceive(Context context, Intent intent) {

                                            System.out.println("Total time -- " + (System.currentTimeMillis() - startTime)/1000.0);

                                            Intent in = new Intent(getApplicationContext(), HomeActivity.class);
                                            in.putExtra("userId", CommonUtils.userId);
                                            startActivity(in);
                                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                            SplashScreenActivity.this.finish();
                                        }
                                    }, ifl);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                    });



                } else {
                    // TODO: Set this on canceling fb dialog or failure to connect
//                    loginButton.setBackgroundResource(R.drawable.profile_login);
//                    ((Button)loginButton).setClickable(true);
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        fbUiLifecycleHelper.onResume();
        Session session = Session.getActiveSession();
        if(session.isClosed()) {
            System.out.println("hello");
        }
        if(CommonUtils.uif == null) CommonUtils.uif = new UserInfoFetcher(getApplicationContext());
        if(CommonUtils.uif.count >= 4){
            Intent in = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(in);
        } else {
            CommonUtils.uif.count = 1;
            CommonUtils.uif.fetchUserInfo();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

}


