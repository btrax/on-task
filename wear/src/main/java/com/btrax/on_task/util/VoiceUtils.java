package com.btrax.on_task.util;

import com.btrax.on_task.data.TaskData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils support voice input
 */
public class VoiceUtils {

    private final static String PLACE_WORD = "at";
    private final static String DUE_WORD = "by";
    private final static String SPACE = " ";

    /**
     * convert voice to task object
     *
     * @param voice
     * @return
     */
    public static TaskData voice2Task(String voice) {
        TaskData td = new TaskData();
        String nameRegrex;
        Pattern p;
        Matcher m;

        String splitedAt[] = voice.split(SPACE + PLACE_WORD + SPACE, 2);
        String splitedBy[] = voice.split(SPACE + DUE_WORD + SPACE, 2);

        if (splitedAt.length == 2 && splitedBy.length == 2) {
            // at by
            nameRegrex = "^(.*)" + SPACE + PLACE_WORD + SPACE + "(.*)" + SPACE + DUE_WORD + SPACE + "(.*)$";
            p = Pattern.compile(nameRegrex);
            m = p.matcher(voice);

            // 場所か時間を含む場合
            if (m.find()) {
                String name =
                        m.group(1).trim();
                String place =
                        m.group(2).trim();
                String due = m.group(3).trim();
                td.name = name;
                td.place = place;
                td.due = due;

                return td;
            }


            // by at
            nameRegrex = "^(.*)" + SPACE + DUE_WORD + SPACE + "(.*)" + SPACE + PLACE_WORD + SPACE + "(.*)$";
            p = Pattern.compile(nameRegrex);
            m = p.matcher(voice);

            if (m.find()) {
                String name =
                        m.group(1).trim();
                String place =
                        m.group(3).trim();
                String due = m.group(2).trim();
                td.name = name;
                td.place = place;
                td.due = due;

                return td;
            }


            // only place
        } else if (splitedAt.length == 2 && splitedBy.length != 2) {
            td.name = splitedAt[0].trim();
            td.place = splitedAt[1].trim();
            return td;

            // only time
        } else if (splitedAt.length != 2 && splitedBy.length == 2) {
            td.name = splitedBy[0].trim();
            td.due = splitedBy[1].trim();
            return td;

            // no place and time
        } else {
            td.name = voice.trim();
            return td;
        }

        return td;
    }
}