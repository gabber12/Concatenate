package com.iplay.concatenate;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import carbon.widget.ImageActionButton;
import carbon.widget.RelativeLayout;
import carbon.widget.TransitionLayout;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class HomeActivity extends FragmentActivity {
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

    public Boolean trans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        trans = true;
        getSupportFragmentManager().beginTransaction().add(R.id.container, new LeaderboardActivity()).commit();

        final TransitionLayout transitionView = (TransitionLayout) findViewById(R.id.transition);
        findViewById(R.id.leaderboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                transitionView.setHotspot(v);
                transitionView.startTransition(TransitionLayout.TransitionType.Radial,trans, 200);
                if (trans) {
                    HomeActivity.this.findViewById(R.id.friendsView).setVisibility(View.VISIBLE);
                    ((RelativeLayout)HomeActivity.this.findViewById(R.id.home)).setElevation(new Float(10));
                    ((FrameLayout)HomeActivity.this.findViewById(R.id.container)).setElevation(new Float(20));
                }
                else {
                    HomeActivity.this.findViewById(R.id.friendsView).setVisibility(View.GONE);
                    ((RelativeLayout)HomeActivity.this.findViewById(R.id.home)).setElevation(new Float(20));
                    ((FrameLayout)HomeActivity.this.findViewById(R.id.container)).setElevation(new Float(10));
                }

                trans = !trans;
            }
        });

        View.OnTouchListener genListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println(event.getX()+" " +event.getY());
                System.out.println(v.getX()+" " +v.getY());
                System.out.println(v.getWidth()+" " +v.getHeight());
                //CIRCLE :      (x-a)^2 + (y-b)^2 = r^2
                float centerX, centerY, touchX, touchY, radius;
                centerX = v.getWidth()/2;
                centerY = v.getHeight()/2;
                System.out.println(centerX+" " +centerY);

                touchX = event.getX();
                touchY = event.getY();
                radius = centerX;
                System.out.println("centerX = "+centerX+", centerY = "+centerY);
                System.out.println("touchX = "+touchX+", touchY = "+touchY);
                System.out.println("radius = "+radius);
                if (Math.pow(touchX - centerX, 2)
                        + Math.pow(touchY - centerY, 2) < Math.pow(radius, 2)) {
                    System.out.println("Inside Circle");
                    return false;
                } else {
                    System.out.println("Outside Circle");
                    return true;
                }
            }
        };

        ImageActionButton leaderboardButton = (ImageActionButton) findViewById(R.id.leaderboard);
        ImageActionButton hostButton = (ImageActionButton) findViewById(R.id.host_game);
        ImageActionButton joinButton = (ImageActionButton) findViewById(R.id.join_game);
        ImageActionButton quickButton = (ImageActionButton) findViewById(R.id.quick_game);

        hostButton.setOnTouchListener(genListener);
        quickButton.setOnTouchListener(genListener);
        joinButton.setOnTouchListener(genListener);
        leaderboardButton.setOnTouchListener(genListener);

        CircularProfilePicView profile_pic = ((CircularProfilePicView)findViewById(R.id.profile_pic));
        profile_pic.setProfileId(CommonUtils.userId);

//        leaderboardButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(getApplicationContext(), LeaderboardActivity.class);
//                startActivity(in);
//                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
//
//            }
//        });
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(in);
            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), JoinGameAcitvity.class);
                startActivity(in);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

        hostButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), InviteFriends.class);
                startActivity(in);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });
		
		findViewById(R.id.quick_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), QuickGameActivity.class);
                startActivity(in);

            }
        });
    }



    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    public class ResizeAnimation extends Animation {
        final int startWidth;
        final int startHeight;
        final int targetWidth;
        final int targetHeight;
        View view;

        public ResizeAnimation(View view, int targetWidth, int targetHeight) {
            this.view = view;
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            startWidth = view.getWidth();
            startHeight = view.getHeight();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = (int) (startWidth + (targetWidth - startWidth) * interpolatedTime);
            int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
            view.getLayoutParams().width = newWidth;
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}
