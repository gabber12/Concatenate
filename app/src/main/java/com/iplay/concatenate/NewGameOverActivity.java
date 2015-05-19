package com.iplay.concatenate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.iplay.concatenate.common.CommonUtils;



public class NewGameOverActivity extends NetworkActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_game_over);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);


        TextSwitcher mSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);

//        Animation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(1000); //You can manage the blinking time with this parameter
//        anim.setStartOffset(20);
//        anim.setRepeatMode(Animation.REVERSE);
//        anim.setRepeatCount(Animation.INFINITE);
//        mSwitcher.startAnimation(anim);

        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like color, size etc
                TextView myText = new TextView(NewGameOverActivity.this);
                myText.setGravity(Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(20);
                myText.setTextColor(Color.WHITE);
                return myText;
            }
        });



        int myScore = MainGameActivity.currentMyScore;
        int yourScore = MainGameActivity.currentYourScore;

        float accuracy = getAccuracy(MainGameActivity.myAttempts, MainGameActivity.myRightAttempts);
        float avgTime = getAvgTime(MainGameActivity.myTotalTime, MainGameActivity.myRightAttempts);

        CommonUtils.setScore(myScore, getApplicationContext());

        if ( myScore > yourScore ) mSwitcher.setText("YOU WIN");
        else if ( myScore < yourScore ) mSwitcher.setText("YOU LOSE");
        else mSwitcher.setText("IT'S A DRAW");

        // TODO: Fill required data/game metrics above and below in two boxes
        // TODO: Add a button to go to home.

    }

    float getAccuracy(int total, float right) {
        if ( total == 0 ) return 0.0f;
        return right/total;
    }

    float getAvgTime(float total, int attempts) {
        if ( attempts == 0 ) return 0.0f;
        return total/attempts;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }



    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, HomeActivity.class);
        startActivity(in);
    }

}
