package com.iplay.concatenate.support;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.iplay.concatenate.HomeActivity;
import com.iplay.concatenate.common.CommonUtils;

/**
 * Created by gabber12 on 17/05/15.
 */
public class NetworkActivity extends Activity {
    MaterialDialog md;
    ConnectivityReciever cr = null;

    @Override
    protected void onResume() {
        super.onResume();

        cr = new ConnectivityReciever();
        super.registerReceiver(cr, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cr != null)
            super.unregisterReceiver(cr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (md != null) {
            md.cancel();
        }
    }

    class ConnectivityReciever extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {

            Log.d("app", "Network connectivity change");
            if (!CommonUtils.isOnline(context)) {
                System.out.println("Network not Available");
                if (md == null)

                    md = new MaterialDialog.Builder(context)
                            .callback(new MaterialDialog.ButtonCallback() {

                            })
                            .title("No Internet Connection Available")
                            .titleGravity(GravityEnum.CENTER)
                            .content("Please Check your Internet Connection")
                            .progress(true, 1)
                            .theme(Theme.LIGHT).cancelable(false)
                            .show();
            } else if (md != null) {
                System.out.println("Network Available");
                md.dismiss();
                md = null;
                Intent in = new Intent(context, HomeActivity.class);
                context.startActivity(in);
            }

        }
    }

    ;
}
