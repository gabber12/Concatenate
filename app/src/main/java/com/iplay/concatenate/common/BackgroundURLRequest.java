package com.iplay.concatenate.common;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by divanshu on 19/04/15.
 */
public class BackgroundURLRequest extends AsyncTask<String, Integer, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
        try {
            String relativeURL = params[0];
            String message = params[1];
            HttpPost post = new HttpPost(CommonUtils.SERVER_BASE + relativeURL);
            StringEntity se = new StringEntity(message);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            HttpResponse response = httpClient.execute(post);

            InputStream is = response.getEntity().getContent();

            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                return sb.toString();
            }

        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
        return "error";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}