/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.csrapi;

import com.axalent.presenter.RxBus;
import com.axalent.presenter.events.MeshRequestEvent;

public class NetServiceBrowser {

    /**
     * Start browsing for services using Bonjour.
     * Response event is GATEWAY_DISCOVERED with data:
     * <ul>
     * <li>EXTRA_GATEWAY_HOST</li>
     * <li>EXTRA_GATEWAY_PORT</li>
     * </ul>
     */
    public static void startBrowsing() {
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.START_BROWSING_GATEWAYS));
    }

    public static void stopBrowsing() {
        RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.STOP_BROWSING_GATEWAYS));
    }

    // Request is handled in MeshLibraryManager as no request id needs to be mapped for these calls.
}
