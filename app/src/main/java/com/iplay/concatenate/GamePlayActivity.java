package com.iplay.concatenate;

import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ibt.ortc.extensibility.OnMessage;
import ibt.ortc.extensibility.OrtcClient;



public class GamePlayActivity extends Activity {

//    private Button bt;
//    private ListView lv;
//    private EditText et;
//    private ArrayList<String> strArr;
//    private ArrayAdapter<String> adapter;
//    private OrtcClient client;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_game_play);
//        setupActionBar();
//
//        final View controlsView = findViewById(R.id.fullscreen_content_controls);
//        final View contentView = findViewById(R.id.fullscreen_content);
//
//
//
//        final String user2 = "divanshu";
//        final String user1 = "aman";
//        client = ORTCUtil.getClient();
//        final String channel = "user_channel_" + user1;
//
//        bt = (Button) findViewById(R.id.Button01);
//        lv = (ListView) findViewById(R.id.listView1);
//        et = (EditText) findViewById(R.id.EditText01);
//
//        strArr = new ArrayList<String>();
//        strArr.add("--- Game started ---");
//
//        adapter = new ArrayAdapter<String>(getApplicationContext(),
//                android.R.layout.simple_list_item_1, strArr);
//        lv.setAdapter(adapter);
//        bt.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//
//                try {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("fromUser", user1);
//                    jsonObject.put("toUser", user2);
//                    jsonObject.put("gameId", 1);
//                    jsonObject.put("gameWord", et.getText().toString());
//                    new BackgroundURLRequest().execute("game_word_entered/", jsonObject.toString());
//                } catch (Exception e) {
//                    Log.e("json", "error while generating a json file and sending to server");
//                }
//
//            }
//        });
//
//
//    }
//
//    public void addListItem(String message) {
//        strArr.add(message);
//        adapter.notifyDataSetChanged();
//    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//    }
//
//    /**
//     * Set up the {@link android.app.ActionBar}, if the API is available.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    private void setupActionBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            // Show the Up button in the action bar.
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            // This ID represents the Home or Up button. In the case of this
//            // activity, the Up button is shown. Use NavUtils to allow users
//            // to navigate up one level in the application structure. For
//            // more details, see the Navigation pattern on Android Design:
//            //
//            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
//            //
//            // TODO: If Settings has multiple levels, Up should navigate up
//            // that hierarchy.
//            NavUtils.navigateUpFromSameTask(this);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * Touch listener to use for in-layout UI controls to delay hiding the
//     * system UI. This is to prevent the jarring behavior of controls going away
//     * while interacting with activity UI.
//     */
//
//
}
