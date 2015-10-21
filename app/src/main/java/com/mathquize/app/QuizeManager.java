package com.mathquize.app;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by lifurong on 15/6/22.
 */
public class QuizeManager {
    private static final int REPEAT_COUNT = 3;
    private static final String TAG = "QuizeManager";
    private static QuizeManager instance = new QuizeManager();

    public static QuizeManager getInstance() {
        return instance;
    }

    private LinkedList<Quize> quizes = new LinkedList<Quize>();

    public void generate() {
        for (int iLeft = 1; iLeft < 10; iLeft++) {
            for (int iRight = 1; iRight < 10; iRight++) {
                for (int repeat = 0; repeat < REPEAT_COUNT; repeat++) {
                    quizes.add(new Quize(iLeft, iRight, true));
                    quizes.add(new Quize(iLeft, iRight, false));
                }
            }
        }
        Collections.shuffle(quizes);
    }

    public void load(String serialized) {
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedList<Quize>>() {
        }.getType();
        quizes = gson.fromJson(serialized, type);
        Log.v(TAG, "loaded" + quizes.size());

    }

    public String export() {
        Gson gson = new Gson();
        Log.v(TAG, "export" + quizes.size());
        return gson.toJson(quizes);
    }

    public Quize next() {
        return quizes.getFirst();
    }

    public void setPassed(Quize quize, boolean isPassed) {
        quizes.remove(quize);
        if (!isPassed) {
            ensureAtLeast3(quize);
        }
    }

    private void ensureAtLeast3(Quize quize) {
        int freq = Collections.frequency(quizes, quize);
        switch (freq) {
            case 0:
                quizes.add(quizes.size() / 3, quize);
            case 1:
                quizes.add((quizes.size() * 2) / 3, quize);
            case 2:
                quizes.addLast(quize);
            case 3:
                break;
        }
    }

    public int getSize() {
        return quizes.size();
    }
}
