package com.obelieve.frame.utils.log;

import android.view.MotionEvent;

/**
 * Created by zxy on 2018/9/19 16:48.
 */

public class MotionEventUtil
{
    public static String name(MotionEvent event)
    {
        String name = null;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                name = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_MOVE:
                name = "ACTION_MOVE";
                break;
            case MotionEvent.ACTION_UP:
                name = "ACTION_UP";
                break;
            case MotionEvent.ACTION_CANCEL:
                name = "ACTION_CANCEL";
                break;
            default:
                name = "MotionEvent action:" + event.getAction();
                break;
        }
        return name;
    }
}
