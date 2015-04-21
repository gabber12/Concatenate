package com.iplay.concatenate;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.iplay.concatenate.common.CommonUtils;


public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        CommonUtils.fetchScore();

        ((TextView)findViewById(R.id.profile_name)).setText(CommonUtils.name);
        ((TextView)findViewById(R.id.profile_score)).setText("Score "+CommonUtils.score);
        ((CircularProfilePicView)findViewById(R.id.profile_pic)).setProfileId(CommonUtils.userId);



    }


}
