/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.model.data.model.devices;

import com.axalent.model.data.model.devices.states.ModelLightState;
import com.axalent.model.data.model.devices.states.ModelPowerState;
import com.axalent.model.data.model.devices.states.ModelState;
import com.csr.csrmesh2.PowerState;


/**
 *
 */
public class LightCSRDevice extends CSRDevice {


    // Constructor
    public LightCSRDevice() {
    }

    @Override
    public int getType() {
        return TYPE_LIGHT;
    }


    /**
     * Get light state
     *
     * @return ModelLightState
     */
    public ModelLightState getLightState() {
        ModelState state = getState(ModelLightState.LIGHT_STATE_KEY);

        if (state != null) {
            return (ModelLightState) state;
        }
        else {
            return null;
        }
    }

    /**
     * Get power state
     *
     * @return PowerState
     */
    public ModelPowerState getPowerState() {
        ModelState state = getState(ModelPowerState.POWER_STATE_KEY);

        if (state != null) {
            return (ModelPowerState) state;
        }
        else {
            return null;
        }
    }

    private ModelPowerState getPowerStateOrCreate() {
        ModelPowerState state = getPowerState();
        if (state == null) {
            state = new ModelPowerState();
        }
        return state;
    }

    private ModelLightState getLightStateOrCreate() {
        ModelLightState state = getLightState();
        if (state == null) {
            state = new ModelLightState();
        }
        return state;
    }

    public void setColor(int color) {

        ModelLightState lightState = getLightStateOrCreate();
        lightState.setColor(color);
        setState(ModelLightState.LIGHT_STATE_KEY, lightState);
    }

    public void setPowerState(PowerState state) {
        ModelPowerState powerState = getPowerStateOrCreate();
        powerState.setState(state);
        setState(ModelPowerState.POWER_STATE_KEY, powerState);
    }
}
