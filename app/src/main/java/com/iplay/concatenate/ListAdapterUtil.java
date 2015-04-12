
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
        ConcurrentLinkedQueue tmp = new ConcurrentLinkedQueue();
        InviteModel[] ins = (InviteModel[])queue.toArray();
        queue.clear();
        for(int i = ins.length - 1; i >= 0; i--) {
            if( !( Id.equalsIgnoreCase(ins[i].getMessage()) ) )
                queue.add(ins[i]);
        }

    }

}
