/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.model.data.model.devices;

/**
 *
 */
public class DeviceFactory {

    public static CSRDevice getDevice(final int appearance) {

        if (appearance == AppearanceDevice.LIGHT_APPEARANCE) {
            return new LightCSRDevice();
        }
        else if (appearance == AppearanceDevice.HEATER_APPEARANCE || appearance == AppearanceDevice.SENSOR_APPEARANCE) {
            return new TemperatureCSRDevice();
        }
        else if (appearance == AppearanceDevice.GATEWAY_APPEARANCE) {
            return new GatewayCSRDevice();
        }
        else {
            return new UnknownCSRDevice();
        }
    }
}
