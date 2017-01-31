package com.sample.foo.simplewebapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by ronny on 18/01/17.
 */

public final class Util {
    public final static int KIBI = 1024;
    public final static int BYTE = 1;
    public final static int KIBIBYTE = KIBI * BYTE;

    /**
     * Private constructor to prevent instantiation
     */
    private Util() {}

    public static JSONArray pickNRandom(JSONArray array, int n) {

        List<String> list = new ArrayList<String>(array.length());
        for (int i = 0; i < array.length(); i++) {
            try {
                String title = array.getString(i);
                list.add(title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.shuffle(list);

        JSONArray answer = new JSONArray();
//        String answer[] = new String[n];
        for (int i = 0; i < n; i++)
            answer.put(list.get(i));

        return answer;

    }
}
