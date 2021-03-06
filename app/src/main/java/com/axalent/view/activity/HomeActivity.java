/**
 * File Name                   : ���������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.adapter.MenuAdapter;
import com.axalent.application.BaseActivity;
import com.axalent.application.MyRequestQueue;
import com.axalent.model.HorizontalCSRDeviceData;
import com.axalent.model.User;
import com.axalent.model.UserAttribute;
import com.axalent.model.data.database.DBManager;
import com.axalent.model.data.model.Place;
import com.axalent.model.data.model.Time;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.presenter.AddDevice;
import com.axalent.presenter.BaseActionThread;
import com.axalent.presenter.RxBus;
import com.axalent.presenter.SyncLocalData;
import com.axalent.application.MyCacheData;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.presenter.controller.Manager;
import com.axalent.presenter.controller.UserManager;
import com.axalent.presenter.controller.impl.DeviceManagerImpl;
import com.axalent.presenter.controller.impl.UserManagerImpl;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.DeviceType;
import com.axalent.model.HintError;
import com.axalent.model.HorizontalData;
import com.axalent.model.Trigger;
import com.axalent.presenter.csrapi.ConnectionUtils;
import com.axalent.presenter.csrapi.MeshLibraryManager;
import com.axalent.presenter.axaapi.UserAPI;
import com.axalent.presenter.events.MeshResponseEvent;
import com.axalent.presenter.events.MeshSystemEvent;
import com.axalent.util.DialogBuilder;
import com.axalent.view.fragment.GroupFragment;
import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.SensorValue;
import com.axalent.view.fragment.MainFragment;
import com.axalent.view.fragment.MeFragment;
import com.axalent.view.fragment.SceneFragment;
import com.axalent.view.fragment.ScheduleFragment;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.InstallationUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.LoadingDialog;
import com.tutk.IOTC.Camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import rx.Subscription;
import rx.functions.Action1;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
	
	private View anchor;
	private int count = 0;
	private PopupWindow pw;
	private TextView titleTxt;
	private ImageButton channelStatusButton;
	private MenuAdapter menuAdapter;
	private BroadcastReceiver receiver;
	private int fragmentIndex = R.id.atyHomeMainBtn;
	private UserManager userManager = new UserManagerImpl();
	private DeviceManager deviceManager = new DeviceManagerImpl();
	private List<HorizontalData> datas = new ArrayList<HorizontalData>();
	private List<HorizontalCSRDeviceData> csrDatas = new ArrayList<HorizontalCSRDeviceData>();
	private Map<Integer, Fragment> fragments = new HashMap<Integer, Fragment>();
	public LoadingDialog loadingDialog;
	public static boolean isForeground = false;
	public BaseActionThread baseActionThread;
	private int refershCount = 0;
	private Subscription subscription;
	private Subscription controlSubscription;

	public DBManager dbManager;
	private Place mPlace;

	private TextView databaseUpdate;
	private SharedPreferences sp;
	private String updateTime;

	public double mTemperature = 0d;
	public int csrDeviceId = 0;

	private Dialog bleDialog=null;

	private static final int ACCESS_COARSE_LOCATION_RESULT_CODE = 4;
	private static final int BLUETOOTH_RESULT_CODE = 5;
	private static final int STORAGE_RESULT_CODE = 6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences(AxalentUtils.USER_FILE_NAME, MODE_PRIVATE);
		initActionBar();
		initView();
		initFragment();
		loadData();
		if (isBluetoothMode()) {
			dbManager = DBManager.getDBManagerInstance(this.getApplicationContext());
			mPlace = dbManager.getPlace(1);
			MeshLibraryManager.getInstance().setNetworkPassPhrase(mPlace != null ? mPlace.getNetworkKey() : "password");
			updateChannelStatus();
			subscribeEvent();
			subscribeEvent2();
			showLocalDataBaseDevice();
		} else {
			initBroadcastReceiver();
			registerMessageReceiver();

			baseActionThread = new BaseActionThread() {
				@Override
				public void onActionStop() {}

				@Override
				public void onActionStart() {
					if (refershCount != 0) {
						return;
					}

					final List<Device> devices = MyCacheData.getDevices();
					LogUtils.i("size:"+devices.size());
					for (int i = 0; i < devices.size(); i++) {
						final Device device = devices.get(i);
						if (device == null || device.getTypeName() == null) {
							refershCount++;
							continue;
						}
						if (device.getTypeName().equals(AxalentUtils.TYPE_CAMERA)) {
							refershCount++;
							continue;
						}
						deviceManager.getDeviceAttributesWithValues(device, new Listener<XmlPullParser>() {
							@Override
							public void onResponse(XmlPullParser pull) {
								LogUtils.i("name:"+device.getDevName());
								refershCount++;
								Device resultDevice = XmlUtils.convertDeviceAttributesWithValues(pull);
								device.setState(resultDevice.getState());
								device.setAttributes(resultDevice.getAttributes());
								if (device.getTypeName().equals(AxalentUtils.TYPE_SL)) {
									String toggle = AxalentUtils.getDeviceAttributeValue(device, AxalentUtils.ATTRIBUTE_LIGHT);
									LogUtils.i("�����豸����:"+device.getDevName()+" toggle:"+toggle);
								}
								if (refershCount == devices.size()) {
									refershCount = 0;
									notifyPageRefresh();
								}
							}
						}, new ErrorListener() {
							public void onErrorResponse(VolleyError arg0) {
								refershCount++;
								HintError hintError = XmlUtils.converErrorMsg(arg0);
								if (AxalentUtils.LOGIN_RESET_CODE.equals(hintError.getErrorCode())) {
									// ��Կ�������µ�¼
									restartLogin();
								}
							};
						});
					}
				}

				@Override
				public void onActionDestroy() {}
			};

			baseActionThread.setSleepTime(3000);

		}
		setupPushTagOrAlias();
	}

	public DBManager getDbManager() {
		return dbManager;
	}
	
	public void setupPushTagOrAlias() {
		if (!sharedPreferences.getBoolean("isSetAlias", false)) {
			JPushInterface.setAliasAndTags(this, InstallationUtils.getImei().replaceAll("-", ""), null, new TagAliasCallback() {
				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					switch (arg0) {
					case 0:
						sharedPreferences.edit().putBoolean("isSetAlias", true).commit();
						LogUtils.i("Set tag and alias success");
						break;
					case 6002:
						LogUtils.i("Failed to set alias and tags due to timeout. Try again after 60s.");
					default:
						LogUtils.i("Failed with errorCode:" + arg0);
					}
				}
			});
		}
	}

	public boolean isBluetoothMode() {
		return AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn;
	}
	
	@Override
	protected void onPause() {
		isForeground = false;
//		JPushInterface.onPause(this);
		if (!isBluetoothMode()) {
			baseActionThread.stopAction();
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		isForeground = true;
//		JPushInterface.onResume(this);
		if (!isBluetoothMode()) {
			baseActionThread.startAction();
		}

		super.onResume();
		checkPermissions();
		if (isBluetoothMode()) {
			verifyBluetooth();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case BLUETOOTH_RESULT_CODE:
			case ACCESS_COARSE_LOCATION_RESULT_CODE:
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission was granted
					checkPermissions();
				} else {
					// permission denied, notify and close the app
					Toast.makeText(this, getString(R.string.denided_permission), Toast.LENGTH_LONG).show();
					finish();
				}
				return;
		}
	}

	//for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.axalent.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	
	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.database_update:
				databaseUpdate.setVisibility(View.GONE);
				syncServerData();
				break;
		}
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
              String messge = intent.getStringExtra(KEY_MESSAGE);
//              String extras = intent.getStringExtra(KEY_EXTRAS);
              StringBuilder showMsg = new StringBuilder();
              showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
              LogUtils.i("msg:"+showMsg.toString());
			}
		}
	}
	
	private void initActionBar() {
		View content = findViewById(R.id.barHomeContent);
		RelativeLayout menu = (RelativeLayout) content.findViewById(R.id.barHomeMenu);
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMenu();
			}
		});

		titleTxt = (TextView) content.findViewById(R.id.barHomeTitleTxt);
		channelStatusButton = (ImageButton) content.findViewById(R.id.channel_status);

		int titleId = R.string.cloud;

		switch (AxalentUtils.getLoginMode()) {
			case R.id.atyLoginCloudBtn:
				titleId = R.string.cloud;
				break;
//			case R.id.atyLoginGatewayBtn:
//				titleId = R.string.gateway;
//				break;
			case R.id.atyLoginBluetoothBtn:
				titleId = R.string.bluetooth;
				channelStatusButton.setVisibility(View.VISIBLE);
				break;
		}

		titleTxt.setText(String.valueOf(getText(R.string.main)) +" - "+ getText(titleId));


	}
	
	private void initBroadcastReceiver() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context ctx, Intent data) {
				
				int action = data.getIntExtra(AxalentUtils.KEY_SKIP, 0);
				Device device = data.getParcelableExtra(AxalentUtils.KEY_DEVICE);
				
				switch (action) {
				case AxalentUtils.REFRESH_DATA:
					loadData();
					break;
				case AxalentUtils.UPDATE_DEVICE:
					((MainFragment) fragments.get(fragmentIndex)).updatePageDevice(device);
					break;
				case AxalentUtils.DELETE_DEVICE:
					((MainFragment) fragments.get(fragmentIndex)).deletePageDevice(device);
					break;
				case AxalentUtils.ADD_DEVICE:
					// new 
					if (device == null || device.getTypeName() == null) {
						return;
					}
					
					if (device.getTypeName().equals(AxalentUtils.TYPE_GATEWAY)) {
						return;
					}
					
					((Manager) fragments.get(fragmentIndex)).addDevice(device);
					
					break;
				}
			}
		};
		IntentFilter filter = new IntentFilter("com.axalent.HomeActivity");
		registerReceiver(receiver, filter);
	}
	
	
	private void showMenu() {
		if (pw == null) {
			ListView listView = (ListView) getLayoutInflater().inflate(R.layout.menu_home, null);
			pw = new PopupWindow(listView, (int)getResources().getDimension(R.dimen.menu_home_width), LayoutParams.WRAP_CONTENT, true);
			pw.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b0000000")));
			pw.setAnimationStyle(R.style.style_home_menu_animation);
			menuAdapter = new MenuAdapter(this, fragmentIndex);
			listView.setAdapter(menuAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					
					switch (fragmentIndex) {
					case R.id.atyHomeSceneBtn:
					case R.id.atyHomeScheduleBtn:
						if (AxalentUtils.getLoginMode() != R.id.atyLoginBluetoothBtn) {
							if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
								pw.dismiss();
								ToastUtils.show(R.string.please_change_to_cloud_mode);
								return;
							}
							((Manager) fragments.get(fragmentIndex)).addDevice(null);
						} else {
							goToAddGroup();
						}
						break;
					case R.id.atyHomeMainBtn:
						Intent intent = null;
						switch (position) {
						case 0:
							if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
								MeshLibraryManager.MeshChannel channel = MeshLibraryManager.getInstance().getChannel();
								if (channel == MeshLibraryManager.MeshChannel.BLUETOOTH) {
									if (MeshLibraryManager.getInstance().isChannelReady()) {
										Intent intent1 = new Intent(HomeActivity.this, DetectedDevicesActivity.class);
										startActivityForResult(intent1, AxalentUtils.REFRESH_DATA);
									}
									else {
										Toast.makeText(HomeActivity.this, getString(R.string.bluetooth_needed_to_add_devices), Toast.LENGTH_SHORT).show();
									}
								} else {
									Toast.makeText(HomeActivity.this, getString(R.string.bluetooth_selected_to_add_devices), Toast.LENGTH_SHORT).show();
									MeshLibraryManager.getInstance().setBluetoothChannelEnabled();
								}
							} else {
								if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
									pw.dismiss();
									ToastUtils.show(R.string.please_change_to_cloud_mode);
									return;
								}
								intent = new Intent(HomeActivity.this, CaptureActivity.class);
								startActivity(intent);

//								if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
//									pw.dismiss();
//									ToastUtils.show(R.string.please_change_to_cloud_mode);
//									return;
//								}
//								intent = new Intent(HomeActivity.this, AddActivity.class);
//								intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.ADD_GATEWAY_DEVICE);
//								startActivity(intent);
							}
							break;
						case 1:
							// �����豸
							if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
								// cloud sync
								Log.i("test", "cloud sync");
								syncServerData();

							}else if (AxalentUtils.getLoginMode() == R.id.atyLoginCloudBtn) {
								startActivity(new Intent(HomeActivity.this, SearchActivity.class));
							} else {
								pw.dismiss();
								ToastUtils.show(R.string.please_change_to_cloud_mode);
								return;
							}
							break;
						case 2:
							if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
								// send data

							} else {
//								if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
//									pw.dismiss();
//									ToastUtils.show(R.string.please_change_to_cloud_mode);
//									return;
//								}
//								intent = new Intent(HomeActivity.this, CaptureActivity.class);
//								startActivity(intent);
							}
							break;
						case 3:
							if (AxalentUtils.getLoginMode() == R.id.atyLoginBluetoothBtn) {
								// update network key
								Log.i("test", "update network key");
//								showUpdateNetworkKeyDialog();
							} else {
								syncLocalData();
							}
							break;
						}
						
						break;
					}
					pw.dismiss();
				}
			});
		} else {
			menuAdapter.setPageType(fragmentIndex);
		}
		pw.showAsDropDown(anchor);
	}

	private void goToAddGroup() {
		Intent intent = new Intent(HomeActivity.this, AddGroupActivity.class);
		startActivityForResult(intent, AxalentUtils.ADD_GROUP);
	}
	
	private void syncLocalData() {
		loadingDialog.show(R.string.is_the_sync);
		SyncLocalData syncLocalData = new SyncLocalData();
		syncLocalData.execute(AxalentUtils.getUrl() + "syncData", getUser());
		syncLocalData.setSyncDataListener(new SyncLocalData.SyncDataListener() {
			@Override
			public void syncDataListener(XmlPullParser result) {
				loadingDialog.close();
				if (result != null) {
					String reqCode = XmlUtils.converRequestCode(result);
					if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
						ToastUtils.show(R.string.sync_success);
						loadData();
					} else {
						ToastUtils.show(R.string.sync_failure);
					}
				} else {
					ToastUtils.show(R.string.sync_failure);
				}
			}
		});
	}
	
//	loadingDialog.show(R.string.is_the_sync);
//	UserManager userManager = new UserManagerImpl();
//	userManager.syncLocalData(getUser(), new Listener<XmlPullParser>() {
//		@Override
//		public void onResponse(XmlPullParser response) {
//			loadingDialog.close();
//			String reqCode = XmlUtils.converRequestCode(response);
//			ToastUtils.show("ͬ�����:"+reqCode);
//			if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
//				ToastUtils.show(R.string.sync_success);
//				((MainFragment) fragments.get(currentFragmentIndex)).onRefresh();
//			} else {
//				ToastUtils.show(R.string.sync_failure);
//			}
//		}
//	}, new ErrorListener() {
//		@Override
//		public void onErrorResponse(VolleyError error) {
//			loadingDialog.close();
//			ToastUtils.show(XmlUtils.converErrorMsg(error));
//		}
//	});

	private void showUpdateNetworkKeyDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.alter_network_key);
		View view = View.inflate(this, R.layout.dialog_input, null);
		builder.setView(view);
		final EditText codeEdit = (EditText) view.findViewById(R.id.dialogInputEdit);
		if (mPlace != null) {
			codeEdit.setText(mPlace.getNetworkKey());
		} else {
			codeEdit.setHint(R.string.please_input_network_key);
		}
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setNetworkKey(codeEdit.getText().toString().trim());
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

	private void setNetworkKey(String networkKey) {
		// 预留

	}
	
	private void initView() {
		anchor = findViewById(R.id.atyHomeAnchor);
		loadingDialog = new LoadingDialog(this);
		RadioGroup group = (RadioGroup) findViewById(R.id.atyHomeGroup);
		if (isBluetoothMode()) {
			RadioButton sceneBtn = (RadioButton) findViewById(R.id.atyHomeSceneBtn);
			sceneBtn.setText(getString(R.string.navigation_group));
			RadioButton scheduleBtn = (RadioButton) findViewById(R.id.atyHomeScheduleBtn);
			scheduleBtn.setVisibility(View.GONE);
			RadioButton groupBtn = (RadioButton) findViewById(R.id.atyHomeGroupBtn);
			groupBtn.setVisibility(View.GONE);
			databaseUpdate = (TextView) findViewById(R.id.database_update);
			databaseUpdate.setVisibility(sp.getBoolean("isShowMsg", false) ? View.VISIBLE : View.GONE);
			databaseUpdate.setOnClickListener(this);
		}
		group.check(R.id.atyHomeMainBtn);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				changeFragment(checkedId);
				changeTitle();
			}
		});
	}
	
	private void changeFragment(int checkedId) {
		cancelRequests();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment hideFragment = fragments.get(fragmentIndex);
		hideFragment.onPause();
		transaction.hide(hideFragment);
		
		Fragment showFragment = fragments.get(checkedId);
		if (showFragment == null) {
			showFragment = getFragment(checkedId);
			transaction.add(R.id.atyHomeFrameLayout, showFragment).commit();
			fragments.put(checkedId, showFragment);
		} else {
			transaction.show(showFragment).commit();
		}

		fragmentIndex = checkedId;


	}

	private void changeTitle() {
		int titleId = R.string.cloud;
		switch (AxalentUtils.getLoginMode()) {
			case R.id.atyLoginCloudBtn:
				titleId = R.string.cloud;
				break;
//			case R.id.atyLoginGatewayBtn:
//				titleId = R.string.gateway;
//				break;
			case R.id.atyLoginBluetoothBtn:
				titleId = R.string.bluetooth;
				break;
		}
		titleTxt.setText(String.valueOf(getText(getTitleId())) +" - "+ getText(titleId));
	}
	
	private int getTitleId() {
		switch (fragmentIndex) {
		case R.id.atyHomeMainBtn:
			return R.string.main;
		case R.id.atyHomeSceneBtn:
			if (isBluetoothMode()) {
				return R.string.group;
			}
			return R.string.scene;
		case R.id.atyHomeGroupBtn:
			return R.string.group;
		case R.id.atyHomeScheduleBtn:
			return R.string.schedule;
		default:
			return R.string.me;
		}
	}
	
	private void initFragment() {
		MainFragment mf = new MainFragment();
		getFragmentManager().beginTransaction().add(
				R.id.atyHomeFrameLayout, mf).commit();
		fragments.put(R.id.atyHomeMainBtn, mf);
	}
	
	private Fragment getFragment(int checkedId) {
		switch (checkedId) {
		case R.id.atyHomeSceneBtn:
			return new SceneFragment();
		case R.id.atyHomeScheduleBtn:
			return new ScheduleFragment();
		case R.id.atyHomeGroupBtn:
			return new GroupFragment();
		default:
			return new MeFragment();
		}
	}
	
	/**
	 * �������
	 */
	public void loadData() {
		count = 0;
		deviceManager.getDeviceList(deviceListSuccess, deviceListError);
		deviceManager.getDeviceTypeList(deviceTypeListSuccess, deviceTypeListError);
		userManager.getUserValueList(userValueListSuccess, userValueListError);
		deviceManager.getTriggerByUser(triggerListSuccess, triggerListError);
	}

	/****************************������ݳɹ�ʧ�ܼ���*************************************/
	private Listener<XmlPullParser> deviceListSuccess = new Listener<XmlPullParser>() {
		@Override
		public void onResponse(XmlPullParser pull) {
			LogUtils.i("get device list success");
			List<Device> devices = XmlUtils.converDeviceList(pull);
			CacheUtils.saveDevices(devices);
			assertEnd();
		}
	};

	private ErrorListener deviceListError = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			LogUtils.i("get device list error");
			HintError hintError = XmlUtils.converErrorMsg(error);
			if (AxalentUtils.LOGIN_RESET_CODE.equals(hintError.getErrorCode())) {
				// ��Կ�������µ�¼
				restartLogin();
			} else {
				assertEnd();
			}
		}
	};

	private Listener<XmlPullParser> deviceTypeListSuccess = new Listener<XmlPullParser>() {
		@Override
		public void onResponse(XmlPullParser pull) {
			LogUtils.i("get device type list success");
			List<DeviceType> deviceTypes = XmlUtils.convertDeviceTypeList(pull);
			CacheUtils.saveDeviceTypes(deviceTypes);
			assertEnd();
		}
	};

	private ErrorListener deviceTypeListError = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			LogUtils.i("get device type list error");
			if (AxalentUtils.getLoginMode() != R.id.atyLoginCloudBtn) {
				try {
					InputStream is = getAssets().open("axalent_device_type.xml");
					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					XmlPullParser pull = factory.newPullParser();
					pull.setInput(is, "UTF-8");
					List<DeviceType> deviceTypes = XmlUtils.convertDeviceTypeList(pull);
					CacheUtils.saveDeviceTypes(deviceTypes);
				} catch (IOException e) {
				} catch (XmlPullParserException e) {
				}
			}
			assertEnd();
		}
	};

	private Listener<XmlPullParser> triggerListSuccess = new Listener<XmlPullParser>() {
		@Override
		public void onResponse(XmlPullParser pull) {
			LogUtils.i("get trigger list success");
			List<Trigger> triggers = XmlUtils.convertTriggerByUser(pull);
			CacheUtils.saveTriggers(triggers);
		}
	};

	private ErrorListener triggerListError = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			LogUtils.i("get trigger ist error");
		}
	};

	private Listener<XmlPullParser> userValueListSuccess = new Listener<XmlPullParser>() {
		@Override
		public void onResponse(XmlPullParser pull) {
			LogUtils.i("get user value list success");
			List<UserAttribute> userAttributes = XmlUtils.converUserValueList(pull);
			CacheUtils.getUser().setUserAttributes(userAttributes);
			assertEnd();
		}
	};

	private ErrorListener userValueListError = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			LogUtils.i("get user value list error");
			assertEnd();
		}
	};
	
	/****************************������ݳɹ�ʧ�ܼ���*************************************/

	private void assertEnd() {
		count++;
		if (ok()) {
			setupDatas();
			examineUserInfo();
			notifyPageRefresh();
			examineGatewayChildInfo();
			if (!isBluetoothMode()) {
				sendMsgToGateway();
			}
		}
		
	}

	private void setupDatas() {
		List<DeviceType> deviceTypes = CacheUtils.getDeviceTypes();
		List<Device> currentDevices = CacheUtils.getDevices();
		
		datas.clear();
		for (DeviceType deviceType : deviceTypes) {
			List<Device> devices = findDevices(deviceType, currentDevices);
			if (devices != null) {
				HorizontalData horiDatas = new HorizontalData(devices, 0, 0, null);
				datas.add(horiDatas);
			}
		}
	}

	public void setupCSRDatas() {
		List<Integer> types = Arrays.asList(CSRDevice.TYPE_LIGHT, CSRDevice.TYPE_TEMPERATURE, CSRDevice.TYPE_GATEWAY, CSRDevice.TYPE_UNKNOWN);
		List<CSRDevice> devices = dbManager.getAllDevicesList();
		csrDatas.clear();
		for (int t : types) {
			List<CSRDevice> tempDevices = findDevices(t, devices);
			if (tempDevices != null) {
				HorizontalCSRDeviceData horizontalCSRDeviceData = new HorizontalCSRDeviceData(tempDevices, 0, 0, null);
				csrDatas.add(horizontalCSRDeviceData);
			}
		}
	}

	private List<CSRDevice> findDevices(int type, List<CSRDevice> currentDevices) {
		List<CSRDevice> childDevices = null;
		for (CSRDevice device : currentDevices) {
			if (device.getType() == type) {
				if (childDevices == null) {
					childDevices = new ArrayList<>();
				}
				if (device.getType() == CSRDevice.TYPE_GATEWAY) {
					if (sharedPreferences.getBoolean("showGateway", false)) {
						childDevices.add(device);
					}
				} else {
					childDevices.add(device);
				}
			}
		}
		return childDevices;
	}
	
	private List<Device> findDevices(DeviceType deviceType, List<Device> currentDevices) {
		List<Device> childDevices = null;
		for (Device device : currentDevices) {
			if (device.getTypeId().equals(deviceType.getId())) {
				device.setTypeName(deviceType.getName());
				device.setDisplayName(deviceType.getDisplayName());
				if (filtration(deviceType.getName())) {
					if (childDevices == null) {
						childDevices = new ArrayList<Device>();
					}
					childDevices.add(device);
				}
			}
		}
		return childDevices;
	}
	
	private boolean ok() {
		return count == 3;
	}
	
	private void examineGatewayChildInfo() {
		List<Device> getaways = CacheUtils.getDeviceByTypeName(AxalentUtils.TYPE_GATEWAY);
		if (getaways.isEmpty()) {
			if (!isBluetoothMode()) {
				new AlertDialog.Builder(this)
						.setIcon(R.drawable.app_logo_1)
						.setTitle(R.string.add_gateway)
						.setMessage(R.string.check_no_gateway)
						.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startActivity(new Intent(HomeActivity.this, CaptureActivity.class));
							}
						})
						.setNegativeButton(R.string.cancel, null)
						.create()
						.show();
			}
		} else {
			for (final Device gateway : getaways) {
				List<DeviceAttribute> deviceAttributes = gateway.getAttributes();
				if (deviceAttributes != null && deviceAttributes.size() > 0) {
					String childInfo = AxalentUtils.getDeviceAttributeValue(deviceAttributes, AxalentUtils.ATTRIBUTE_CHILDINFO);
					detection(gateway, childInfo);
				} else {
					deviceManager.getDeviceAttribute(gateway.getDevId(), AxalentUtils.ATTRIBUTE_CHILDINFO, new Listener<XmlPullParser>() {
						@Override
						public void onResponse(XmlPullParser response) {
							DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(response);
							String childInfo = deviceAttribute.getValue();
							detection(gateway, childInfo);
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							
						}
					});
				}
			}
		}
	}
	
	/**
	 * �����ص� ChildInfo
	 * @param childInfo
	 */
	private void detection(final Device gateway, String childInfo) {
		if (!TextUtils.isEmpty(childInfo)) {
			int index = 0; 
			StringBuilder sb = new StringBuilder();
			char[] c = childInfo.toCharArray();

			for (int i = 0; i < c.length; i++) {
				sb.append(c[i]);
				if (sb.length() == 2) {
					index ++;
					final String childInfoValue = sb.toString();
					final String devName = AxalentUtils.getDevName(index, gateway.getDevName());
					final Device device = CacheUtils.getDeviceByName(devName);
//					LogUtils.i("detection childInfo device:"+devName);
					if (device != null) {
						// ���豸
						final String typeName = AxalentUtils.getTypeName(childInfoValue);

						Log.i("type_group_name",typeName);

						if (device.getTypeName() != null && !device.getTypeName().equalsIgnoreCase(typeName) &&
								!device.getTypeName().equalsIgnoreCase(AxalentUtils.TYPE_AXALENT_SCENE)) {
							// ���Ͳ�ƥ��,֤��֮ǰ����д��󣬣��Ƶ� ChildInfoValue��82 ������ֵ����82��ɾ���������
							deviceManager.removeDeviceFromUser(device.getDevId(), new Listener<XmlPullParser>() {
								@Override
								public void onResponse(XmlPullParser response) {
									// ɾ��ɹ�
									String reqCode = XmlUtils.converRequestCode(response);
									if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
										// ɾ������ϵ��Ǹ��豸
										((MainFragment) fragments.get(R.id.atyHomeMainBtn)).deletePageDevice(device);
										// ɾ����е��豸
										CacheUtils.deleteDeviceById(device.getDevId());
										// �������
										if (!"00".equals(childInfoValue)) {
											Device currentDevice = new Device();
											currentDevice.setTypeId("");
											currentDevice.setPassword("XX");
											currentDevice.setDevName(devName);
											currentDevice.setTypeName(typeName);
											currentDevice.setDisplayName(AxalentUtils.getDisplayName(typeName));
											addDetectionDevice(currentDevice);
										}
									}
								}
							}, null);
						}
					} else {
						// ���豸,�����
						if (!"00".equals(childInfoValue)) {
							Device currentDevice = new Device();
							currentDevice.setTypeId("");
							currentDevice.setPassword("XX");
							currentDevice.setDevName(devName);
							currentDevice.setTypeName(AxalentUtils.getTypeName(childInfoValue));
							currentDevice.setDisplayName(AxalentUtils.getDisplayName(currentDevice.getTypeName()));
							addDetectionDevice(currentDevice);
						}
					}
				
					sb.setLength(0);
				}
			}
		}
	}
	
	private void addDetectionDevice(Device device) {
		final String typeName = device.getTypeName();
		AddDevice addDevice = new AddDevice(deviceManager);
		addDevice.setOnAddDeviceListener(new AddDevice.OnAddDeviceListener() {
			@Override
			public void onAddDevice(Device newDevice, HintError error) {
				if (newDevice != null) {
//					LogUtils.i("����豸�ɹ�:"+newDevice.getTypeName());
					if (AxalentUtils.TYPE_GATEWAY_SCHEDULE.equalsIgnoreCase(typeName)
							|| AxalentUtils.TYPE_GATEWAY_SCENE.equalsIgnoreCase(typeName)) {
						return;
					}
					((MainFragment) fragments.get(R.id.atyHomeMainBtn)).addDevice(newDevice);
				} else {
//					LogUtils.i("����豸ʧ��");
				}
			}
		});
		addDevice.addDevice(device);
	}
	
	
	
	private void notifyPageRefresh() {
		for (Entry<Integer, Fragment> entry : fragments.entrySet()) {
			Manager manager = (Manager) entry.getValue();
			manager.notifyPageRefresh();
		}
	}
	
	
	/**
	 * ����û��Ƿ�ӵ������ͷ�������������ӵ�������
	 */
	private void examineUserInfo() {
		String cameraInfo = CacheUtils.getUserAttribute(AxalentUtils.ATTRIBUTE_CAMERA_INFO);
		if (!cameraInfo.equals("")) {
			List<Device> cameras = new ArrayList<Device>();
			try {
				JSONArray array = new JSONArray(cameraInfo);
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					Device camera = new Device();
					camera.setDevName("camera" + object.optString("cameraId"));
					camera.setUid(object.optString("uid"));
					camera.setPassword(object.optString("password"));
					camera.setDisplayName("Camera");
					camera.setDevId(camera.getDevName());
					camera.setTypeId("");
					camera.setTypeName(AxalentUtils.TYPE_CAMERA);
					camera.setState(AxalentUtils.ON);
					camera.setAttributes(new ArrayList<DeviceAttribute>());
					cameras.add(camera);
				}
				
				if (!cameras.isEmpty()) {
					CacheUtils.getDevices().addAll(cameras);
					List<HorizontalData> horizontalDatas = getHorizontalDatas();
					horizontalDatas.add(new HorizontalData(cameras, 0, 0, null));
					Camera.init();
				}
				
			} catch (JSONException e) {
			}
}
}


private boolean filtration(String typeName) {
		if (AxalentUtils.TYPE_AXALENT_SCENE.equalsIgnoreCase(typeName)) {
			return false;
		} else if (AxalentUtils.TYPE_GATEWAY_GROUP.equals(typeName)) {
			return false;
		} else if (AxalentUtils.TYPE_GATEWAY_SCENE.equalsIgnoreCase(typeName)) {
			return false;
		} else if (AxalentUtils.TYPE_GATEWAY_SCHEDULE.equalsIgnoreCase(typeName)) {
			return false;
		} else if (AxalentUtils.TYPE_GATEWAY.equalsIgnoreCase(typeName)) {
			return sharedPreferences.getBoolean("showGateway", false);
		}
		return true;
	}
	
	private void restartLogin() {
		ToastUtils.show(R.string.token_lapsed_restart_login_start);
		userManager.userLogin(getUser(), new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				User tempUser = XmlUtils.convertUserLogin(response);
				MyCacheData.getInstance().getCacheUser().setUserId(tempUser.getUserId());
				MyCacheData.getInstance().getCacheUser().setSecurityToken(tempUser.getSecurityToken());
				ToastUtils.show(R.string.restart_login_success);
				loadData();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
	}
	
	public DeviceManager getDeviceManager() {
		return deviceManager;
	}
	
	public List<HorizontalData> getHorizontalDatas() {
		return datas;
	}

	public List<HorizontalCSRDeviceData> getHorizontalCSRDeviceDatas() {
		return csrDatas;
	}
	
	private User getUser() {
		SharedPreferences sp = getSharedPreferences(AxalentUtils.USER_FILE_NAME, MODE_PRIVATE);
		String username = sp.getString("username", "");
		String password = sp.getString("password", "");
		return new User(username, password);
	}
	
	
	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case AxalentUtils.ADD_GROUP:
				((SceneFragment) fragments.get(fragmentIndex)).loadDatas();
				break;
			case AxalentUtils.SET_AVATAR:
				((MeFragment) fragments.get(fragmentIndex)).setAvatar(data);
				break;
			case AxalentUtils.REFRESH_DATA:
				Log.i("test", "onActivityResult");
				if (isBluetoothMode()) {
					// load local data
					setupCSRDatas();
					notifyPageRefresh();
				} else {
					loadData();
				}
				break;
			case AxalentUtils.ADD_ACCOUNT:
				boolean isShowMsg = sp.getBoolean("isShowMsg", false);
				databaseUpdate.setVisibility(isShowMsg ? View.VISIBLE : View.GONE);
				if (resultCode != AxalentUtils.ACCOUNT_FINISH) {
					loadData();
					syncServerData();
				}
				break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		LogUtils.i("�����");
		Camera.uninit();
		if (isBluetoothMode()) {
			subscription.unsubscribe();
			controlSubscription.unsubscribe();
		} else {
			unregisterReceiver(receiver);
			unregisterReceiver(mMessageReceiver);
			baseActionThread.destroyAction();
		}
		sp.edit().putInt("loginMode", R.id.atyLoginCloudBtn).commit();
		super.onDestroy();
	}

	private void showLocalDataBaseDevice() {
		setupCSRDatas();
	}

	private void subscribeEvent() {
		subscription = RxBus.getDefaultInstance()
				.toObservable(MeshSystemEvent.class)
				.subscribe(new Action1<MeshSystemEvent>() {
					@Override
					public void call(MeshSystemEvent meshSystemEvent) {
						switch (meshSystemEvent.what) {
							case SERVICE_BIND:
								updateConnectionSettings();
								break;
							case CHANNEL_READY:
								updateChannelStatus();
								break;
							case CHANNEL_NOT_READY:
								updateChannelStatus();
								break;
							case PLACE_CHANGED:
								updateConnectionSettings();
								break;
						}
					}
				});
	}

	private void subscribeEvent2() {
		controlSubscription = RxBus.getDefaultInstance()
				.toObservable(MeshResponseEvent.class)
				.subscribe(new Action1<MeshResponseEvent>() {
					@Override
					public void call(MeshResponseEvent meshResponseEvent) {
						switch (meshResponseEvent.what) {
							case SENSOR_VALUE: {
								csrDeviceId = meshResponseEvent.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
								Log.i("csr device", csrDeviceId+"");
								SensorValue value1 = (SensorValue) meshResponseEvent.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE1);
								SensorValue value2 = null;
								if (meshResponseEvent.data.containsKey(MeshConstants.EXTRA_SENSOR_VALUE2)) {
									value2 = (SensorValue) meshResponseEvent.data.getParcelable(MeshConstants.EXTRA_SENSOR_VALUE2);
								}
								populateSensorValue(value1, value2);
							}
							case REFRESH_PAGE:
								setupCSRDatas();
								notifyPageRefresh();
								break;
							case DATABASE_UPDATE:
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										databaseUpdate.setVisibility(View.VISIBLE);
										SharedPreferences.Editor editor = sp.edit();
										editor.putBoolean("isShowMsg", true);
										editor.apply();
									}
								});
								break;
						}
					}
				});
	}

	public double getTemperature() {
		return mTemperature;
	}

	private void populateSensorValue(SensorValue... sensorValues) {

		if (sensorValues == null) {
			return;
		}
		for (int i = 0; i < sensorValues.length; i++) {
			SensorValue sensorValue = sensorValues[i];

			if (sensorValue.getType() == SensorValue.SensorType.INTERNAL_AIR_TEMPERATURE) {
				// store the temperature in the status array.
				final double tempCelsius = AxalentUtils.convertKelvinToCelsius((float)sensorValue.getValue().get("temperatureKelvin"));

				LogUtils.i("Celsius:" + tempCelsius);

				mTemperature = tempCelsius;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setupCSRDatas();
						notifyPageRefresh();
					}
				});
			}
		}
	}

	private void updateChannelStatus() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MeshLibraryManager.MeshChannel channel = MeshLibraryManager.getInstance().getChannel();
				if (channel == MeshLibraryManager.MeshChannel.BLUETOOTH) {
					if (MeshLibraryManager.getInstance().isChannelReady()) {
						channelStatusButton.setImageDrawable(AxalentUtils.getDrawable(HomeActivity.this, R.drawable.ic_bluetooth_black_24dp));
						channelStatusButton.setColorFilter(0xffffffff);
						ToastUtils.show(getString(R.string.have_channel));
						RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.POWER_STATUS));
					}
					else {
						channelStatusButton.setImageDrawable(AxalentUtils.getDrawable(HomeActivity.this, R.drawable.ic_bluetooth_disabled_black_24dp));
						channelStatusButton.setColorFilter(Color.parseColor("#3f51b5"));
						ToastUtils.show(getString(R.string.wait_channel));
					}
				}
			}
		});
	}

	private void updateConnectionSettings() {
		MeshLibraryManager.getInstance().setNetworkPassPhrase(dbManager.getPlace(1).getNetworkKey());
		// Set REST host and port to point to Selected GATEWAY
		int connectionType = ConnectionUtils.getConnectionType(this);
		if(connectionType == ConnectionUtils.TYPE_WIFI) {
			MeshLibraryManager.getInstance().restartBonjour();
		}
	}

	private void showRefresh() {
		if (fragments.get(fragmentIndex) instanceof MainFragment) {
			((MainFragment) fragments.get(fragmentIndex)).autoRefresh();
		}
	}

	private void closeRefresh() {
		if (fragments.get(fragmentIndex) instanceof MainFragment) {
			((MainFragment) fragments.get(fragmentIndex)).closeRefresh();
		}
	}

	// 切换页面时取消网络请求
	private void cancelRequests() {
		LogUtils.i("取消网络请求");
		MyRequestQueue.cancelPendingRequests();
		closeRefresh();
	}

	// sync data to cloud
	private void configuration() {
		showRefresh();
		UserAPI.getUserValueList(new Response.Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser xmlPullParser) {
				List<UserAttribute> userAttributes = XmlUtils.converUserValueList(xmlPullParser);
				for (int i = 0; i < userAttributes.size(); i++) {
					if (AxalentUtils.ATTRIBUTE_DATABASE.equals(userAttributes.get(i).getName())) {
						String serverTime = paresServerTime(userAttributes.get(i).getValue());
						updateTime = userAttributes.get(i).getUpdTime();
						LogUtils.i("updateTime:" + updateTime);
						Time time = dbManager.getTime(1);
						if (time != null && !serverTime.isEmpty()) {
							String updTime = AxalentUtils.gmtToLoLocal2(serverTime);
							int node = AxalentUtils.compareDate(updTime, AxalentUtils.gmtToLoLocal2(time.getTime()));
							switch (node) {
								case AxalentUtils.GREATER_TIME:
									closeRefresh();
									dbManager.updateLocalDatabase(userAttributes.get(i).getValue());
									databaseUpdate.setVisibility(View.GONE);
									break;
								case AxalentUtils.LESS_TIME:
								case AxalentUtils.SAME_TIME:
									uploadLocalData();
									break;
							}
						} else if (serverTime.isEmpty() && time != null) {
							uploadLocalData();
						} else if (!serverTime.isEmpty() && time == null){
							closeRefresh();
							dbManager.updateLocalDatabase(userAttributes.get(i).getValue());
							databaseUpdate.setVisibility(View.GONE);
						} else {
							closeRefresh();
							ToastUtils.show(getString(R.string.no_synchronization));
						}
					}
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				closeRefresh();
				badSync();
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("isShowMsg", true);
				editor.apply();
				ToastUtils.show(getString(R.string.get_server_data_error));
			}
		});
	}

	private String paresServerTime(String valueList) {
		try {
			JSONObject objJson = new JSONObject(valueList);
			return objJson.get("timestamp").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void syncServerData() {
		if (MyCacheData.getInstance().getCacheUser().getSecurityToken() != null) {
			configuration();
		} else {
			goToAddAccount();
		}
	}

	private void goToAddAccount() {
		Intent intent = new Intent(this, AddAccountActivity.class);
		startActivityForResult(intent, AxalentUtils.ADD_ACCOUNT);
	}

	private void badSync() {
		databaseUpdate.setText(getString(R.string.bad_sync));
		databaseUpdate.setVisibility(View.VISIBLE);
	}

	// upload local data to server
	private void uploadLocalData() {
		// Get configuration data.
		String configuration = dbManager.getDataBaseAsJson();
		Log.i("configuration:" , configuration+"");
		if (configuration == null) {
			configuration = "";
		}
		// upload configuration to server
		UserAPI.setUserAttribute(AxalentUtils.ATTRIBUTE_DATABASE, configuration, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				setDeviceTimeAttr();
				sendMsgToGateway();
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("isShowMsg", false);
				editor.apply();
				databaseUpdate.setVisibility(View.GONE);
				ToastUtils.show(getString(R.string.upload_success_homepage));
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				closeRefresh();
				badSync();
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("isShowMsg", true);
				editor.apply();
				ToastUtils.show(getString(R.string.upload_failure_homepage));
			}
		});
	}

	public void sendMsgToGateway() {
		if (MyCacheData.getInstance().getCacheUser().getSecurityToken() == null) {
			closeRefresh();
			goToAddAccount();
		} else {
			closeRefresh();
			String appId = MyCacheData.getInstance().getCacheUser().getAppId();
			String name = MyCacheData.getInstance().getCacheUser().getUsername();
			String pass = MyCacheData.getInstance().getCacheUser().getPassword();
			StringBuilder sb = new StringBuilder();
			final String attr = sb.append(name).append(" ").append(pass).append(" ").append(appId).toString();
			LogUtils.i(CacheUtils.getDevices().size()+"");
			LogUtils.i(attr);
			for (final Device device : CacheUtils.getDevices()) {
				LogUtils.i("deviceType:" + device.getTypeName());
				if (AxalentUtils.TYPE_GATEWAY.equals(device.getTypeName())) {
					LogUtils.i("gatewayId:" + device.getDevId());
					// 判断网关是否需要拉取数据
					deviceManager.getPresenceInfo(device.getDevId(), new Listener<XmlPullParser>() {
						@Override
						public void onResponse(XmlPullParser xmlPullParser) {
							String state = XmlUtils.convertPresenceInfo(xmlPullParser);
							device.setState(state);
							refreshChildInfo(device, attr);
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError volleyError) {

						}
					});
				}
			}
		}
	}

	private void refreshChildInfo(final Device device, final String attr) {
		deviceManager.setDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_REFRESH_CHILDINFO, AxalentUtils.ON, new Response.Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser xmlPullParser) {
				isSendMsgToGateway(device, attr);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				ToastUtils.show(getString(R.string.child_info_refresh_error));
			}
		});
	}

	private void isSendMsgToGateway(final Device device, final String attr) {
		if (device.getState() != null && device.getState().equals("1")) {
			deviceManager.getDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_GATEWAY_TIME, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser xmlPullParser) {
					DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(xmlPullParser);
					final String gatewayTime = deviceAttribute.getValue();
					deviceManager.getDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_DATE_TIME, new Listener<XmlPullParser>() {
						@Override
						public void onResponse(XmlPullParser xmlPullParser) {
							DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(xmlPullParser);
							String updateTime = deviceAttribute.getValue();
							if (!gatewayTime.isEmpty() && !updateTime.isEmpty()) {
								long unp = Long.parseLong(updateTime);
								long gate = Long.parseLong(gatewayTime);
								LogUtils.i("long_node:" + unp);
								LogUtils.i("long_node:" + gate);
								LogUtils.i("long_node_dx:" + (unp > gate));
								if (unp > gate) {
									setSyncDbAttr(device, attr, updateTime);
								}
							} else {
								setSyncDbAttr(device, attr, updateTime);
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError volleyError) {
							ToastUtils.show(getString(R.string.get_gateway_time_error));
						}
					});
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError volleyError) {
					ToastUtils.show(getString(R.string.get_gateway_time_error));
				}
			});
		} else {
			ToastUtils.show(getString(R.string.gateway_not_online));
		}
	}

	private void setUserTimeStamp(String unpTime) {
		UserAPI.setUserAttribute(AxalentUtils.ATTRIBUTE_TIME_STAMP, unpTime, new Listener<String>() {
			@Override
			public void onResponse(String s) {

			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {

			}
		});
	}

	private void setDeviceTimeAttr() {
		for (final Device device : CacheUtils.getDevices()) {
			LogUtils.i("deviceType:" + device.getTypeName());
			if (AxalentUtils.TYPE_GATEWAY.equals(device.getTypeName())) {
				LogUtils.i("gatewayId:" + device.getDevId());
				UserAPI.getUserValueList(new Response.Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser xmlPullParser) {
						List<UserAttribute> userAttributes = XmlUtils.converUserValueList(xmlPullParser);
						for (int i = 0; i < userAttributes.size(); i++) {
							if (AxalentUtils.ATTRIBUTE_DATABASE.equals(userAttributes.get(i).getName())) {
								String updateTime = userAttributes.get(i).getUpdTime();
								LogUtils.i("updateTime:" + updateTime);
								deviceManager.setDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_DATE_TIME, updateTime, new Listener<XmlPullParser>() {
									@Override
									public void onResponse(XmlPullParser xmlPullParser) {

									}
								}, new ErrorListener() {
									@Override
									public void onErrorResponse(VolleyError volleyError) {
									}
								});
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						ToastUtils.show(getString(R.string.get_server_data_error));
					}
				});
			}
		}
	}

	private void setSyncDbAttr(final Device device, String attr, final String unpTime) {
		showRefresh();
		deviceManager.setDeviceAttribute(device.getDevId(), AxalentUtils.ATTRIBUTE_SYNCDB, attr, new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser xmlPullParser) {
				ToastUtils.show(getString(R.string.sync_success_homepage));
				setUserTimeStamp(unpTime);
				closeRefresh();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				ToastUtils.show(getString(R.string.sync_failure_homepage));
				closeRefresh();
			}
		});
	}

	// checkPermissions android 6.0+
	private void checkPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
						ACCESS_COARSE_LOCATION_RESULT_CODE);
			}
			else if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.BLUETOOTH},
						BLUETOOTH_RESULT_CODE);
			}
			else if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						STORAGE_RESULT_CODE);
			}
			else {
				// Everything ok.
			}
		}
	}

	@TargetApi(18)
	public void verifyBluetooth() {

		try {
			if (!MeshLibraryManager.checkAvailability(getApplicationContext())) {
				showBTStatusDialog(true);
			}
		} catch (RuntimeException e) {

			final Dialog bleDialog = DialogBuilder.createSimpleOkErrorDialog(
					this,
					getString(R.string.dialog_error_ble_not_supported),
					getString(R.string.error_message_bluetooth_le_not_supported)
			);
			bleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
				}
			});
			bleDialog.show();
		}
	}

	private void showBTStatusDialog(boolean btDialog) {
		if(btDialog) {
			bleDialog = DialogBuilder.createSimpleDialog(
					this,
					getString(R.string.dialog_error_ble_not_enabled),
					getString(R.string.error_message_enable_bluetooth),btDialog
			);

			bleDialog.show();
		}else{
			bleDialog = DialogBuilder.createSimpleDialog(
					this,
					getString(R.string.dialog_error_Location_not_enabled),
					getString(R.string.error_message_enable_Location),btDialog
			);

			bleDialog.show();
		}
	}

}
