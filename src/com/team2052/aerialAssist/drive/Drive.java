package com.team2052.aerialAssist.drive;

import com.team2052.aerialAssist.lib.Constants;
import com.team2052.lib.util.DeadZone;
import com.team2052.lib.Updatable;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;

/**
 * Drive for the robot
 *
 * @author Madeline
 */
public class Drive implements Updatable {

    private final Talon talOne = new Talon(1), talTwo = new Talon(2), talThree = new Talon(3), talFour = new Talon(4);
    private final Joystick joy1 = new Joystick(1), joy2 = new Joystick(2);
    private final DeadZone deadZone = new DeadZone(Constants.DEAD_ZONE);

    public void update() {
        double tank = deadZone.isInDeadZone(joy1.getY()) ? Constants.NO_SPEED : Power.powf(joy1.getY(), Constants.SPEED_CURVE);
        double turn = deadZone.isInDeadZone(joy1.getX()) ? Constants.NO_SPEED : Power.powf(joy1.getX(), Constants.SPEED_CURVE);
        double strafe = deadZone.isInDeadZone(joy2.getX()) ? Constants.NO_SPEED : Power.powf(joy2.getX(), Constants.SPEED_CURVE);

        if (joy1.getTrigger()) {
            autoMoveBack(tank, turn, strafe);
        } else {
            autoMove(tank, turn, strafe);
        }
    }

    private void autoMove(double tank, double turn, double strafe) {
        talOne.set(tank + turn + strafe); //bl
        talTwo.set(-tank + turn - strafe);  //fr
        talThree.set(-tank + turn + strafe); //br
        talFour.set(tank + turn - strafe); //fl
    }

    private void autoMoveBack(double tank, double turn, double strafe) {
        talOne.set(-tank + turn - strafe);
        talTwo.set(tank + turn + strafe);
        talThree.set(tank + turn - strafe);
        talFour.set(-tank + turn + strafe);
    }

    public void talSet(double flMove, double frMove, double blMove, double brMove) {
        talOne.set(blMove);
        talTwo.set(-frMove);
        talThree.set(-brMove);
        talFour.set(flMove);
    }

    public void stop() {
        talSet(Constants.NO_SPEED, Constants.NO_SPEED, Constants.NO_SPEED, Constants.NO_SPEED);
    }

}
