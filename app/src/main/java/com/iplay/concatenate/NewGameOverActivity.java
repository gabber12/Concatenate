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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;

import org.json.simple.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class NewGameOverActivity extends NetworkActivity {

    private int allAvailable;
    private String surrender = null;
    TextSwitcher mSwitcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        allAvailable = 0;
        super.onCreate(savedInstanceState);

        CommonUtils.onGameOver = true;

        setContentView(R.layout.activity_new_game_over);

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceivedGameOver, new IntentFilter("gameover_received"));

        mSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        mSwitcher.startAnimation(anim);

//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(1000); //You can manage the blinking time with this parameter
//        anim.setStartOffset(20);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        mSwitcher.startAnimation(anim);

        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                // create new textView and set the properties like color, size etc
                TextView myText = new TextView(NewGameOverActivity.this);
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

        boolean fromServer = getIntent().getBooleanExtra("server_request", false);
        if ( fromServer ) {
            surrender = getIntent().getStringExtra("surrender");
            animateShowScore();
        }

        ((CircularProfilePicView)findViewById(R.id.mypicfb)).setProfileId(CommonUtils.userId);
        ((CircularProfilePicView)findViewById(R.id.yourpicfb)).setProfileId(CommonUtils.againstId);

        mSwitcher.setText("FETCHING SCORE...");



        Timer gameOverTimer = new Timer();
        gameOverTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                CommonUtils.taskThread = Thread.currentThread();
                NewGameOverActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animateShowScore();
                    }
                });
            }
        }, 3000);

    }

    private void animateShowScore() {
        allAvailable++;
        if ( allAvailable == 2 ) {

            int myScore = MainGameActivity.currentMyScore;
            int yourScore = MainGameActivity.currentYourScore;

            float accuracy = getAccuracy(MainGameActivity.myAttempts, MainGameActivity.myRightAttempts);
            float avgTime = getAvgTime(MainGameActivity.myTotalTime, Math.min(MainGameActivity.myMoves, MainGameActivity.MAX_MOVES));

            mSwitcher.clearAnimation();
            Animation fadeOutCustom = new AlphaAnimation(mSwitcher.getAlpha(),0);
            fadeOutCustom.setDuration((long)(mSwitcher.getAlpha()*500));
            mSwitcher.setOutAnimation(fadeOutCustom);
            if ( surrender != null ) {
                if ( surrender.equals(CommonUtils.userId) ) {
                    mSwitcher.setText("YOU SURRENDERED");
                    myScore = 0;
                }
                else {
                    mSwitcher.setText("OPPONENT SURRENDERED");
                    yourScore = 0;
                }
            } else {
                if (myScore > yourScore) mSwitcher.setText("YOU WIN");
                else if (myScore < yourScore) mSwitcher.setText("YOU LOSE");
                else mSwitcher.setText("ITS A DRAW");
            }

            CommonUtils.setScore(myScore, getApplicationContext());
            // set score for bot also here
            JSONObject sendjsonObject = new JSONObject();
            sendjsonObject.put("id", CommonUtils.againstId);
            sendjsonObject.put("score", yourScore);
            new BackgroundURLRequest().execute("update_score_for_bot/", sendjsonObject.toString());


            ((TextView)(findViewById(R.id.myname))).setText(CommonUtils.name);
            ((TextView)(findViewById(R.id.mylevel))).setText("Score - " + myScore);
            ((TextView)(findViewById(R.id.myaccuracy))).setText("Accuracy: " + String.format("%.2f",accuracy) + "%");
            ((TextView)(findViewById(R.id.myavgtime))).setText("Avg Time: " + String.format("%.2f",avgTime) + "s");

            ((TextView)(findViewById(R.id.yourname))).setText(CommonUtils.againstUserName);
            ((TextView)(findViewById(R.id.yourlevel))).setText("Score - " + yourScore);

            findViewById(R.id.yourinfolayout).setVisibility(View.VISIBLE);
            findViewById(R.id.myinfolayout).setVisibility(View.VISIBLE);

            Animation moveLeft = new TranslateAnimation(-600, 0, 0, 0);
            Animation moveRight = new TranslateAnimation(600, 0, 0, 0);
            moveLeft.setDuration(500); moveRight.setDuration(500);

            findViewById(R.id.yourinfolayout).startAnimation(moveRight);
            findViewById(R.id.myinfolayout).startAnimation(moveLeft);

        }
    }

    float getAccuracy(int total, float right) {
        if ( total == 0 ) return 0.0f;
        return (float)((right/total)*100.0);
    }

    float getAvgTime(float total, int attempts) {
        if ( attempts == 0 ) return 0.0f;
        return (float)((total/attempts)/1000.0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private BroadcastReceiver mReceivedGameOver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ( !intent.getStringExtra("surrender").equals("") )
                surrender = intent.getStringExtra("surrender");
            animateShowScore();
        }
    };

    @Override
    public void onBackPressed() {
        CommonUtils.onGameOver = false;
        Intent in = new Intent(this, HomeActivity.class);
        startActivity(in);
    }

}
