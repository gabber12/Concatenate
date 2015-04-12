package com.iplay.concatenate.common;

import com.facebook.FacebookGraphObjectException;
import com.facebook.widget.LoginButton;

import java.util.Set;

/**
 * Created by divanshu on 06/04/15.
 */
public class CommonUtils {

    public static String userId = null;

    public static String getChannelNameFromUserID(String id) {
        return "user_channel_" + id;
    }

    public static LoginButton loginButton = null;
    public static LoginButton getLoginButton() { if (loginButton == null) throw new FacebookGraphObjectException(); return loginButton; }
    public static void setLoginButton(LoginButton lb) { loginButton = lb; }

    public static Set<String> words = null;
}
