/**
 * File Name                   : ����ͷ����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.fragment;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.axalent.R;
import com.axalent.view.activity.MonitoringActivity;
import com.axalent.view.activity.ShowDeviceActivity;
import com.axalent.view.activity.EventActivity;
import com.axalent.view.activity.ShowRecordActivity;
import com.axalent.adapter.CameraAdapter;
import com.axalent.adapter.CameraAdapter.OnItemClickListener;
import com.axalent.presenter.controller.impl.UserManagerImpl;
import com.axalent.model.Device;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.SwitchDialog;
import com.axalent.view.widget.HorizontalList;
import com.axalent.view.widget.LoadingDialog;
import com.axalent.view.widget.VisualAngleDialog;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.CameraListener;
import com.tutk.IOTC.IMonitor;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.MonitorClickListener;
import com.tutk.IOTC.Packet;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CameraFragment extends Fragment implements IRegisterIOTCListener, 
MonitorClickListener, CameraListener, OnClickListener, OnItemClickListener {
	
	private View view;
	private Device device;
	private int speed = 0;
	private IMonitor monitor;
	private Camera camera;
	// Camera �Ƿ�������
	private boolean isConnect = false;
	// �Ƿ���˵��
	private boolean isSpeaking = false;
	// �Ƿ�������
	private boolean isListening = false;
	// �Ƿ���¼��
	private boolean isRecording = false;
	// �Ƿ��ڱ���
	private boolean isAlarm = false;
	// ��Ļ����
	private boolean verticalFlip, levelFlip = false;
	// �Ƿ����˼�������Ļ(������ʾ������4���������ҿ��Ƶİ�ť)
	private boolean isClickMonitor = false;
	// �Ƿ��޸ĵ�����ͷ����
	private boolean isTouchCameraList = false;
	// �Ƿ��������� camera
	private boolean isRestart = false;
	// camera ������������Ϊ 5�� ����ͶϿ����ӣ�֤�����糬������
//	private int restartCount = 0;
	// �Ƿ����� Camera ������
//	private boolean isDisposeTouchCameraList = false;
	// ��ǰ��Ļ�ķ���
	private int currentScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
	// �ӽ� Dialog
	private VisualAngleDialog visualAngleDialog;
	// ����
	private SwitchDialog alarmDialog, infraredDialog;
	// �����ļ���
	private String photoFileName = "";
	// ��������
	private int onlineNum = 0;
	// ���Ʒ��� �İ�ť
	private ImageButton topBtn, buttomBtn, leftBtn, rightBtn;
	// camera ������״̬����������
	private TextView connectStateValueTxt, onlinePopulationValueTxt;
	// ��ʪ��
//	private TextView humidityValueTxt, temperatureValueTxt;
	private RelativeLayout cameraMonitorRelative;
	private RelativeLayout cameraStateRelative;
	private HorizontalScrollView cameraBtnLinear;
	private HorizontalList cameraList;
	
	private FrameLayout showDeviceFrame;
	private LinearLayout showDeviceNameLinear;
	private RelativeLayout showDeviceRelative;
	private LinearLayout showDeviceCustomLinear;
	
	/**
	 * ������Ƶ�ײ������� items
	 */
	private String[] cameraActions = null;
	
	private CameraAdapter cameraAdapter;
	
	/**
	 * ��Ƶ view �Ŀ��
	 */
	private int videoWidth = 0;
	private int videoHeight = 0;
	
	private static final int DELAYED_MOVE = 0x11;
	// ������ճɹ�
	private static final int SNAPSHOT_SUCCESS = 0x22;
	//
	private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;
	// ���� cameraList�ı�ʶ
	public static final int SET_CAMERA_LIST_STATE = 0x7834;
	// �ƶ�����ͷö��
	private MoveState cameraMoveState = MoveState.MOVE_STOP;
	// ѡ�����Ƶ����
	private int videoQuality = 0;
	// ѡ��Ļ���ģʽ
	private int environmentMode = 0;
	
	private Handler handler = new Handler() {
		
		public void handleMessage(Message msg) {
			
			Bundle bundle = msg.getData();
			byte[] data = bundle.getByteArray("data");
			
			switch (msg.what) {
			case Camera.CONNECTION_STATE_CONNECTING:
				// ���ӿ�ʼ
				setConnectStateText(R.string.connecting);
				break;
			case Camera.CONNECTION_STATE_CONNECTED:
				// �Ƿ�����
				if (camera.isSessionConnected() && camera.isChannelConnected(0)) {
					setConnectStateText(R.string.device_online);
					isConnect = true;
					if (isRestart) {
						isRestart = false;
						ToastUtils.show(R.string.restart_success);
					}
				}
				break;
			case Camera.CONNECTION_STATE_DISCONNECTED:
				// �Ͽ�����
				setConnectStateText(R.string.device_offline);
				isConnect = false;
				isRestart = false;
				break;
			case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
				// δ֪�豸
				setConnectStateText(R.string.unknown_device);
				isConnect = false;
				isRestart = false;
				break;
			case Camera.CONNECTION_STATE_WRONG_PASSWORD:
				// ��������
				setConnectStateText(R.string.password_failure);
				isConnect = false;
				isRestart = false;
				break;
			case Camera.CONNECTION_STATE_TIMEOUT:
				// ���ӳ�ʱ
				setConnectStateText(R.string.connect_timeout);
				isConnect = false;
				isRestart = false;
				break;
			case Camera.CONNECTION_STATE_CONNECT_FAILED:
				//����ʧ��
				setConnectStateText(R.string.connect_failure);
				isConnect = false;
				isRestart = false;
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_EVENT_REPORT:
				// 
				byte[] t = new byte[8];
				System.arraycopy(data, 0, t, 0, 8);
				AVIOCTRLDEFs.STimeDay incidentTime = new AVIOCTRLDEFs.STimeDay(t);
//				int camChannel = Packet.byteArrayToInt_Little(data, 12);
				int evtType = Packet.byteArrayToInt_Little(data, 16);
				if (evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONPASS && evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARMPASS) {
					showNotification(incidentTime.getTimeInMillis());
				}
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP:
				
				if (data == null) {
					return;
				}
				
				if(data[4] == 1) {
					levelFlip = true;
				} else if(data[4] == 2) {
					verticalFlip = true;
				} else if(data[4] == 3) {
					levelFlip = true;
					verticalFlip = true;
				}
			
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP:

				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP:

				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETGUARD_RESP:
				
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_NAME_RESP:
				
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_Temperature_Humidity_RESP:
				// ���ӳɹ�
//				byte[] ct=new byte[4];
//				System.arraycopy(data,0,ct, 0, 4);// -30.0 ~ 100.0��
//				byte[] ch=new byte[4];
//				System.arraycopy(data,4,ch, 0, 4);// 0% ~100%
//				byte[] lt=new byte[4];
//				System.arraycopy(data,8,lt, 0, 4);// -30.0 ~ 100.0��
//				byte[] ht=new byte[4];
//				System.arraycopy(data,12,ht, 0, 4);// -30.0 ~ 100.0��
//				byte[] lh=new byte[4];
//				System.arraycopy(data,16,lh, 0, 4);// 0% ~100%
//				byte[] hh=new byte[4];
//				System.arraycopy(data,20,hh, 0, 4);// 0% ~100%
//				byte[] ton=new byte[4];
//				System.arraycopy(data,24,ton, 0, 4);/*�¶ȱ������� 1:on 0:off*/
//				byte[] hon=new byte[4];
//				System.arraycopy(data,28,hon, 0, 4);/* ʪ�ȱ������� 1:on 0:off*/
				
//				int tempon=Packet.byteArrayToInt_Little(ton);
//				int humion=Packet.byteArrayToInt_Little(hon);
				// ��ǰʪ��
//				int currentHumidity=(int) byte2float(ch, 0);
//				int lowHumidity=(int) byte2float(lh, 0);
//				int highHumidity=(int) byte2float(hh, 0);
				// ��ǰ�¶�
//				float currentTemperature=byte2float(ct,0);
//				float lowTemp=byte2float(lt,0);
//				float highTemp=byte2float(ht,0);
				
//				if (humidityValueTxt != null) {
//					humidityValueTxt.setText(String.valueOf(currentHumidity));
//				}
//				
//				if (temperatureValueTxt !=
//						null) {
//					temperatureValueTxt.setText(String.valueOf(currentTemperature));
//				}
				
				break;
			case STS_CHANGE_CHANNEL_STREAMINFO:
				// ������������
				if (onlinePopulationValueTxt != null) {
					onlinePopulationValueTxt.setText(String.valueOf(onlineNum));
				}
				break;
			case DELAYED_MOVE:
				cameraMoveState = MoveState.MOVE_STOP;
				break;
			case SNAPSHOT_SUCCESS:
				ToastUtils.show(R.string.snapshot_success);
				break;
			case SET_CAMERA_LIST_STATE:
				if (currentScreenOrientation != Configuration.ORIENTATION_PORTRAIT && !isTouchCameraList) {
					cameraList.setVisibility(View.GONE);
				}
				break;
			}
		};
	};
	
	/**
	 * �ƶ�����ͷö��
	 */
	private enum MoveState {
		MOVE_NONE,
		MOVE_START,
		MOVE_STOP
	}

	@Override
	public void onAttach(Activity activity) {
		// ��ʼ��
		initCameraView(activity);
		super.onAttach(activity);
	}
	
	@Override
	public void onResume() {
		startCamera();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		pauseCamera();
		super.onPause();
	}
	
	private void startCamera() {
		if (camera != null) {
			camera.startShow(0, true, true);
		}
		startMonitor();
	}
	
//	private void stopCamera() {
//		if (camera != null) {
//			camera.disconnect();
//		}
//		pauseMonitor();
//	}
	
	private void startMonitor() {
		if (monitor != null){
			monitor.enableDither(camera.mEnableDither);
			monitor.attachCamera(camera, 0);
		}
	}
	
	private void pauseMonitor() {
		if (monitor != null){
			monitor.deattachCamera();
		}
	}
	
	
	private void pauseCamera() {
		if (camera != null) {
			camera.stopSpeaking(0);
			camera.stopListening(0);
			camera.stopShow(0);
		}
		pauseMonitor();
	}
	
	private void restartCamera() {
		if (TextUtils.isEmpty(device.getPassword()) || camera == null) {
			ToastUtils.show(R.string.not_configuration_password);
			return;
		}
		if (!isRestart) {
			isConnect = false;
			isRestart = true;
			pauseCamera();
			camera.disconnect();
			camera.connect(device.getUid());
			camera.start(Camera.DEFAULT_AV_CHANNEL, "admin", device.getPassword());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(0, (byte) 1));
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETGUARD_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetGuardReq.parseContent(0));
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_NAME_REQ, AVIOCTRLDEFs.SMsgAVIoctrlCamGetNameReq.parseContent(0));
			startCamera();
		} else {
			ToastUtils.show(R.string.be_being_restart);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_camera, null);
			initView();
			initData();
		}
		return view;
	}
	
	private void initView() {
		connectStateValueTxt = (TextView) view.findViewById(R.id.fragCameraConnectStateValueTxt);
		onlinePopulationValueTxt = (TextView) view.findViewById(R.id.fragCameraOnlinePopulationValueTxt);
		
//		humidityValueTxt = (TextView) view.findViewById(R.id.fragCameraHumidityValueTxt);
//		temperatureValueTxt = (TextView) view.findViewById(R.id.fragCameraTemperatureValueTxt);
		
		Button passwordBtn = (Button) view.findViewById(R.id.fragCameraPasswordBtn);
		Button connectBtn = (Button) view.findViewById(R.id.fragCameraConnectBtn);
		Button incidentBtn = (Button) view.findViewById(R.id.fragCameraIncidentBtn);
		Button monitoringBtn = (Button) view.findViewById(R.id.fragCameraMonitoringBtn);
		
		connectBtn.setOnClickListener(this);
		passwordBtn.setOnClickListener(this);
		incidentBtn.setOnClickListener(this);
		monitoringBtn.setOnClickListener(this);
		
		topBtn = (ImageButton) view.findViewById(R.id.fragCameraTopBtn);
		buttomBtn = (ImageButton) view.findViewById(R.id.fragCameraBottomBtn);
		leftBtn = (ImageButton) view.findViewById(R.id.fragCameraLeftBtn);
		rightBtn = (ImageButton) view.findViewById(R.id.fragCameraRightBtn);
//		ImageButton fullScreenBtn = (ImageButton) view.findViewById(R.id.fragCameraFullScreenBtn);
		
		topBtn.setOnClickListener(controlListene);
		buttomBtn.setOnClickListener(controlListene);
		leftBtn.setOnClickListener(controlListene);
		rightBtn.setOnClickListener(controlListene);
//		fullScreenBtn.setOnClickListener(this);
		
		cameraList = (HorizontalList) view.findViewById(R.id.fragCameraList);
		cameraActions = getResources().getStringArray(R.array.array_frag_camera);
		cameraAdapter = new CameraAdapter(getActivity(), cameraActions);
		cameraAdapter.setOnItemClickListener(this);
		cameraList.setAdapter(cameraAdapter);

		cameraMonitorRelative = (RelativeLayout) view.findViewById(R.id.fragCameraMonitorRelative);
		cameraStateRelative = (RelativeLayout) view.findViewById(R.id.fragCameraStateRelative);
		cameraBtnLinear = (HorizontalScrollView) view.findViewById(R.id.fragCameraBtnLinear);
		
		cameraList.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					isTouchCameraList = true;
					break;
				case MotionEvent.ACTION_UP:
					isTouchCameraList = false;
					setCameraListState();
					break;
				}
				return false;
			}
		});
	}
	
	
	private OnClickListener controlListene = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			if (!cameraIsNormalWork()) {
				ToastUtils.show(R.string.device_offline);
				return;
			}
			
			switch (v.getId()) {
			case R.id.fragCameraTopBtn:
				moveCamera(AVIOCTRLDEFs.AVIOCTRL_PTZ_UP);
				break;
			case R.id.fragCameraBottomBtn:
				moveCamera(AVIOCTRLDEFs.AVIOCTRL_PTZ_DOWN);
				break;
			case R.id.fragCameraLeftBtn:
				moveCamera(AVIOCTRLDEFs.AVIOCTRL_PTZ_LEFT);
				break;
			case R.id.fragCameraRightBtn:
				moveCamera(AVIOCTRLDEFs.AVIOCTRL_PTZ_RIGHT);
				break;
			}
		}
	};
	
	
	private void initData() {
		device = ((ShowDeviceActivity) getActivity()).getCurrentDevice();
		if (TextUtils.isEmpty(device.getPassword())) {
			new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.app_logo_1)
			.setTitle(R.string.warm_hint)
			.setMessage(R.string.camera_not_password_hint)
			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			})
			.create()
			.show();
			setConnectStateText(R.string.ununited);
		} else {
//			passwordEdit.setText(device.getPassword());
			initCamera();
		}
	}
	
	private void initCamera() {
//		UUID mUUID = UUID.randomUUID();
		if (camera == null) {
			camera = new Camera();
			camera.registerIOTCListener(this);
			camera.SetCameraListener(this);
			camera.connect(device.getUid());
			camera.start(Camera.DEFAULT_AV_CHANNEL, "admin", device.getPassword());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETISOPTZOOM_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetIsOptZoomReq.parseContent(0));
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetVideoModeReq.parseContent(0));
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(0, (byte) 1));
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_Temperature_Humidity_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetTemperatureHumidityReq.parseContent(Camera.DEFAULT_AV_CHANNEL));
			camera.startShow(0, true, true);
			initMonitor();
		}
	}
	
	private void initMonitor() {
		if (monitor == null) {
			monitor = (IMonitor) view.findViewById(R.id.fragCameraMonitor);
			monitor.setMaxZoom(3.0f);
			monitor.enableDither(camera.mEnableDither);
			monitor.attachCamera(camera, 0);
			monitor.SetOnMonitorClickListener(this);
			monitor.cleanFrameQueue();
		}
	}
	
	private void setUserAttribute(final String password) {
		final LoadingDialog loadingDialog = ((ShowDeviceActivity) getActivity()).getLoadingDialog();
		loadingDialog.show(R.string.is_the_configuration);
		final String newCamerainfo = getNewCameraInfo(password);
		if (!TextUtils.isEmpty(newCamerainfo)) {
			new UserManagerImpl().setUserAttribute(AxalentUtils.ATTRIBUTE_CAMERA_INFO, newCamerainfo, new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					String reqCode = XmlUtils.converRequestCode(response);
					if (AxalentUtils.REQUEST_OK.equals(reqCode)) {
						device.setPassword(password);
						if (isConnect) {
							restartCamera();
						} else {
							if (camera == null) {
								initCamera();
							} else {
								restartCamera();
							}
						}
						
						loadingDialog.close();
						ToastUtils.show(R.string.configuration_success_be_being_connect);
						
						CacheUtils.setUserAttribute(AxalentUtils.ATTRIBUTE_CAMERA_INFO, newCamerainfo);
						Device bufferDevice = CacheUtils.getDeviceByName(device.getDevName());
						if (bufferDevice != null) {
							bufferDevice.setPassword(device.getPassword());
							Intent intent = new Intent("com.axalent.HomeActivity");
							intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.UPDATE_DEVICE);
							intent.putExtra(AxalentUtils.KEY_DEVICE, device);
							getActivity().sendBroadcast(intent);
						}
						
					} else {
						loadingDialog.close();
						ToastUtils.show(R.string.configuration_failure);
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					loadingDialog.close();
					ToastUtils.show(R.string.configuration_failure);
				}
			});
		} else {
			loadingDialog.close();
			ToastUtils.show(R.string.configuration_failure);
		}
	}
	
	
	private void initCameraView(Activity activity) {
		showDeviceFrame = (FrameLayout) activity.findViewById(R.id.atyShowDeviceFrame);
		showDeviceRelative = (RelativeLayout) activity.findViewById(R.id.atyShowDeviceRelative);
		showDeviceNameLinear = (LinearLayout) activity.findViewById(R.id.atyShowDeviceNameLinear);
		showDeviceCustomLinear = (LinearLayout) activity.findViewById(R.id.atyShowDeviceCustomLinear);
	}
	
	
	// IRegisterIOTCListener -------------------------------
	@Override
	public void receiveChannelInfo(Camera arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
//		LogUtils.i("receiveChannelInfo:"+arg2);
		handler.sendEmptyMessage(arg2);
	}

	@Override
	public void receiveFrameData(Camera arg0, int arg1, Bitmap arg2) {
//		LogUtils.i("receiveFrameData:"+arg1);
		this.videoWidth = arg2.getWidth();
		this.videoHeight = arg2.getHeight();
	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera arg0, int arg1, byte[] arg2, int arg3, int arg4, byte[] arg5,
			boolean arg6, int arg7) {
//		LogUtils.i("receiveFrameDataForMediaCodec");
	}

	@Override
	public void receiveFrameInfo(Camera camera, int sessionChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {
//		LogUtils.i("receiveFrameInfo");
		this.onlineNum = onlineNm;
		handler.sendEmptyMessage(STS_CHANGE_CHANNEL_STREAMINFO);
	}

	@Override
	public void receiveIOCtrlData(Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub
//		LogUtils.i(" receiveIOCtrlData:"
//				+" sessionChannel:"+sessionChannel
//				+" avIOCtrlMsgType:"+avIOCtrlMsgType);
		
		Bundle bundle = new Bundle();
		bundle.putByteArray("data", data);
		Message msg = handler.obtainMessage();
		msg.what = avIOCtrlMsgType;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		// TODO Auto-generated method stub
//		LogUtils.i("receiveSessionInfo"+resultCode);
		handler.sendEmptyMessage(resultCode);
	}
	// IRegisterIOTCListener -------------------------------
	
	@Override
	public void onDestroy() {
		if (camera != null) {
			camera.disconnect();
			camera.unregisterIOTCListener(this);
		}
		super.onDestroy();
	}

	@Override
	public void OnSnapshotComplete() {
//		LogUtils.i("OnSnapshotComplete");
		MediaScannerConnection.scanFile(getActivity(), new String[] {photoFileName}, new String[] {"image/*"}, new MediaScannerConnection.OnScanCompletedListener() {
			@Override
			public void onScanCompleted(String path, Uri uri) {
				handler.sendEmptyMessage(SNAPSHOT_SUCCESS);
			}
		});
		
	}
	
	private void setConnectStateText(int rid) {
		if (connectStateValueTxt != null) {
			connectStateValueTxt.setText(rid);
		}
	}
	
	/**
	 * ����ͷ��Ļ��������¼�, ������ʾ������4���������ҿ��Ƶİ�ť
	 */
	@Override
	public void OnClick() {
		int visibility = isClickMonitor ? View.VISIBLE : View.GONE;
		topBtn.setVisibility(visibility);
		buttomBtn.setVisibility(visibility);
		leftBtn.setVisibility(visibility);
		rightBtn.setVisibility(visibility);
		
		// ����Ǻ��� + 
		if (Configuration.ORIENTATION_LANDSCAPE == currentScreenOrientation) {
			cameraList.setVisibility(visibility);
			if (isClickMonitor) {
				setCameraListState();
			}
		}
		
		isClickMonitor = !isClickMonitor;
	}
	
	/**
	 * ��� camera �Ƿ�����
	 * @return
	 */
	private boolean cameraIsNormalWork() {
		if (camera != null && isConnect) {
			return true;
		}
		return false;
	}

	@Override
	public void onItemClick(View v, int postion) {
		
		if (postion != 3 && !cameraIsNormalWork()) {
			ToastUtils.show(R.string.device_offline);
			return;
		}
		
		switch (postion) {
		case 0:
			// ʵʱ�Խ�
			speaking(postion);
			break;
		case 1:
			// ¼����Ƶ
			recording(postion);
			break;
		case 2:
			// ����
			snapshoot();
			break;
		case 3:
			// �鿴���պ���Ƶ
			skipToShowRecord();
			break;
		case 4:
			 // ����
			
			if (alarmDialog == null) {
				alarmDialog = new SwitchDialog(getActivity(), R.string.alarm_switch);
				alarmDialog.setOnCheckedChangeListener(new SwitchDialog.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETALARMRING_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetAlarmRingReq.parseContent(0, isChecked ? 1 : 0));
						isAlarm = !isAlarm;
						ToastUtils.show(isChecked ? R.string.open_alarm_switch : R.string.close_alarm_switch);
					}
				});
			}
			alarmDialog.show();
			break;
		case 5:
			// �ӽ�
			
			if (visualAngleDialog == null) {
				visualAngleDialog = new VisualAngleDialog(getActivity());
				visualAngleDialog.setOnClickListener(new VisualAngleDialog.OnClickListener() {
					@Override
					public void onClick(View v) {
						// �����ӽ�
						camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETPRESET_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetPresetReq.parseContent(0, getCurrentVisualAng(v.getId())));
						visualAngleDialog.dismiss();
					}
				});
				visualAngleDialog.setOnLongClickListener(new VisualAngleDialog.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						// �����ӽ�
						camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPRESET_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetPresetReq.parseContent(0, getCurrentVisualAng(v.getId())));
						ToastUtils.show(R.string.save_visual_angle_success);
						return true;
					}
				});
			}
			visualAngleDialog.show();
			break;
		case 6:
			// �����
			if (infraredDialog == null) {
				infraredDialog = new SwitchDialog(getActivity(), R.string.infrared_switch);
				infraredDialog.setOnCheckedChangeListener(new SwitchDialog.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETIRLIGHT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetIRLightReq.parseContent(0, isChecked ? 1 : 0));
						ToastUtils.show(isChecked ? R.string.open_infrared_switch : R.string.close_infrared_switch);
					}
				});
			}
			infraredDialog.show();
			break;
		case 7:
			// �����л�
			changeScreenOrientation(getUpOrDown());
			break;
		case 8:
			// �����л�
			changeScreenOrientation(getLeftOrRight());
			break;
		case 9:
			// ��Ƶ����
			new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.app_logo_1)
			.setTitle(R.string.video_quality)
			.setSingleChoiceItems(R.array.frag_video_qualitys, videoQuality, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// ����û�ѡ�����Ƶ����
					videoQuality = which;
				}
			})
			
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// ��ȷ�����л���Ƶ����
					camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(0, getVideoQuality()));
					// ��ͣ
					pauseCamera();
					// ����һ�� camera
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							startCamera();
						}
					}, 1000);
					
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			})
			.create()
			.show();
			break;
		case 10:
			// ����ģʽ
			new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.app_logo_1)
			.setTitle(R.string.environment_mode)
			.setSingleChoiceItems(R.array.frag_environment_modes, environmentMode, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// ����û�ѡ��Ļ���ģʽ
					environmentMode = which;
				}
			})
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// ��ȷ�����л�����ģʽ
					camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq.parseContent(0, getEnvironmentMode()));
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			})
			.create()
			.show();
			
			break;
		}
	}
	
	/**
	 * ����
	 */
	private void snapshoot() {
		
		// �ж��Ƿ��� SD ��
		if (!AxalentUtils.isHaveSDCard()) {
			ToastUtils.show(R.string.not_recording_because_sdcard_not_find);
			return;
		}

		File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/axalent/");
		if (!rootFolder.exists()) {
			rootFolder.mkdir();
		}

		String devName = ((ShowDeviceActivity) getActivity()).getCurrentDevice().getDevName();
		// �ļ��������豸��
		File targetFolder = new File(rootFolder.getAbsolutePath() + "/photo");
		if (!targetFolder.exists()) {
			targetFolder.mkdir();
		}
		
		photoFileName = "/sdcard/axalent/photo/" + devName + "_" + getFileNameWithTime(".jpg");
		camera.setSnapshot(getActivity(), photoFileName);
	}
	
	/**
	 * ֹͣ¼����Ƶ
	 */
	private void stopRecording() {
		camera.stopRecording();
		camera.stopListening(0);
		camera.stopSpeaking(0);
	}
	
	/**
	 * // ¼��
	 * @param postion
	 */
	private void recording(final int postion) {
		
		if (isRecording) {
			stopRecording();
			cameraActions[postion] = (String) getText(R.string.record);
			ToastUtils.show(R.string.stop_recording);
		} else {
			
			// ����ڶԽ���ֹͣ
			if (isSpeaking) {
				speaking(0);
			}
			
			// �ж��Ƿ��� SD ��
			if (!AxalentUtils.isHaveSDCard()) {
				ToastUtils.show(R.string.not_recording_because_sdcard_not_find);
				return;
			}
			// SD �� ��������

			if (AxalentUtils.getAvailaleSize() <= 300) {
				ToastUtils.show(R.string.memory_capacity_insufficient);
				return;
			}
			// �ֻ�֧����Ƶ��ʽ
			// if (AVFrame.MEDIA_CODEC_VIDEO_H264 ==
			// camera.codec_ID_for_recording) {
			// ToastUtils.show(R.string.you_phone_not_support_play_current_format);
			// return;
			// }

			// ����������ļ���
			File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/axalent/");
			if (!rootFolder.exists()) {
				rootFolder.mkdir();
			}

			String devName = ((ShowDeviceActivity) getActivity()).getCurrentDevice().getDevName();
			// �ļ��������豸��
			File targetFolder = new File(rootFolder.getAbsolutePath() + "/video");
			if (!targetFolder.exists()) {
				targetFolder.mkdir();
			}
			String videoFileName = "/sdcard/axalent/video/" + devName + "_" + getFileNameWithTime(".mp4");
			// ¼�������·��
			camera.startListening(0, true);
			camera.stopSpeaking(0);
			camera.startRecording(videoFileName);
			// ��ʾ
			cameraActions[postion] = (String) getText(R.string.recording);
			ToastUtils.show(R.string.is_the_recording);
			// 5���Ӻ������¼���Զ�ֹͣ
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (isRecording) {
						stopRecording();
						cameraActions[postion] = (String) getText(R.string.record);
						ToastUtils.show(R.string.stop_recording);
						isRecording = false;
						cameraAdapter.notifyDataSetChanged();
					}
				}
			}, 500000);
		}
		isRecording = !isRecording;
		cameraAdapter.notifyDataSetChanged();
	}
	
	/**
	 * ˵��
	 */
	private void speaking(int postion) {
		
		if (isSpeaking) {
			camera.stopSpeaking(0);
			camera.stopListening(0);
			cameraActions[postion] = (String) getText(R.string.call);
			ToastUtils.show(R.string.stop_call);
		} else {
			// �����¼����Ƶ��ֹͣ
			if (isRecording) {
				recording(1);
			}
			camera.startSpeaking(0);
			camera.startListening(0, isRecording);
			cameraActions[postion] = (String) getText(R.string.calling);
			ToastUtils.show(R.string.correct_microphone_can_call);
		}
		isSpeaking = !isSpeaking;
		cameraAdapter.notifyDataSetChanged();
	}
	
	private void skipToShowRecord() {
		// ��������ͼ��
		if (cameraIsNormalWork()) {
			// ����ڶԽ���ֹͣ
			if (isSpeaking) {
				speaking(0);
			}
			
			// �����¼����Ƶ��ֹͣ
			if (isRecording) {
				recording(1);
			}
		}
		
		startActivity(new Intent(getActivity(), ShowRecordActivity.class));
	}
	
	/**
	 * ��û���ģʽ
	 * @return
	 */
	private byte getEnvironmentMode() {
		switch (environmentMode) {
		case 0:
			return 0;
		case 1:
			return 1;
		default:
			return 0;
		}
	}
	
	/**
	 * �����Ƶ����
	 * @return
	 */
	private byte getVideoQuality() {
		switch (videoQuality) {
		case 0:
			// ��
			return 1;
		case 1:
			// ��
			return 3;
		case 2:
			// ��
			return 4;
		default:
			return 4;
		}
	}
	
	/**
	 * �����Ļ���ϻ��ǳ���
	 * @return
	 */
	private byte getUpOrDown() {
		if (!verticalFlip) {
			if (!levelFlip) {
				levelFlip = true;
				return (byte) 1;
			} else {
				levelFlip = false;
				return (byte) 0;
			}
		} else {
			if (!levelFlip) {
				levelFlip = true;
				return (byte) 3;
			} else {
				levelFlip = false;
				return (byte) 2;
			}

		}
	}
	
	/**
	 * �����Ļ�����ǳ���
	 * @return
	 */
	private byte getLeftOrRight() {
		if (!levelFlip) {
			if (!verticalFlip) {
				verticalFlip = true;
				return (byte) 2;
			} else {
				verticalFlip = false;
				return (byte) 0;
			}
		} else {
			if (!verticalFlip) {
				verticalFlip = true;
				return (byte) 3;
			} else {
				verticalFlip = false;
				return (byte) 1;
			}

		}
	}
	
	/**
	 * �ı���Ļ����
	 * @param b
	 */
	private void changeScreenOrientation(byte b) {
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq.parseContent(0, b));
	}
	
	/**
	 * ��õ�ǰ�ӽ� 
	 * @param id
	 * @return
	 */
	private int getCurrentVisualAng(int id) {
		switch (id) {
		case R.id.dialogVisualAngleOne:
			return 0;
		case R.id.dialogVisualAngleTwo:
			return 1;
		case R.id.dialogVisualAngleThree:
			return 2;
		case R.id.dialogVisualAngleFour:
			return 3;
		default:
			return 0;
		}
	}

	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.fragCameraConnectBtn:
			// ��������
			restartCamera();
			break;
		case R.id.fragCameraPasswordBtn:
			// �޸�����
			setCameraPassword();
			break;
		case R.id.fragCameraIncidentBtn:// �鿴�¼�
		case R.id.fragCameraMonitoringBtn:// ȫ�̼�� 
			
			
			if (TextUtils.isEmpty(device.getPassword())) {
				ToastUtils.show(R.string.not_configuration_password);
				return;
			}
			
			if (camera != null) {
				camera.disconnect();
			}
			
			Class<?> cls = id == R.id.fragCameraIncidentBtn ? EventActivity.class : MonitoringActivity.class;
			
			Intent intent = new Intent(getActivity(), cls);
			intent.putExtra(AxalentUtils.KEY_DEVICE, device);
			startActivityForResult(intent, 0);
			
			break;
		}
	
//		case R.id.fragCameraFullScreenBtn:
//			if (monitor != null) {
//				
//			}
//			break;
	}
	
	
	
	private void setCameraPassword() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.configuration_password);
		View view = View.inflate(getActivity(), R.layout.dialog_input, null);
		builder.setView(view);
		final EditText codeEdit = (EditText) view.findViewById(R.id.dialogInputEdit);
		codeEdit.setHint(R.string.please_enter_the_password);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String password = codeEdit.getText().toString().trim();
				if (TextUtils.isEmpty(password)) {
					ToastUtils.show(R.string.password_not_null);
				} else {
					// ��������
					setUserAttribute(password);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
		
	}
	
	/**
	 * �ƶ�����ͷ
	 * @param direction
	 */
	private void moveCamera(int direction) {
		if (MoveState.MOVE_STOP == cameraMoveState) {
			cameraMoveState = MoveState.MOVE_START;
			camera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND, AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent((byte)direction,(byte)speed,(byte)0,(byte)0,(byte)0,(byte)0));
			handler.sendEmptyMessageDelayed(DELAYED_MOVE, 500);
		}
	}
	
	
	private static String getFileNameWithTime(String formatName) {

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH) + 1;
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mSec = c.get(Calendar.SECOND);

		StringBuffer sb = new StringBuffer();
		sb.append(mYear);
		if (mMonth < 10) {
			sb.append('0');
		}
		sb.append(mMonth);
		if (mDay < 10) {
			sb.append('0');
		}
		sb.append(mDay);
		sb.append('_');
		if (mHour < 10) {
			sb.append('0');
		}
		sb.append(mHour);
		if (mMinute < 10) {
			sb.append('0');
		}
		sb.append(mMinute);
		if (mSec < 10) {
			sb.append('0');
		}
		sb.append(mSec);
		sb.append(formatName);
		return sb.toString();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		currentScreenOrientation = newConfig.orientation;
		switch (currentScreenOrientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			// ����
			controlActivityTopChunk("hide");
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			// ����
			controlActivityTopChunk("show");
			break;
		}
		super.onConfigurationChanged(newConfig);
	}
	
	private int getDimension(int id) {
		return (int) getActivity().getResources().getDimension(id);
	}


	public void controlActivityTopChunk(String status) {
		
		if ("show".equals(status)) {
			showWindow();
			setCameraFullScreenTopLayoutStatus(View.VISIBLE);
			showDeviceFrame.setLayoutParams(getNewLayoutParams(showDeviceFrame, LayoutParams.WRAP_CONTENT));
			cameraMonitorRelative.setLayoutParams(getNewLayoutParams(cameraMonitorRelative, getDimension(R.dimen.frag_camera_height)));
			cameraStateRelative.setVisibility(View.VISIBLE);
			cameraBtnLinear.setVisibility(View.VISIBLE);
			cameraMonitorRelative.setPadding(
					getDimension(R.dimen.frag_camera_padding_left), 
					getDimension(R.dimen.frag_camera_padding_top), 
					getDimension(R.dimen.frag_camera_padding_right),
					0);
			
			RelativeLayout.LayoutParams lp = getRelativeLayoutLayoutParams();
			lp.leftMargin = getDimension(R.dimen.frag_camera_list_margin_left);
			lp.rightMargin = getDimension(R.dimen.frag_camera_list_margin_right);
			setCameraListOrientation(R.id.fragCameraMonitorRelative, RelativeLayout.BELOW, lp);
			cameraList.setVisibility(View.VISIBLE);
			
		} else if ("hide".equals(status)) {
			hideWindow();
			setCameraFullScreenTopLayoutStatus(View.GONE);
			cameraBtnLinear.setVisibility(View.GONE);
			int height = getWindowDisplayHeight();
			showDeviceFrame.setLayoutParams(getNewLayoutParams(showDeviceFrame, height));
			cameraMonitorRelative.setLayoutParams(getNewLayoutParams(cameraMonitorRelative, height));
			cameraStateRelative.setVisibility(View.GONE);
			cameraMonitorRelative.setPadding(0, 0, 0, 0);
			setCameraListOrientation(R.id.fragCameraStateRelative, RelativeLayout.ALIGN_PARENT_BOTTOM, getRelativeLayoutLayoutParams());
			
//			isDisposeTouchCameraList = false;
			setCameraListState();
		}
	}
	
	private void setCameraListState() {
		// �Ƿ����ڴ��?�� camram ��������û�д���ͷ��ʹ�����Ϣ
		handler.removeMessages(SET_CAMERA_LIST_STATE);
		handler.sendEmptyMessageDelayed(SET_CAMERA_LIST_STATE, 3000);
	}
	
	private void setCameraListOrientation(int id, int verb, RelativeLayout.LayoutParams lp) {
		RelativeLayout contentLayout = (RelativeLayout) view;
		contentLayout.removeView(cameraList);
		lp.addRule(verb, id);
		contentLayout.addView(cameraList, lp);
	}
	
	private RelativeLayout.LayoutParams getRelativeLayoutLayoutParams() {
		return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getDimension(R.dimen.listview_height));
	}
	
	private LayoutParams getNewLayoutParams(ViewGroup group, int paramsHeight) {
		LayoutParams params = group.getLayoutParams();
		params.height = paramsHeight;
		return params;
	}
	
	private void setCameraFullScreenTopLayoutStatus(int visibility) {
		showDeviceRelative.setVisibility(visibility);
		showDeviceNameLinear.setVisibility(visibility);
		showDeviceCustomLinear.setVisibility(visibility);
	}
	
	private void showWindow() {
		getActivity().getActionBar().show();
		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	}
	
	private void hideWindow() {
		getActivity().getActionBar().hide();
		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	// ���ڵĿ��
	private int getWindowDisplayHeight() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	
	/**
	 * ������ʪ�ȵķ���
	 */
//	private float byte2float(byte[] b, int index) {    
//	    int l;                                             
//	    l = b[index + 0];                                  
//	    l &= 0xff;                                         
//	    l |= ((long) b[index + 1] << 8);                   
//	    l &= 0xffff;                                       
//	    l |= ((long) b[index + 2] << 16);                  
//	    l &= 0xffffff;                                     
//	    l |= ((long) b[index + 3] << 24);                  
//	    return Float.intBitsToFloat(l);                    
//	} 
	
	/**
	 * ��ʾ֪ͨ
	 */
	@SuppressWarnings("deprecation")
	private void showNotification(long incidentTime) {
		//����API Level 11�汾��Ҳ����Android 2.3.3���µ�ϵͳ�У�setLatestEventInfo()������Ψһ��ʵ�ַ���
		NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//		Intent intent = new Intent(getActivity(), IncidentActivity.class);
//		intent.putExtra(AxalentUtils.KEY_DEVICE, device);
//		PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		Notification notification = new Notification();
//		notification.icon = R.drawable.app_logo_1;
//		notification.when = getCurrentIncidentTime(incidentTime);
//		notification.tickerText = getText(R.string.receive_alarm_Incident);
//		// ������֪ͨ���û����ʱ��֪ͨ�������  
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notification.flags |= Notification.FLAG_NO_CLEAR;
//		notification.setLatestEventInfo(getActivity(), getText(R.string.alarm), getText(R.string.receive_alarm_Incident), pendingIntent);
//		notificationManager.notify(1, notification);
		
		//����API Level 16 (Android 4.1.2)�汾
		Intent intent = new Intent(getActivity(), EventActivity.class);
		intent.putExtra(AxalentUtils.KEY_DEVICE, device);
		PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification.Builder builder = new Notification.Builder(getActivity())  
	            .setAutoCancel(true)  
	            .setContentTitle(getText(R.string.receive_alarm_Incident))  
	            .setContentText(getText(R.string.receive_alarm_Incident))  
	            .setContentIntent(pendingIntent)  
	            .setSmallIcon(R.drawable.app_logo_1)  
	            .setWhen(getCurrentIncidentTime(incidentTime))  
	            .setOngoing(true)
				.setAutoCancel(true);  
		
		Notification notification = builder.getNotification();
		notificationManager.notify(1, notification);
	}
	
	private long getCurrentIncidentTime(long incidentTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(incidentTime);
		calendar.add(Calendar.MONTH, 0);
		return calendar.getTimeInMillis();
	}
	
	private String getNewCameraInfo(String password) {
		String cameraInfo = CacheUtils.getUserAttribute(AxalentUtils.ATTRIBUTE_CAMERA_INFO);
		if (!TextUtils.isEmpty(cameraInfo)) {
			try {
				JSONArray array = new JSONArray(cameraInfo);
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					String uid = (String) object.opt("uid");
					if (device.getUid().equals(uid)) {
						object.put("password", password);
						return array.toString();
					}
				}
			} catch (JSONException e) {
				return "";
			}
		}
		return "";
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ��������
		restartCamera();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
//	private void setScreenOrientation(int orientation) {
//		getActivity().setRequestedOrientation(orientation);
//	}
	
}
