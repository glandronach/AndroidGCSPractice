package com.viasofts.mygcs.utils;

import android.animation.ValueAnimator;
import android.graphics.Color;

public class ColorAnumation {

    public void startAnimation() {
        final float[] from = new float[3],
                to =   new float[3];

        Color.colorToHSV(Color.parseColor("#FFFFFFFF"), from);   // from white
        Color.colorToHSV(Color.parseColor("#FFFF0000"), to);     // to red

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(1000);                              // for 300 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                float value = (float) animation.getAnimatedValue();
                // TODO : Do something with animated value

            }
        });

        anim.start();
    }
}
