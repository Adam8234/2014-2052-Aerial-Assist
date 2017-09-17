package com.team2052.aerialAssist.shooter;

import com.team2052.aerialAssist.lib.Constants;
import com.team2052.lib.util.TimeUtil;
import com.team2052.lib.Updatable;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Jon, Andy, Sr. Presidente Jenjoe
 */
public class Catapult implements Updatable {

    private static final int COCKED = 0;
    private static final int DRAWING = 1;
    private static final int TRIGGERING = 2;
    private static final int FIRING = 3;

    private final Talon shooterTalon = new Talon(5);
    private final Timer timer = new Timer();
    private final Solenoid trapDoorIn = new Solenoid(3), trapDoorOut = new Solenoid(4);
    private final DigitalInput magnetSensor = new DigitalInput(3);
    private int currentState = COCKED;
    private boolean lastMagValue;
    private final double BURST_TIME = 0.25;

    public Catapult() {
        timer.start();
    }

    public boolean isCocked() {
        return currentState == COCKED;
    }

    public boolean isDrawing() {
        return currentState == DRAWING;
    }

    /**
     * Sets the trap door on the catapult
     *
     * @param state true = up false = down
     */
    public void setTrapDoor(boolean state) {
        trapDoorIn.set(!state);
        trapDoorOut.set(state);
    }

    public void update() {

        if (!lastMagValue && magnetSensor.get()) {
            currentState = COCKED;
        }

        lastMagValue = magnetSensor.get();

        switch (currentState) {
            case FIRING:
                stateFiring();
                return;
            case DRAWING:
                draw();
                return;
            case TRIGGERING:
                stateTriggering();
                return;
            case COCKED:
                setShooterSpeed(Constants.NO_SPEED);
        }

    }

    /**
     * If the time is greater than the BURST_TIME it sets the state to FIRING
     * and sets the shooter speed to zero. If the time is less than the
     * BURST_TIME it keeps drawing to shoot
     */
    private void stateTriggering() {
        if (TimeUtil.isTimeGreaterThan(timer, BURST_TIME)) {
            currentState = FIRING;
            setShooterSpeed(Constants.NO_SPEED);
            timer.reset();
        } else if (TimeUtil.isTimeLessThan(timer, BURST_TIME)) {
            draw();
        }
    }

    /**
     * If the timer is greater than the RECOCK_DELAY it will set the state to
     * DRAWING Timer is used to add a re-cock delay after shooting
     */
    private void stateFiring() {
        if (TimeUtil.isTimeGreaterThan(timer, Constants.RECOCK_DELAY)) {
            currentState = DRAWING;
            draw();
        }
    }

    /**
     * if the current state is COCKED it sets the state to TRIGGERING
     */
    public void shoot() {
        switch (currentState) {
            case COCKED:
                currentState = TRIGGERING;
                draw();
                timer.reset();
                break;
            default:
                setShooterSpeed(Constants.NO_SPEED);
        }
    }

    public void draw() {
        setShooterSpeed(Constants.DRAW_SPEED);
    }

    public void setShooterSpeed(double speed) {
        shooterTalon.set(speed);
    }

}
