/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.model.data.model.devices;

/**
 *
 */
public class TemperatureCSRDevice extends CSRDevice {

    private int temperature;
    private boolean on;


    // Constructor
    public TemperatureCSRDevice() {
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public int getType() {
        return TYPE_TEMPERATURE;
    }

}
