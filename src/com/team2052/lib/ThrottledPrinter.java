package com.team2052.lib;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Adam
 */
public class ThrottledPrinter {

    private final double periodSec;
    private double lastPrintTimeSec = 0;

    public ThrottledPrinter(double periodSec) {
        this.periodSec = periodSec;
    }

    public void println(String text) {
        if (Timer.getFPGATimestamp() - lastPrintTimeSec >= periodSec) {
            System.out.println(text);
            lastPrintTimeSec = Timer.getFPGATimestamp();
        }
    }
}
