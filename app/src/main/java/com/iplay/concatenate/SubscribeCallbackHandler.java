package com.iplay.concatenate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.iplay.concatenate.common.CommonUtils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
            switch (((int)(long)jsonObject.get("typeFlag"))) {

                case 1:
                    Intent intent = new Intent("invite_received");
                    intent.putExtra("sender_id", (String)jsonObject.get("fromUser") );
                    InviteModel newRequest = new InviteModel((String)jsonObject.get("fromUser"),"Accept the challenge?");
                    newRequest.setName(CommonUtils.friendsMap.get((String)jsonObject.get("fromUser")).getName());
                    ListAdapterUtil.getQueue().add(newRequest);
                    LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                    // remove the request after 30 secs
                    final Timer buttonTimer = new Timer();
                    buttonTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            buttonTimer.cancel();
                            ListAdapterUtil.removeInviteById( (String) jsonObject.get("fromUser") );
                            Intent intent = new Intent("invite_received"); // but it is cancelled here. update req. rename?
                            intent.putExtra("sender_id", (String)jsonObject.get("fromUser") );
                            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                        }
                    }, 30000);
                    break;
                case 2:
                    if ( CommonUtils.onHostGame && CommonUtils.waitingFor != null && CommonUtils.waitingFor.equals((String)jsonObject.get("fromUser")) ) {

                        try {
                            JSONObject sendjsonObject = new JSONObject();
                            sendjsonObject.put("typeFlag", 2);
                            sendjsonObject.put("fromUser", CommonUtils.userId);
                            sendjsonObject.put("toUser", CommonUtils.waitingFor);
                            System.out.println(sendjsonObject.toString());
                            ORTCUtil.getClient().send(CommonUtils.getChannelNameFromUserID(CommonUtils.waitingFor), sendjsonObject.toString());
                        } catch ( Exception e ) {
                            Log.e("json", "Error while encoding json for server");
                        }

                        CommonUtils.disableTimer(CommonUtils.hostGameTimer);

//                        intent = new Intent(ctx, StartingGame.class);
                        intent = new Intent("starting_game");
                        intent.putExtra("sender_id", (String)jsonObject.get("fromUser") );
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        ctx.startActivity(intent);
                        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                    }
                    break;
                case 3:
                    ListAdapterUtil.removeInviteById( (String) jsonObject.get("fromUser") );
                    intent = new Intent("invite_received"); // but it is cancelled here. update req. rename?
                    intent.putExtra("sender_id", (String)jsonObject.get("fromUser") );
                    LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                    break;
                case 4:
                    String against = (String) jsonObject.get("fromUser");
                    if ( against.equals(CommonUtils.userId) )
                        against = (String) jsonObject.get("toUser");
                    if ( CommonUtils.waitingFor != null && CommonUtils.waitingFor.equals(against) ) {
                        CommonUtils.waitingFor = null;
                        String gameWord = (String) jsonObject.get("gameWord");
                        int gameId = (int)(long) jsonObject.get("gameId");
                        String userTurn = (String) jsonObject.get("userTurn");
                        Boolean isBot = (Boolean) jsonObject.get("isBot");
                        Intent in = new Intent(ctx, MainGameActivity.class);
                        in.putExtra("game_word", gameWord);
                        in.putExtra("game_id", gameId);
                        in.putExtra("user_turn", userTurn);
                        in.putExtra("against_user", against);
                        in.putExtra("is_bot", isBot);
                        in.putExtra("timestamp", System.currentTimeMillis());
                        CommonUtils.startGameIntent = in;
//                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        ctx.startActivity(in);
                    }
                    break;
                case 5:
                    intent = new Intent("gameword_received");
                    intent.putExtra("sender_id", (String)jsonObject.get("fromUser") );
                    intent.putExtra("game_id", (int)(long) jsonObject.get("gameId") );
                    intent.putExtra("game_word", (String)jsonObject.get("gameWord") );
                    intent.putExtra("your_score", (int)(long)jsonObject.get("myScore") ); // senders my score = your score.. here.
                    LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                    break;
                case 6:
                    CommonUtils.disableTimer(CommonUtils.mainGameTimer);
                    Intent in = new Intent(ctx, NewGameOverActivity.class);
                    in.putExtra("my_score", MainGameActivity.currentMyScore);
                    in.putExtra("your_score", MainGameActivity.currentYourScore);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(in);
                    break;
                case 7:
                    // Ignoring the word timed out request
                    break;
                case 8:
                    if ( CommonUtils.onQuickGame ) {
                        if (CommonUtils.quickGameTimer != null) {
                            CommonUtils.disableTimer(CommonUtils.quickGameTimer);
                            try {
                                JSONObject sendjsonObject = new JSONObject();
                                sendjsonObject.put("typeFlag", 2);
                                sendjsonObject.put("fromUser", CommonUtils.userId);
                                sendjsonObject.put("toUser", (String) jsonObject.get("fromUser"));
                                System.out.println(sendjsonObject.toString());
                                ORTCUtil.getClient().send(CommonUtils.getChannelNameFromUserID((String) jsonObject.get("fromUser")), sendjsonObject.toString());
                            } catch (Exception e) {
                                Log.e("json", "Error while encoding json for server");
                            }

//                            intent = new Intent(ctx, StartingGame.class);
                            intent = new Intent("starting_game");
                            intent.putExtra("sender_id", (String) jsonObject.get("fromUser"));
                            intent.putExtra("is_bot", (Boolean) jsonObject.get("isBot"));
                            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            ctx.startActivity(intent);
                        } else if ((Boolean) jsonObject.get("isBot")) {
//                            intent = new Intent(ctx, StartingGame.class);
                            intent = new Intent("starting_game");
                            intent.putExtra("sender_id", (String) jsonObject.get("fromUser"));
                            intent.putExtra("is_bot", (Boolean) jsonObject.get("isBot"));
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            ctx.startActivity(intent);
                            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                        }

                    }
                    break;

            }
        } catch ( Exception pe ) {
            System.out.println("Error while parsing: " + pe.getMessage());
        }
        System.out.println("Message received");
    }



}
