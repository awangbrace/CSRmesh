package com.axalent.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.axalent.R;
import com.axalent.presenter.RxBus;
import com.axalent.presenter.csrapi.MeshLibraryManager;
import com.axalent.presenter.events.MeshRequestEvent;
import com.axalent.presenter.events.MeshResponseEvent;
import com.axalent.util.AxalentUtils;
import com.axalent.view.material.TemperatureCircle;
import com.axalent.view.material.TemperatureControllerInterface;
import com.csr.csrmesh2.ActuatorModelApi;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.sensor.DesiredAirTemperature;
import com.csr.csrmesh2.sensor.InternalAirTemperature;
import com.csr.csrmesh2.sensor.SensorValue;

import java.text.DecimalFormat;

import rx.Subscription;
import rx.functions.Action1;

/**
 * File Name                   : TemperatureFragment
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/9/21
 * Revision History            : 18:26
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */
public class TemperatureFragment extends Fragment implements TemperatureControllerInterface {

    // How often to send a temperature value - i.e. how often the periodic timer fires.
    private static final int TRANSMIT_TEMPERATURE_PERIOD_MS = 500;

    // UI elements
    private LinearLayout mGetButton;
    private TextView mCurrentValue;
    private TextView mCurrentKelvinValue;
    private static double temperatureValue;

    // Controllers
    private TemperatureCircle mTemperatureControler;

    // Fragment variables
    private double mValue = TEMPERATURE_DEFAULT_VALUE;
    DecimalFormat mDecimalFactor = new DecimalFormat("0.0");

    // static values
    private static final double MAX_TEMPERATURE_VALUE = 50.0;
    private static final double MIN_TEMPERATURE_VALUE = 0.0;
    private static final double TEMPERATURE_DEFAULT_VALUE = 20.0;

    DesiredAirTemperature mTemperatureToSend = null;
    boolean mPendingDesiredTemperatureRequest = false;
    private Handler mHandler = new Handler();
    static int mSendDeviceId;
    int mLastActuatorMeshId;

    private Subscription subscription;


    public static TemperatureFragment newInstance(int deviceId, double value) {
        TemperatureFragment fragment = new TemperatureFragment();

        mSendDeviceId = deviceId;
        temperatureValue = value;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temperature, container, false);
        mGetButton = (LinearLayout) rootView.findViewById(R.id.buttonGet);
        mCurrentValue = (TextView) rootView.findViewById(R.id.currentValue);
        mCurrentValue.setText(mDecimalFactor.format(temperatureValue));
        mCurrentKelvinValue = (TextView) rootView.findViewById(R.id.currentKelvinValue);
        mTemperatureControler = (TemperatureCircle) rootView.findViewById(R.id.temperatureControler);
        mGetButton.setOnClickListener(mGetButtonListener);
        mTemperatureControler.setTemperatureInterface(this);
//        subscribeEvent();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        setNewValue(TEMPERATURE_DEFAULT_VALUE, false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        subscription.unsubscribe();
    }

    private View.OnClickListener mGetButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //nothing to do.
        }
    };

    @Override
    public void buttonUpClicked() {
        double newValue = mValue + 0.5;
        if (newValue <= MAX_TEMPERATURE_VALUE) {
            setNewValue(newValue, true);
        }
    }

    @Override
    public void buttonDownClicked() {
        double newValue = mValue - 0.5;
        if (newValue >= MIN_TEMPERATURE_VALUE) {
            setNewValue(newValue, true);
        }
    }

    @Override
    public void buttonTextClicked() {
    }

    private void setNewValue(double value, boolean request) {
        mValue = value;
        mTemperatureControler.setValue(mDecimalFactor.format(mValue));

        if (request) {
            setDesiredTemperature((float) value);
            mTemperatureControler.setValueConfirmed(false);
        }
    }

    private void setDesiredTemperature(float celsius) {
        double kelvin = AxalentUtils.convertCelsiusToKelvin(celsius);
        mTemperatureToSend = new DesiredAirTemperature((float) kelvin);

        if (mTemperatureToSend != null) {
            mPendingDesiredTemperatureRequest = true;
        }
        mHandler.removeCallbacks(transmitTempCallback);
        mHandler.postDelayed(transmitTempCallback, TRANSMIT_TEMPERATURE_PERIOD_MS);
    }

    public void confirmDesiredTemperature() {
        mTemperatureControler.setValueConfirmed(true);
    }

    public void setCurrentTemperature(double value, double value2) {
        mCurrentValue.setText(mDecimalFactor.format(value));
//        mCurrentKelvinValue.setText(mDecimalFactor.format(value2));
    }

    public void setDesiredTemperature(double value) {
        setNewValue(value, false);
    }

    private Runnable transmitTempCallback = new Runnable() {
        @Override
        public void run() {

            if (mLastActuatorMeshId != 0) {
                Bundle data = new Bundle();
                data.putInt(MeshConstants.EXTRA_DATA, mLastActuatorMeshId);
                RxBus.getDefaultInstance().post(new MeshRequestEvent(MeshRequestEvent.RequestEvent.KILL_TRANSACTION, data));
            }

            if (mTemperatureToSend != null) {
                mLastActuatorMeshId = ActuatorModelApi.setValue(mSendDeviceId, mTemperatureToSend, true);
                mTemperatureControler.setValueConfirmed(false);
            }
        }
    };

    private void subscribeEvent() {
        subscription = RxBus.getDefaultInstance()
                .toObservable(MeshResponseEvent.class)
                .subscribe(new Action1<MeshResponseEvent>() {
                    @Override
                    public void call(MeshResponseEvent meshResponseEvent) {
                        int meshRequestId = meshResponseEvent.data.getInt(MeshLibraryManager.EXTRA_REQUEST_ID);
                        if (mPendingDesiredTemperatureRequest) {
                            mPendingDesiredTemperatureRequest = (meshRequestId != mLastActuatorMeshId);
                        }
                        switch (meshResponseEvent.what) {
                            case ACTUATOR_VALUE: {
                                if (mLastActuatorMeshId == meshRequestId) {
                                    confirmDesiredTemperature();
                                }
                                break;
                            }
                            case SENSOR_VALUE: {

                                Log.i("mesh_data", "call: " + meshResponseEvent.data);

                                int deviceId = meshResponseEvent.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                                SensorValue value1 = (SensorValue) meshResponseEvent.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE1);
                                SensorValue value2 = null;

                                if (meshResponseEvent.data.containsKey(MeshConstants.EXTRA_SENSOR_VALUE2)) {
                                    value2 = (SensorValue) meshResponseEvent.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE2);
                                }
                                if (deviceId == mSendDeviceId) {
                                    Log.i("SENSOR_VALUE", "call: "+ value1);
                                    Log.i("SENSOR_VALUE", "call: "+ value2);
                                    populateSensorValue(value1, value2);
                                }
                            }
                        }
                    }
                });
    }

    private void populateSensorValue(SensorValue... sensorValues) {

        if (sensorValues == null) {
            return;
        }
        for (int i = 0; i < sensorValues.length; i++) {
            SensorValue sensorValue = sensorValues[i];

            if (sensorValue instanceof InternalAirTemperature) {
                // store the temperature in the status array.
                final double tempCelsius = ((InternalAirTemperature) sensorValue).getCelsiusValue();
                final double kelvins = ((InternalAirTemperature) sensorValue).getKelvinValue();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        setCurrentTemperature(tempCelsius, kelvins);
                    }
                });

            }
            else if (sensorValue instanceof DesiredAirTemperature) {
                final double tempCelsius = ((DesiredAirTemperature) sensorValue).getCelsiusValue();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (!mPendingDesiredTemperatureRequest) {
                            setDesiredTemperature(tempCelsius);
                        }
                    }
                });
            }
        }
    }
}
