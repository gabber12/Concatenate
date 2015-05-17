
package com.iplay.concatenate;

import android.content.Context;

import java.util.concurrent.ConcurrentLinkedQueue;


public class ListAdapterUtil {
    public static MyAdapter ma = null;
    public static ConcurrentLinkedQueue<InviteModel> queue;
    public static MyAdapter getAdapter(Context ctx){
        
        if(ma == null){
            ma = new MyAdapter(ctx, ListAdapterUtil.getQueue());
        }
        return ma;
    }
    public static ConcurrentLinkedQueue<InviteModel> getQueue() {
        if ( queue == null) {
            queue = new ConcurrentLinkedQueue<InviteModel>();
        }

        return queue;
    }
    public static void removeInviteById(String Id){
        ConcurrentLinkedQueue<InviteModel> a = getQueue();
        InviteModel[] ins = new InviteModel[a.size()];
        for(int i = 0; i < a.size(); i++) {
            ins[i] = a.poll();
        }
        for(int i = 0; i < ins.length; i++) {
            if( !( Id.equalsIgnoreCase(ins[i].getSenderId()) )  )
                getQueue().add(ins[i]);
        }

    }

}
