package com.axalent.model.data.model.devices;

/**
 * File Name                   : GatewayCSRDevice
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/9/28
 * Revision History            : 16:58
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */

public class GatewayCSRDevice extends CSRDevice {
    @Override
    public int getType() {
        return TYPE_GATEWAY;
    }
}
