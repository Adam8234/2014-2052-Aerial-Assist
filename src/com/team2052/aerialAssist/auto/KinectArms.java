package com.team2052.aerialAssist.auto;

import edu.wpi.first.wpilibj.KinectStick;

/**
 *
 * @author Adam
 */
public class KinectArms {

    private final KinectStick leftArm = new KinectStick(1), rightArm = new KinectStick(2);
    private final double ARM_UP = -1.0;

    public boolean isLeftArmUp() {
        return getLeftArmY() == ARM_UP;
    }

    public boolean isRightArmUp() {
        return getRightArmY() == ARM_UP;
    }

    public boolean isBothArmsUp() {
        return isLeftArmUp() && isRightArmUp();
    }

    public double getLeftArmY() {
        return leftArm.getY();
    }

    public double getRightArmY() {
        return rightArm.getY();
    }
}
