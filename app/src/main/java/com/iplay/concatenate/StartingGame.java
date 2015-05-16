package com.iplay.concatenate;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.util.Timer;
import java.util.TimerTask;



public class StartingGame extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CommonUtils.onStartingGame = true;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_starting_game);




        final String senderId = getIntent().getStringExtra("sender_id");
        final Boolean isBot = getIntent().getBooleanExtra("is_bot", false);

        CommonUtils.waitingFor = senderId;

        if ( isBot ) {

            JSONObject sendjsonObject = new JSONObject();
            sendjsonObject.put("fromUser", CommonUtils.userId );
            sendjsonObject.put("toUser", senderId);
            System.out.println(sendjsonObject.toString());
            new BackgroundURLRequest().execute("start_game_with_bot/", sendjsonObject.toString());

        }
//        else {
            // say the opponent left after 15 secs
            CommonUtils.startingGameTimer = new Timer();
            CommonUtils.startingGameTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                        CommonUtils.disableTimer(CommonUtils.startingGameTimer);

                        if ( CommonUtils.startGameIntent != null && System.currentTimeMillis() - CommonUtils.startGameIntent.getLongExtra("timestamp", System.currentTimeMillis()) <= 20*1000  ) {
                            startActivity(CommonUtils.startGameIntent);
                            CommonUtils.waitingFor = null;
                        } else {
                            Toast t = Toast.makeText(getApplicationContext(), "Opponent has left :(", Toast.LENGTH_LONG);
                            t.show();
                            final Intent intent = new Intent(StartingGame.this, HomeActivity.class);
                            startActivity(intent);
                            CommonUtils.waitingFor = null;
                        }
                        CommonUtils.startGameIntent = null;
                }
            }, 15000);
//        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    public void onBackPressed() {

        new MaterialDialog.Builder(this)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        StartingGame.super.onBackPressed();
                    }
                })
                .title("Leave Game")
                .titleGravity(GravityEnum.CENTER)
                .content("You will lose if you leave the game. Still continue?")
                .positiveText("QUIT")
                .negativeText("PLAY ON")
                .theme(Theme.LIGHT)
                .negativeColorRes(R.color.material_deep_teal_500)
                .positiveColorRes(R.color.material_red_500)
                .show();

//        new AlertDialog.Builder(this)
//                .setTitle("Really Exit?")
//                .setMessage("You will lose if you exit. Continue?")
//                .setNegativeButton(android.R.string.no, null)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        Intent in = new Intent(getApplicationContext(), HomeActivity.class);
//                        startActivity(in);
//                    }
//                }).create().show();
    }
}
