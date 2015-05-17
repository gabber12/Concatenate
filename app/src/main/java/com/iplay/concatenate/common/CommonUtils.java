package com.iplay.concatenate.common;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;

import com.facebook.FacebookGraphObjectException;
import com.facebook.FacebookRequestError;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;
import com.facebook.internal.Logger;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.widget.LoginButton;
import com.iplay.concatenate.DataListener;
import com.iplay.concatenate.FriendListAdapter;
import com.iplay.concatenate.FriendModel;
import com.iplay.concatenate.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TreeMap;

/**
 * Created by divanshu on 06/04/15.
 */
public class CommonUtils {
    public static ImageResponse imageResponse;
//    concaty.tk
    public final static String SERVER_BASE = "http://concaty.tk/";

    public static String userId = null;
	public static String waitingFor = null;

    public static boolean onQuickGame = false;
    public static boolean onStartingGame = false;
    public static boolean onHostGame = false;


    public static Timer startingGameTimer = null;
    public static Timer hostGameTimer = null;
    public static Timer mainGameTimer = null;
    public static Timer quickGameTimer = null;

    public static Intent startGameIntent = null;

    public static String name;
    public static int score;
    public static Map<String, FriendModel> friendsMap = null;
    public static String getChannelNameFromUserID(String id) {
        return "user_channel_" + id;
    }

    public static LoginButton loginButton = null;
    public static LoginButton getLoginButton() { if (loginButton == null) throw new FacebookGraphObjectException(); return loginButton; }
    public static void setLoginButton(LoginButton lb) { loginButton = lb; }

    public static void disableTimer(Timer t) {
        if ( t != null ) {
            t.cancel();
            t.purge();
            t = null;
        }
    }

    public static Set<String> words = null;
    private static List<DataListener> subscribers;
    static {
        subscribers = new ArrayList<DataListener>();
    }
    public static void addAsSubscriber(DataListener dl) {

        subscribers.add(dl);
    }
    public static void fetchFriendScore(final DataListener sub) {

            if (friendsMap == null) {
            friendsMap = new TreeMap<String, FriendModel>();
        } else {
            return ;
        }

        final Session session = Session.getActiveSession();
        Request scoresGraphPathRequest = Request.newGraphPathRequest(session,
                session.getApplicationId()+"/scores",
                new Request.Callback() {

                    @Override
                    public void onCompleted(Response response) {
                        FacebookRequestError error = response.getError();
                        System.out.println(""+session.getApplicationId());
                        System.out.println(""+ R.string.facebook_app_id);

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

                                    friendsMap.put(userID,   new FriendModel(userName, userID, score));
                                }
                            }
                        }
                        score = friendsMap.get(userId).getScore();
                        for(DataListener dl : subscribers){
                            if(dl != null)
                            dl.dataSetAvailable();
                        }


                        if (sub != null) {
                            sub.dataSetAvailable();
                        }
                    }


                });
        Request.executeBatchAsync(scoresGraphPathRequest);
    }

    public static FriendModel getFriendById(String id) {
        if (friendsMap == null) {
            Log.e("Error", "Friend List not initialized");
            return null;
        }
        return friendsMap.get(id);
    }
    public static void setScore(int score) {
        CommonUtils.score = score+CommonUtils.score;
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
                    jobj.put("score", CommonUtils.score);
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
        final  Session session = Session.getActiveSession();
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
    }
    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

}
