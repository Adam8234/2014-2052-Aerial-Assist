package com.team2052.aerialAssist.shooter;

import com.team2052.aerialAssist.lib.Constants;
import com.team2052.lib.util.TimeUtil;
import com.team2052.lib.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

/**
 * Handles all the shoot functions for secondary driver
 *
 * @author Adam
 */
public class ShootHandler implements Updatable {

    private Thread shootThread;
    private boolean lastPickUp;
    private final Pickupper pickUp;
    private final Catapult catapult;
    private final Puncher puncher;
    private final Timer timer = new Timer(), shootTimer = new Timer();
    private final Joystick joy3 = new Joystick(3);
    private final DriverStation driverStation = DriverStation.getInstance();

    public ShootHandler(Pickupper pickUp, Catapult catapult, Puncher puncher) {
        this.puncher = puncher;
        this.pickUp = pickUp;
        this.catapult = catapult;
    }

    public void update() {

        boolean manualOverride = driverStation.getDigitalIn(8);

        if (!manualOverride) {
            catapult.update();
        }

        if (lastPickUp && !joy3.getRawButton(3) && TimeUtil.isTimeGreaterThan(timer, Constants.PICKUP_DELAY)) {
            puncher.set(false);
            pickUp.talStop();
            pickUp.armUp();
            catapult.setTrapDoor(true);
            timer.stop();
            timer.reset();
            lastPickUp = false;
        }

        if (manualOverride) {
            overrideMode();
        } else {
            normalMode();
        }

    }

    private void normalMode() {
        //Pickup and shoot functions
        if (joy3.getRawButton(3) && !joy3.getRawButton(4)) { //normal pickup is joy 3 button 3
            pickUpBall();
        } else if (!joy3.getRawButton(3) && joy3.getRawButton(4)) { //slow pass is button 4 on joy 3
            slowPass();
        } else if (joy3.getTrigger()) {
            normalShoot();
        } else if (joy3.getRawButton(2)) {
            kissRecive();
        } else if (joy3.getRawButton(6)) {
            trussShoot();
        } else {
            pickUp.talStop();
            puncher.set(false);
        }

        if (shootThread != null && !shootThread.isAlive()) {
            catapult.setTrapDoor(true);
            pickUp.armUp();
            pickUp.talStop();
            puncher.set(false);
            shootTimer.stop();
            shootTimer.reset();
            shootThread = null;
        }

    }

    private void overrideMode() {
        //This is so we can shoot in override mode
        if (joy3.getRawButton(11) && joy3.getRawButton(10)) {
            catapult.setShooterSpeed(Constants.OVERRIDE_SHOOTER_SPEED);
            pickUp.armDown();
        } else if (joy3.getRawButton(11) && !joy3.getRawButton(10)) {
            catapult.setShooterSpeed(Constants.OVERRIDE_SHOOTER_SPEED);
            pickUp.armUp();
        } else if (!joy3.getRawButton(11) && joy3.getRawButton(10)) {
            pickUp.armDown();
            catapult.setShooterSpeed(Constants.NO_SPEED);
        } else if (!joy3.getRawButton(11) && !joy3.getRawButton(10)) {
            pickUp.armUp();
            catapult.setShooterSpeed(Constants.NO_SPEED);
        }
        if (joy3.getRawButton(3) && !joy3.getRawButton(4)) { //normal pickup is joy 3 button 3
            pickUpBall();
        } else if (!joy3.getRawButton(3) && joy3.getRawButton(4)) { //slow pass is button 4 on joy 3
            slowPass();
        } else if (joy3.getRawButton(2)) {
            kissRecive();
        } else {
            pickUp.talStop();
            puncher.set(false);
        }
    }

    private void kissRecive() {
        catapult.setTrapDoor(false);
        puncher.set(false);
        pickUp.armUp();
        pickUp.possess();
        timer.start();
        lastPickUp = true;
    }

    private void slowPass() {
        catapult.setTrapDoor(false);
        pickUp.armUp();
        Timer.delay(.3);
        puncher.set(true);
        pickUp.unpossess();
    }

    private void trussShoot() {
        if (shootThread != null && shootThread.isAlive()) {
            return;
        }
        shootThread = new TrussShot();
        shootThread.start();
    }

    public void normalShoot() {
        if (shootThread != null && shootThread.isAlive()) {
            return;
        }
        shootThread = new NormalShot();
        shootThread.start();
    }

    private void pickUpBall() {
        catapult.setTrapDoor(false);
        puncher.set(false);
        pickUp.armDown();
        pickUp.possess();
        timer.start();
        lastPickUp = true;
    }

    private class NormalShot extends Thread {

        public void run() {
            catapult.setTrapDoor(true);
            puncher.set(false);
            
            if (driverStation.getDigitalIn(7)) {
                pickUp.armDown();
                Timer.delay(Constants.ARM_SHOOT_DELAY);
            } else {
                shootTimer.start();
                while (!pickUp.getReedSwitch()) {
                    pickUp.armDown();
                    if (TimeUtil.isTimeGreaterThan(shootTimer, Constants.REED_TIME_OUT)) {
                        return;
                    }
                }
            }
            
            catapult.shoot();
        }

    }

    private class TrussShot extends Thread {

        public void run() {
            puncher.set(false);
            pickUp.armDown();
            Timer.delay(Constants.ARM_SHOOT_DELAY);
            catapult.setTrapDoor(false);
            Timer.delay(Constants.TRUSS_TRAP_DELAY);
            catapult.shoot();
            Timer.delay(2);
        }

    }
}
