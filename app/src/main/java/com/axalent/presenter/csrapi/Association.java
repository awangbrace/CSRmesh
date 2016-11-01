/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.csrapi;

import android.os.Bundle;

import com.axalent.presenter.RxBus;
import com.axalent.presenter.events.MeshRequestEvent;
import com.csr.csrmesh2.MeshConstants;

public class Association {
    public static void discoverDevices(boolean enabled) {
        Bundle data = new Bundle();
        data.putBoolean(MeshConstants.EXTRA_ENABLED, enabled);
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.DISCOVER_DEVICES, data));
    }

    public static int associateDevice(int deviceHash, long authorizationCode, boolean authorizationCodeKnown, int deviceId) {
        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshConstants.EXTRA_UUIDHASH_31, deviceHash);
        data.putLong(MeshConstants.EXTRA_AUTH_CODE, authorizationCode);
        data.putBoolean(MeshConstants.EXTRA_AUTH_CODE_KNOWN, authorizationCodeKnown);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ASSOCIATE_DEVICE, data));
        return id;
    }

    public static void attentionPreAssociation(int deviceHash, boolean enabled, int duration) {
        Bundle data = new Bundle();
        data.putInt(MeshConstants.EXTRA_UUIDHASH_31, deviceHash);
        data.putBoolean(MeshConstants.EXTRA_ENABLED, enabled);
        data.putInt(MeshConstants.EXTRA_DURATION, duration);
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ATTENTION_PRE_ASSOCIATION, data));
    }

    /*package*/
    static void handleRequest(MeshRequestEvent event) {
        int libId = 0;
        switch (event.what) {
            case DISCOVER_DEVICES:
                MeshLibraryManager.getInstance().getMeshService().setDeviceDiscoveryFilterEnabled(event.data.getBoolean(MeshConstants.EXTRA_ENABLED));
                break;
            case ATTENTION_PRE_ASSOCIATION:
                MeshLibraryManager.getInstance().getMeshService().setAttentionPreAssociation(
                        event.data.getInt(MeshConstants.EXTRA_UUIDHASH_31),
                        event.data.getBoolean(MeshConstants.EXTRA_ENABLED),
                        event.data.getInt(MeshConstants.EXTRA_DURATION));
                break;
            case ASSOCIATE_DEVICE:
                libId = MeshLibraryManager.getInstance().getMeshService().associateDevice(
                        event.data.getInt(MeshConstants.EXTRA_UUIDHASH_31),
                        event.data.getLong(MeshConstants.EXTRA_AUTH_CODE),
                        event.data.getBoolean(MeshConstants.EXTRA_AUTH_CODE_KNOWN),
                        event.data.getInt(MeshConstants.EXTRA_DEVICE_ID));

                break;
        }
        if (libId != 0) {
            int internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
            MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
        }
    }
}
