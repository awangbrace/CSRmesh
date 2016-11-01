/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.csrapi;

import android.os.Bundle;

import com.axalent.presenter.RxBus;
import com.axalent.presenter.events.MeshRequestEvent;
import com.csr.csrmesh2.AttentionModelApi;
import com.csr.csrmesh2.MeshConstants;

/**
 *
 */
public class AttentionModel {

    public static int setState(final int deviceId, boolean attractAttention, int duration) {

        Bundle data = new Bundle();
        int id = MeshLibraryManager.getInstance().getNextRequestId();
        data.putInt(MeshLibraryManager.EXTRA_REQUEST_ID, id);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        data.putBoolean(MeshConstants.EXTRA_ATTRACT_ATTENTION, attractAttention);
        data.putInt(MeshConstants.EXTRA_DURATION, duration);
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.ATTENTION_SET_STATE, data));
        return id;
    }


    static void handleRequest(MeshRequestEvent event) {

        int libId = -1;
        int internalId;
        int deviceId;
        boolean attractAttention;
        int duration;

        switch (event.what) {

            case ATTENTION_SET_STATE:
                deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                attractAttention = event.data.getBoolean(MeshConstants.EXTRA_ATTRACT_ATTENTION);
                duration = event.data.getInt(MeshConstants.EXTRA_DURATION);

                // Do API call
                libId = AttentionModelApi.setState(deviceId, attractAttention, duration);
                break;

            default:
                break;
        }
        internalId = event.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
        MeshLibraryManager.getInstance().setRequestIdMapping(libId, internalId);
    }
}
