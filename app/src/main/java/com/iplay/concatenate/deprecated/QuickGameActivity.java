package com.iplay.concatenate.deprecated;

import android.content.Intent;
import android.os.Bundle;

import com.iplay.concatenate.HomeActivity;
import com.iplay.concatenate.R;
import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.support.NetworkActivity;

import java.util.Timer;
import java.util.TimerTask;


@Deprecated
public class QuickGameActivity extends NetworkActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quick_game);
        CommonUtils.onQuickGame = true;

        new BackgroundURLRequest().execute("add_me_to_wait_pool/", CommonUtils.userId);


        CommonUtils.quickGameTimer = new Timer();
        CommonUtils.quickGameTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                System.out.println("Sending bot request");
                new BackgroundURLRequest().execute("give_me_bot/", CommonUtils.userId);
                CommonUtils.disableTimer(CommonUtils.quickGameTimer);
            }
        }, 30000);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


    @Override
    public void onPause() {
        new BackgroundURLRequest().execute("remove_me_from_pool/", CommonUtils.userId);
        CommonUtils.onQuickGame = false;
        CommonUtils.disableTimer(CommonUtils.quickGameTimer);
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, HomeActivity.class);
        startActivity(in);
    }
}
