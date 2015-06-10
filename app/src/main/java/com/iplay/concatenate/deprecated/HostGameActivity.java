package com.iplay.concatenate.deprecated;


import android.os.Bundle;
import android.widget.Toast;

import com.iplay.concatenate.R;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.support.CircularProfilePicView;
import com.iplay.concatenate.support.NetworkActivity;
import com.iplay.concatenate.support.ORTCUtil;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimerTask;


@Deprecated
public class HostGameActivity extends NetworkActivity {

    public ProgressWheel pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);
        Bundle intentExtra = getIntent().getExtras();
        final String opponentId = intentExtra.getString("id");
        ((CircularProfilePicView) findViewById(R.id.ppic)).setProfileId(opponentId);

        pw = (ProgressWheel) findViewById(R.id.progress_wheel);
        pw.setSpinSpeed(new Float(0));
        final long startTime = System.currentTimeMillis();
        CommonUtils.hostGameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                HostGameActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long left = (30 * 1000 - System.currentTimeMillis() + startTime);
                        pw.setInstantProgress((30 * 1000 - left) / new Float(30 * 1000));
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
