package com.muhil.zohokart.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by muhil-ga42 on 19/11/15.
 */
public class SwipeDirectionDetector
{

    private static int calculateAngle(float x1, float x2, float y1, float y2)
    {
        double angle = Math.toDegrees(Math.atan2((y1-y2), (x1-x2)));
        if (angle >= 45 && angle < 135)
        {
            return 1;
        }
        else if ((angle >= 135 && angle < -135))
        {
            return 2;
        }
        else if (angle >= -135 && angle < -45)
        {
            return 3;
        }
        else if (angle >= -45 && angle < 45)
        {
            return 4;
        }
        else
        {
            return 0;
        }
    }

}
