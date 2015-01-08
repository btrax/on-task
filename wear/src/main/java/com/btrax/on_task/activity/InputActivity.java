package com.btrax.on_task.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;

import com.btrax.on_task.R;
import com.btrax.on_task.analytics.SendEventManager;
import com.btrax.on_task.data.TaskData;
import com.btrax.on_task.model.TaskModel;
import com.btrax.on_task.util.VoiceUtils;

import java.util.ArrayList;

/**
 * input by using voice.
 */
public class InputActivity extends BaseActivity {

    private final static String TAG = InputActivity.class.getSimpleName();
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        startVoiceRecognitionActivity();

        /*
        String voice = "buy milk at Safeway by 9 pm";
        TaskData td = VoiceUtils.voice2Task(voice);
        new TaskModel(this).save(td);
        */

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> matches = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String voice = matches.get(0);

                TaskData td = VoiceUtils.voice2Task(voice);
                new TaskModel(this).save(td);
                new SendEventManager(getApplicationContext()).sendInputTask(voice);
            }
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.prompt_voice_input));
        startActivityForResult(intent, REQUEST_CODE);
    }
}
