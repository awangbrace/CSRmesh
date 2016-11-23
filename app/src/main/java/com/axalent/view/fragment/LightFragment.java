/**
 * File Name                   : Light 界面
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.application.MyCacheData;
import com.axalent.model.data.model.Area;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.view.widget.HSVCircle;
import com.csr.csrmesh2.LightModelApi;
import com.csr.csrmesh2.PowerModelApi;
import com.csr.csrmesh2.PowerState;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;

import static android.content.Context.MODE_PRIVATE;

public class LightFragment extends Fragment implements OnTouchListener, OnSeekBarChangeListener{
	
	private HSVCircle circle;
	private SeekBar dimmerSeekBar, warmSeekBar;
	private float[] hsv = new float[3];
    private int dimmer = 100;
    private int warm = dimmer;
    private ShowDeviceActivity aty;
    private Device device;
    private int r,g,b;
	private Switch mPowerSwitch = null;
	private boolean mSwitchOnlyUI = false;

	private static final int TRANSMIT_COLOR_PERIOD_MS = 240;

	private CSRDevice csrDevice;

	private Area area;

	private Handler mHandler = new Handler();

	private int mCurrentColor = Color.rgb(0, 0, 0);
	private boolean mNewColor = false;
	private boolean mActionUp = false;

	// True if the device id of the light being controlled is a group.
	private static boolean mIsGroup = false;
	private SharedPreferences sp;

	public static LightFragment newInstance(boolean isGroup) {
		LightFragment fragment = new LightFragment();
		mIsGroup = isGroup;
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		aty = (ShowDeviceActivity) activity;
		sp = aty.getSharedPreferences(AxalentUtils.USER_FILE_NAME, MODE_PRIVATE);
		if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
			csrDevice = aty.getCurrentCSRDevice();
			area = aty.getCurrentArea();
		} else {
			device = aty.getCurrentDevice();
		}
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_light, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		circle = (HSVCircle) view.findViewById(R.id.fragLightCircle);
		dimmerSeekBar = (SeekBar) view.findViewById(R.id.fragDimmerSeekBar);
		dimmerSeekBar.setOnSeekBarChangeListener(this);
		dimmerSeekBar.setProgress(100);
		circle.setOnTouchListener(this);

		if (ShowDeviceActivity.result == AxalentUtils.GROUP) {
			mPowerSwitch = (Switch) view.findViewById(R.id.powerSwitch);
			mPowerSwitch.setVisibility(View.VISIBLE);
			mPowerSwitch.setOnCheckedChangeListener(powerChange);
		}

		if (AxalentUtils.getLoginMode() != R.id.atyLoginBluetoothBtn) {
			int count = 0;
			String dimmerKey = AxalentUtils.ATTRIBUTE_DIMMER;
			String dimmer = "0";
			String warm = "0";
//		String colorKey = AxalentUtils.ATTRIBUTE_HSV;
//		String colorVal = "0 0 0";

			if (device.getTypeName().equalsIgnoreCase(AxalentUtils.TYPE_GUNI_LAMP)) {
//			colorKey = AxalentUtils.ATTRIBUTE_GU_RGB;
				dimmerKey = AxalentUtils.ATTRIBUTE_GU_DIMMER;
			}

			List<DeviceAttribute> deviceAttributes = device.getAttributes();
			if (deviceAttributes != null && deviceAttributes.size() > 0) {
				for (DeviceAttribute deviceAttribute : deviceAttributes) {
					String name = deviceAttribute.getName();
					String value = deviceAttribute.getValue();
					if (name.equals(dimmerKey)) {
						dimmer = value == null || value.equals("") ? "0" : value;
						count++;
					}
//				if (name.equals(colorKey)) {
//					colorVal = value == null || value.equals("") ? "0 0 0" : value;
//					count++;
//				}
					if (name.equals(AxalentUtils.ATTRIBUTE_GU_WARM)) {
						warm = value == null || value.equals("") ? "0" : value;
						count++;
					}
					if (count == 2) {
						break;
					}
				}
			}

			dimmerSeekBar.setProgress(Integer.parseInt(dimmer));
//		LogUtils.i("colorVal:"+colorVal);
//		String[] colorVals = colorVal.split(" ");

			if (device.getTypeName().equalsIgnoreCase(AxalentUtils.TYPE_GUNI_LAMP)) {
				view.findViewById(R.id.fragWarmLayout).setVisibility(View.VISIBLE);
				warmSeekBar = (SeekBar) view.findViewById(R.id.fragWarmSeekBar);
				warmSeekBar.setProgress(Integer.parseInt(warm));
				warmSeekBar.setOnSeekBarChangeListener(this);

//			if (colorVals.length >= 3) {
//				r = Integer.parseInt(colorVals[0]);
//				g = Integer.parseInt(colorVals[1]);
//				b = Integer.parseInt(colorVals[2]);
//				Color.RGBToHSV(r, g, b, hsv);
//				circle.setCursor(hsv[0], hsv[1]);
//				circle.invalidate();
//			}

			} else {
				// 322.96875 0.5019608 1.0

//			if (colorVals.length >= 3) {
//				hsv[0] = Float.parseFloat(colorVals[0]);
//				hsv[1] = Float.parseFloat(colorVals[1]);
//				hsv[2] = Float.parseFloat(colorVals[2]);
//				circle.setCursor(hsv[0], hsv[1]);
//				circle.invalidate();
//			}
			}

//		if (colorVals.length >= 3) LogUtils.i("hsv:"+hsv[0]+" "+hsv[1]+" "+hsv[2]);
		}

	}

	
	
	private void setDeviceAttribute(final String name, final String value) {
		LogUtils.i("name:"+name+" value:"+value);
		final String updateId = aty.getCurrentDevice().getDevId();
		aty.getDeviceManager().setDeviceAttribute(updateId, name, value, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				CacheUtils.updateDeviceAttibuteByDevId(updateId, name, value);
				ToastUtils.show(R.string.alter_success);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.show(R.string.alter_failure);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (ShowDeviceActivity.result == AxalentUtils.GROUP) {
			refreshUI();
		}
		startPeriodicTransmit();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopPeriodicTransmit();
	}

	@Override
	public void onDestroy() {
		circle.onDestroyView();
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = MotionEventCompat.getActionMasked(event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
				mActionUp = false;
				int x = (int) event.getX();
				int y = (int) event.getY();

				HSVCircle circleView = (HSVCircle) v;

				int pixelColor;
				try {
					pixelColor = circleView.getPixelColorAt(x, y);
				}
				catch (IndexOutOfBoundsException e) {
					return true;
				}

				// Don't use values from the background of the image (outside the wheel).
				if (Color.alpha(pixelColor) < 0xFF) {
					return true;
				}

				if (ShowDeviceActivity.result == AxalentUtils.GROUP) {
					switchONPowerToggleUI();
				}

				// The cursor is a small circle drawn over the colour wheel image over the selected colour.
				// Set the cursor position and invalidate the imageView so that it is redrawn.
				circle.setCursor(x, y);
				circle.invalidate();

				// Extract the R,G,B from the colour.
				int tempColor = Color.rgb(Color.red(pixelColor), Color.green(pixelColor), Color.blue(pixelColor));
				float[] hsv = new float[3];
				Color.colorToHSV(tempColor, hsv);
				hsv[2] = ((float) dimmerSeekBar.getProgress() + 1) / 100.0f;
				mCurrentColor = Color.HSVToColor(hsv);

				mNewColor = true;

				return true;
			} else {
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
				r = Color.red(pixelColor);
				g = Color.green(pixelColor);
				b = Color.blue(pixelColor);
				Color.RGBToHSV(r, g, b, hsv);
				return true;
			}
		case MotionEvent.ACTION_UP:
			if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
				mActionUp = true;
				mNewColor = true;
				return false;
			} else {
				String name = "";
				String value = "";
				if (device.getTypeName().equals(AxalentUtils.TYPE_SL)
						|| device.getTypeName().equals(AxalentUtils.TYPE_GATEWAY_GROUP)) {
					name = AxalentUtils.ATTRIBUTE_HSV;
					value = hsv[0]+" "+hsv[1]+" "+hsv[2];
				} else {
					name = AxalentUtils.ATTRIBUTE_GU_RGB;
					value = r + " "+g+" "+b;
				}
				setDeviceAttribute(name, value);
				return true;
			}
		default:
			return false;
		}
	}
	

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
			if (seekBar.getId() == R.id.fragDimmerSeekBar) {

				if (ShowDeviceActivity.result == AxalentUtils.GROUP) {
					switchONPowerToggleUI();
				}

				int pixelColor;
				try {
					pixelColor = circle.getColorAtCursor();
					float[] hsv = new float[3];
					Color.colorToHSV(pixelColor, hsv);
					hsv[2] = ((float) seekBar.getProgress() + 1) / 100.0f;
					mCurrentColor = Color.HSVToColor(hsv);

					mNewColor = true;
				}
				catch (IndexOutOfBoundsException e) {
				}
			}
		} else {
			if (seekBar.getId() == R.id.fragDimmerSeekBar) {
				dimmer = progress;
			} else {
				warm = progress;
			}
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (AxalentUtils.getLoginMode() != R.id.atyLoginBluetoothBtn) {
			String name = "";
			String value = "";
			if (seekBar.getId() == R.id.fragDimmerSeekBar) {
				name = device.getTypeName().equalsIgnoreCase(AxalentUtils.TYPE_GUNI_LAMP) ? AxalentUtils.ATTRIBUTE_GU_DIMMER : AxalentUtils.ATTRIBUTE_DIMMER;
				value = String.valueOf(dimmer);
			} else {
				name = AxalentUtils.ATTRIBUTE_GU_WARM;
				value = String.valueOf(warm);
			}
			setDeviceAttribute(name, value);
		}
	}

	private Runnable transmitColorCallback = new Runnable() {
		@Override
		public void run() {
			if (mNewColor) {
				// If we are in cloud mode set RGB only when finger up from screen
				if(mActionUp) {
					setRGBColors();
				}
			}
			mHandler.postDelayed(this, TRANSMIT_COLOR_PERIOD_MS);
		}
	};

	private void setRGBColors() {
		// If the destination id is a group, then the message can't be an ack message.
		if (ShowDeviceActivity.result == AxalentUtils.SING) {
			setRGB(csrDevice.getDeviceID());
			MyCacheData.getInstance().setState(csrDevice.getDeviceID(), PowerState.ON);
		} else if (ShowDeviceActivity.result == AxalentUtils.GROUP) {
			setRGB(area.getAreaID());
		}
		// Colour sent so clear the flag.
		mNewColor = false;
	}

	private void setRGB(int deviceID) {
		LightModelApi.setRgb(deviceID, mCurrentColor, 0, !mIsGroup && mActionUp);
	}

	/**
	 * Start transmitting colours to the current send address. Colours are sent every TRANSMIT_COLOR_PERIOD_MS ms
	 */
	private void startPeriodicTransmit() {
		mHandler.postDelayed(transmitColorCallback, TRANSMIT_COLOR_PERIOD_MS);
	}

	/**
	 * Stop transmitting colours to the current send address.
	 */
	private void stopPeriodicTransmit() {
		mHandler.removeCallbacks(transmitColorCallback);
	}

	/**
	 * Set the toggle ON but do not execute REST call - since it is unnecessary
	 */
	private void switchONPowerToggleUI(){
		if(mPowerSwitch != null && !mPowerSwitch.isChecked()) {
			LogUtils.i("isChecked:" + mPowerSwitch.isChecked());
			mSwitchOnlyUI = true;
			mPowerSwitch.setChecked(true);
		}
	}

	/**
	 * Called when power button is pressed.
	 */
	protected CompoundButton.OnCheckedChangeListener powerChange = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// do call if switch is not UI only toggle (avoiding unnecessary call to PowerState.ON)
			if(!mSwitchOnlyUI) {
				PowerState state = isChecked ? PowerState.ON : PowerState.OFF;

				// If the destination id is a group, then the message can't be an ack message.
				PowerModelApi.setState(area.getAreaID(), state, !mIsGroup);
			}
			sp.edit().putBoolean("area-" + area.getAreaID(), isChecked).apply();
			mSwitchOnlyUI = false;
		}
	};

	/**
	 * Update the UI with the state of the selected device, if its state is known.
	 */
	protected void updateControls() {
		mPowerSwitch.setChecked(false);
		circle.setCursor(0, 0);
		circle.invalidate();
	}

	/**
	 * Reload the displayed devices and groups.
	 */
	public void refreshUI() {
		updateControls();
	}
	
}
