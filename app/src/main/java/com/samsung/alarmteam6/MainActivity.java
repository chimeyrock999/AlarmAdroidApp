package com.samsung.alarmteam6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements EventHandler {
    ImageButton alarmBtn, stopwatchBtn, timerBtn;
    Fragment alarmFragment, stopwatchFragment, timerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmBtn = findViewById(R.id.alarm_btn);
        stopwatchBtn = findViewById(R.id.stopwatch_btn);
        timerBtn = findViewById(R.id.timer_btn);

        alarmFragment = new AlarmFragment();
        stopwatchFragment = new StopwatchFragment();
        timerFragment = new TimerFragment();
        switchFragment(alarmFragment);
        alarmBtn.setImageDrawable(getDrawable(R.drawable.ic_alarm_white));
        stopwatchBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_timelapse_24));
        timerBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_timer_24));

        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(alarmFragment);
                alarmBtn.setImageDrawable(getDrawable(R.drawable.ic_alarm_white));
                stopwatchBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_timelapse_24));
                timerBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_timer_24));
            }
        });

        stopwatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(stopwatchFragment);
                alarmBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_access_alarm_24));
                stopwatchBtn.setImageDrawable(getDrawable(R.drawable.ic_timelapse_white));
                timerBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_timer_24));
            }
        });

        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(timerFragment);
                alarmBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_access_alarm_24));
                stopwatchBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_timelapse_24));
                timerBtn.setImageDrawable(getDrawable(R.drawable.ic_timer_white));
            }
        });
    }


    public void switchFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(int pos) {

    }

    @Override
    public void onLongClick(int pos) {

    }
}