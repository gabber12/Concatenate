package com.iplay.concatenate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.support.CircularProfilePicView;
import com.iplay.concatenate.support.NetworkActivity;
import com.iplay.concatenate.support.ORTCUtil;

import org.json.simple.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class NewQuickGame extends NetworkActivity {

    private TextSwitcher mSwitcher;

    private int allAvailable = 0;
    private BroadcastReceiver mDetailsFetched = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String userid = intent.getStringExtra("userid");
            String username = intent.getStringExtra("username");
            int score = intent.getIntExtra("score", 0);
            System.out.println(userid + " " + username);
            if (userid.equals(CommonUtils.waitingFor)) {
                TextView yournameTextView = ((TextView) findViewById(R.id.yourname));
                TextView yourlevelTextView = ((TextView) findViewById(R.id.yourlevel));

                yournameTextView.setText(username);
                yourlevelTextView.setText(String.valueOf(score) + " XP");
                allAvailable++;
                animateNameAndLevel();
            }
        }
    };
    private String against = "";
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((CircularProfilePicView) findViewById(R.id.yourpicfb)).setVisibility(View.VISIBLE);
            ((CircularProfilePicView) findViewById(R.id.yourpicfb)).setProfileId(against);


            Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(1000);
            Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(1000);
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    findViewById(R.id.yourpicimage).setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            ((CircularProfilePicView) findViewById(R.id.yourpicfb)).startAnimation(fadeIn);
            findViewById(R.id.yourpicimage).startAnimation(fadeOut);

        }
    };
    private BroadcastReceiver mGameStarting = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            System.out.println("hello!");
            String sender_id = intent.getStringExtra("sender_id");
            String sender_name = intent.getStringExtra("sender_name");
            int sender_score = intent.getIntExtra("sender_score", 0);
            onGameStarted(sender_id, sender_name, sender_score, intent.getBooleanExtra("is_bot", false));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allAvailable = 0;

        setContentView(R.layout.activity_new_quick_game);
        CommonUtils.onQuickGame = true;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("pic_loaded"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mGameStarting, new IntentFilter("starting_game"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDetailsFetched, new IntentFilter("details_fetched"));

        setupScreen();

    }

    private void setupScreen() {

        TextView mynameTextView = ((TextView) findViewById(R.id.myname));
        TextView mylevelTextView = ((TextView) findViewById(R.id.mylevel));

        mynameTextView.setText(CommonUtils.name);
        mylevelTextView.setText(String.valueOf(CommonUtils.score) + " XP");
        ((CircularProfilePicView) findViewById(R.id.mypic)).setProfileId(CommonUtils.userId);

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
                myText.setTypeface(CommonUtils.FreightSansFont);
                return myText;
            }
        });

        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setStartOffset(500);

        mSwitcher.setInAnimation(fadeIn);
        mSwitcher.setOutAnimation(fadeOut);

        mSwitcher.setText("SEARCHING OPPONENT");

        ((TextView) findViewById(R.id.textBox)).setTypeface(CommonUtils.FreightSansFont);


        // add progress bar too.
        JSONObject sendjsonObject = new JSONObject();
        sendjsonObject.put("id", CommonUtils.userId);
        sendjsonObject.put("name", CommonUtils.name);
        sendjsonObject.put("score", CommonUtils.score);
        final String sendJSONString = sendjsonObject.toString();
        System.out.println(sendjsonObject.toString());


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    if (ORTCUtil.getClient().getIsConnected()) {
                        new BackgroundURLRequest().execute("add_me_to_wait_pool/", sendJSONString);
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("error in check connected thread: " + e);
                    }
                }
//        new BackgroundURLRequest().execute("add_me_to_wait_pool/", CommonUtils.userId);


                CommonUtils.quickGameTimer = new Timer();
                CommonUtils.quickGameTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        CommonUtils.taskThread = Thread.currentThread();
                        NewQuickGame.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Sending bot request");
                                new BackgroundURLRequest().execute("give_me_bot/", CommonUtils.userId);
                                CommonUtils.disableTimer(CommonUtils.quickGameTimer);
                            }
                        });
                    }
                }, 22000);

            }
        });
        thread.start();

    }

    private void onGameStarted(final String senderId, final String senderName, final int senderScore, final boolean isBot) {

        against = senderId;

        TextView yournameTextView = ((TextView) findViewById(R.id.yourname));
        TextView yourlevelTextView = ((TextView) findViewById(R.id.yourlevel));

        yournameTextView.setText(senderName);
        yourlevelTextView.setText(senderScore + " XP");
        CommonUtils.againstUserName = senderName;
        CommonUtils.againstUserScore = senderScore;

        CommonUtils.onQuickGame = false;
        CommonUtils.onStartingGame = true;
        CommonUtils.waitingFor = senderId;
        CommonUtils.getPic(CommonUtils.waitingFor, getApplicationContext());
        findViewById(R.id.progress_wheel_quick).setVisibility(View.GONE);

        mSwitcher.clearAnimation();
        Animation fadeOutCustom = new AlphaAnimation(mSwitcher.getAlpha(), 0);
        fadeOutCustom.setDuration((long) (mSwitcher.getAlpha() * 500));
        mSwitcher.setOutAnimation(fadeOutCustom);
        mSwitcher.setText("CONCATY !");

        long duration = 2000;
        long offset = 1200;
        Animation zoomOut = new ScaleAnimation(1.0f, 1.25f, 1.0f, 1.25f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation zoomIn = new ScaleAnimation(1.0f, 0.83333f, 1.0f, 0.83333f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomIn.setDuration(duration);
        zoomOut.setDuration(duration);
        zoomIn.setStartOffset(offset);
        zoomOut.setStartOffset(offset);
        zoomIn.setFillEnabled(true);
        zoomOut.setFillEnabled(true);
        zoomIn.setFillAfter(true);
        zoomOut.setFillAfter(true);

        Interpolator bounceInterpolator = new BounceInterpolator();
        zoomIn.setInterpolator(bounceInterpolator);
        zoomOut.setInterpolator(bounceInterpolator);

        Animation moveUp = new TranslateAnimation(0, 0, 0, -30);
        moveUp.setFillAfter(true);
        moveUp.setFillAfter(true);
        moveUp.setDuration(800);
        moveUp.setStartOffset(offset);

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

                        allAvailable++;
                        animateNameAndLevel();

                    }
                });
            }
        };
        thread.start();

//                final String senderId = getIntent().getStringExtra("sender_id");
//                final Boolean isBot = getIntent().getBooleanExtra("is_bot", false);


        if (isBot) {

            JSONObject sendjsonObject = new JSONObject();
            sendjsonObject.put("fromUser", CommonUtils.userId);
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
                CommonUtils.taskThread = Thread.currentThread();
                NewQuickGame.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (CommonUtils.startGameIntent != null && System.currentTimeMillis() - CommonUtils.startGameIntent.getLongExtra("timestamp", System.currentTimeMillis()) <= 10 * 1000
                                && System.currentTimeMillis() - CommonUtils.startGameIntent.getLongExtra("timestamp", System.currentTimeMillis()) >= 5 * 1000) {
                            CommonUtils.disableTimer(CommonUtils.startingGameTimer);
                            startActivity(CommonUtils.startGameIntent);
                            overridePendingTransition(R.anim.trans_fade_in, R.anim.trans_fade_out);
                        } else if (System.currentTimeMillis() - startTime >= 20 * 1000) {
                            Toast t = Toast.makeText(getApplicationContext(), "Opponent has left :(", Toast.LENGTH_LONG);
                            t.show();
                            final Intent intent = new Intent(NewQuickGame.this, HomeActivity.class);
                            CommonUtils.waitingFor = null;
                            CommonUtils.disableTimer(CommonUtils.startingGameTimer);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_fade_in, R.anim.trans_fade_out);
                        }
                    }
                });

            }
        }, new Long(0), new Long(1000));


    }

    private void animateNameAndLevel() {

        System.out.println("Calling animate and level");

        // this check is not required. Just for extension.
        if (allAvailable != 1) return;

        Interpolator decelerate = new DecelerateInterpolator();
        Interpolator accelerate = new AccelerateInterpolator();
        Animation compressIn = new ScaleAnimation(1.0f, 0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation compressOut = new ScaleAnimation(0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        compressIn.setDuration(500);
        compressIn.setInterpolator(decelerate);
        compressOut.setDuration(500);
        compressOut.setInterpolator(accelerate);
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
        moveLeft.setDuration(500);
        moveRight.setDuration(500);
        findViewById(R.id.barLeftrel).startAnimation(moveLeft);
        findViewById(R.id.barRightrel).startAnimation(moveRight);

        findViewById(R.id.yourinfolayout).startAnimation(moveLeft);
        findViewById(R.id.myinfolayout).startAnimation(moveRight);

    }

    @Override
    public void onBackPressed() {

        if (CommonUtils.onStartingGame) {
            new MaterialDialog.Builder(this)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            closeTheState();
                            Intent in = new Intent(NewQuickGame.this, HomeActivity.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.trans_fade_in, R.anim.trans_fade_out);
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
            closeTheState();
            Intent in = new Intent(this, HomeActivity.class);
            startActivity(in);
            overridePendingTransition(R.anim.trans_fade_in, R.anim.trans_fade_out);
        }
    }

    @Override
    protected void onDestroy() {
        new BackgroundURLRequest().execute("remove_me_from_pool/", CommonUtils.userId);
        closeTheState();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGameStarting);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDetailsFetched);
        super.onDestroy();
    }

    private void closeTheState() {
        CommonUtils.onQuickGame = false;
        CommonUtils.disableTimer(CommonUtils.quickGameTimer);
        CommonUtils.waitingFor = null;
        CommonUtils.onStartingGame = false;
        CommonUtils.disableTimer(CommonUtils.startingGameTimer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


}
