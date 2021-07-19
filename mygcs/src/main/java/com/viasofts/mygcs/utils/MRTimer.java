package com.viasofts.mygcs.utils;

import java.util.Timer;
import java.util.TimerTask;

public class MRTimer {

    int timerCount = 0;

    void initTimer(long delay, long period) {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Logger.d("Timer", "timer : " + timerCount);
                timerCount++;
            }
        };

        Timer timer = new Timer();
        timer.schedule(tt, delay, period);
    }
}
