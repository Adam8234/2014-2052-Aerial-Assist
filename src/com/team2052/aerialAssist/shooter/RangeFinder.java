/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team2052.aerialAssist.shooter;

import com.team2052.lib.Updatable;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author coding
 */
public class RangeFinder extends SensorBase implements Updatable {

    private final AnalogChannel analogChannel;
    private final double VOLTS_TO_CM = 0.0049;
    private final double CM_TO_FT = 0.328;

    //Range for robot as of 5/10/14
    private final double MAX_RANGE = 15.0;
    private final double MIN_RANGE = 7.5;

    public RangeFinder(int channel) {
        analogChannel = new AnalogChannel(channel);
    }

    public double getVoltage() {
        return analogChannel.getVoltage();
    }
    
    public double getRangeinCM() {
        return getVoltage() / VOLTS_TO_CM;
    }

    public double getRangeinFt() {
        return getRangeinCM() * CM_TO_FT;
    }

    public void update() {
        double range = getRangeinFt();
        SmartDashboard.putNumber("Range", range);
        SmartDashboard.putBoolean("is in range", (range < MAX_RANGE && range > MIN_RANGE));
    }

}
