package com.sample.foo.simplewebapi;

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

    public static int getYear() {
        return GregorianCalendar.YEAR;
    }

    public static int[] pickNRandom(int[] array, int n) {

        List<Integer> list = new ArrayList<Integer>(array.length);
        for (int i : array)
            list.add(i);
        Collections.shuffle(list);

        int[] answer = new int[n];
        for (int i = 0; i < n; i++)
            answer[i] = list.get(i);
        Arrays.sort(answer);

        return answer;

    }
}
