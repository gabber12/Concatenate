package com.iplay.concatenate;


import com.facebook.FacebookException;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;
import com.pnikosis.materialishprogress.ProgressWheel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ibt.ortc.extensibility.OrtcClient;



public class HostGameActivity extends Activity {

    public ProgressWheel pw ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);
        Bundle intentExtra  = getIntent().getExtras();
        final String opponentId = intentExtra.getString("id");
        ((CircularProfilePicView)findViewById(R.id.ppic)).setProfileId(opponentId);

        pw = (ProgressWheel)findViewById(R.id.progress_wheel);
        pw.setSpinSpeed(new Float(0));
        final long startTime = System.currentTimeMillis();
        CommonUtils.hostGameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                HostGameActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long left = (30 * 1000 - System.currentTimeMillis() + startTime);
                        pw.setInstantProgress((30*1000-left)/new Float(30*1000));
                        if (left <= 0) {
                            CommonUtils.hostGameTimer.cancel();
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("typeFlag", 3);
                                jsonObject.put("toUser", opponentId);
                                jsonObject.put("fromUser", CommonUtils.userId);
                                ORTCUtil.getClient().send(CommonUtils.getChannelNameFromUserID(opponentId), jsonObject.toString());
                            } catch (JSONException je) {
                                System.out.println("Unable to encode json: " + je.getMessage());
                            }
                            CommonUtils.waitingFor = null;
                            Toast toast = Toast.makeText(getApplicationContext(), "Opponent did not join. :(", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
            }
        }, new Long(0), new Long(20));

    }

}
