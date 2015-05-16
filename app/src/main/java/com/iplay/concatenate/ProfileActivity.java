package com.iplay.concatenate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.iplay.concatenate.common.CommonUtils;

import org.json.JSONException;

import java.io.InputStream;


public class ProfileActivity extends Activity {
    public String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ((Button)findViewById(R.id.logoutButton)).setBackgroundResource(R.drawable.profile_logout);


        ((TextView)findViewById(R.id.profile_name)).setText(CommonUtils.name);
        ((TextView)findViewById(R.id.profile_score)).setText("Score "+CommonUtils.score);
        ((CircularProfilePicView)findViewById(R.id.profile_pic)).setProfileId(CommonUtils.userId);
        Bundle bd = new Bundle();
        bd.putString("fields", "cover");
        new Request(
                Session.getActiveSession(),
                CommonUtils.userId,
                bd,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        Response res = response;
                        try {
                            if (response != null) {
                                url = response.getGraphObject().getInnerJSONObject().getJSONObject("cover").getString("source");
                                new DownloadImageTask((ImageView) findViewById(R.id.cover)).execute(url);
                                System.out.print(url);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            MediaStore.Images.Media.insertImage(getContentResolver(), result,
                    "cover", "");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


}
