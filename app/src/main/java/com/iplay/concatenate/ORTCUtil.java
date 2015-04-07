package com.iplay.concatenate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.iplay.concatenate.common.CommonUtils;

import java.util.HashMap;

import ibt.ortc.api.ChannelPermissions;
import ibt.ortc.api.Ortc;
import ibt.ortc.extensibility.OnConnected;
import ibt.ortc.extensibility.OnDisconnected;
import ibt.ortc.extensibility.OnException;
import ibt.ortc.extensibility.OnMessage;
import ibt.ortc.extensibility.OnReconnected;
import ibt.ortc.extensibility.OnReconnecting;
import ibt.ortc.extensibility.OnSubscribed;
import ibt.ortc.extensibility.OnUnsubscribed;
import ibt.ortc.extensibility.OrtcClient;
import ibt.ortc.extensibility.OrtcFactory;

/**
 * Created by gabber12 on 02/04/15.
 */
public class ORTCUtil {
    private static final boolean defaultNeedsAuthentication = false;

    private static OrtcClient client = null;
    private static int reconnectingTries = 0;
    private static final int RESULT_SETTINGS = 1;

    private static String server;
    private static String appKey;
    private static String privateKey;
    private static String authToken;
    private static String connectionMetadata;
    private static boolean isCluster = true;
    public static void init() {
        server = "http://ortc-developers.realtime.co/server/2.1/";
        appKey = "NMRZDS";
        privateKey = "HPr4bQwJUssL";
        authToken = "poll_token";
        connectionMetadata = "AndroidConnMeta";

    }

    public static OrtcClient getClient() {
        if(client != null) return client;

        if ( CommonUtils.userId == null ) {
            System.out.println("FB user id not available yet. Returning null client.");
            return null;
        }
        init();
        try {
            Ortc ortc = new Ortc();

            OrtcFactory factory;

            factory = ortc.loadOrtcFactory("IbtRealtimeSJ");

            client = factory.createClient();


            //client.setApplicationContext(getApplicationContext());
            //client.setGoogleProjectId("your_google_project_id");

        } catch (Exception e) {
        }

        if (client != null) {
            try {

                client.onDisconnected = new OnDisconnected() {

                    public void run(OrtcClient arg0) {

                    }
                };

                client.onSubscribed = new OnSubscribed() {

                    public void run(OrtcClient sender, String channel) {
                        final String subscribedChannel = channel;

                    }
                };

                client.onUnsubscribed = new OnUnsubscribed() {

                    public void run(OrtcClient sender, String channel) {
                        final String subscribedChannel = channel;

                    }
                };

                client.onException = new OnException() {

                    public void run(OrtcClient send, Exception ex) {
                        final Exception exception = ex;

                    }
                };

                client.onReconnected = new OnReconnected() {

                    public void run(final OrtcClient sender) {

                    }
                };

                client.onReconnecting = new OnReconnecting() {

                    public void run(OrtcClient sender) {

                    }
                };

//                client.onConnected = new OnConnected() {
//                    @Override
//                    public void run(OrtcClient ortcClient) {
//                        System.out.println("Connected to ORTC");
//                        ortcClient.subscribe(CommonUtils.getChannelNameFromUserID(CommonUtils.userId), true,
//                                new SubscribeCallbackHandler(getApplicationContext()));
//                    }
//                };

//                connect();
            } catch (Exception e) {
            }

        }

        return client;
    }


    public static void connect() {


        if (defaultNeedsAuthentication) {
            try {
                HashMap<String, ChannelPermissions> permissions = new HashMap<String, ChannelPermissions>();

                permissions.put("yellow:*", ChannelPermissions.Write);
                permissions.put("yellow", ChannelPermissions.Write);
                permissions.put("test:*", ChannelPermissions.Write);
                permissions.put("test", ChannelPermissions.Write);

                if (!Ortc.saveAuthentication(server, isCluster, authToken, false, appKey, 14000, privateKey, permissions)) {
                }
                else {
                }
            } catch (Exception e) {
            }
        }

        if (isCluster) {
            client.setClusterUrl(server);
        }
        else {
            client.setUrl(server);
        }

        client.setConnectionMetadata(connectionMetadata);

        client.connect(appKey, authToken);
    }

    private static void disconnect() {
        client.disconnect();
    }
/*
    public void presenceClickEventHandler(View v) {

        Ortc.presence(
                server,
                isCluster,
                appKey,
                authToken, channel, new OnPresence() {
                    @Override
                    public void run(Exception error, Presence presence) {
                        final Exception exception = error;
                        final Presence presenceData = presence;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(exception != null){
                                    log(String.format("Error: %s", exception.getMessage()));
                                }else{
                                    Iterator<?> metadataIterator = presenceData.getMetadata().entrySet().iterator();
                                    while(metadataIterator.hasNext()){
                                        @SuppressWarnings("unchecked")
                                        Map.Entry<String, Long> entry = (Map.Entry<String, Long>) metadataIterator.next();
                                        log(entry.getKey() + " - " + entry.getValue());
                                    }
                                    log("Subscriptions - " + presenceData.getSubscriptions());
                                }
                            }
                        });
                    }
                });
    }

    private void enablePresence() {

        Ortc.enablePresence(
                server,
                isCluster,
                appKey,
                privateKey,
                channel,
                true,
                new OnEnablePresence() {
                    @Override
                    public void run(Exception error, String result) {
                        final Exception exception = error;
                        final String resultText = result;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(exception != null){
                                    log(String.format("Error: %s", exception.getMessage()));
                                }else{
                                    log(resultText);
                                }
                            }
                        });
                    }
                });
    }

    private void disablePresence() {

        Ortc.disablePresence(
                server,
                isCluster,
                appKey,
                privateKey,
                channel,
                new OnDisablePresence() {
                    @Override
                    public void run(Exception error, String result) {
                        final Exception exception = error;
                        final String resultText = result;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (exception != null) {
                                    log(String.format("Error: %s", exception.getMessage()));
                                } else {
                                    log(resultText);
                                }
                            }
                        });
                    }
                });
    }


*/


}
