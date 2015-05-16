package com.iplay.concatenate;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import carbon.widget.ImageActionButton;
import carbon.widget.RelativeLayout;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;



public class HomeActivity extends FragmentActivity {


    public Boolean trans;
    public Boolean trans1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        trans1 = true;
        trans = true;
        getSupportFragmentManager().beginTransaction().add(R.id.container_profile, new ProfileActivity()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, new LeaderboardActivity()).commit();


        final RevealFrameLayout transitionView = (RevealFrameLayout) findViewById(R.id.transition);
        final RevealFrameLayout transitionView1 = (RevealFrameLayout) findViewById(R.id.transition1);
        transitionView.setVisibility(View.INVISIBLE);
        transitionView1.setVisibility(View.INVISIBLE);
        final RevealFrameLayout myView = transitionView;
        final RevealFrameLayout myView1 = transitionView1;
        final ImageActionButton iab = (ImageActionButton)findViewById(R.id.leaderboard);


        final CircularProfilePicView iab1 = (CircularProfilePicView)findViewById(R.id.profile_pic_user);



        findViewById(R.id.leaderboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int cx = (iab.getLeft() + iab.getRight()) / 2;
                final int cy = (iab.getTop() + iab.getBottom()) / 2;


                // get the final radius for the clipping circle
                final int finalRadius = (int)Math.sqrt(myView.getWidth()*myView.getWidth()+myView.getHeight()*myView.getHeight()); //Math.max(myView.getWidth(), myView.getHeight());


                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(myView, cx, cy, finalRadius, 0);





                if (trans) {
                    myView.setVisibility(View.VISIBLE);

                    animator=
                            ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

                    AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);

                    anim.setDuration(500);
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            ((CircularProfilePicView)findViewById(R.id.profile_pic_user)).setClickable(false);

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            ((CircularProfilePicView)findViewById(R.id.profile_pic_user)).setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    ((CircularProfilePicView)findViewById(R.id.profile_pic_user)).setAnimation(anim);
                    anim.start();
                } else {

                    AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setStartOffset(300);
                    anim.setDuration(500);
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            ((CircularProfilePicView)findViewById(R.id.profile_pic_user)).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            ((CircularProfilePicView)findViewById(R.id.profile_pic_user)).setClickable(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    ((CircularProfilePicView)findViewById(R.id.profile_pic_user)).setAnimation(anim);
                    anim.start();
                    ((CircularProfilePicView)findViewById(R.id.profile_pic_user)).setVisibility(View.VISIBLE);

                    animator =
                            ViewAnimationUtils.createCircularReveal(myView, cx, cy, finalRadius, 0);

                }



                animator.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                        iab.setClickable(false);
                    }

                    @Override
                    public void onAnimationEnd() {
                        if(!trans) {
                            myView.setVisibility(View.INVISIBLE);


                        }
                        iab.setClickable(true);

                        trans = !trans;
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
        });


        findViewById(R.id.profile_pic_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int cx1 = (iab1.getLeft() + iab1.getRight()) / 2;
                final int cy1 = (iab1.getTop() + iab1.getBottom()) / 2;
                // get the final radius for the clipping circle
                final int finalRadius = (int)Math.sqrt(myView1.getWidth()*myView1.getWidth()+myView1.getHeight()*myView1.getHeight());

                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(myView1, cx1, cy1, finalRadius, 0);


                if (trans1) {
                    myView1.setVisibility(View.VISIBLE);

                    animator=
                            ViewAnimationUtils.createCircularReveal(myView1, cx1, cy1, 0, finalRadius);
                    AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                    anim.setDuration(500);
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            ((ImageActionButton)findViewById(R.id.leaderboard)).setClickable(false);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            ((ImageActionButton)findViewById(R.id.leaderboard)).setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    ((ImageActionButton)findViewById(R.id.leaderboard)).setAnimation(anim);
                    anim.start();
                } else {

                    AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setStartOffset(300);
                    anim.setDuration(500);
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            ((ImageActionButton)findViewById(R.id.leaderboard)).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            ((ImageActionButton)findViewById(R.id.leaderboard)).setClickable(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    ((ImageActionButton)findViewById(R.id.leaderboard)).setAnimation(anim);
                    anim.start();
                    ((ImageActionButton)findViewById(R.id.leaderboard)).setVisibility(View.VISIBLE);

                    animator =
                            ViewAnimationUtils.createCircularReveal(myView1, cx1, cy1, finalRadius, 0);

                }



                animator.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                        iab1.setClickable(false);


                    }

                    @Override
                    public void onAnimationEnd() {
                        if(!trans1) {
                            myView1.setVisibility(View.INVISIBLE);

                        } else {

                        }
                        iab1.setClickable(true);
                        trans1 = !trans1;
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

//        leaderboardButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(getApplicationContext(), LeaderboardActivity.class);
//                startActivity(in);
//                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
//
//            }
//        });

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
                Intent in = new Intent(getApplicationContext(), NewQuickGame.class);
                startActivity(in);

            }
        });
        Drawable RippleDraw =
                hostButton.getDrawable());
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
}
