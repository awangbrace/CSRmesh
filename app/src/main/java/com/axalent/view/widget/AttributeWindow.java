/**
 * File Name                   : ���Դ�����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.widget;

import java.util.HashMap;
import java.util.Map;

import com.axalent.R;
import com.axalent.util.AxalentUtils;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MotionEventCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class AttributeWindow implements View.OnClickListener {
	
	private Activity aty;
	private float[] hsv = new float[3];
	private PopupWindow attributeWindow;
	private PitchonAttributeListener pitchonAttributeListener;
	private Map<String, String> attributesMap = new HashMap<String, String>();
	
	public void setPitchonAttributeListener(PitchonAttributeListener pitchonAttributeListener) {
		this.pitchonAttributeListener = pitchonAttributeListener;
	}
	
	public AttributeWindow(Activity aty, String typeName) {
		this.aty = aty;
		int width = (int) aty.getResources().getDimension(R.dimen.window_attribute_width);
		int height = (int) aty.getResources().getDimension(R.dimen.window_attribute_height);
		View contentView = View.inflate(aty, R.layout.window_attribute, null);
		attributeWindow = new PopupWindow(contentView, width, height, true);
		attributeWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
		RelativeLayout closeLayout = (RelativeLayout) contentView.findViewById(R.id.windowAttributeCloseLayout);
		LinearLayout contentLayout = (LinearLayout) contentView.findViewById(R.id.windowAttributeContentLayout);
		Button confirmBtn = (Button) contentView.findViewById(R.id.windowAttributeConfirmBtn);
		closeLayout.setOnClickListener(this);
		confirmBtn.setOnClickListener(this);
		if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName)) {
			contentLayout.addView(getSeekBar(AxalentUtils.ATTRIBUTE_DIMMER));
			contentLayout.addView(getHSVCircle());
		} else if (AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
			Switch switch0 = getSwitch(AxalentUtils.ATTRIBUTE_SWITCH_0);
			Switch switch1 = getSwitch(AxalentUtils.ATTRIBUTE_SWITCH_1);
			switch0.setText(R.string.switch_0);
			switch1.setText(R.string.switch_1);
			contentLayout.addView(switch0);
			contentLayout.addView(switch1);
		} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
			LinearLayout linearLayout = new LinearLayout(aty);
			linearLayout.setGravity(Gravity.CENTER);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.addView(getTextView(R.string.cover));
			linearLayout.addView(getSeekBar(AxalentUtils.ATTRIBUTE_COVER));
			contentLayout.addView(linearLayout);
		}
	}

	public void show(View anchor) {
		attributeWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0);
	}
	
	private SeekBar getSeekBar(final String key) {
		SeekBar seekBar = new SeekBar(aty);
		seekBar.setMax(AxalentUtils.ATTRIBUTE_COVER.equals(key) ? 99 : 100);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				attributesMap.put(key, String.valueOf(seekBar.getProgress()));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
		});
		return seekBar;
	}
	
	
	private HSVCircle getHSVCircle() {
		final HSVCircle circle = new HSVCircle(aty, null);
		int width = (int) aty.getResources().getDimension(R.dimen.window_attribute_color_circle_width);
		int height = (int) aty.getResources().getDimension(R.dimen.window_attribute_color_circle_width);
		circle.setLayoutParams(new LayoutParams(width, height));
		circle.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = MotionEventCompat.getActionMasked(event);
				switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					int x = (int) event.getX();
					int y = (int) event.getY();
					HSVCircle circleView = (HSVCircle) v;
					int pixelColor = 0;
					try {
						pixelColor = circleView.getPixelColorAt(x, y);
					} catch (IndexOutOfBoundsException e) {
						return true;
					}
					if (Color.alpha(pixelColor) < 0xFF) {
						return true;
					}
					circle.setCursor(x, y);
					circle.invalidate();
					Color.RGBToHSV(Color.red(pixelColor), Color.green(pixelColor), Color.blue(pixelColor), hsv);
					return true;
				case MotionEvent.ACTION_UP:
					attributesMap.put(AxalentUtils.ATTRIBUTE_HSV, hsv[0]+" "+hsv[1]+" "+hsv[2]);
					return true;
				default:
					return false;
				}
			}
		});
		return circle;
	}
	
	
	private Switch getSwitch(final String key) {
		Switch mSwitch = new Switch(aty);
		mSwitch.setText(R.string.switch_0);
		mSwitch.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				attributesMap.put(key, isChecked ? AxalentUtils.ON : AxalentUtils.OFF);
			}
		});
		return mSwitch;
	}
	
	private TextView getTextView(int id) {
		TextView textView = new TextView(aty);
		textView.setText(id);
		textView.setTextColor(Color.BLACK);
		textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		return textView;
	}
	
	private void close() {
		if (attributeWindow.isShowing()) {
			attributeWindow.dismiss();
		}
	}
	
	public interface PitchonAttributeListener {
		void onPitchonAttribute(Map<String, String> attributesMap);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.windowAttributeCloseLayout:
			close();
			break;
		case R.id.windowAttributeConfirmBtn:
			close();
			if (pitchonAttributeListener != null) {
				pitchonAttributeListener.onPitchonAttribute(attributesMap);
			}
			break;
		}
	}
	
	
}
