package com.team2052.aerialAssist.shooter;

import edu.wpi.first.wpilibj.Solenoid;

/**
 *
 * @author Adam
 */
public class Puncher {

    private final Solenoid punchOut = new Solenoid(5), punchIn = new Solenoid(6);

    /**
     *
     * @param state true = out false = in
     */
    public void set(boolean state) {
        punchOut.set(state);
        punchIn.set(!state);
    }
}
