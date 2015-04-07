package com.iplay.concatenate;


import com.facebook.FacebookException;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class HostGameActivity extends Activity {
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

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private WebDialog dialog;
    private void showDialogWithoutNotificationBar(String action, Bundle params){
        System.out.println(Session.getActiveSession().getAccessToken());
        dialog = new WebDialog.Builder(HostGameActivity.this, Session.getActiveSession(), action, params).
                setOnCompleteListener(new WebDialog.OnCompleteListener() {
                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if (error != null && !(error instanceof FacebookOperationCanceledException)) {

                        }
                        if(values != null)
                            System.out.println("to=>,"+ values.toString());

                        String opponentId = values.getString("to[0]");
                        dialog = null;
                        OrtcClient client = ORTCUtil.getClient();
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("typeFlag", 1);
                            jsonObject.put("toUser", opponentId);
                            jsonObject.put("fromUser", CommonUtils.userId);
                            client.send(CommonUtils.getChannelNameFromUserID(opponentId), jsonObject.toString());
                        } catch ( JSONException je ) {
                            System.out.println("Unable to encode json: " + je.getMessage());
                        }

                    }
                }).build();

        Window dialog_window = dialog.getWindow();
        dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_host_game);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        Bundle params = new Bundle();
        params.putString("message", "I just smashed " +
                " friends! Can you beat it?");
        params.putInt("max_recipients", 1);
        showDialogWithoutNotificationBar("apprequests", params);

//        callbackManager = CallbackManager.Factory.create();
//        requestDialog = new GameRequestDialog(this);
//
//        final Activity that = this;
//        content = new GameRequestContent.Builder()
//                .setMessage("Come play this level with me")
//                .build();
//        System.out.println(content.getData());
//        requestDialog.registerCallback(callbackManager, new FacebookCallback<GameRequestDialog.Result>() {
//
//            public void onSuccess(GameRequestDialog.Result result) {
//                String str = content.getTo();
//                System.out.println(content.getData());
//                final String id = result.getRequestId();
//                System.out.println(">>>>"+id);
//                Log.d("Error", "hello" + id);
//                final Timer t = new Timer();
//                final long startTime = System.currentTimeMillis();
//
//                t.scheduleAtFixedRate(new TimerTask() {
//
//
//
//                    @Override
//                    public void run() {
//                        OrtcClient client = ORTCUtil.getClient();
//                        String payload = "";
//                        client.send("host_game_"+id, payload);
//                        that.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                TextView dummy_text = (TextView) findViewById(R.id.fullscreen_content);
//                                Log.d("", "" + System.currentTimeMillis());
//                                long left = (30 * 1000 - System.currentTimeMillis() + startTime);
//                                if(  left <=0 ){
//                                    t.cancel();
//                                    left = 0;
//                                    Toast t = Toast.makeText(getApplicationContext(), "No one joind your game :(", Toast.LENGTH_LONG);
//                                    t.show();
//
//                                }
//
//                                dummy_text.setText("" + Math.round(left/100.0)/10.0 + "s left!!");
//
//                            }
//                        });
//
//                    }
//                }, new Long(0), new Long(100));
//            }
//
//            public void onCancel() {
//                Log.d("Error", "hello1");
//            }
//
//            public void onError(FacebookException error) {
//                Log.d("Error", "hello2");
//            }
//        });





    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
