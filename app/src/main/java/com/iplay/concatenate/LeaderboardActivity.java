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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LeaderboardActivity extends Fragment {
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

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    public List<FriendModel> friends;
    View myFragmentView;
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         myFragmentView = inflater.inflate(R.layout.activity_leaderboard, container, false);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        final Session session = Session.getActiveSession();
//        askForPublishActionsForScores();
        friends = new ArrayList<FriendModel>();
        String userId = CommonUtils.userId;
        GraphObject go = new GraphObject() {
            String score;
            JSONObject jobj = new JSONObject();
            @Override
            public <T extends GraphObject> T cast(Class<T> tClass) {
                return null;
            }

            @Override
            public Map<String, Object> asMap() {
                Map<String, Object> ma = new TreeMap<String, Object>();
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

                                    friends.add(new FriendModel(userName, userID, score));
                                    System.out.println(userName+" "+score);
                                }



                                // Populate the scoreboard on the UI thread
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ListView friendList = (ListView)myFragmentView.findViewById(R.id.friendsView);

                                        FriendListAdapter adapter = new FriendListAdapter(getActivity().getApplicationContext(), R.layout.friendlistlayout_leaderboard, friends);
                                        friendList.setAdapter(adapter);
                                    }
                                });
                            }
                        }
                    }


                });
        Request.executeBatchAsync(scoresGraphPathRequest);
        return myFragmentView;
    }

}
