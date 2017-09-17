package com.team2052.aerialAssist.auto;

import com.team2052.aerialAssist.drive.Drive;
import com.team2052.aerialAssist.shooter.Catapult;
import com.team2052.aerialAssist.shooter.Pickupper;
import com.team2052.aerialAssist.shooter.ShootHandler;
import com.team2052.lib.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Adam, Alex, and Vishnu
 */
public class Autonomous implements Updatable {

    private static final int TWO_BALL_AUTO = 0;
    private static final int ONE_BALL_AUTO = 1;
    private static final int NO_BALL_AUTO = 2;
    private static final int KINECT_AUTO = 3;
    private final Timer time = new Timer();
    private final Catapult catapult;
    private final Drive drive;
    private final Pickupper pickUp;
    private final ShootHandler shootHandler;
    private final KinectArms kinectArms;
    private Thread pickupThread;
    private final DriverStation station = DriverStation.getInstance();
    private final boolean twoBallAuto = station.getDigitalIn(1);
    private final boolean oneBallAuto = station.getDigitalIn(2);
    private final boolean kinectAuto = station.getDigitalIn(3);
    private final double START_DRIVE_FORWARD_TIME = 0.0;
    private final double PICK_UP_DRAG_SPEED = station.getAnalogIn(4);
    private final double DRIVE_RIGHT_SPEED = station.getAnalogIn(3);
    private final double DRIVE_LEFT_SPEED = station.getAnalogIn(2);
    private final double END_DRIVE_FORWARD_DELAY_TIME = station.getAnalogIn(1) + START_DRIVE_FORWARD_TIME;
    private final int autoMode = twoBallAuto ? TWO_BALL_AUTO : (oneBallAuto ? ONE_BALL_AUTO : (kinectAuto ? KINECT_AUTO : NO_BALL_AUTO));
    private boolean toggled = false;
    private double autoTime = 0.0;

    public Autonomous(Catapult catapult, Drive drive, Pickupper pickup, ShootHandler shootHandler, KinectArms kinectArms) {
        this.catapult = catapult;
        this.drive = drive;
        this.pickUp = pickup;
        this.shootHandler = shootHandler;
        this.kinectArms = kinectArms;
        // Speeds and time for driving forward // 1.300// 0.500// 0.700
        time.start();
    }

    public void update() {
        autoTime = time.get();
        catapult.update();

        switch (autoMode) {
            case TWO_BALL_AUTO:
                loopTwoBallAuto();
                break;
            case ONE_BALL_AUTO:
                autoDriveForwardLoop(START_DRIVE_FORWARD_TIME, END_DRIVE_FORWARD_DELAY_TIME);
                catapult.setTrapDoor(true);
                autoShootLoop(5);
                break;
            case NO_BALL_AUTO:
                autoDriveForwardLoop(START_DRIVE_FORWARD_TIME, END_DRIVE_FORWARD_DELAY_TIME);
                break;
            case KINECT_AUTO:
                loopKinectAuto();
                break;
            default:
                System.out.println("There was a problem running autonomous");
        }
    }

    private void loopKinectAuto() {
        autoDriveForwardLoop(START_DRIVE_FORWARD_TIME, END_DRIVE_FORWARD_DELAY_TIME);
        if (!toggled) {
            if (isAutoTimeBetween(END_DRIVE_FORWARD_DELAY_TIME, 6.0) && kinectArms.isBothArmsUp()) {
                toggled = true;
                shootHandler.normalShoot();
            } else if (isAutoTimeGreaterThan(6.0)) {
                toggled = true;
                shootHandler.normalShoot();
            }
        }

    }

    private void loopTwoBallAuto() {
        if (isAutoTimeBetween(START_DRIVE_FORWARD_TIME, END_DRIVE_FORWARD_DELAY_TIME)) {
            pickUp.armDown();
        }

        autoDriveForwardLoop(START_DRIVE_FORWARD_TIME + 1.0, END_DRIVE_FORWARD_DELAY_TIME + 1.0);

        if (isAutoTimeBetween(START_DRIVE_FORWARD_TIME + 1.0, END_DRIVE_FORWARD_DELAY_TIME + 1.0)) {
            pickUp.setPickupSpeed(PICK_UP_DRAG_SPEED);
        }

        if (isAutoTimeBetween(END_DRIVE_FORWARD_DELAY_TIME + 1.6, END_DRIVE_FORWARD_DELAY_TIME + 2.1)) {
            pickUp.talStop();
            pickUp.armUp();
        }

        if (isAutoTimeBetween(END_DRIVE_FORWARD_DELAY_TIME + 2.1, END_DRIVE_FORWARD_DELAY_TIME + 2.3)) {
            shootHandler.normalShoot();
        }

        if (catapult.isDrawing()) {
            catapult.setTrapDoor(false);
        }

        if (catapult.isCocked() && isAutoTimeGreaterThan(END_DRIVE_FORWARD_DELAY_TIME + 3.0) && !toggled) {
            toggled = true;
            pickupThread = new TwoBallPickupShoot();
            pickupThread.start();
        }
    }

    /**
     * Same as other but if we want to drive forward after the stop time
     *
     * @p+ram timeToStart
     * @param timeToStop
     * @param timeToStopStopping
     */
    private void autoDriveForwardLoop(double timeToStart, double timeToStop, double timeToStopStopping) {
        if (isAutoTimeBetween(timeToStart, timeToStop)) {
            driveForward();
        }
        if (isAutoTimeBetween(timeToStop, timeToStopStopping)) {
            drive.stop();
        }
    }

    /**
     * A simple drive forward loop with time parameters.
     *
     * @param timeToStart
     * @param timeToStop
     */
    private void autoDriveForwardLoop(double timeToStart, double timeToStop) {
        if (isAutoTimeBetween(timeToStart, timeToStop)) {
            driveForward();
        } else {
            drive.stop();
        }
    }

    private void driveForward() {
        drive.talSet(DRIVE_LEFT_SPEED, DRIVE_RIGHT_SPEED, DRIVE_LEFT_SPEED, DRIVE_RIGHT_SPEED);
    }

    /**
     *
     * @param timeToShoot time to start autoShooting. putting the arm down
     */
    private void autoShootLoop(double timeToShoot) {
        if (isAutoTimeBetween(timeToShoot, timeToShoot + 0.2)) {
            shootHandler.normalShoot();
        }
    }

    private boolean isAutoTimeBetween(double low, double high) {
        return autoTime >= low && autoTime <= high;
    }

    private boolean isAutoTimeGreaterThan(double time) {
        return autoTime >= time;
    }

    private boolean isAutoTimeLessThan(double time) {
        return autoTime <= time;
    }

    private class TwoBallPickupShoot extends Thread {

        public void run() {
            catapult.setTrapDoor(false);
            pickUp.possess();
            pickUp.armDown();
            Timer.delay(0.9);
            pickUp.armUp();
            pickUp.talStop();
            catapult.setTrapDoor(true);
            Timer.delay(1);
            shootHandler.normalShoot();
        }
    }

}
