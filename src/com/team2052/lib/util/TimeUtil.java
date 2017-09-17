package com.team2052.lib.util;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Adam
 */
public class TimeUtil {

    public static boolean isTimeBetween(Timer timer, double high, double low) {
        return isTimeLessThan(timer, high) && isTimeGreaterThan(timer, low);
    }

    public static boolean isTimeLessThan(Timer timer, double time) {
        return timer.get() <= time;
    }

    public static boolean isTimeGreaterThan(Timer timer, double time) {
        return timer.get() >= time;
    }
}
