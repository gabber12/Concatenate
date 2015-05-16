package com.iplay.concatenate;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.json.simple.JSONObject;

import java.util.Timer;
import java.util.TimerTask;



public class NewQuickGame extends Activity {

    private TextSwitcher mSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_quick_game);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);



        // my code begins here

        CommonUtils.onQuickGame = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(mGameStarting, new IntentFilter("starting_game"));

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
                TextView myText = new TextView(NewQuickGame.this);
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

        mSwitcher.setText("SEARCHING OPPONENT");


        // add progress bar too.

        new BackgroundURLRequest().execute("add_me_to_wait_pool/", CommonUtils.userId);


        CommonUtils.quickGameTimer = new Timer();
        CommonUtils.quickGameTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                NewQuickGame.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Sending bot request");
                        new BackgroundURLRequest().execute("give_me_bot/", CommonUtils.userId);
                        CommonUtils.disableTimer(CommonUtils.quickGameTimer);
                    }
                });
            }
        }, 10000); // TODO: change the time to 30 seconds



    }

    private void onGameStarted(final String senderId, final boolean isBot) {

        CommonUtils.onQuickGame = false;
        CommonUtils.onStartingGame = true;
        findViewById(R.id.progress_wheel_quick).setVisibility(View.GONE);

        // TODO: Add the joining users pic and cover

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
//                        CommonUtils.disableTimer(CommonUtils.startingGameTimer);
                        NewQuickGame.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (CommonUtils.startGameIntent != null && System.currentTimeMillis() - CommonUtils.startGameIntent.getLongExtra("timestamp", System.currentTimeMillis()) <= 10 * 1000
                                        && System.currentTimeMillis() - CommonUtils.startGameIntent.getLongExtra("timestamp", System.currentTimeMillis()) >= 5 * 1000) {
                                    CommonUtils.disableTimer(CommonUtils.startingGameTimer);
                                    startActivity(CommonUtils.startGameIntent);
                                } else if (System.currentTimeMillis() - startTime >= 20 * 1000) {
                                    Toast t = Toast.makeText(getApplicationContext(), "Opponent has left :(", Toast.LENGTH_LONG);
                                    t.show();
                                    final Intent intent = new Intent(NewQuickGame.this, HomeActivity.class);
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
                            NewQuickGame.super.onBackPressed();
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

    private BroadcastReceiver mGameStarting = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            System.out.println("hello!");
            onGameStarted( intent.getStringExtra("sender_id"), intent.getBooleanExtra("is_bot", false) );
        }
    };

    @Override
    public void onPause() {
        new BackgroundURLRequest().execute("remove_me_from_pool/", CommonUtils.userId);
        CommonUtils.onQuickGame = false;
        CommonUtils.disableTimer(CommonUtils.quickGameTimer);
        CommonUtils.waitingFor = null;
        CommonUtils.onStartingGame = false;
        CommonUtils.disableTimer(CommonUtils.startingGameTimer);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGameStarting);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }



}
