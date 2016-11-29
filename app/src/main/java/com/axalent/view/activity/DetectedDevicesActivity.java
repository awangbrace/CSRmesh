package com.axalent.view.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.model.data.database.DBManager;
import com.axalent.model.data.model.devices.AppearanceDevice;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.model.data.model.devices.ScanCSRDevice;
import com.axalent.model.data.model.devices.UnknownCSRDevice;
import com.axalent.presenter.RxBus;
import com.axalent.presenter.csrapi.Association;
import com.axalent.presenter.csrapi.BluetoothChannel;
import com.axalent.presenter.csrapi.ConfigModel;
import com.axalent.presenter.csrapi.GroupModel;
import com.axalent.presenter.events.MeshResponseEvent;
import com.axalent.util.AxalentUtils;
import com.axalent.adapter.DiscoveredDevicesAdapter;
import com.axalent.util.LogUtils;
import com.axalent.view.material.ButtonFlatMaterial;
import com.axalent.view.material.Constants;
import com.axalent.view.material.DialogMaterial;
import com.axalent.view.material.ProgressBarDeterminateMaterial;
import com.csr.csrmesh2.ConfigModelApi;
import com.csr.csrmesh2.DeviceInfo;
import com.csr.csrmesh2.MeshConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;

/**
 * File Name                   : DetectedDevicesActivity
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/9/8
 * Revision History            : 14:49
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */
public class DetectedDevicesActivity extends BaseActivity {

    private ScanCSRDevice mDeviceSelected;

    private DiscoveredDevicesAdapter mDiscoveredDevicesAdapter;

    private ArrayList<ScanCSRDevice> mDiscoveredDevices = new ArrayList<ScanCSRDevice>();
    private HashMap<Integer, AppearanceDevice> mAppearances = new HashMap<Integer, AppearanceDevice>();
    // A list of model ids that are waiting on a query being sent to find out how many groups are supported.
    private Queue<Integer> mModelsToQueryForGroups = new LinkedList<Integer>();

    private ButtonFlatMaterial mNextButton;
    private SwipeRefreshLayout mSwipeContainer;
    private ProgressBar barSearchBar;
    private DialogMaterial dialog = null;
    private ProgressBarDeterminateMaterial dialogProgressBar;
    private TextView progressText = null;
    private EditText authCodeText;
    private DBManager dbManager;

    private android.os.Handler mHandler = new android.os.Handler();

    private CSRDevice mTempCSRDevice;
    private DeviceInfo mCurrentRequestState = null;

    private static final int SCANNING_PERIOD_MS = 20 * 1000;
    public final static int FIRST_ID_IN_RANGE = 0x8001;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected);
        initActionBar();
        subscribeEvent();

        mDiscoveredDevicesAdapter = new DiscoveredDevicesAdapter(this);
        dbManager = DBManager.getDBManagerInstance(this.getApplicationContext());

        ListView discoveredList = (ListView) findViewById(R.id.list);
        discoveredList.setEmptyView(findViewById(R.id.empty_view));
        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        barSearchBar = (ProgressBar) findViewById(R.id.barSearchBar);
        mNextButton = (ButtonFlatMaterial) findViewById(R.id.selectDevice);
        setSelectedDevice(null);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askAuthCode(mDeviceSelected);
            }
        });

        discoveredList.setAdapter(mDiscoveredDevicesAdapter);
        discoveredList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setSelectedDevice(getDeviceFromList(position));
            }
        });

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startScanning();
            }
        });

    }

    private void initActionBar() {
        View content = findViewById(R.id.barSearchContent);
        RelativeLayout backLayour = (RelativeLayout) content.findViewById(R.id.barSearchBack);
        backLayour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView barText = (TextView) content.findViewById(R.id.barSearchTitleTxt);
        barText.setText(this.getString(R.string.detected_device));

    }

    public void askAuthCode(final ScanCSRDevice device) {
        if (dialog == null) {
            dialog = new DialogMaterial(this, getString(R.string.security), null);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.addCancelButton(getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    deselectDevice();
                    dialog = null;
                }
            });
            dialog.addAcceptButton(getString(R.string.next), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String authcode = authCodeText.getText().toString();
                    boolean isNext = false;
                    if (device.appearance.getAppearanceType() == AppearanceDevice.LIGHT_APPEARANCE
                            || device.appearance.getAppearanceType() == AppearanceDevice.GATEWAY_APPEARANCE) {
                        isNext = true;
                    }
                    else {
                        if (authcode.isEmpty()) {
                            isNext = false;
                        } else {
                            isNext = true;
                        }
                    }

                    if (isNext) {
                        try {

                            if (authcode.length() != 0) {
                                long auth = ((Long.parseLong(authcode.substring(0, 8), 16) & 0xFFFFFFFFFFFFFFFFL) << 32)
                                        | ((Long.parseLong(authcode.substring(8), 16) & 0xFFFFFFFFFFFFFFFFL));
                                device.setAuthCode(auth);
                            }


                        } catch (Exception e) {
                            Toast.makeText(DetectedDevicesActivity.this, getString(R.string.wrong_auth_code), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            dialog = null;
                            deselectDevice();
                            hide_keyboard();
                            return;
                        }
                        dialog.dismiss();
                        dialog = null;
                        hide_keyboard();
                        startAssociation(device);
                    } else {
                        Toast.makeText(DetectedDevicesActivity.this, getString(R.string.input_auth_code), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.fragment_security, null);

        authCodeText = (EditText) vi.findViewById(R.id.auth_code);

        dialog.setBodyView(vi);
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startScanning();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScanning();
    }

    private void setSelectedDevice(ScanCSRDevice device) {
        mDeviceSelected = device;

        // Update "associate device" button if selection state has changed.
        if (device != null) {
            mNextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            mNextButton.setTextColor(Color.BLACK);
            mNextButton.setText(this.getString(R.string.associate_device));
            mNextButton.setEnabled(true);
            mDiscoveredDevicesAdapter.setHashSelected(mDeviceSelected.getUuidHash());
        }
        else {
            mNextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.background_led_disabled));
            mNextButton.setTextColor(Color.BLACK);
            mNextButton.setText(this.getString(R.string.select_a_device));
            mNextButton.setEnabled(false);
            mDiscoveredDevicesAdapter.setHashSelected(0);
        }
        updateList(null);
    }

    public void updateList(final ArrayList<ScanCSRDevice> scanDevices) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDiscoveredDevicesAdapter.updateDevices(scanDevices);
                }
            });
    }

    public ArrayList<ScanCSRDevice> getDiscoveredDevices() {
        ArrayList<ScanCSRDevice> array = new ArrayList<>();

        Iterator<ScanCSRDevice> iterator = mDiscoveredDevices.iterator();
        while(iterator.hasNext()) {
            ScanCSRDevice scanDevice = iterator.next();

            // Applying a filter.
            if (scanDevice.hasAppearance() && scanDevice.getAppearance() != AppearanceDevice.GATEWAY_APPEARANCE && !scanDevice.getName().toLowerCase().contains("csrmeshgw")) {
                array.add(scanDevice);
            }
        }

        return array;
    }

    public ScanCSRDevice getDeviceFromList(int position) {
        return mDiscoveredDevicesAdapter.getItem(position);
    }

    public void deselectDevice() {
        setSelectedDevice(null);
    }

    private Runnable scanningTimeOutCallback = new Runnable() {
        @Override
        public void run() {
            stopScanning();
        }
    };

    public void hideProgressBar() {
        barSearchBar.setVisibility(View.INVISIBLE);
    }
    public void showProgressBar() {
        barSearchBar.setVisibility(View.VISIBLE);
    }

    private void startScanning() {
        mHandler.postDelayed(scanningTimeOutCallback, SCANNING_PERIOD_MS);
        mSwipeContainer.setRefreshing(false);
        Association.discoverDevices(true);
        showProgressBar();
    }

    private void stopScanning() {
        Association.discoverDevices(false);
        mHandler.removeCallbacks(scanningTimeOutCallback);
        hideProgressBar();
    }

    /**
     * This method request start an association and displays a dialog to show the association progress.
     */
    private int mAssociationRequestId;
    public void startAssociation(ScanCSRDevice device) {

        List<Integer> devicesIdList = dbManager.getAllDevicesIDsList();
        Log.i("test", "devicesIdList: " + devicesIdList.size());
        int deviceId = FIRST_ID_IN_RANGE;
        if (devicesIdList.size() > 0) {
            //TODO find gaps.
            deviceId = devicesIdList.get(devicesIdList.size() - 1) + 1;
        }
        Log.i("test", "deviceId: " + deviceId);
        mAssociationRequestId = Association.associateDevice(device.getUuidHash(), device.getAuthCode(), device.getAuthCode() != Constants.INVALID_VALUE, deviceId);
        Log.i("test", "mAssociationRequestId: " + mAssociationRequestId);

        if (dialog == null) {
            dialog = new DialogMaterial(this, getString(R.string.associating_device), null);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.addCancelButton(getString(R.string.cancel));
            dialog.setOnCancelButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean success = true;
                    try {
                        BluetoothChannel.cancelTransaction(mAssociationRequestId);
                    }
                    catch (IllegalArgumentException e) {
                        success = false;
                    }
                    if (success) {
                        deselectDevice();
                        dialog.dismiss();
                        dialog = null;
                        finish();
                    }
                    else {
                        // As we disable the cancel button this should never happen, but handle the case anyway.
                        Toast.makeText(getBaseContext(), "Could not cancel!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        ButtonFlatMaterial cancelButton = dialog.getButtonCancel();
        if (cancelButton != null) {
            cancelButton.setEnabled(true);
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.fragment_associating, null);

        dialogProgressBar = (ProgressBarDeterminateMaterial) vi.findViewById(R.id.progress_bar);
        progressText = (TextView) vi.findViewById(R.id.progress_text);

        dialog.setBodyView(vi);
        updateProgress(0);


        dialog.show();
    }

    private void updateProgress(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (dialogProgressBar != null) {
                    dialogProgressBar.setProgress(progress);
                    progressText.setText(getString(R.string.associating_device) + " " + progress + " %");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    private void associationComplete(final boolean success) {

        if (success) {
            Toast.makeText(this, mTempCSRDevice.getName() + " " + getString(R.string.device_associated), Toast.LENGTH_SHORT).show();
            setResult(AxalentUtils.REFRESH_DATA);
        }
        else {
            Toast.makeText(this, getString(R.string.association_failed), Toast.LENGTH_SHORT).show();
        }
        if (dialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    dialog = null;
                }
            });
        }
        finish();

    }

    private void subscribeEvent() {
        subscription = RxBus.getDefaultInstance()
                .toObservable(MeshResponseEvent.class)
                .subscribe(new Action1<MeshResponseEvent>() {
                    @Override
                    public void call(MeshResponseEvent meshResponseEvent) {
                        if (meshResponseEvent ==null || meshResponseEvent.data == null) {
                            return;
                        }
                        switch (meshResponseEvent.what) {
                            case DEVICE_UUID: {
                                ParcelUuid uuid = meshResponseEvent.data.getParcelable(MeshConstants.EXTRA_UUID);
                                int uuidHash = meshResponseEvent.data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                                int rssi = meshResponseEvent.data.getInt(MeshConstants.EXTRA_RSSI);
                                int ttl = meshResponseEvent.data.getInt(MeshConstants.EXTRA_TTL);
                                boolean existing = false;
                                for (ScanCSRDevice info : mDiscoveredDevices) {
                                    if (uuid != null && info.uuid.equalsIgnoreCase(uuid.toString())) {
                                        info.rssi = rssi;
                                        info.ttl = ttl;
                                        // check if we already have appearance info according with the uuidHash
                                        if (mAppearances.containsKey(uuidHash)) {
                                            info.setAppearance(mAppearances.get(uuidHash));
                                        }
                                        info.updated();
                                        updateList(getDiscoveredDevices());
                                        existing = true;
                                        break;
                                    }
                                }
                                if (!existing) {
                                    ScanCSRDevice info = new ScanCSRDevice(uuid.toString().toUpperCase(), rssi, uuidHash, ttl);
                                    // check if we already have appearance info according with the uuidHash
                                    if (mAppearances.containsKey(uuidHash)) {
                                        info.setAppearance(mAppearances.get(uuidHash));
                                    }
                                    mDiscoveredDevices.add(info);
                                    updateList(getDiscoveredDevices());
                                }
                                break;
                            }
                            case DEVICE_APPEARANCE: {
                                byte[] appearance = meshResponseEvent.data.getByteArray(MeshConstants.EXTRA_APPEARANCE);
                                String shortName = meshResponseEvent.data.getString(MeshConstants.EXTRA_SHORTNAME);
                                int uuidHash = meshResponseEvent.data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                                for (ScanCSRDevice info : mDiscoveredDevices) {
                                    if (info.uuidHash == uuidHash) {
                                        info.setAppearance(new AppearanceDevice(appearance, shortName));
                                        info.updated();
                                    }
                                }
                                mAppearances.put(uuidHash, new AppearanceDevice(appearance, shortName));
                                updateList(getDiscoveredDevices());

                                break;
                            }
                            case ASSOCIATION_PROGRESS: {
                                int progress = meshResponseEvent.data.getInt(MeshConstants.EXTRA_PROGRESS_INFORMATION);
                                if (!meshResponseEvent.data.getBoolean(MeshConstants.EXTRA_CAN_BE_CANCELLED)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog != null) {
                                                dialog.getButtonCancel().setEnabled(false);
                                            }

                                        }
                                    });
                                }
                                updateProgress(progress);
                                break;
                            }
                            case GROUP_NUMBER_OF_MODEL_GROUPIDS: {
                                int numIds = meshResponseEvent.data.getInt(MeshConstants.EXTRA_NUM_GROUP_IDS);
                                int modelNo = meshResponseEvent.data.getInt(MeshConstants.EXTRA_MODEL_NO);
                                int expectedModelNo = mModelsToQueryForGroups.peek();
                                int deviceId = meshResponseEvent.data.getInt(MeshConstants.EXTRA_DEVICE_ID);

                                if (expectedModelNo == modelNo && mTempCSRDevice.getDeviceID() == deviceId) {

                                    if (numIds > 0) {
                                        int minNumGroups = mTempCSRDevice.getNumGroups() <= 0 ? numIds : Math.min(mTempCSRDevice.getNumGroups(), numIds);
                                        mTempCSRDevice.setNumGroups(minNumGroups);
                                    }

                                    // We know how many groups are supported for this model now so remove it from the queue.
                                    mModelsToQueryForGroups.remove();
                                    if (mModelsToQueryForGroups.isEmpty()) {
                                        mTempCSRDevice = dbManager.createOrUpdateDevice(mTempCSRDevice);
                                        associationComplete(true);
                                    }
                                    else {
                                        // Otherwise ask how many groups the next model supports, by taking the next model number from the queue.
                                        GroupModel.getNumberOfModelGroupIds(mTempCSRDevice.getDeviceID(), mModelsToQueryForGroups.peek());
                                    }

                                }
                                break;
                            }
                            case DEVICE_ASSOCIATED: {
                                int deviceId = meshResponseEvent.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                                int uuidHash = meshResponseEvent.data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                                byte[] dhmKey = meshResponseEvent.data.getByteArray(MeshConstants.EXTRA_RESET_KEY);
                                mTempCSRDevice = new UnknownCSRDevice();
                                mTempCSRDevice.setDeviceID(deviceId);
                                mTempCSRDevice.setDeviceHash(uuidHash);
                                mTempCSRDevice.setDmKey(dhmKey);
                                mTempCSRDevice.setPlaceID(dbManager.getPlace(1).getId());
                                mTempCSRDevice.setAssociated(true);
                                AppearanceDevice appearanceDevice = mAppearances.get(uuidHash);

                                if (appearanceDevice != null) {
                                    mTempCSRDevice.setName(appearanceDevice.getShortName().trim() + " " + (deviceId - Constants.MIN_DEVICE_ID));
                                    mTempCSRDevice.setAppearance(appearanceDevice.getAppearanceType());


                                    // Request model low from the device.
                                    mCurrentRequestState = DeviceInfo.MODEL_LOW;
                                    ConfigModel.getInfo(mTempCSRDevice.getDeviceID(), DeviceInfo.MODEL_LOW);
                                }
                                else {
                                    mTempCSRDevice.setName(getString(R.string.unknown) + " " + (deviceId - Constants.MIN_DEVICE_ID));
                                    // Request appearance from the device.
                                    mCurrentRequestState = DeviceInfo.APPEARANCE;
                                    ConfigModel.getInfo(mTempCSRDevice.getDeviceID(), DeviceInfo.APPEARANCE);
                                }

                                mTempCSRDevice = dbManager.createOrUpdateDevice(mTempCSRDevice);
                                break;
                            }
                            case CONFIG_INFO: {
                                int deviceId = meshResponseEvent.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                                DeviceInfo type = DeviceInfo.values()[meshResponseEvent.data.getInt(MeshConstants.EXTRA_DEVICE_INFO_TYPE)];

                                if (type == DeviceInfo.APPEARANCE) {

                                    // Store appearance into the database
                                    int appearance = (int) meshResponseEvent.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
                                    AppearanceDevice appearanceDevice = new AppearanceDevice(appearance, null);

                                    mTempCSRDevice.setName(appearanceDevice.getShortName() + " " + (deviceId - Constants.MIN_DEVICE_ID));
                                    mTempCSRDevice.setAppearance(appearanceDevice.getAppearanceType());
                                    mTempCSRDevice = dbManager.createOrUpdateDevice(mTempCSRDevice);


                                    // Request model low from the device.
                                    mCurrentRequestState = DeviceInfo.MODEL_LOW;
                                    ConfigModel.getInfo(deviceId, DeviceInfo.MODEL_LOW);
                                }
                                else if (type == DeviceInfo.MODEL_LOW) {
                                    // Store modelLow into the database
                                    long modelLow = meshResponseEvent.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
                                    mTempCSRDevice.setModelLow(modelLow);
                                    mTempCSRDevice = dbManager.createOrUpdateDevice(mTempCSRDevice);

                                    // Request model high from the device.
                                    mCurrentRequestState = DeviceInfo.MODEL_HIGH;
                                    ConfigModel.getInfo(deviceId, DeviceInfo.MODEL_HIGH);
                                }
                                else if (type == DeviceInfo.MODEL_HIGH) {
                                    // Store modelHigh into the database
                                    long modelHigh = meshResponseEvent.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
                                    mTempCSRDevice.setModelHigh(modelHigh);
                                    mTempCSRDevice = dbManager.createOrUpdateDevice(mTempCSRDevice);

                                    mCurrentRequestState = null;
                                    //associationComplete(true);
                                    mModelsToQueryForGroups.addAll(mTempCSRDevice.getModelsSupported());
                                    if (!mModelsToQueryForGroups.isEmpty()) {
                                        GroupModel.getNumberOfModelGroupIds(mTempCSRDevice.getDeviceID(), mModelsToQueryForGroups.peek());
                                    }
                                }
                                else {
                                    associationComplete(false);
                                }
                                break;
                            }
                            case TIMEOUT: {
                                if (meshResponseEvent.data.getInt(MeshConstants.EXTRA_EXPECTED_MESSAGE) == MeshConstants.MESSAGE_DEVICE_ASSOCIATED) {
                                    associationComplete(false);
                                }
                                else if (meshResponseEvent.data.getInt(MeshConstants.EXTRA_EXPECTED_MESSAGE) == MeshConstants.MESSAGE_CONFIG_DEVICE_INFO) {
                                    int deviceId = meshResponseEvent.data.getInt(MeshConstants.EXTRA_DEVICE_ID);

                                    if (mCurrentRequestState == DeviceInfo.APPEARANCE) {

                                        mTempCSRDevice.setName(getString(R.string.device) + " " + (deviceId - Constants.MIN_DEVICE_ID));
                                        mTempCSRDevice = dbManager.createOrUpdateDevice(mTempCSRDevice);

                                        // Request model low from the device.
                                        mCurrentRequestState = DeviceInfo.MODEL_LOW;
                                        ConfigModel.getInfo(deviceId, DeviceInfo.MODEL_LOW);
                                    }
                                    else if (mCurrentRequestState == DeviceInfo.MODEL_LOW) {

                                        // Request model high from the device.
                                        mCurrentRequestState = DeviceInfo.MODEL_HIGH;
                                        ConfigModel.getInfo(deviceId, DeviceInfo.MODEL_HIGH);
                                    }
                                    else if (mCurrentRequestState == DeviceInfo.MODEL_HIGH) {
                                        mCurrentRequestState = null;
                                        associationComplete(true);
                                    }
                                }
                                else if (meshResponseEvent.data.getInt(MeshConstants.EXTRA_EXPECTED_MESSAGE) == MeshConstants.MESSAGE_GROUP_NUM_GROUPIDS) {
                                    associationComplete(true);
                                }
                                break;
                            }
                        }
                    }
                });
    }

    private void hide_keyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
