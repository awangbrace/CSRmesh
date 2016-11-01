/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/


package com.axalent.adapter.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.axalent.R;


/**
 *
 */
public class DevicePickerViewHolder extends RecyclerView.ViewHolder {

    public TextView areaName;
    public ImageView selectedImage;
    public ImageView iconImage;

    public DevicePickerViewHolder(View itemView) {
        super(itemView);
        areaName = (TextView) itemView.findViewById(R.id.name);
        selectedImage = (ImageView) itemView.findViewById(R.id.selected_image);
        iconImage = (ImageView) itemView.findViewById(R.id.icon);
    }
}
