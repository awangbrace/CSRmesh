/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.csrapi;

import android.os.Bundle;

import com.axalent.presenter.RxBus;
import com.axalent.presenter.events.MeshRequestEvent;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.SensorModelApi;
import com.csr.csrmesh2.SensorValue;

public class SensorModel {

    public static int getValue(int deviceId, SensorValue.SensorType type1, SensorValue.SensorType type2) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE1, type1.ordinal());
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE2, type2.ordinal());
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_GET_VALUE, data));
        return id;
    }

    public static int setValue(int deviceId, SensorValue value1, SensorValue value2, boolean acknowledged) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putParcelable(MeshConstants.EXTRA_SENSOR_VALUE1, value1);
        data.putParcelable(MeshConstants.EXTRA_SENSOR_VALUE2, value2);
        data.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_SET_VALUE, data));
        return id;
    }

    public static int getTypes(int deviceId, SensorValue.SensorType firstType) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE1, firstType.ordinal());
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_GET_TYPES, data));
        return id;
    }

    public static int setState(int deviceId, SensorValue.SensorType sensorType, int repeatInterval) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE1, sensorType.ordinal());
        data.putInt(MeshConstants.EXTRA_REPEAT_INTERVAL, repeatInterval);
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_SET_STATE, data));
        return id;
    }

    public static int getState(final int deviceId, SensorValue.SensorType sensorType) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshConstants.EXTRA_SENSOR_TYPE1, sensorType.ordinal());
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.SENSOR_GET_STATE, data));
        return id;
    }

    static void handleRequest(MeshRequestEvent event) {
        int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
        int libId = 0;
        SensorValue.SensorType type1 = SensorValue.SensorType.values()[event.data.getInt(MeshConstants.EXTRA_SENSOR_TYPE1)];
        SensorValue.SensorType type2 = SensorValue.SensorType.values()[event.data.getInt(MeshConstants.EXTRA_SENSOR_TYPE2)];
        switch (event.what) {
            case SENSOR_GET_VALUE:
                libId = SensorModelApi.getValue(deviceId, type1, type2);
                break;
            case SENSOR_SET_VALUE:
                SensorValue value1 = event.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE1);
                SensorValue value2 = event.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE2);
                boolean ack = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
                libId = SensorModelApi.setValue(deviceId, value1, value2, ack);
                break;
            case SENSOR_GET_TYPES:
                libId = SensorModelApi.getTypes(deviceId, type1);
                break;
            case SENSOR_SET_STATE:
                int repeatInterval = event.data.getInt(MeshConstants.EXTRA_REPEAT_INTERVAL);
                libId = SensorModelApi.setState(deviceId, type1, repeatInterval);
                break;
            case SENSOR_GET_STATE:
                libId = SensorModelApi.getState(deviceId, type1);
                break;
        }
        if (libId != 0) {
            int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
