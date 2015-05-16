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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;



public class SplashScreenActivity extends NetworkActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);



        ImageView background_image = (ImageView) findViewById(R.id.splash_logo);
        Animation scale = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1000);

        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashScreenActivity.this, FullscreenActivity.class);
                SplashScreenActivity.this.startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        background_image.startAnimation(scale);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }



}
