/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.csrapi;

import android.os.Bundle;

import com.axalent.presenter.RxBus;
import com.axalent.presenter.events.MeshRequestEvent;
import com.csr.csrmesh2.ActuatorModelApi;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.sensor.SensorValue;

/**
 *
 */
public class ActuatorModel {

    public static int setValue(final int deviceId, SensorValue value1, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putParcelable(MeshConstants.EXTRA_SENSOR_VALUE1, value1);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ACTUATOR_SET_VALUE, data));
        return id;
    }

    public static int getTypes(final int deviceId, SensorValue.SensorType firstType) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE, firstType.ordinal());
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ACTUATOR_GET_TYPES, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {

        int libId = -1;
        int internalId;
        int deviceId;
        SensorValue value1;
        int firstType;
        boolean acknowledged;

        switch (event.what) {

            case ACTUATOR_SET_VALUE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                value1 = event.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE1);
                acknowledged = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
                // Do API call
                libId = ActuatorModelApi.setValue(deviceId, value1, acknowledged);
                break;

            case ACTUATOR_GET_TYPES:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                firstType = event.data.getInt(MeshConstants.EXTRA_SENSOR_TYPE);
                // Do API call
                libId = ActuatorModelApi.getTypes(deviceId, SensorValue.SensorType.values()[firstType]);
                break;

            default:
                break;
        }
        internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
        MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
    }
}
