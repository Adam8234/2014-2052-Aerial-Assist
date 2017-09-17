package com.team2052.aerialAssist.shooter;

import com.team2052.aerialAssist.lib.Constants;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Mohammed, Kshitij, and Sam
 */
public class Pickupper {

    private final Talon intakeTalon = new Talon(6);
    private final DigitalInput reedSwitch = new DigitalInput(4); //Reed Switch for arm
    private final Solenoid armIn = new Solenoid(1), armOut = new Solenoid(2);

    public void armDown() {
        armIn.set(false);
        armOut.set(true);
    }

    public void armUp() {
        armIn.set(true);
        armOut.set(false);
    }

    public void possess() {
        intakeTalon.set(Constants.PICKUP_POSSESS_SPEED);
    }

    public void talStop() {
        intakeTalon.set(Constants.NO_SPEED);
    }

    public void unpossess() {
        intakeTalon.set(Constants.PICKUP_UNPOSSESS_SPEED);
    }

    public void setPickupSpeed(double speed) {
        intakeTalon.set(speed);
    }

    /**
     *
     * @return if the reed switch is activated
     */
    public boolean getReedSwitch() {
        return !reedSwitch.get();
    }
}
