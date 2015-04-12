package com.iplay.concatenate;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.LoginButton;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LeaderboardActivity extends Activity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaderboard);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        final Session session = Session.getActiveSession();
//        askForPublishActionsForScores();
        Bundle bd = getIntent().getExtras();
        String userId = bd.getString("userId");
        GraphObject go = new GraphObject() {
            String score;
            JSONObject jobj = new JSONObject();
            @Override
            public <T extends GraphObject> T cast(Class<T> tClass) {
                return null;
            }

            @Override
            public Map<String, Object> asMap() {
                Map<String, Object> ma = new TreeMap<>();
                ma.put("score", 123);
                return ma;
            }

            @Override
            public JSONObject getInnerJSONObject() {
                JSONObject jobj = new JSONObject();
                try {
                    jobj.put("score", 123);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Object getProperty(String s) {
                return null;
            }

            @Override
            public <T extends GraphObject> T getPropertyAs(String s, Class<T> tClass) {
                return null;
            }

            @Override
            public <T extends GraphObject> GraphObjectList<T> getPropertyAsList(String s, Class<T> tClass) {
                return null;
            }

            @Override
            public void setProperty(String s, Object o) {

            }

            @Override
            public void removeProperty(String s) {

            }
        };
        //askForPublishActionsForScores();
        Request postScore = Request.newPostRequest(session, userId + "/scores", go, new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                if (response.getError() != null) {
                    Log.e("Facebook", response.getError().toString());
                }

            }
        });
        Request.executeBatchAsync(postScore);
        Request scoresGraphPathRequest = Request.newGraphPathRequest(session,
                session.getApplicationId()+"/scores",
                new Request.Callback() {

                    @Override
                    public void onCompleted(Response response) {
                        FacebookRequestError error = response.getError();
                        System.out.println(""+session.getApplicationId());
                        System.out.println(""+R.string.facebook_app_id);

                        if (error != null) {
                            Log.e("Error", error.toString());

                            // TODO: Show an error or handle it better.
                            //((ScoreboardActivity)getActivity()).handleError(error, false);
                        } else if (session == Session.getActiveSession()) {
                            if (response != null) {
                                GraphObject graphObject = response.getGraphObject();
                                JSONArray dataArray = (JSONArray)graphObject.getProperty("data");

                                final ArrayList<Integer> scoreboardEntriesList = new ArrayList<Integer>();
                                System.out.println("Number of players: "+dataArray.length());
                                for (int i=0; i< dataArray.length(); i++) {
                                    JSONObject oneData = dataArray.optJSONObject(i);
                                    int score = oneData.optInt("score");

                                    JSONObject userObj = oneData.optJSONObject("user");
                                    String userID = userObj.optString("id");
                                    String userName = userObj.optString("name");

                                    scoreboardEntriesList.add(new Integer(score));

                                    System.out.println(userName+" "+score);
                                }


                                Comparator<Integer> comparator = Collections.reverseOrder();
                                Collections.sort(scoreboardEntriesList);

                                // Populate the scoreboard on the UI thread
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ListView friendList = (ListView)findViewById(R.id.friendsView);
                                        for(int i= 0; i < scoreboardEntriesList.size(); i++){

                                        }
                                    }
                                });
                            }
                        }
                    }


                });
        Request.executeBatchAsync(scoresGraphPathRequest);
    }
    private void requestPublishPermissions() {

        LoginButton lb = (LoginButton) CommonUtils.getLoginButton();
        lb.clearPermissions();
        lb.setPublishPermissions("publish_Actions");

    }
    private void askForPublishActionsForScores() {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    private DialogInterface dialog;
                    private int id;

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        this.dialog = dialog;
                        this.id = id;
                        // User hit OK. Request Facebook friends permission.
                        requestPublishPermissions();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User hit cancel.
                        // Hide the gameOverContainer
                    }
                })
                .setTitle(R.string.publish_scores_dialog_title)
                .setMessage(R.string.publish_scores_dialog_message)
                .show();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
