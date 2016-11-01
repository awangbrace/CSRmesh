/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.axalent.R;
import com.axalent.model.data.model.devices.AppearanceDevice;
import com.axalent.model.data.model.devices.ScanCSRDevice;
import com.axalent.presenter.csrapi.Association;
import com.axalent.view.material.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiscoveredDevicesAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;
    List<ScanCSRDevice> mDiscoveredDevices = Collections.emptyList();
    private int mHashSelected = -1;

    public DiscoveredDevicesAdapter(Activity a) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mDiscoveredDevices.size();
    }

    public void updateDevices(ArrayList<ScanCSRDevice> discoveredDevices) {
        if (discoveredDevices != null) {
            mDiscoveredDevices = discoveredDevices;
        }
        notifyDataSetChanged();
    }

    public ScanCSRDevice getItem(int position) {
        return mDiscoveredDevices.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.adapter_device_discovered, null);
        }

        TextView uuid = (TextView) vi.findViewById(R.id.title);
        TextView deviceName = (TextView) vi.findViewById(R.id.subtitle);
        TextView distance = (TextView) vi.findViewById(R.id.distance);
        ImageView imageView = (ImageView) vi.findViewById(R.id.icon);

        ScanCSRDevice device = mDiscoveredDevices.get(position);
        String textToShow = "";

        // Update some UI values from the appearance.
        if (device.appearance != null) {
            deviceName.setText(device.appearance.getShortName());
            if (device.appearance.getAppearanceType() == AppearanceDevice.LIGHT_APPEARANCE) {
                imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wb_incandescent_black_36dp));
            }
            else if (device.appearance.getAppearanceType() == AppearanceDevice.HEATER_APPEARANCE
                    || device.appearance.getAppearanceType() == AppearanceDevice.SENSOR_APPEARANCE) {
                imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_whatshot_black_36dp));
            }
            else {
                imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_launcher));
            }
        }
        else {
            deviceName.setText(activity.getString(R.string.unknown_device));
            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_launcher));
        }
        uuid.setText("" + device.uuid);

        // Update rssi value.
        if (device.rssi != 0) {
            distance.setText("" + device.rssi + activity.getString(R.string.decibel_milliwatts));
        }

        // Turn orange if device has been selected or leave it grey.
        if (device.getUuidHash() == mHashSelected) {
            // Change bg.
            imageView.setColorFilter(activity.getResources().getColor(R.color.ripple_tile_led_on), PorterDuff.Mode.SRC_IN);
            deviceName.setTextColor(activity.getResources().getColor(R.color.ripple_tile_led_on));
        }
        else {
            imageView.setColorFilter(activity.getResources().getColor(R.color.secondary_icon_grey), PorterDuff.Mode.SRC_IN);
            deviceName.setTextColor(Color.BLACK);
        }

        return vi;
    }

    /**
     * Set the selected device by uuid hash. Selected device will be displayed in a different colour.
     * To de-select a device, pass uuidHash31 as zero.
     * @param uuidHash31 The 31-bit UUID hash of the device.
     */
    public void setHashSelected(int uuidHash31) {
        if (uuidHash31 != 0) sendPreassociationEvent(mHashSelected, false);
        mHashSelected = uuidHash31;
        if (uuidHash31 != 0) sendPreassociationEvent(mHashSelected, true);
    }

    private void sendPreassociationEvent(int hash, boolean enabled) {
        if (mHashSelected == Constants.INVALID_VALUE) {
            return;
        }
        Association.attentionPreAssociation(hash, enabled, 5000);
    }
}
