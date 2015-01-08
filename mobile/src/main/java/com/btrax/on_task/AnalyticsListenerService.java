package com.btrax.on_task;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.HashMap;
import java.util.Map;

/**
 * receive events info from watch and send them to analytics tools.
 */
public class AnalyticsListenerService extends WearableListenerService {

    private final static String TAG = AnalyticsListenerService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        FlurryAgent.onStartSession(this, AppConsts.FLURRY_API_KEY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        MyLog.d(TAG, "on message");
        MyLog.d(TAG, "path:" + messageEvent.getPath());
        MyLog.d(TAG, new String(messageEvent.getData()));

        String path = messageEvent.getPath();
        String value = new String(messageEvent.getData());

        if (path.equals("/task/input")) {

            MyLog.d(TAG, "/task/input");
            logEventTaskInput(value);

        } else if (path.equals("/task/complete")) {

            MyLog.d(TAG, "/task/complete");
            logEventTaskComplete(value);

        } else if (path.equals("/task/expand")) {
            MyLog.d(TAG, "/task/expand");
            logEventTaskExpand(value);

        } else if (path.equals("/watch/set")) {

            MyLog.d(TAG, "/watch/set");
            FlurryAgent.logEvent("WATCH_SET");

        } else if (path.equals("/watch/unset")) {

            MyLog.d(TAG, "/watch/unset");
            FlurryAgent.logEvent("WATCH_UNSET");
        }
    }

    private void logEventTaskComplete(String value) {
        String taskName = value;
        Map<String, String> articleParams = new HashMap<String, String>();
        articleParams.put("name", taskName);
        FlurryAgent.logEvent("TASK_COMPLETE", articleParams, true);
    }

    private void logEventTaskExpand(String value) {
        String taskName = value;
        Map<String, String> articleParams = new HashMap<String, String>();
        articleParams.put("name", taskName);
        FlurryAgent.logEvent("TASK_EXPAND", articleParams, true);
    }

    private void logEventTaskInput(String value) {
        String taskName = value;
        Map<String, String> articleParams = new HashMap<String, String>();
        articleParams.put("name", taskName);
        FlurryAgent.logEvent("TASK_INPUT", articleParams, true);
    }
}
