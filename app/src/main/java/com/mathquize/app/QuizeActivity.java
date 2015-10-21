package com.mathquize.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by lifurong on 15/6/22.
 */
public class QuizeActivity extends Activity {
    private static final String PREFS_NAME = "QuizeActivity";
    private static final String PREFS_KEY_QUIZES = "quizes";
    private static final String TAG = "QuizeActivity";
    private static final long TIME_LIMIT = 7 * 1000;
    private View mainView;
    private TextView quizeView;
    private EditText answerView;
    private Chronometer chronometer;
    private SharedPreferences prefs;
    private QuizeManager quizeManager;
    private Quize quize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        getViews();
        registerListeners();
        prefs = this.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        quizeManager = QuizeManager.getInstance();
        loadQuizes();
    }

    private void loadQuizes() {
        String saved = prefs.getString(PREFS_KEY_QUIZES, "");
        if (saved.equals("")) {
            quizeManager.generate();
        } else {
            quizeManager.load(saved);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showNextQuize();
    }

    private void showNextQuize() {
        quize = quizeManager.next();
        quizeView.setText(quize.toString());
        answerView.setText("");
        answerView.setEnabled(true);
        resetProgress();
        setTitle("还有" + quizeManager.getSize() + "道题");
    }

    private void resetProgress() {
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setFormat("%s");
        chronometer.start();
        mainView.setBackgroundColor(0xff424242);
    }


    private void registerListeners() {
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (!inTime()) {
                    mainView.setBackgroundColor(0xffdc143c);
                }
            }
        });
        answerView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String answer = editable.toString();
                String correctAnswer = "" + quize.getAnswer();
                boolean acceptable = correctAnswer.startsWith(answer);
                if (!acceptable) {
                    answerView.setBackgroundColor(0xffDC143C);
//                    answerView.setEnabled(false);
                    answerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            answerView.setBackgroundColor(0x00000000);
                            answerView.setText("");
//                            answerView.setEnabled(true);
                        }
                    }, 400);
                    quizeManager.setPassed(quize, false);
                } else {
                    if (correctAnswer.equals(answer)) {
                        answerView.setBackgroundColor(0xff9ACD32);
//                        answerView.setEnabled(false);
                        answerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                answerView.setBackgroundColor(0x00000000);
                                submitAnswer();
                            }
                        }, 400);

                    }
                }
            }
        });
    }

    private void submitAnswer() {
        quizeManager.setPassed(quize, inTime());
        showNextQuize();
    }

    private boolean inTime() {
        long timeUsed = SystemClock.elapsedRealtime() - chronometer.getBase();
        return timeUsed < TIME_LIMIT;
    }

    private void getViews() {
        quizeView = (TextView) findViewById(R.id.quizeView);
        answerView = (EditText) findViewById(R.id.answerView);
        mainView = findViewById(R.id.mainView);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
    }

    @Override
    protected void onDestroy() {
        saveQuizes();
        super.onDestroy();
    }

    private void saveQuizes() {
        prefs.edit().putString(PREFS_KEY_QUIZES, quizeManager.export()).commit();
    }
}
