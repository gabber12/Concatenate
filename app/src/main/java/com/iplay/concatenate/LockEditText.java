package com.iplay.concatenate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by divanshu on 08/04/15.
 */
public class LockEditText extends EditText {
    /* Must use this constructor in order for the layout files to instantiate the class properly */
    public LockEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event)
    {
        // Return true if I handle the event:
        // In my case i want the keyboard to not be dismissible so i simply return true
        // Other people might want to handle the event differently
        System.out.println("onKeyPreIme " + event);

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            Intent intent = new Intent("game_back_button");
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            return false;
        }

        return true;
    }

}
