package com.btrax.on_task.analytics;

import android.content.Context;
import android.os.Bundle;

import com.btrax.on_task.util.MyLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * send event information to mobile. The events will be send from mobile.
 *
 * @see AnalyticsListenerService in Mobile code
 */
public class SendEventManager {

    protected GoogleApiClient mGoogleApiClient;
    private final static String TAG = SendEventManager.class.getSimpleName();

    public SendEventManager(Context context) {
        init(context);
    }

    private void init(Context context) {
        mGoogleApiClient = new GoogleApiClient
                .Builder(context)
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                MyLog.d(TAG, "onConnected");
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                MyLog.d(TAG, "onConnectionSuspended");
                            }
                        }

                ).addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult connectionResult) {
                                MyLog.d("MyFragment", "onConnectionFailed");
                            }
                        }

                ).addApi(Wearable.API).build();
    }

    public void sendInputTask(String taskName) {
        send("/task/input", taskName);
    }

    public void sendExpandTask(String taskName) {
        send("/task/expand", taskName);
    }

    public void sendCompleteTask(String taskName) {
        send("/task/complete", taskName);
    }

    public void sendWatchSet() {
        send("/watch/set", "");
    }

    public void sendWatchUnset() {
        send("/watch/unset", "");
    }

    private void send(final String path, final String value) {

        mGoogleApiClient.connect();

        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            path,
                            value.getBytes())
                            .await();

                    MyLog.d(TAG, "send message!");
                    if (result.getStatus().isSuccess()) {
                        MyLog.d(TAG, "isSuccess is true");
                    } else {
                        MyLog.d(TAG, "isSuccess is false");
                    }
                }
                mGoogleApiClient.disconnect();
            }
        }).start();
    }
}
