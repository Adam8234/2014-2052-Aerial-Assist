package com.team2052.aerialAssist.drive;

import com.sun.squawk.util.MathUtils;

/**
 *
 * @author 2052 coder
 */
public class Power {

    public static double powf(double base, double power) {
        if (base == 0.0) {
            return 0.0;
        }
        if (power == 0.0) {
            return 1.0;
        }
        if (base > 0.0) {
            return MathUtils.exp(MathUtils.log(base) * power);
        }
        if (base < 0.0) {
            return -MathUtils.exp(MathUtils.log(-base) * power);
        }
        return 0.0;
    }
}
