/**
 * File Name                   : ��ʾ�豸����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.adapter.MenuAdapter;
import com.axalent.application.BaseActivity;
import com.axalent.model.data.database.DBManager;
import com.axalent.model.data.model.Area;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.presenter.RxBus;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.presenter.controller.impl.DeviceManagerImpl;
import com.axalent.presenter.controller.impl.UserManagerImpl;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.presenter.csrapi.Association;
import com.axalent.presenter.csrapi.ConfigModel;
import com.axalent.presenter.csrapi.GroupModel;
import com.axalent.presenter.csrapi.MeshLibraryManager;
import com.axalent.presenter.events.MeshResponseEvent;
import com.axalent.view.fragment.CameraFragment;
import com.axalent.view.fragment.GasSensorFragment;
import com.axalent.view.fragment.GatewayFragment;
import com.axalent.view.fragment.GuniSensorFragment;
import com.axalent.view.fragment.HistoryFragment;
import com.axalent.view.fragment.HtmFragment;
import com.axalent.view.fragment.HtsFragment;
import com.axalent.view.fragment.LightFragment;
import com.axalent.view.fragment.MtmSensorFragment;
import com.axalent.view.fragment.PowerplugFragment;
import com.axalent.view.fragment.SwitchFragment;
import com.axalent.view.fragment.TemperatureFragment;
import com.axalent.view.fragment.WindowCoverFragment;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.material.Constants;
import com.axalent.view.material.DialogMaterial;
import com.axalent.view.widget.LoadingDialog;
import com.csr.csrmesh2.ConfigCloudApi;
import com.csr.csrmesh2.DeviceInfo;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.MeshService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Toast;

import rx.Subscription;
import rx.functions.Action1;

public class ShowDeviceActivity extends BaseActivity implements OnClickListener {
	
	private View anchor;
	private TextView titleTxt;
	private TextView devNameTxt;
	private TextView custNameTxt;
	private TextView custText;
	private TextView devText;
	private ImageView deviceImg;
	private Device currentDevice;
	private PopupWindow menuWindow;
	private MenuAdapter menuAdapter;
	private LoadingDialog loadingDialog;
	private DeviceManager deviceManager = new DeviceManagerImpl();

	public TextView val1, val2;

	private CSRDevice csrDevice;
	private DBManager dbManager;
	private Area area;
	public static int result;

	private DialogMaterial mDialog;

	private Handler mHandler = new Handler();
	private int mHashExpected = Constants.INVALID_VALUE ;
	private final static int WAITING_RESPONSE_MS = 10* 1000;
	private int areaId = 0;
	// A list of model ids that are waiting on a query being sent to find out how many groups are supported.
	private Queue<Integer> mModelsToQueryForGroups = new LinkedList<Integer>();
	private DeviceInfo mCurrentRequestState = null;

	private double temperatureValue;
	private Subscription controlSubscription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_device);
		result = getIntent().getIntExtra(AxalentUtils.GROUP_OR_SING, 0);
		temperatureValue = getIntent().getDoubleExtra("temperature", 0d);
		initView();
		if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
			subscribeEvent();
			dbManager = DBManager.getDBManagerInstance(this.getApplicationContext());
			loadDataBaseCSRDevice();
			setDisplay();
			initCSRDeviceFragment();
			if (result == AxalentUtils.SING) {
				initGroups();
			}
		} else {
			initExtraData();
			initData();
		}
		initActionBar();
	}

	private void loadDataBaseCSRDevice() {
		if (result == AxalentUtils.GROUP) {
			int areaId = getIntent().getIntExtra(AddGroupActivity.KEY_AREA_ID, 0);
			area = dbManager.getArea(areaId);
		} else if (result == AxalentUtils.SING) {
			int id = getIntent().getIntExtra(AxalentUtils.KEY_ID_DATABASE_DEVICE,0);
			areaId = getIntent().getIntExtra(AxalentUtils.KEY_ID_DATABASE_AREA,0);
			csrDevice = dbManager.getDevice(id);
		}
	}
	
	private void initExtraData() {
		currentDevice = getIntent().getParcelableExtra(AxalentUtils.KEY_DEVICE);
		if (currentDevice != null && currentDevice.getTypeName() != null) {
			if (!AxalentUtils.TYPE_CAMERA.equalsIgnoreCase(currentDevice.getTypeName())) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			} 
		}
	}

	private void initGroups() {
		if (csrDevice.getNumGroups() == Constants.INVALID_VALUE) {

			mModelsToQueryForGroups.addAll(csrDevice.getModelsSupported());

			if (!mModelsToQueryForGroups.isEmpty()) {
				GroupModel.getNumberOfModelGroupIds(csrDevice.getDeviceID(), mModelsToQueryForGroups.peek());
			}
			else {
				// No models supported info. request that info again.
				mCurrentRequestState = DeviceInfo.MODEL_LOW;
				ConfigModel.getInfo(csrDevice.getDeviceID(), DeviceInfo.MODEL_LOW);
			}
		}
	}
	
	private void initActionBar() {
		View customView = findViewById(R.id.showDeviceContent);
		titleTxt = (TextView) customView.findViewById(R.id.barShowDeviceTitleTxt);
		if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
			if (result == AxalentUtils.GROUP) {
				titleTxt.setText(area.getName());
			} else if (result == AxalentUtils.SING) {
				titleTxt.setText(csrDevice.getName());
			}
		} else {
			titleTxt.setText(currentDevice.getDisplayName());
		}
		RelativeLayout back = (RelativeLayout) customView.findViewById(R.id.barShowDeviceBack);
		back.setOnClickListener(this);
		RelativeLayout menu = (RelativeLayout) customView.findViewById(R.id.barShowDeviceMenu);
		menu.setOnClickListener(this);
	}
	
	private void initView() {
		loadingDialog = new LoadingDialog(this);
		anchor = findViewById(R.id.atyShowDeviceAnchor);
		deviceImg = (ImageView) findViewById(R.id.atyShowDeviceImg);
		devNameTxt = (TextView) findViewById(R.id.atyShowDeviceName);
		custNameTxt = (TextView) findViewById(R.id.atyShowDeviceCustName);
		custText = (TextView) findViewById(R.id.custom_text);
		devText = (TextView) findViewById(R.id.device_text);
		val1 = (TextView) findViewById(R.id.val1);
		val2 = (TextView) findViewById(R.id.val2);
		custNameTxt.setOnClickListener(this);
	}

	private void setDisplay() {
		if (result == AxalentUtils.SING) {
			deviceImg.setBackgroundResource(AxalentUtils.getCSRDeviceImageByTypeName(csrDevice.getType()));
			custNameTxt.setText(csrDevice.getName());
			devNameTxt.setText(csrDevice.getName());
		} else if (result == AxalentUtils.GROUP) {
			deviceImg.setBackgroundResource(R.drawable.scene_user_define);
			custNameTxt.setVisibility(View.GONE);
			devNameTxt.setVisibility(View.GONE);
			custText.setVisibility(View.GONE);
			devText.setVisibility(View.GONE);
		}
	}
	
	private void initFragment() {
		final String typeName = currentDevice.getTypeName();
		if (AxalentUtils.TYPE_SL.equalsIgnoreCase(typeName) || AxalentUtils.TYPE_GUNI_LAMP.equalsIgnoreCase(typeName)) {
			// Light Bulb
			AxalentUtils.commitFragment(this, new LightFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_SM.equalsIgnoreCase(typeName)) {
			// Motion Sensor
			AxalentUtils.commitFragment(this, new HistoryFragment(AxalentUtils.ATTRIBUTE_MOTION), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_SSMOKE.equalsIgnoreCase(typeName)) {
			// Smoke Sensor
			AxalentUtils.commitFragment(this, new HistoryFragment(AxalentUtils.ATTRIBUTE_SMOKE), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_GATEWAY.equalsIgnoreCase(typeName)) {
			// Gateway
			AxalentUtils.commitFragment(this, new GatewayFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_POWER_PLUG.equalsIgnoreCase(typeName) || AxalentUtils.TYPE_GUNI_PLUG.equalsIgnoreCase(typeName)) {
			// Power Plug
			AxalentUtils.commitFragment(this, new PowerplugFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_SC.equalsIgnoreCase(typeName)) {
			// Contact Sensor
			AxalentUtils.commitFragment(this, new HistoryFragment(AxalentUtils.ATTRIBUTE_CONTACT), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_HTM.equalsIgnoreCase(typeName)) {
			// Health Thermometer
			AxalentUtils.commitFragment(this, new HtmFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_HTS.equalsIgnoreCase(typeName)) {
			// Humidity and Temperature Sensor
			AxalentUtils.commitFragment(this, new HtsFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_LIGHTSENSOR.equalsIgnoreCase(typeName)) {
			// light sensor
			AxalentUtils.commitFragment(this, new HistoryFragment(AxalentUtils.ATTRIBUTE_LUX), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_WINDOW_COVER.equalsIgnoreCase(typeName)) {
			// Window Cover
			AxalentUtils.commitFragment(this, new WindowCoverFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_SWITCH_TWO.equalsIgnoreCase(typeName)) {
			// Two-way Switch
			AxalentUtils.commitFragment(this, new SwitchFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_BPM.equalsIgnoreCase(typeName)) {
			
		} else if (AxalentUtils.TYPE_REMOTECONTROL.equalsIgnoreCase(typeName)) {
			// RemoteControl
		} else if (AxalentUtils.TYPE_BEACON.equalsIgnoreCase(typeName)) {
			// Beacon
		} else if (AxalentUtils.TYPE_CAMERA.equalsIgnoreCase(typeName)) {
			// Camera
			AxalentUtils.commitFragment(this, new CameraFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_GAS_SENSOR.equalsIgnoreCase(typeName)) {
			AxalentUtils.commitFragment(this, new GasSensorFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_EBIO_EPAD.equalsIgnoreCase(typeName)) {
			AxalentUtils.commitFragment(this, new HistoryFragment(AxalentUtils.ATTRIBUTE_STATUS), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_MTM_SM.equalsIgnoreCase(typeName)) {
			AxalentUtils.commitFragment(this, new MtmSensorFragment(AxalentUtils.ATTRIBUTE_ROLL), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_SENSOR_PIR.equalsIgnoreCase(typeName)) {
			AxalentUtils.commitFragment(this, new GuniSensorFragment(), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_VALVE.equalsIgnoreCase(typeName)) {
			AxalentUtils.commitFragment(this, new HistoryFragment(AxalentUtils.ATTRIBUTE_VALVE), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_FLOOD_SENSOR.equalsIgnoreCase(typeName)) {
			AxalentUtils.commitFragment(this, new HistoryFragment(AxalentUtils.ATTRIBUTE_FLOOD), R.id.atyShowDeviceFrame);
		} else if (AxalentUtils.TYPE_SHCK.equalsIgnoreCase(typeName)) {
			AxalentUtils.commitFragment(this, new HistoryFragment(AxalentUtils.ATTRIBUTE_SHOCK), R.id.atyShowDeviceFrame);
		}
	}

	private void initCSRDeviceFragment() {
		if (result == AxalentUtils.GROUP) {
			AxalentUtils.commitFragment(this, LightFragment.newInstance(true), R.id.atyShowDeviceFrame);
		} else if (result == AxalentUtils.SING) {
			int type = csrDevice.getType();
			switch (type) {
				case CSRDevice.TYPE_LIGHT:
					AxalentUtils.commitFragment(this, new LightFragment(), R.id.atyShowDeviceFrame);
					break;
				case CSRDevice.TYPE_TEMPERATURE:
					if (areaId != 0) {
						AxalentUtils.commitFragment(this, TemperatureFragment.newInstance(areaId, temperatureValue), R.id.atyShowDeviceFrame);
					}
					break;
				case CSRDevice.TYPE_UNKNOWN:
					break;
			}
		}
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.barShowDeviceBack:
//			setResult(AxalentUtils.REFRESH_DATA);
			finish();
			break;
		case R.id.barShowDeviceMenu:
			showMenu();
			break;
		case R.id.atyShowDeviceCustName:
			if (AxalentUtils.getLoginMode() != R.id.atyLoginBluetoothBtn) {
				if (AxalentUtils.TYPE_CAMERA.equalsIgnoreCase(currentDevice.getTypeName())) {
					ToastUtils.show(R.string.device_not_support_hereon_edit);
					return;
				}
			}
			showInputDialog();
			break;
		}
	}
	
	private void initData() {
		List<DeviceAttribute> deviceAttributes = currentDevice.getAttributes();
		if (deviceAttributes != null) {
			setPageAttribute();
			initFragment();
		} else {
			getDeviceAttribute();
		}
	}
	
	private void getDeviceAttribute() {
		loadingDialog.show(R.string.load_data);
		deviceManager.getDeviceAttributesWithValues(currentDevice, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				Device tempDevice = XmlUtils.convertDeviceAttributesWithValues(response);
				currentDevice.setState(tempDevice.getState());
				currentDevice.setTypeId(tempDevice.getTypeId());
				currentDevice.setTypeName(tempDevice.getTypeName());
				currentDevice.setAttributes(tempDevice.getAttributes());
				setPageAttribute();
				initFragment();
				loadingDialog.close();
				CacheUtils.replaceDevice(currentDevice);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				setPageAttribute();
				loadingDialog.close();
			}
		});
	}
	
	private void setPageAttribute() {
		Log.i("Device", "setPageAttribute: " + currentDevice);
		deviceImg.setBackgroundResource(AxalentUtils.getDeviceImageByTypeName(currentDevice.getTypeName()));
		custNameTxt.setText(currentDevice.getCustomName());
		devNameTxt.setText(currentDevice.getDevName());
	}
	
	public Device getCurrentDevice() {
		return currentDevice;
	}

	public CSRDevice getCurrentCSRDevice() {
		return csrDevice;
	}

	public Area getCurrentArea() {
		return area;
	}

	public DeviceManager getDeviceManager() {
		return deviceManager;
	}
	
	private void showMenu() {
		if (menuWindow == null) {
			ListView listView = (ListView) getLayoutInflater().inflate(R.layout.menu_home, null);
			menuWindow = new PopupWindow(listView, (int)getResources().getDimension(R.dimen.menu_home_width), LayoutParams.WRAP_CONTENT, true);
			menuWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b0000000")));
			menuWindow.setAnimationStyle(R.style.style_home_menu_animation);
			menuAdapter = new MenuAdapter(this, AxalentUtils.SHOW_DEVICE);
			listView.setAdapter(menuAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					
					if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn &&
							AxalentUtils.getLoginMode() != R.id.atyLoginBluetoothBtn) {
						menuWindow.dismiss();
						ToastUtils.show(R.string.please_change_to_cloud_mode);
						return;
					}
					
					switch (position) {
					case 0:
						if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
							if (result != AxalentUtils.GROUP) {
								showDeleteDeviceDialog();
							}
						} else {
							if (currentDevice.getTypeName().equalsIgnoreCase(AxalentUtils.TYPE_SENSOR_PIR)) {
								Intent intent = new Intent(ShowDeviceActivity.this, AddActivity.class);
								intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.ADD_GUNI_TRIGGER);
								intent.putExtra(AxalentUtils.KEY_DEVICE, getCurrentDevice());
								startActivityForResult(intent, 0);
							} else {
								locationToShowTrigger();
							}
						}
						break;
					case 1:
						showDeleteDeviceDialog();
						break;
					}
					menuWindow.dismiss();
				}
			});
		}
		menuWindow.showAsDropDown(anchor);
	}
	
	
	private void locationToShowTrigger() {
		// 				|| AxalentUtils.TYPE_HTS.equalsIgnoreCase(typeName)
		String typeName = currentDevice.getTypeName();
		if (AxalentUtils.TYPE_SM.equalsIgnoreCase(typeName) 
				|| AxalentUtils.TYPE_SC.equalsIgnoreCase(typeName) 
				|| AxalentUtils.TYPE_HTS.equalsIgnoreCase(typeName)) {
			Intent intent = new Intent(this, ShowTriggerActivity.class);
			intent.putExtra(AxalentUtils.KEY_DEVICE, currentDevice);
			startActivity(intent);
		} else {
			ToastUtils.show(R.string.device_not_support_add_trigger);
		}
	}
	
	public void showInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.alter_name);
		View view = View.inflate(this, R.layout.dialog_input, null);
		builder.setView(view);
		final EditText codeEdit = (EditText) view.findViewById(R.id.dialogInputEdit);
		codeEdit.setHint(R.string.please_input_device_name);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setCustName(codeEdit.getText().toString().trim());
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}
	
	private void setCustName(final String custName) {
		if (TextUtils.isEmpty(custName)) {
			ToastUtils.show(R.string.device_name_not_null);
		} else {
			loadingDialog.show(R.string.is_the_alter);
			if (AxalentUtils.getLoginMode() != R.id.atyLoginBluetoothBtn) {
				deviceManager.setDeviceAttribute(currentDevice.getDevId(), AxalentUtils.ATTRIBUTE_CUSTOM_NAME, custName, new Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser response) {
						currentDevice.setCustomName(custName);
						setNewCustomNameToCacheDevice(custName);
						custNameTxt.setText(custName);
						refreshPageItemDevice();
						loadingDialog.close();
						ToastUtils.show(R.string.alter_success);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtils.show(R.string.alter_failure);
					}
				});
			} else {
				csrDevice.setName(custName);
				CSRDevice resultDevice = dbManager.createOrUpdateDevice(csrDevice);
				loadingDialog.close();
				if (resultDevice != null) {
					titleTxt.setText(resultDevice.getName());
					custNameTxt.setText(resultDevice.getName());
					devNameTxt.setText(resultDevice.getName());
					ToastUtils.show(R.string.alter_success);
				} else {
					ToastUtils.show(R.string.alter_failure);
				}
			}
		}
	}
	
	private void refreshPageItemDevice() {
		Intent intent = new Intent("com.axalent.HomeActivity");
		intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.UPDATE_DEVICE);
		intent.putExtra(AxalentUtils.KEY_DEVICE, currentDevice);
		sendBroadcast(intent);
	}
	
	private void setNewCustomNameToCacheDevice(String newCustName) {
		Device device = CacheUtils.getDeviceByDevId(currentDevice.getDevId());
		if (device != null) {
			device.setCustomName(newCustName);
		}
	}
	
	private void showDeleteDeviceDialog() {
		if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
			deleteCSRDevice(false);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.app_logo_1);
			builder.setTitle(R.string.delete_device);
			builder.setMessage(R.string.confirm_delete_device);
			builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteDevice();
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.create().show();
		}
	}

	private void deleteCSRDevice(final boolean force) {
		final DialogMaterial dialog;
		if (csrDevice.getType() == CSRDevice.TYPE_GATEWAY) {
			dialog = new DialogMaterial(this, "Delete Gateway", "", true, R.color.red);
			TextView tv = new TextView(this);
			tv.setTextColor(getResources().getColor(R.color.red));
			tv.setTextSize(20f);
			tv.setText("If you delete the gateway you will not be able to use the remote function");
			dialog.setBodyView(tv);

		} else {
			dialog = new DialogMaterial(this, "Delete device", force?"Device wasn't found. Do you want to delete it anyway?":"Are you sure that you want to delete this device?");
		}

		dialog.addCancelButton(getString(R.string.cancel));
		dialog.addAcceptButton(getString(R.string.accept), new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (force) {
					deleteDeviceAndFinish();
				}
				else {
					dialog.dismiss();
					startCheckingScanInfo();
					mHashExpected = csrDevice.getDeviceHash();
					if (csrDevice.getUuid() == null) {
						ConfigModel.getInfo(csrDevice.getDeviceID(), DeviceInfo.UUID_LOW);
					} else {
						resetDevice();
					}
				}

			}
		});
		dialog.show();
	}

	private void blacklistDevice() {
		UUID deviceUUID = csrDevice.getUuid();
		if (deviceUUID != null) {
			ConfigCloudApi.addDeviceToBlacklist(
					csrDevice.getDeviceID(),
					MeshService.getDeviceHash64FromUuid(csrDevice.getUuid()),
					csrDevice.getDmKey(),
					0
			);
		}
	}

	private void resetDevice() {
		UUID deviceUUID = csrDevice.getUuid();
		if (deviceUUID != null) {
			ConfigModel.resetDevice(csrDevice.getDeviceID(),
					MeshService.getDeviceHash64FromUuid(csrDevice.getUuid()),
					csrDevice.getDmKey());
		}
		else {
			Association.resetDevice(csrDevice.getDeviceID(), csrDevice.getDmKey());
		}
	}
	
	private void deleteDevice() {
		loadingDialog.show(R.string.is_the_delete);
		if (AxalentUtils.TYPE_CAMERA.equalsIgnoreCase(currentDevice.getTypeName())) {
			String cameraInfo = CacheUtils.getUserAttribute(AxalentUtils.ATTRIBUTE_CAMERA_INFO);
			if (!TextUtils.isEmpty(cameraInfo)) {
				try {
					JSONArray array = new JSONArray(cameraInfo);
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						String uid = (String) object.opt("uid");
						if (currentDevice.getUid().equals(uid)) {
							array.remove(i);
							break;
						}
					}
					deleteUserAttribute(AxalentUtils.ATTRIBUTE_CAMERA_INFO, array.toString());
				} catch (JSONException e) {
					loadingDialog.close();
					ToastUtils.show(R.string.delete_failure);
				}
			} else {
				loadingDialog.close();
				ToastUtils.show(R.string.delete_failure);
			}
		} else {
			deviceManager.removeDeviceFromUser(currentDevice.getDevId(), new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					// ɾ���� Trigger
					CacheUtils.deleteTriggersByDevId(currentDevice.getDevId());
					// ɾ���� Device 
					CacheUtils.deleteDeviceById(currentDevice.getDevId());
					// ɾ��������� Device
					sendDeletePageItemDevice();
					// ɾ�� ChildInfo ��λ��
					deleteChildInfoIndex();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					loadingDialog.close();
					ToastUtils.show(R.string.delete_failure);
				}
			});
		}
	}

	private void startCheckingScanInfo() {
		showProgress();
		mHandler.postDelayed(removeDeviceTimeOut, WAITING_RESPONSE_MS);
		Association.discoverDevices(true);
	}

	private void stopCheckingScanInfo() {
		hideProgress();
		Association.discoverDevices(false);
	}

	private void deleteDeviceAndFinish() {
		Toast.makeText(this,csrDevice.getName() + " removed successfully.",Toast.LENGTH_SHORT).show();
		dbManager.removeDevice(csrDevice.getId());
		setResult(AxalentUtils.REFRESH_DATA);
		finish();
	}

	private Runnable removeDeviceTimeOut = new Runnable() {
		@Override
		public void run() {
			mHashExpected = Constants.INVALID_VALUE;
			stopCheckingScanInfo();
			deleteCSRDevice(true);
		}
	};

	private void showProgress() {
		mDialog = new DialogMaterial(this, getString(R.string.deleting_device), null);
		TextView text = new TextView(this);
		text.setText("Resetting your device. Please wait...");
		mDialog.setBodyView(text);
		mDialog.setCancelable(false);
		mDialog.setShowProgress(true);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
	}

	private void hideProgress() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		super.onDestroy();
		if (mHashExpected != Constants.INVALID_VALUE) {
			stopCheckingScanInfo();
		}
		mHandler.removeCallbacks(removeDeviceTimeOut);
		if (controlSubscription != null) {
			controlSubscription.unsubscribe();
		}
	}
	
	private void sendDeletePageItemDevice() {
		Intent intent = new Intent("com.axalent.HomeActivity");
		intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.DELETE_DEVICE);
		intent.putExtra(AxalentUtils.KEY_DEVICE, currentDevice);
		sendBroadcast(intent);
	}
	
	private void deleteChildInfoIndex() {
		Map<String, String> map = AxalentUtils.getGatewayNameAndIndex(currentDevice.getDevName());
		String index = map.get("index");
		String gatewayName = map.get("gatewayName");
		if (!TextUtils.isEmpty(gatewayName) && !TextUtils.isEmpty(index)) {
			Device gateway = CacheUtils.getDeviceByName(gatewayName);
			if (gateway != null) {
				deviceManager.setDeviceAttribute(gateway.getDevId(), AxalentUtils.ATTRIBUTE_DELETEDEVICE, index, new Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser response) {
						disposeDeleteSuccess();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						disposeDeleteSuccess();
					}
				});
			} else {
				disposeDeleteSuccess();
			}
		} else {
			disposeDeleteSuccess();
		}
	}

	private void disposeDeleteSuccess() {
		loadingDialog.close();
		ToastUtils.show(R.string.delete_success);
		finish();
	}
	
	public void sendUpdateCommand(String key, String val) {
		Intent intent = new Intent("com.axalent.HomeActivity");
		intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.UPDATE_DEVICE);
		AxalentUtils.updateDeviceAttibuteByDevId(currentDevice, key, val);
		intent.putExtra(AxalentUtils.KEY_DEVICE, currentDevice);
		sendBroadcast(intent);
	}
	
	private void deleteUserAttribute(final String name, final String value) {
		new UserManagerImpl().setUserAttribute(name, value, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				String reqCode = XmlUtils.converRequestCode(response);
				if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
					// ɾ���� Trigger
					CacheUtils.deleteDeviceByName(currentDevice.getDevName());
					// ���û�����û�camera����
					CacheUtils.setUserAttribute(name, value);
					// ɾ��������� Device
					sendDeletePageItemDevice();
					// ����ɾ��ɹ���
					disposeDeleteSuccess();
				} else {
					loadingDialog.close();
					ToastUtils.show(R.string.delete_failure);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				loadingDialog.close();
				ToastUtils.show(R.string.delete_failure);
			}
		});
	}
	
	public LoadingDialog getLoadingDialog() {
		return loadingDialog;
	}

	public ImageView getDeviceImg() {
		return deviceImg;
	}

	private void subscribeEvent() {
		controlSubscription = RxBus.getDefaultInstance()
				.toObservable(MeshResponseEvent.class)
				.subscribe(new Action1<MeshResponseEvent>() {
					@Override
					public void call(MeshResponseEvent event) {
						switch (event.what) {
							case GROUP_NUMBER_OF_MODEL_GROUPIDS: {

								// This statement is just to avoid crashes.
								if (!mModelsToQueryForGroups.isEmpty()) {
									int numIds = event.data.getInt(MeshConstants.EXTRA_NUM_GROUP_IDS);
									int modelNo = event.data.getInt(MeshConstants.EXTRA_MODEL_NO);
									int expectedModelNo = mModelsToQueryForGroups.peek();
									int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);

									if (expectedModelNo == modelNo && csrDevice.getDeviceID() == deviceId) {

										if (numIds > 0) {
											int minNumGroups = csrDevice.getNumGroups() <= 0 ? numIds : Math.min(csrDevice.getNumGroups(), numIds);
											csrDevice.setNumGroups(minNumGroups);
										}

										// We know how many groups are supported for this model now so remove it from the queue.
										mModelsToQueryForGroups.remove();
										if (mModelsToQueryForGroups.isEmpty()) {
											dbManager.createOrUpdateDevice(csrDevice);

										} else {
											// Otherwise ask how many groups the next model supports, by taking the next model number from the queue.
											GroupModel.getNumberOfModelGroupIds(csrDevice.getDeviceID(), mModelsToQueryForGroups.peek());
										}

									}
								}

								break;
							}
							case CONFIG_INFO: {
								int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
								DeviceInfo type = DeviceInfo.values()[event.data.getInt(MeshConstants.EXTRA_DEVICE_INFO_TYPE)];

								if (deviceId == csrDevice.getDeviceID()) {
									if (type == DeviceInfo.MODEL_LOW) {
										long modelLow = event.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
										csrDevice.setModelLow(modelLow);
										csrDevice = dbManager.createOrUpdateDevice(csrDevice);
										mCurrentRequestState = DeviceInfo.MODEL_HIGH;
										ConfigModel.getInfo(csrDevice.getDeviceID(), DeviceInfo.MODEL_HIGH);
									}
									else if (type == DeviceInfo.MODEL_HIGH) {
										long modelHigh = event.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
										csrDevice.setModelHigh(modelHigh);
										csrDevice = dbManager.createOrUpdateDevice(csrDevice);
									}
									else if (type == DeviceInfo.UUID_LOW) {

										long uuidLow = event.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
										csrDevice.setUuidLow(uuidLow);
										mCurrentRequestState = DeviceInfo.UUID_HIGH;
										ConfigModel.getInfo(csrDevice.getDeviceID(), DeviceInfo.UUID_HIGH);
									}
									else if (type == DeviceInfo.UUID_HIGH) {
										long uuidHigh = event.data.getLong(MeshConstants.EXTRA_DEVICE_INFORMATION);
										csrDevice.setUuidHigh(uuidHigh);

										// if we are in Bluetooth run Config.Reset if over rest call Blacklist
										if (MeshLibraryManager.getInstance().getChannel() == MeshLibraryManager.MeshChannel.BLUETOOTH) {
											resetDevice();
										} else {
											blacklistDevice();
										}
									}
								}
								break;
							}
							case TIMEOUT: {
								if (event.data.getInt(MeshConstants.EXTRA_EXPECTED_MESSAGE) == MeshConstants.MESSAGE_GROUP_NUM_GROUPIDS) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(ShowDeviceActivity.this, R.string.error_getting_group_info, Toast.LENGTH_SHORT).show();
										}
									});
								}
								else if (event.data.getInt(MeshConstants.EXTRA_EXPECTED_MESSAGE) == MeshConstants.MESSAGE_CONFIG_DEVICE_INFO) {
									int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);

									if (deviceId == csrDevice.getDeviceID()) {
										if (mCurrentRequestState == DeviceInfo.MODEL_HIGH || mCurrentRequestState == DeviceInfo.MODEL_LOW) {
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(ShowDeviceActivity.this, R.string.error_getting_model_info, Toast.LENGTH_SHORT).show();
												}
											});
										}
										if (mCurrentRequestState == DeviceInfo.UUID_LOW || mCurrentRequestState == DeviceInfo.UUID_HIGH) {
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(ShowDeviceActivity.this, R.string.error_removing_device, Toast.LENGTH_SHORT).show();
												}
											});
										}
									}
								}
								break;
							}
							case DEVICE_UUID:
								int uuidHash = event.data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
								if (uuidHash == mHashExpected) {
									deleteDeviceAndFinish();
								}
								break;
						}
					}
				});
	}

}
