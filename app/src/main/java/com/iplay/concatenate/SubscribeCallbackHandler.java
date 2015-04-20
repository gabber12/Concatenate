package com.iplay.concatenate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.iplay.concatenate.common.CommonUtils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import ibt.ortc.extensibility.OnMessage;
import ibt.ortc.extensibility.OrtcClient;

/**
 * Created by divanshu on 06/04/15.
 */
public class SubscribeCallbackHandler implements OnMessage {
    private Context ctx;
    public SubscribeCallbackHandler(Context ctx) {
        this.ctx = ctx;
    }
    @Override
    public void run(OrtcClient sender, String channel, String message) {
        ConcurrentLinkedQueue<InviteModel> joinGame = ListAdapterUtil.getQueue();
//        MyAdapter ma = ListAdapterUtil.getAdapter(ctx);

        final String subscribedChannel = channel;
        final String messageReceived = message;
        Log.d("pubsub", String.format("Message on channel %s: %s", subscribedChannel, messageReceived));

        try {
            JSONParser jsonParser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) jsonParser.parse(messageReceived);

            // Handle all the cases here!
            switch (((Integer)jsonObject.get("typeFlag"))) {

                case 1:
                    Intent intent = new Intent("invite_recieved");
                    intent.putExtra("sender_id", (String)jsonObject.get("fromUser") );
                    LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
//                    joinGame.add( new InviteModel((String)jsonObject.get("fromUser"), "I challenge you !") );
//                    ma.notifyDataSetChanged();
//                    final Timer t = new Timer();
//                    final long startTime = System.currentTimeMillis();
//                    t.scheduleAtFixedRate(new TimerTask() {
//                        @Override
//                        public void run() {
//
//                            long left = (30 * 1000 - System.currentTimeMillis() + startTime);
//                            double timeLeft = Math.round(left/100.0)/10.0;
//                            if ( timeLeft <= 0.0 ) {
//                                t.cancel();
//                                ListAdapterUtil.removeInviteById( (String)jsonObject.get("fromUser") );
//                                MyAdapter ma = ListAdapterUtil.getAdapter(null);
//                                ma.notifyDataSetChanged(); // is it okay to make this final ?
//                            }
//
//                        }
//                    }, new Long(0), new Long(100));
                    break;
                case 2:
                    // ignoring accept of invite
                    break;
                case 3:
                    ListAdapterUtil.removeInviteById( (String) jsonObject.get("fromUser") );
//                    ma.notifyDataSetChanged();
                    break;
                case 4:
                    String against = (String) jsonObject.get("fromUser");
                    if ( against.equals(CommonUtils.userId) )
                        against = (String) jsonObject.get("toUser");
                    String gameWord = (String) jsonObject.get("gameWord");
                    int gameId = (Integer) jsonObject.get("gameId");
                    String userTurn = (String) jsonObject.get("userTurn");
                    Intent in = new Intent(ctx, MainGameActivity.class);
                    in.putExtra("game_word", gameWord);
                    in.putExtra("game_id", gameId);
                    in.putExtra("user_turn", userTurn);
                    in.putExtra("against_user", against);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    in.putExtra("userTurn", userTurn);
                    ctx.startActivity(in);
                    break;
                case 5:
                    intent = new Intent("gameword_recieved");
                    intent.putExtra("sender_id", (String)jsonObject.get("fromUser") );
                    intent.putExtra("game_id", (Integer) jsonObject.get("gameId") );
                    intent.putExtra("game_word", (String)jsonObject.get("gameWord") );
                    LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                    break;
                case 6:
                    // TODO: game over request
                    break;

            }
        } catch ( Exception pe ) {
            System.out.println("Error while parsing: " + pe.getMessage());
        }
        System.out.println("Message recieved");
    }



}
