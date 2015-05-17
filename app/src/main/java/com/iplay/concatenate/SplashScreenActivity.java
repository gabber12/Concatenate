package com.iplay.concatenate;

import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import ibt.ortc.extensibility.OrtcClient;


public class SplashScreenActivity extends NetworkActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);



        ImageView background_image = (ImageView) findViewById(R.id.splash_logo);
        Animation scale = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(5000);

        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // start the animation to fullscreen if not logged in otherwise just start your caching.
                animateToShowLogin();

//                Intent intent = new Intent(SplashScreenActivity.this, FullscreenActivity.class);
//                SplashScreenActivity.this.startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        background_image.startAnimation(scale);
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



}
