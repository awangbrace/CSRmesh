/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/


package com.axalent.view.material;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * This view is extends ImageView. This view should be used to be able to tint images for SDK >= LOLLIPOP.
 */
public class TintImageView extends ImageView {

    public TintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList imageTintList = getImageTintList();
            if (imageTintList == null) {
                return;
            }

            setColorFilter(imageTintList.getDefaultColor(), PorterDuff.Mode.SRC_IN);
        }
    }
}