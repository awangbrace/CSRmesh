/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.csrapi;

import android.os.Bundle;

import com.axalent.presenter.RxBus;
import com.axalent.presenter.events.MeshRequestEvent;
import com.csr.csrmesh2.BatteryModelApi;
import com.csr.csrmesh2.MeshConstants;

public final class BatteryModel {

    public static int getState(int deviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.BATTERY_GET_STATE, data));
        return id;
    }

    static void handleRequest(MeshRequestEvent event) {
        switch (event.what) {
            case BATTERY_GET_STATE:
                int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                int libId = BatteryModelApi.getState(deviceId);
                int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
                MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
                break;
            default:
                break;
        }
    }

}
