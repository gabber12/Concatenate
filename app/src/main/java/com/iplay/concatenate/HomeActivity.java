package com.iplay.concatenate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.iplay.concatenate.common.CommonUtils;

import carbon.widget.ImageActionButton;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;



public class HomeActivity extends FragmentActivity {


    public Boolean trans;
    public Boolean trans1;

    public void radialTransitionShow(final View fragment, final View viewButton , final View otherButton, final View container) {
        final int cx = (viewButton.getLeft() + viewButton.getRight()) / 2;
        final int cy = (viewButton.getTop() + viewButton.getBottom()) / 2;


        // get the final radius for the clipping circle
        final int finalRadius = (int)Math.sqrt(fragment.getWidth() * fragment.getWidth() + fragment.getHeight() * fragment.getHeight()); //Math.max(myView.getWidth(), myView.getHeight());

        fragment.setVisibility(View.VISIBLE);

        System.out.println(fragment.getParent());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(container,  cx, cy, 0, finalRadius);



        animator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {

                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(800);
                anim.setFillAfter(true);
                anim.setFillEnabled(true);
                otherButton.setClickable(false);
                otherButton.setAnimation(anim);
                otherButton.startAnimation(anim);

                viewButton.setClickable(false);
            }

            @Override
            public void onAnimationEnd() {
                viewButton.setClickable(true);
            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });



        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(800);
        animator.start();
    }

    public void radialTransitionHide(final View fragment, final View viewButton, final View otherButton, final View container) {
        final int cx = (viewButton.getLeft() + viewButton.getRight()) / 2;
        final int cy = (viewButton.getTop() + viewButton.getBottom()) / 2;

        // get the final radius for the clipping circle
        final int finalRadius = (int)Math.sqrt(fragment.getWidth() * fragment.getWidth() + fragment.getHeight() * fragment.getHeight()); //Math.max(myView.getWidth(), myView.getHeight());

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(container, cx, cy, finalRadius, 0);

        animator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
                viewButton.setClickable(false);

                AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setStartOffset(300);
                anim.setDuration(500);
                anim.setFillAfter(true);
                anim.setFillEnabled(true);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        otherButton.setClickable(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                otherButton.setAnimation(anim);
                otherButton.startAnimation(anim);


            }

            @Override
            public void onAnimationEnd() {

                fragment.setVisibility(View.INVISIBLE);
                viewButton.setClickable(true);


            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });



        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(800);
        animator.start();

    }
    RevealFrameLayout transitionView1;
    RevealFrameLayout transitionView;
    View transitionViewContainer;
    View transitionViewContainer1;

    ImageActionButton iab ;


    CircularProfilePicView iab1;
    MaterialDialog md = null;

    class ConnectivityReciever extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {

            Log.d("app", "Network connectivity change");
            if(!CommonUtils.isOnline(context) ){
                System.out.println("Network not Available");
                if(md == null)

                    md = new MaterialDialog.Builder(context)
                            .callback(new MaterialDialog.ButtonCallback() {

                            })
                            .title("No Internet Connection Available")
                            .titleGravity(GravityEnum.CENTER)
                            .content("Please Check your Internet Connection")
                            .progress(true, 1)
                            .theme(Theme.LIGHT).cancelable(false)
                            .show();
            } else if(md != null) {
                System.out.println("Network Available");
                md.dismiss();
                md = null;

            }

        }
    }
    ConnectivityReciever cr = null;
    @Override
    protected void onPause() {
        super.onPause();
        if(cr != null)
            super.unregisterReceiver(cr);
    }
    protected  void onResume() {
        super.onResume();
        super.registerReceiver(cr, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        cr = new ConnectivityReciever();

        ((TextView)findViewById(R.id.home_title)).setTypeface(CommonUtils.FreightSansFont);

        SharedPreferences settings = getSharedPreferences(CommonUtils.PREFS, 0);
        int score = settings.getInt("score", CommonUtils.score);
        if(score >= CommonUtils.score) {
            CommonUtils.setScore(score, getApplicationContext());
            CommonUtils.score = score;
        }

        trans1 = true;
        trans = true;
        getSupportFragmentManager().beginTransaction().add(R.id.container_profile, new ProfileActivity()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, new LeaderboardActivity()).commit();


        transitionView = (RevealFrameLayout) findViewById(R.id.transition);
        transitionViewContainer = findViewById(R.id.container);
        transitionView1 = (RevealFrameLayout) findViewById(R.id.transition1);
        transitionViewContainer1 = findViewById(R.id.container_profile);

        transitionView.setVisibility(View.INVISIBLE);
        transitionView1.setVisibility(View.INVISIBLE);

        iab = (ImageActionButton)findViewById(R.id.leaderboard);


        iab1 = (CircularProfilePicView)findViewById(R.id.profile_pic_user);



        findViewById(R.id.leaderboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trans) {
                    radialTransitionShow(transitionView, iab, iab1, transitionViewContainer);
                } else {
                    radialTransitionHide(transitionView, iab, iab1, transitionViewContainer);
                }
                trans = !trans;
            }
        });


        findViewById(R.id.profile_pic_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trans1) {
                    radialTransitionShow(transitionView1, iab1, iab, transitionViewContainer1);
                } else {
                    radialTransitionHide(transitionView1, iab1, iab, transitionViewContainer1);
                }
                trans1 = !trans1;
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

        CircularProfilePicView profile_pic = ((CircularProfilePicView)findViewById(R.id.profile_pic_user));
        profile_pic.setProfileId(CommonUtils.userId);
//        profile_pic.setProfileId("1403774539942585");

//        leaderboardButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(getApplicationContext(), LeaderboardActivity.class);
//                startActivity(in);
//                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
//
//            }
//        });


//        RippleDrawable rippleDrawable = ((ImageActionButton) findViewById(R.id.join_game)).getRippleDrawable();
//        rippleDrawable.setBounds(20,20,20,20);
//        rippleDrawable.setAlpha(0);



        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                RippleDrawable rippleDrawable = ((ImageActionButton) findViewById(R.id.join_game)).getRippleDrawable();
//                rippleDrawable.setBounds(20,20,20,20);
//                rippleDrawable.setAlpha(0);

                Intent in = new Intent(getApplicationContext(), JoinGameAcitvity.class);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                startActivity(in);
            }
        });

        hostButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), InviteFriends.class);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                startActivity(in);
            }
        });
		
		findViewById(R.id.quick_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), NewQuickGame.class);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                startActivity(in);

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
        if(!trans || !trans1) {
            if(!trans) {
                radialTransitionHide(transitionView, iab, iab1, transitionViewContainer);
                trans = !trans;
            } else {
                radialTransitionHide(transitionView1, iab1, iab, transitionViewContainer1);
                trans1 = !trans1;
            }

            return ;
        }
        new MaterialDialog.Builder(this)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        HomeActivity.this.moveTaskToBack(true);
                    }
                })
                .title("Quit Game")
                .titleGravity(GravityEnum.CENTER)
                .content("Do you really want to quit the game?")
                .positiveText("QUIT")
                .negativeText("PLAY ON")
                .theme(Theme.LIGHT)
                .negativeColorRes(R.color.material_deep_teal_500)
                .positiveColorRes(R.color.material_red_500)
                .show();

//        new AlertDialog.Builder(this)
//                .setTitle("Really Exit?")
//                .setMessage("Are you sure you want to exit?")
//                .setNegativeButton(android.R.string.no, null)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        HomeActivity.super.onBackPressed();
//                    }
//                }).create().show();
    }

    public void myfunc_leaderboard(View v){

//        Bundle params = new Bundle();
//        params.putString("message", "I challenge you for a Concaty showdown!");
//        params.putString("to",((CircularProfilePicView) v.findViewById(R.id.profile_pic)).getProfileId());
//        params.putInt("max_recipients", 1);
//        showDialogWithoutNotificationBar("apprequests", params);
//        System.out.print("Hello");
    }




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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(md != null) {
            md.cancel();
        }
    }
}
