/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.events;

import android.os.Bundle;

public class MeshResponseEvent extends MeshEvent {

    public enum ResponseEvent {
        ERROR,
        DEVICE_UUID,
        DEVICE_APPEARANCE,
        ASSOCIATION_PROGRESS,
        TIMEOUT,
        DEVICE_ASSOCIATED,

        // Actuator model
        ACTUATOR_TYPES,
        ACTUATOR_VALUE,

        // Attention model
        ATTENTION_STATE,

        // Battery model
        BATTERY_STATE,

        // Bearer model
        BEARER_STATE,

        // Config model
        CONFIG_PARAMETERS,
        CONFIG_DEVICE_IDENTIFIER,
        CONFIG_INFO,

        // Data model
        DATA_RECEIVE_STREAM,
        DATA_RECEIVE_BLOCK,
        DATA_RECEIVE_STREAM_END,
        DATA_SENT,

        // Firmware model
        FIRMWARE_VERSION_INFO,
        FIRMWARE_UPDATE_ACKNOWLEDGED,

        // Group model
        GROUP_NUMBER_OF_MODEL_GROUPIDS,
        GROUP_MODEL_GROUPID,

        // Light model
        LIGHT_STATE,

        // Ping model
        PING_RESPONSE,

        // Power model
        POWER_STATE,

        // Sensor model
        SENSOR_TYPES,
        SENSOR_STATE,
        SENSOR_VALUE,

        // Config gateway
        GATEWAY_PROFILE,
        GATEWAY_REMOVE_NETWORK,

        // Config cloud
        TENANT_RESULTS,
        TENANT_CREATED,
        TENANT_INFO,
        TENANT_DELETED,
        TENANT_UPDATED,
        SITE_RESULTS,
        SITE_CREATED,
        SITE_INFO,
        SITE_DELETED,
        SITE_UPDATED,

        // refresh
        REFRESH_PAGE,
        DATABASE_UPDATE,
        POWER_STATUS
    }

    public ResponseEvent what;

    public MeshResponseEvent(ResponseEvent what) {
        this.what = what;
    }

    public MeshResponseEvent(ResponseEvent what, Bundle data) {
        this.what = what;
        this.data = data;
    }
}
