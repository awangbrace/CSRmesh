/*
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 */

package com.axalent.presenter.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.axalent.model.data.database.DBManager;
import com.axalent.model.data.model.Gateway;
import com.axalent.presenter.RxBus;
import com.axalent.presenter.csrapi.ConfigGateway;
import com.axalent.presenter.csrapi.MeshLibraryManager;
import com.axalent.presenter.csrapi.NetServiceBrowser;
import com.axalent.presenter.csrapi.RestChannel;
import com.axalent.presenter.events.MeshResponseEvent;
import com.axalent.presenter.events.MeshSystemEvent;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.MeshService;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;


/**
 * Service which runs in the background and retrieves the GW avaiable using bonjour and the GW stored
 * in our DB to determine which should be the selected GW.
 */
public class SearchSelectedGatewayService extends Service {

    private final String TAG = "SearchSelectedGatewaySe";
    private final int SERVICE_TIMEOUT = 15000;

    private ArrayList<Gateway> mScannedGateways = new ArrayList<>(); // without UUID
    private List<Gateway> mAssociatedGatewayList;
    private Gateway mGatewayRetrievingUUID = null;
    private Handler mTimeoutHandler = new Handler();

    private Subscription subscription;
    private Subscription subscriptionResponse;

    private Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            completeScannedGatewayProcess(true);
        }
    };

    @Override
    public void onCreate() {
        subscribeEvent();
        subscriptionResponse();
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        subscriptionResponse.unsubscribe();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Retrieve all associated GW for current place
        DBManager mDBManager = DBManager.getDBManagerInstance(getApplicationContext());
        mAssociatedGatewayList = mDBManager.getAllGatewaysFromCurrentPlace();
        if(mAssociatedGatewayList.size() > 0) {
            // Search for Selected GW
            startBrowsing();
        } else {
            closeService();
        }

        return Service.START_NOT_STICKY;
    }


    private void subscribeEvent() {
        subscription = RxBus.getDefaultInstance()
                .toObservable(MeshSystemEvent.class)
                .subscribe(new Action1<MeshSystemEvent>() {
                    @Override
                    public void call(MeshSystemEvent meshSystemEvent) {
                        switch (meshSystemEvent.what) {
                            case GATEWAY_DISCOVERED: {
                                // When a GW is discovered retrieve details
                                String name = meshSystemEvent.data.getString(MeshConstants.EXTRA_GATEWAY_NAME);
                                String host = meshSystemEvent.data.getString(MeshConstants.EXTRA_GATEWAY_HOST);
                                host = host.replace("/", "");
                                String port = meshSystemEvent.data.getString(MeshConstants.EXTRA_GATEWAY_PORT);

                                Gateway gateway = new Gateway(name, host, port);
                                mScannedGateways.add(gateway);

                                // If currently we are not retrieving any UUID of any GW start retrieving UUID for pending GW
                                if(mGatewayRetrievingUUID == null) {
                                    retrieveUUIDOfPendingGateways();
                                }
                                break;
                            }
                        }
                    }
                });
    }

    /**
     * Retrieve the UUID of the pending Gateways
     */
    public void retrieveUUIDOfPendingGateways() {

        // Take next scannedGateway
        mGatewayRetrievingUUID = mScannedGateways.get(0);

        // Set REST host and port to point to GATEWAY
        RestChannel.setRestParameters(MeshService.ServerComponent.CONFIG, mGatewayRetrievingUUID.getHost(), mGatewayRetrievingUUID.getPort(), RestChannel.BASE_PATH_CGI + RestChannel.BASE_PATH_CONFIG, RestChannel.URI_SCHEMA_HTTP);

        // set Application code in MeshService
        MeshLibraryManager.getInstance().setApplicationCode(RestChannel.REST_APPLICATION_CODE);

        // Retrieve profiles (uuid and basePath)
        try {
            ConfigGateway.getProfile();
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public void subscriptionResponse() {
        subscriptionResponse = RxBus.getDefaultInstance()
                .toObservable(MeshResponseEvent.class)
                .subscribe(new Action1<MeshResponseEvent>() {
                    @Override
                    public void call(MeshResponseEvent meshResponseEvent) {
                        switch (meshResponseEvent.what) {
                            case GATEWAY_PROFILE: {
                                String uuid = meshResponseEvent.data.getString(MeshConstants.EXTRA_GATEWAY_UUID);
                                String basePath = meshResponseEvent.data.getString(MeshConstants.EXTRA_GATEWAY_BASE_PATH);
                                mGatewayRetrievingUUID.setUuid(uuid);
                                mGatewayRetrievingUUID.setBasePath(basePath);

                                // Check if discovered gateway is already associated
                                boolean alreadyAssociated = false;
                                for(Gateway associatedGw : mAssociatedGatewayList) {
                                    if(associatedGw.getUuid().equals(mGatewayRetrievingUUID.getUuid())) {
                                        alreadyAssociated = true;
                                        break;
                                    }
                                }

                                // Set Selected GW and also the appropriate Rest parameters
                                if (alreadyAssociated) {
                                    MeshLibraryManager.getInstance().setSelectedGatewayUUID(mGatewayRetrievingUUID.getUuid());
                                    RestChannel.setRestParameters(MeshService.ServerComponent.CONFIG, mGatewayRetrievingUUID.getHost(), mGatewayRetrievingUUID.getPort(), RestChannel.BASE_PATH_CGI + RestChannel.BASE_PATH_CONFIG, RestChannel.URI_SCHEMA_HTTP);
                                }

                                completeScannedGatewayProcess();
                                break;
                            }

                            case TIMEOUT:
                            case ERROR:
                                int expectedMessage = meshResponseEvent.data.getInt(MeshConstants.EXTRA_EXPECTED_MESSAGE);
                                int errorCode = meshResponseEvent.data.getInt(MeshConstants.EXTRA_ERROR_CODE);

                                if(expectedMessage == MeshConstants.MESSAGE_GATEWAY_PROFILE) {
                                    completeScannedGatewayProcess(true);
                                }
                                String message = "Unknown error " + errorCode + " - Expected Operation Message ID: " + expectedMessage;
                                Log.e(TAG, message);
                                break;
                        }
                    }
                });
    }

    private void completeScannedGatewayProcess() {
        completeScannedGatewayProcess(false);
    }

    private void completeScannedGatewayProcess(boolean force) {
        mScannedGateways.remove(mGatewayRetrievingUUID);
        mGatewayRetrievingUUID = null;
        if(mScannedGateways.size() == 0 || force) {
            mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
            stopBrowsing();
            closeService();

        } else {
            retrieveUUIDOfPendingGateways();
        }
    }

    private void startServiceTimeout() {
        mTimeoutHandler.postDelayed(mTimeoutRunnable, SERVICE_TIMEOUT);
    }

    private void startBrowsing() {
        NetServiceBrowser.startBrowsing();
        startServiceTimeout();
    }

    private void stopBrowsing() {
        NetServiceBrowser.stopBrowsing();
    }


    private void closeService() {
        Log.d(TAG, "Stopping SearchSelectedGatewayService");
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
