package com.Util;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * Created by Loong Fly on 2018/3/16.
 */

public class VibreateUtil {
    public static void Vibrate(final Activity activity, long millisecends){
        /**
         * start vibrate
         */
        Vibrator vib=(Vibrator)activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(millisecends);
    }

    public static void StopVibrate(final Activity activity){
        Vibrator vib=(Vibrator)activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
    }
}
