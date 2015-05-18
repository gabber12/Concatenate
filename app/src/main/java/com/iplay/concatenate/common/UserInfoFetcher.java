package com.iplay.concatenate.common;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;
import com.facebook.model.GraphObject;
import com.iplay.concatenate.DataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by gabber12 on 17/05/15.
 */
public class  UserInfoFetcher  implements DataListener{

    public int count;
    public Context ctx;
    public UserInfoFetcher(Context ctx) {
        this.ctx = ctx;
        count = 0;
    }
    public void fetchName() {
        Request req = Request.newGraphPathRequest(Session.getActiveSession(), CommonUtils.userId, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                GraphObject obj = response.getGraphObject();
                JSONObject jobj = obj.getInnerJSONObject();
                try {
                    CommonUtils.name = (String)jobj.get("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataSetAvailable();
            }
        });
        req.executeAsync();
    }
    private static ImageRequest lastRequest = null;
    public void fetchProfilePic() {
        try {
            ImageRequest.Builder requestBuilder = new ImageRequest.Builder(
                    ctx
                    ,
                    ImageRequest.getProfilePictureUrl(CommonUtils.userId, 800, 800));


            ImageRequest request = requestBuilder.setAllowCachedRedirects(false)
                    .setCallerTag(this)
                    .setCallback(
                            new ImageRequest.Callback() {
                                @Override
                                public void onCompleted(ImageResponse response) {
                                    CommonUtils.imageResponse = response;
                                    dataSetAvailable();

                                }
                            }
                    )
                    .build();

            // Make sure to cancel the old request before sending the new one to prevent
            // accidental cancellation of the new request. This could happen if the URL and
            // caller tag stayed the same.
            if (lastRequest != null) {
                ImageDownloader.cancelRequest(lastRequest);
            }
            lastRequest = request;

            ImageDownloader.downloadAsync(request);
        } catch (URISyntaxException e) {
            Log.e("Error", "Problem With downloading profile_pic");
        }
    }
    public void fetchUserInfo() {

         if(CommonUtils.userId == null) {
             Log.e("Error", "User Id is null");
             return ;
         }
        CommonUtils.fetchFriendScore(this, true);
        fetchName();
        fetchProfilePic();
    }


    @Override
    public void dataSetAvailable() {
        Log.e("Info", "Data set Available");
        if(count >= 3) {
            Log.e("Info", "Data set Available INN");
            Intent intent = new Intent("data_loaded");

            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
        }
        count++;
    }
}