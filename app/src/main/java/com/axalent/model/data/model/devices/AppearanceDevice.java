/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.model.data.model.devices;

import com.axalent.view.material.MaterialUtils;

public class AppearanceDevice {
    private byte[] mAppearanceCode;
    private String mShortName;
    private int mAppearance;

    public static int LIGHT_APPEARANCE = 4192;
    public static int HEATER_APPEARANCE = 4195;
    public static int SENSOR_APPEARANCE = 4194;
    public static int GATEWAY_APPEARANCE = 4208;


    public AppearanceDevice(byte[] appearanceCode, String shortName) {
        setAppearanceCode(appearanceCode);
        setShortName(shortName);

        mAppearance = MaterialUtils.convertBytesToInteger(appearanceCode, false);
    }

    public AppearanceDevice(int appearance, String mShortName) {
        mAppearance = appearance;
        setShortName(mShortName == null ? getNameByAppearance() : mShortName);
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String mShortName) {
        this.mShortName = mShortName;
    }

    public byte[] getAppearanceCode() {
        return mAppearanceCode;
    }

    public void setAppearanceCode(byte[] mAppearanceCode) {
        this.mAppearanceCode = mAppearanceCode;
    }

    public int getAppearanceType() {
        return mAppearance;
    }

    private String getNameByAppearance() {
        if (mAppearance == LIGHT_APPEARANCE) {
            return "Light";
        }
        else if (mAppearance == HEATER_APPEARANCE) {
            return "Heater";
        }
        else if (mAppearance == SENSOR_APPEARANCE) {
            return "Sensor";
        }
        else if (mAppearance == GATEWAY_APPEARANCE) {
            return "Gateway";
        }
        else {
            return "Unknown";
        }
    }

}
