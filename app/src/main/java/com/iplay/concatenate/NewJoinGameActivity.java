package com.iplay.concatenate;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.json.simple.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class NewJoinGameActivity extends Activity {
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
    private TextSwitcher mSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_join_game);

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

        // code begins here

        String senderId = getIntent().getStringExtra("sender_id");
        CommonUtils.waitingFor = senderId;

        mSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        mSwitcher.startAnimation(anim);

        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like color, size etc
                TextView myText = new TextView(NewJoinGameActivity.this);
                myText.setGravity(Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(20);
                myText.setTextColor(Color.WHITE);
                return myText;
            }
        });

        Animation fadeIn = new AlphaAnimation(0.0f,1.0f);
        fadeIn.setDuration(500);
        Animation fadeOut = new AlphaAnimation(1.0f,0.0f);
        fadeOut.setDuration(500); fadeOut.setStartOffset(500);

        mSwitcher.setInAnimation(fadeIn);
        mSwitcher.setOutAnimation(fadeOut);

        mSwitcher.setText("JOINING");

        onGameStarted(senderId, false);

    }

    private void onGameStarted(final String senderId, final boolean isBot) {

        CommonUtils.onStartingGame = true;

        // TODO: Add the joining users cover

        mSwitcher.clearAnimation();
        Animation fadeOutCustom = new AlphaAnimation(mSwitcher.getAlpha(),0);
        fadeOutCustom.setDuration(500);
        mSwitcher.setOutAnimation(fadeOutCustom);
        mSwitcher.setText("CONCATY !");

        long duration = 2000; long offset = 1200;
        Animation zoomOut = new ScaleAnimation(1.0f, 1.25f, 1.0f, 1.25f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation zoomIn = new ScaleAnimation(1.0f, 0.83333f, 1.0f, 0.83333f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomIn.setDuration(duration); zoomOut.setDuration(duration);
        zoomIn.setStartOffset(offset); zoomOut.setStartOffset(offset);
        zoomIn.setFillEnabled(true); zoomOut.setFillEnabled(true);
        zoomIn.setFillAfter(true); zoomOut.setFillAfter(true);

        Interpolator bounceInterpolator = new BounceInterpolator();
        zoomIn.setInterpolator(bounceInterpolator);
        zoomOut.setInterpolator(bounceInterpolator);

        Animation moveUp = new TranslateAnimation(0,0,0,-30);
        moveUp.setFillAfter(true); moveUp.setFillAfter(true);
        moveUp.setDuration(800); moveUp.setStartOffset(offset);

        findViewById(R.id.mypic).startAnimation(zoomOut);
        findViewById(R.id.yourpic).startAnimation(zoomIn);
        findViewById(R.id.textSwitcherlayout).startAnimation(moveUp);


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Do some stuff

                        Interpolator decelerate = new DecelerateInterpolator();
                        Interpolator accelerate = new AccelerateInterpolator();
                        Animation compressIn = new ScaleAnimation(1.0f, 0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        Animation compressOut = new ScaleAnimation(0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        compressIn.setDuration(500); compressIn.setInterpolator(decelerate);
                        compressOut.setDuration(500); compressOut.setInterpolator(accelerate);
//                                compressIn.setStartOffset(3200);
                        compressOut.setStartOffset(500);
//                                mSwitcher.setOutAnimation(compressIn);
//                                mSwitcher.setInAnimation(compressOut);

                        findViewById(R.id.textSwitcherrel).setVisibility(View.GONE);
                        findViewById(R.id.textBoxrel).setVisibility(View.VISIBLE);
                        findViewById(R.id.barRightrel).setVisibility(View.VISIBLE);
                        findViewById(R.id.barLeftrel).setVisibility(View.VISIBLE);
                        findViewById(R.id.yourinfolayout).setVisibility(View.VISIBLE);
                        findViewById(R.id.myinfolayout).setVisibility(View.VISIBLE);

                        Animation moveLeft = new TranslateAnimation(-600, 0, 0, 0);
                        Animation moveRight = new TranslateAnimation(600, 0, 0, 0);
                        moveLeft.setDuration(500); moveRight.setDuration(500);
                        findViewById(R.id.barLeftrel).startAnimation(moveLeft);
                        findViewById(R.id.barRightrel).startAnimation(moveRight);

                        findViewById(R.id.yourinfolayout).startAnimation(moveLeft);
                        findViewById(R.id.myinfolayout).startAnimation(moveRight);



                    }
                });
            }
        };
        thread.start();

//                final String senderId = getIntent().getStringExtra("sender_id");
//                final Boolean isBot = getIntent().getBooleanExtra("is_bot", false);

        CommonUtils.waitingFor = senderId;

        if ( isBot ) {

            JSONObject sendjsonObject = new JSONObject();
            sendjsonObject.put("fromUser", CommonUtils.userId );
            sendjsonObject.put("toUser", senderId);
            System.out.println(sendjsonObject.toString());
            new BackgroundURLRequest().execute("start_game_with_bot/", sendjsonObject.toString());

        }
        // say the opponent left after 20 secs
        final long startTime = System.currentTimeMillis();
        CommonUtils.startingGameTimer = new Timer();
        CommonUtils.startingGameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                NewJoinGameActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (CommonUtils.startGameIntent != null && System.currentTimeMillis() - CommonUtils.startGameIntent.getLongExtra("timestamp", System.currentTimeMillis()) <= 10 * 1000
                                && System.currentTimeMillis() - CommonUtils.startGameIntent.getLongExtra("timestamp", System.currentTimeMillis()) >= 5 * 1000) {
                            CommonUtils.disableTimer(CommonUtils.startingGameTimer);
                            startActivity(CommonUtils.startGameIntent);
                        } else if (System.currentTimeMillis() - startTime >= 30 * 1000) {
                            Toast t = Toast.makeText(getApplicationContext(), "Opponent has left :(", Toast.LENGTH_LONG);
                            t.show();
                            final Intent intent = new Intent(NewJoinGameActivity.this, HomeActivity.class);
                            CommonUtils.waitingFor = null;
                            CommonUtils.disableTimer(CommonUtils.startingGameTimer);
                            startActivity(intent);
                        }
                    }
                });

            }
        }, new Long(0), new Long(1000));


    }

    @Override
    public void onBackPressed() {
        if ( CommonUtils.onStartingGame ) {
            new MaterialDialog.Builder(this)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            NewJoinGameActivity.super.onBackPressed();
                        }
                    })
                    .title("Leave Game")
                    .titleGravity(GravityEnum.CENTER)
                    .content("You will lose if you leave the game. Still continue?")
                    .positiveText("QUIT")
                    .negativeText("PLAY ON")
                    .theme(Theme.LIGHT)
                    .negativeColorRes(R.color.material_deep_teal_500)
                    .positiveColorRes(R.color.material_red_500)
                    .show();
        } else {
            Intent in = new Intent(this, HomeActivity.class);
            startActivity(in);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonUtils.onStartingGame = false;
        CommonUtils.waitingFor = null;
        CommonUtils.disableTimer(CommonUtils.startingGameTimer);
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
