package com.iplay.concatenate;

import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class GameOverActivity extends NetworkActivity {

    private GameOverActivity that;

    private TextView winOrLose, finalScore;
    private Button playAnother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        that = this;
        setContentView(R.layout.activity_game_over);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.


        winOrLose = (TextView) findViewById(R.id.win_or_lose);
        playAnother = (Button) findViewById(R.id.play_another);
        finalScore = (TextView) findViewById(R.id.final_score);

        int myScore = getIntent().getIntExtra("my_score", 0);
        int yourScore = getIntent().getIntExtra("your_score", 0);

        if ( myScore > yourScore ) winOrLose.setText("You win :D");
        else if ( myScore < yourScore ) winOrLose.setText("You lose :(");
        else winOrLose.setText("Its a draw :|");

        finalScore.setText("Your score: " + String.valueOf(myScore));

        playAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(that, HomeActivity.class);
                that.startActivity(intent);
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }



    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, HomeActivity.class);
        startActivity(in);
    }

}
