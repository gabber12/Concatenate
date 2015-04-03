package com.iplay.concatenate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by gabber12 on 04/04/15.
 */
public class Invites {
    private static ConcurrentLinkedQueue<String> invitations = null;
    public static ConcurrentLinkedQueue<String> getInvitations() {
        if(invitations == null) {
            invitations = new ConcurrentLinkedQueue<String>();

        }
       return invitations;
    }

}
