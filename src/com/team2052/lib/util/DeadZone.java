package com.team2052.lib.util;

/**
 *
 * @author Adam
 */
public class DeadZone {

    private final double deadzoneVal;

    public DeadZone(double deadzoneVal) {
        this.deadzoneVal = deadzoneVal;
    }

    public boolean isInDeadZone(double joystickVal) {
        return joystickVal > -deadzoneVal && joystickVal < deadzoneVal;
    }
}
