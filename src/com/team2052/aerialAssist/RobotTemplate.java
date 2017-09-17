package com.team2052.aerialAssist;

import com.team2052.aerialAssist.auto.*;
import com.team2052.aerialAssist.drive.Drive;
import com.team2052.aerialAssist.shooter.*;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;

public class RobotTemplate extends SimpleRobot {

    private Drive drive;
    private Pickupper pickUp;
    private Puncher puncher;
    private Catapult catapult;
    private Compressor compress;
    private ShootHandler shootHandler;
    private KinectArms kinectArms;
    private Autonomous autonomous;
    private RangeFinder range;

    public void robotInit() {
        compress = new Compressor(1, 1);
        drive = new Drive();
        pickUp = new Pickupper();
        puncher = new Puncher();
        catapult = new Catapult();
        shootHandler = new ShootHandler(pickUp, catapult, puncher);
        kinectArms = new KinectArms();
        range = new RangeFinder(7);
        System.out.println("Code Deployment Successful");
    }

    public void autonomous() {
        autonomous = new Autonomous(catapult, drive, pickUp, shootHandler, kinectArms);
        compress.start();
        while (isAutonomous() && isEnabled()) {
            autonomous.update();
            Timer.delay(0.05);
        }
    }

    public void operatorControl() {
        compress.start();
        catapult.setTrapDoor(true);
        pickUp.armUp();
        puncher.set(false);
        while (isEnabled() && isOperatorControl()) {
            drive.update();
            shootHandler.update();
            range.update();
            Timer.delay(0.05);
        }
    }

    /**
     * This will be used for drivers to test before a match. You can use this to
     * charge the compressor and check to see if driving works. I can add things
     * per request.
     */
    public void test() {
        pickUp.armUp();
        catapult.setTrapDoor(true);
        puncher.set(false);
        compress.start();
        while (isTest() && isEnabled()) {
            drive.update();
            range.update();
            Timer.delay(0.05);
        }
    }

}
