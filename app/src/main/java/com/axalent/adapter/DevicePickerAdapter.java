/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.axalent.R;
import com.axalent.adapter.viewholders.DevicePickerViewHolder;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.model.data.model.devices.LightCSRDevice;
import com.axalent.model.data.model.devices.TemperatureCSRDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that helps to populate groups list data
 */
public class DevicePickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CSRDevice> mDevices;
    private List<Integer> mDevicesSelected = new ArrayList<>();
    private List<Integer> mDevicesSelectedDefault;
    private static LayoutInflater mInflater = null;


    public DevicePickerAdapter(List<CSRDevice> devices, List<Integer> devicesSelected, Context a) {
        mContext = a;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDevicesSelectedDefault = devicesSelected;
        if (mDevicesSelectedDefault != null) {
            mDevicesSelected.addAll(mDevicesSelectedDefault);
        }

        setData(devices);
    }

    public void setData(List<CSRDevice> devices) {
        mDevices = new ArrayList<>(devices);
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (mContext == null) {
            mContext = viewGroup.getContext();
        }

        View areaView = mInflater.inflate(R.layout.adapter_device_picker_row, viewGroup, false);
        return new DevicePickerViewHolder(areaView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CSRDevice deviceInfo = mDevices.get(position);

        DevicePickerViewHolder devicePickerViewHolder = (DevicePickerViewHolder) viewHolder;
        devicePickerViewHolder.areaName.setText(deviceInfo.getName());
        if (deviceInfo.getClass() == LightCSRDevice.class) {
            devicePickerViewHolder.iconImage.setImageResource(R.drawable.ic_wb_incandescent_black_36dp);
        }
        else if (deviceInfo.getClass() == TemperatureCSRDevice.class) {
            devicePickerViewHolder.iconImage.setImageResource(R.drawable.ic_whatshot_black_36dp);
        }
        else {
            devicePickerViewHolder.iconImage.setImageResource(R.drawable.ic_launcher);
        }

            if (mDevicesSelected.contains(deviceInfo.getId())) {
                devicePickerViewHolder.selectedImage.setVisibility(View.VISIBLE);
                devicePickerViewHolder.selectedImage.setColorFilter(mContext.getResources().getColor(R.color.icon_light));
                devicePickerViewHolder.iconImage.setColorFilter(mContext.getResources().getColor(R.color.icon_light));
                devicePickerViewHolder.areaName.setTextColor(mContext.getResources().getColor(R.color.icon_light));
            } else {
                devicePickerViewHolder.selectedImage.setVisibility(View.GONE);
                devicePickerViewHolder.selectedImage.setColorFilter(Color.BLACK);
                devicePickerViewHolder.areaName.setTextColor(Color.BLACK);
                devicePickerViewHolder.iconImage.setColorFilter(Color.BLACK);
            }

            devicePickerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDevicesSelected.contains(deviceInfo.getId())) {
                        mDevicesSelected.remove((Object) deviceInfo.getId());
                    }
                    else {
                        mDevicesSelected.add(deviceInfo.getId());
                    }
                    notifyDataSetChanged();
                }
            });
    }

    public List<Integer> getDevicesToAdd() {
        if (mDevicesSelectedDefault.size() != mDevicesSelected.size() || !mDevicesSelectedDefault.containsAll(mDevicesSelected)) {
            List<Integer> devices = new ArrayList<>();
            devices.addAll(mDevicesSelected);
            devices.removeAll(mDevicesSelectedDefault);
            return devices;
        }
        return null;
    }

    public List<Integer> getDevicesToDelete() {
        if (mDevicesSelectedDefault.size() != mDevicesSelected.size() || !mDevicesSelectedDefault.containsAll(mDevicesSelected)) {
            List<Integer> devices = new ArrayList<>();
            devices.addAll(mDevicesSelectedDefault);
            devices.removeAll(mDevicesSelected);

            return devices;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void animateTo(List<CSRDevice> data) {
        applyAndAnimateRemovals(data);
        applyAndAnimateAdditions(data);
        applyAndAnimateMovedItems(data);
    }

    private void applyAndAnimateRemovals(List<CSRDevice> newData) {
        for (int i = mDevices.size() - 1; i >= 0; i--) {
            final CSRDevice item = mDevices.get(i);
            if (!newData.contains(item)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<CSRDevice> newData) {
        for (int i = 0, count = newData.size(); i < count; i++) {
            final CSRDevice item = newData.get(i);
            if (!mDevices.contains(item)) {
                addItem(i, item);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<CSRDevice> newData) {
        for (int toPosition = newData.size() - 1; toPosition >= 0; toPosition--) {
            final CSRDevice item = newData.get(toPosition);
            final int fromPosition = mDevices.indexOf(item);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    // Helper methods to manipulate the list of devices

    public CSRDevice removeItem(int position) {
        final CSRDevice model = mDevices.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, CSRDevice model) {
        mDevices.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final CSRDevice model = mDevices.remove(fromPosition);
        mDevices.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

}
