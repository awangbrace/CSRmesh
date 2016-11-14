/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.events;

import android.os.Bundle;

public class MeshSystemEvent extends MeshEvent {

    public enum SystemEvent {
        SERVICE_SHUTDOWN,
        SERVICE_BIND,
        DEVICE_SCANNED,
        BRIDGE_CONNECTED,
        BRIDGE_DISCONNECTED,
        CHANNEL_READY,
        CHANNEL_NOT_READY,
        PLACE_CHANGED,
        GATEWAY_NOT_CONFIGURED,
        GATEWAY_DISCOVERED,
        GATEWAY_SETUP_POPUP_CLOSED,
        AREA_CHANGED,
        BT_REQUEST,
        DEVICE_STATUS_CHANGE
    }

    ;

    public SystemEvent what;

    public MeshSystemEvent(SystemEvent what) {
        this.what = what;
    }

    public MeshSystemEvent(SystemEvent what, Bundle data) {
        this.what = what;
        this.data = data;
    }

}
