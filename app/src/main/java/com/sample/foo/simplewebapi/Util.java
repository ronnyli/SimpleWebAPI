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
        for (int i = 0; i < n; i++)
            answer.put(list.get(i));

        return answer;

    }

    public static JSONArray pickNRandomWeighted(JSONArray array, int n) {
        // Results are weighted by their position in array
        // weight is 1/(1+index)^0.5
        List<Double> weights = new ArrayList<Double>(array.length());
        Double completeWeight = 0.0;
        for (int i = 0; i < array.length(); i++) {
            Double weight = 1 / Math.sqrt(1 + i);
            weights.add(weight);
            completeWeight += weight;
        }
        // initialize for while loop
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        int i = 0;
        JSONArray answer = new JSONArray();
        List<Integer> selected = new ArrayList<Integer>(n);
        while (answer.length() < n) {
            countWeight += weights.get(i);
            if (countWeight >= r && !selected.contains(i)) {
                // answer and index should be retained
                //completeWeight needs to be adjusted down
                try {
                    answer.put(array.getString(i));
                    selected.add(i);
                    completeWeight -= weights.get(i);
                    // Restart loop
                    r = Math.random() * completeWeight;
                    countWeight = 0.0;
                    i = 0;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                continue;
            }
            i++;
        }
        return answer;
    }
}
