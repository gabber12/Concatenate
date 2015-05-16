package com.iplay.concatenate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.iplay.concatenate.common.CommonUtils;

/**
 * Created by gabber12 on 17/05/15.
 */
public class NetworkActivity extends Activity {
    MaterialDialog md;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        super.registerReceiver(new ConnectivityReciever(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
    class ConnectivityReciever extends  BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {

            Log.d("app", "Network connectivity change");
            if(!CommonUtils.isOnline(context) ){
                System.out.println("Network not Available");
                if(md == null)

                    md = new MaterialDialog.Builder(context)
                            .callback(new MaterialDialog.ButtonCallback() {

                            })
                            .title("No Internet Connection Available")
                            .titleGravity(GravityEnum.CENTER)
                            .content("Please Check your Internet Connection")
                            .progress(true, 1)
                            .theme(Theme.LIGHT).cancelable(false)
                            .show();
            } else if(md != null) {
                System.out.println("Network Available");
                md.dismiss();
                md = null;
                Intent in = new Intent(context, FullscreenActivity.class);
                context.startActivity(in);
            }

        }
    };
}
