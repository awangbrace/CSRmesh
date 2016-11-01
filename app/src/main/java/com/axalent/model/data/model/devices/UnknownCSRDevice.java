/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.model.data.model.devices;

/**
 *
 */
public class UnknownCSRDevice extends CSRDevice {

    // Constructor
    public UnknownCSRDevice() {
    }

    @Override
    public int getType() {
        return TYPE_UNKNOWN;
    }

}
