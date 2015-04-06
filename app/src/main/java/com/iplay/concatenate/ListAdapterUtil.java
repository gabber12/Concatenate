
package com.iplay.concatenate;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import ibt.ortc.extensibility.OnMessage;
import ibt.ortc.extensibility.OrtcClient;


public class ListAdapterUtil {
    public static MyAdapter ma = null;
    public static ConcurrentLinkedQueue queue;    
    public static MyAdapter getAdapter(Context ctx  ){
        
        if(ma == null){
            ma = new MyAdapter(ctx, ListAdapterUtil.getQueue());
        }
        return ma;
    }
    public static ConcurrentLinkedQueue getQueue() {
        if ( queue == null) {
            queue = new ConcurrentLinkedQueue();
        }

        return queue;
    }

}
