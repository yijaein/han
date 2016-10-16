package kr.co.multi.beacontest;

import android.content.Context;
import android.os.PowerManager;

public class AlarmWakeLock {
    private static PowerManager.WakeLock mWakeLock;

    public static void wakeLock(Context context) {
        if (mWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);

        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWake");
        mWakeLock.acquire();
    }

    public static void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}