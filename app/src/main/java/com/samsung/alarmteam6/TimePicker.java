package com.samsung.alarmteam6;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TimePicker extends AppCompatActivity {
    private int hour, minute, second;
    private long time;
    FloatingActionButton btnResume, btnStop, btnPause;
    TextView timeView, totalView;
    ImageView icanchor;
    ObjectAnimator animator;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        hour = getIntent().getIntExtra("hour", 0);
        minute = getIntent().getIntExtra("minute",0);
        second = getIntent().getIntExtra("second",0);
        btnResume = findViewById(R.id.btnResume);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        timeView = findViewById(R.id.timeView);
        icanchor = findViewById(R.id.icanchor);
        totalView =findViewById(R.id.total_time);
        totalView.setText("Total time is " +hour +" hours "+minute+" minutes "+second+" seconds");
        time = hour*3600000+minute*60000+second*1000;
        btnResume.setVisibility(View.GONE);
        animator = ObjectAnimator.ofFloat(icanchor,"rotation",0,360);
        timeView.setText(returnTime(time));
        animator.setDuration(time);
        animator.setRepeatCount(0xffffffff);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.start();
        countDownTimer = new CountDownTimer(time,1000) {
            @Override
            public void onTick(long l) {
                timeView.setText(returnTime(l));
                hour   = (int)(l /3600000);
                minute = (int)(l - hour*3600000)/60000;
                second = (int)(l - hour*3600000- minute*60000)/1000;
                System.out.println(second);
            }

            @Override
            public void onFinish() {
                timeView.setText("done!");
                animator.end();

            }
        };
        countDownTimer.start();
        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = hour*3600000+minute*60000+second*1000;
                btnPause.setVisibility(View.VISIBLE);
                btnResume.setVisibility(View.GONE);
                animator.resume();
                countDownTimer = new CountDownTimer(time,1000) {
                    @Override
                    public void onTick(long l) {
                        timeView.setText(returnTime(l));
                        hour   = (int)(l /3600000);
                        minute = (int)(l - hour*3600000)/60000;
                        second = (int)(l - hour*3600000- minute*60000)/1000;
                    }

                    @Override
                    public void onFinish() {
                        timeView.setText("done!");
                        animator.end();

                    }
                };
                countDownTimer.start();

            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnResume.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
                animator.pause();
                countDownTimer.cancel();
            }
        });

    }
    public String returnTime(long time){
        int h   = (int)(time /3600000);
        int m = (int)(time - h*3600000)/60000;
        int s= (int)(time - h*3600000- m*60000)/1000;
        String t =(h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
        return t;
    }
}