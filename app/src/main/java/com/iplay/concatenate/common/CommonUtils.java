package com.iplay.concatenate.common;

import android.util.Log;
import android.util.Pair;

import com.facebook.FacebookGraphObjectException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;
import com.iplay.concatenate.FriendModel;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;

/**
 * Created by divanshu on 06/04/15.
 */
public class CommonUtils {

//    52.6.17.252
    public final static String SERVER_BASE = "http://ec2-52-6-17-252.compute-1.amazonaws.com/";

    public static String userId = null;
	public static String waitingFor = null;
    public static Timer startingGameTimer = null;
    public static Timer hostGameTimer = null;
    public static Timer mainGameTimer = null;
    public static Timer quickGameTimer = null;
    public static String name;
    public static int score;
    public static List<FriendModel> friendArrayList;
    public static String getChannelNameFromUserID(String id) {
        return "user_channel_" + id;
    }

    public static LoginButton loginButton = null;
    public static LoginButton getLoginButton() { if (loginButton == null) throw new FacebookGraphObjectException(); return loginButton; }
    public static void setLoginButton(LoginButton lb) { loginButton = lb; }

    public static Set<String> words = null;
    public static void fetchScore() {
        if(userId == null) {
            Log.e("Error", "userId null, cant fetch score");
            return ;
        }
        final Session session = Session.getActiveSession();
        Request scoresGraphPathRequest = Request.newGraphPathRequest(session,
                session.getApplicationId()+"/scores",
                new Request.Callback() {

                    @Override
                    public void onCompleted(Response response) {
                        FacebookRequestError error = response.getError();

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
                                    int sc = oneData.optInt("score");

                                    JSONObject userObj = oneData.optJSONObject("user");
                                    String userID = userObj.optString("id");
                                    String userName = userObj.optString("name");

                                    scoreboardEntriesList.add(new Integer(score));
                                    score = sc;
                                }
                            }
                        }
                    }
                });
        Request.executeBatchAsync(scoresGraphPathRequest);

    }
}
