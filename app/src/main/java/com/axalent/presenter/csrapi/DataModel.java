/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.csrapi;

import android.os.Bundle;

import com.axalent.presenter.RxBus;
import com.axalent.presenter.events.MeshRequestEvent;
import com.csr.csrmesh2.DataModelApi;
import com.csr.csrmesh2.MeshConstants;

public class DataModel {
    public static int sendData(final int deviceId, byte[] data, boolean acknowledged) {
        Bundle databundle = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        databundle.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        databundle.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        databundle.putByteArray(MeshConstants.EXTRA_DATA, data);
        databundle.putBoolean(MeshConstants.EXTRA_ACKNOWLEDGED, acknowledged);
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.DATA_SEND_DATA, databundle));
        return id;
    }

    static void handleRequest(MeshRequestEvent event) {
        int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
        byte[] data = event.data.getByteArray(MeshConstants.EXTRA_DATA);
        boolean ack = event.data.getBoolean(MeshConstants.EXTRA_ACKNOWLEDGED);
        int libId = 0;
        switch (event.what) {
            case DATA_SEND_DATA:
                libId = DataModelApi.sendData(deviceId, data, ack);
                break;
        }
        if (libId != 0) {
            int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
