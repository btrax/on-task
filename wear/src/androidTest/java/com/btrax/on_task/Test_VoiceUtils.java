package com.btrax.on_task;

import android.test.InstrumentationTestCase;
import com.btrax.on_task.util.VoiceUtils;

public class Test_VoiceUtils extends InstrumentationTestCase {

    public void test_voice2Task_name() throws Exception {
        String voice = "buy milkby!!";

        assertEquals(voice, VoiceUtils.voice2Task(voice).name);
    }

    public void test_voice2Task_name_at() throws Exception {
        String voice = "buy milk!! at sabyfe waaty";

        assertEquals("sabyfe waaty", VoiceUtils.voice2Task(voice).place);
        assertEquals( "buy milk!!", VoiceUtils.voice2Task(voice).name);
    }

    public void test_voice2Task_name_at_by() throws Exception {
        String voice = "buy milk!! at safe way by 12th 12pm";

        assertEquals( "buy milk!!", VoiceUtils.voice2Task(voice).name);
        assertEquals("safe way", VoiceUtils.voice2Task(voice).place);
        assertEquals("12th 12pm", VoiceUtils.voice2Task(voice).due);
    }

    public void test_voice2Task_name_by() throws Exception {
        String voice = "buy milk!! by 12th 12pm";

        assertEquals( "buy milk!!", VoiceUtils.voice2Task(voice).name);
        assertEquals("12th 12pm", VoiceUtils.voice2Task(voice).due);
    }

    public void test_voice2Task_name_by_at() throws Exception {
        String voice = "buy milk!! by 12th 12pm at safe way";

        assertEquals( "buy milk!!", VoiceUtils.voice2Task(voice).name);
        assertEquals("safe way", VoiceUtils.voice2Task(voice).place);
        assertEquals("12th 12pm", VoiceUtils.voice2Task(voice).due);
    }
}