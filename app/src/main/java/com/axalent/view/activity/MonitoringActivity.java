/**
 * File Name                   : ����ͷ����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.model.Device;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.CameraListener;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Monitor;
import com.tutk.IOTC.MonitorClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MonitoringActivity extends BaseActivity {
	
	private Device currentDevice;
	private String[] cameraNames;
	private List<Device> cameras;
	private DrawerLayout contentDrawer;
	private int clickMonitorId = R.id.atyMonitoringLeftUpRelative;
	private Camera leftUpCamera, rightUpCamera, leftDownCamera, rightDownCamera;
	private Monitor leftUpMonitor, rightUpMonitor, leftDownMonitor, rightDownMonitor;
	private RelativeLayout leftUpRelative, rightUpRelative, leftDownRelative, rightDownRelative;
	// ���������Ѿ�������� Camera 
	private Map<String, Integer> alreadyAddedCamera = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitoring);
		
		currentDevice = getIntent().getParcelableExtra(AxalentUtils.KEY_DEVICE);
		
		contentDrawer = (DrawerLayout) findViewById(R.id.atyMonitoringContentDrawer);
//		contentDrawer.setDrawerShadow(R.drawable.app_logo_1, GravityCompat.START);
		
		leftUpRelative = (RelativeLayout) findViewById(R.id.atyMonitoringLeftUpRelative);
		rightUpRelative = (RelativeLayout) findViewById(R.id.atyMonitoringRightUpRelative);
		leftDownRelative = (RelativeLayout) findViewById(R.id.atyMonitoringLeftDownRelative);
		rightDownRelative = (RelativeLayout) findViewById(R.id.atyMonitoringRightDownRelative);
		
		leftUpRelative.setOnClickListener(onClickListener);
		rightUpRelative.setOnClickListener(onClickListener);
		leftDownRelative.setOnClickListener(onClickListener);
		rightDownRelative.setOnClickListener(onClickListener);
		
		
		if (currentDevice != null) {
			// ��ʶ�Ѿ�����ӹ���
			alreadyAddedCamera.put(currentDevice.getDevName(), leftUpRelative.getId());
			
			leftUpCamera = new Camera();
			initCamera(leftUpCamera, currentDevice);
			
			leftUpMonitor = new Monitor(this, null);
			initMonitor(leftUpCamera, leftUpMonitor, 0);
			
			leftUpRelative.addView(leftUpMonitor);
		}
		
		
		getAllCamera();
		
		ListView leftDrawerList = (ListView) findViewById(R.id.atyMonitoringLeftDrawerList);
		leftDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cameraNames));
		leftDrawerList.setOnItemClickListener(onItemClickListener);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(0, 1, Menu.NONE, R.string.delete);
//		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 1:
			
			break;
		}
		return true;
	}
	
	
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			clickMonitorId = v.getId();
			contentDrawer.openDrawer(Gravity.LEFT);
		}
	};
	
	private IRegisterIOTCListener iotcListener = new IRegisterIOTCListener() {
		
		@Override
		public void receiveChannelInfo(Camera arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			LogUtils.i("receiveChannelInfo:"+arg2);
//			handler.sendEmptyMessage(arg2);
		}

		@Override
		public void receiveFrameData(Camera arg0, int arg1, Bitmap arg2) {
			LogUtils.i("receiveFrameData:"+arg1);
//			this.videoWidth = arg2.getWidth();
//			this.videoHeight = arg2.getHeight();
		}

		@Override
		public void receiveFrameDataForMediaCodec(Camera arg0, int arg1, byte[] arg2, int arg3, int arg4, byte[] arg5,
				boolean arg6, int arg7) {
//			LogUtils.i("receiveFrameDataForMediaCodec");
		}

		@Override
		public void receiveFrameInfo(Camera camera, int sessionChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {
			LogUtils.i("receiveFrameInfo");
//			this.onlineNum = onlineNm;
//			handler.sendEmptyMessage(STS_CHANGE_CHANNEL_STREAMINFO);
		}

		@Override
		public void receiveIOCtrlData(Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {
			// TODO Auto-generated method stub
			LogUtils.i(" receiveIOCtrlData:"
					+" sessionChannel:"+sessionChannel
					+" avIOCtrlMsgType:"+avIOCtrlMsgType);
			
//			Bundle bundle = new Bundle();
//			bundle.putByteArray("data", data);
//			Message msg = handler.obtainMessage();
//			msg.what = avIOCtrlMsgType;
//			msg.setData(bundle);
//			handler.sendMessage(msg);
		}

		@Override
		public void receiveSessionInfo(Camera camera, int resultCode) {
			// TODO Auto-generated method stub
			LogUtils.i("receiveSessionInfo"+resultCode);
//			handler.sendEmptyMessage(resultCode);
		}
	};
	
	private CameraListener cameraListener = new CameraListener() {
		@Override
		public void OnSnapshotComplete() {
			// TODO Auto-generated method stub
		}
	};
	
	private class MyMonitorClickListener implements MonitorClickListener {
		
		private View v;
		
		public MyMonitorClickListener(View v) {
			this.v = v;
		}

		@Override
		public void OnClick() {
			new AlertDialog.Builder(MonitoringActivity.this)
			.setIcon(R.drawable.app_logo_1)
			.setTitle(R.string.remove_camera)
			.setMessage(R.string.sure_remove_camera)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (v.getId()) {
					case 0:
						disconnectCamera(leftUpCamera);
						leftUpRelative.removeView(leftUpMonitor);
						removeAlreadyAddCamera(leftUpRelative.getId());
						break;
					case 1:
						disconnectCamera(rightUpCamera);
						rightUpRelative.removeView(rightUpMonitor);
						removeAlreadyAddCamera(rightUpRelative.getId());
						break;
					case 2:
						disconnectCamera(leftDownCamera);
						leftDownRelative.removeView(leftDownMonitor);
						removeAlreadyAddCamera(leftDownRelative.getId());
						break;
					case 3:
						disconnectCamera(rightDownCamera);
						rightDownRelative.removeView(rightDownMonitor);
						removeAlreadyAddCamera(rightDownRelative.getId());
						break;
					}
					
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			})
			.create()
			.show();
		}
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			contentDrawer.closeDrawer(Gravity.LEFT);
			
			Device mCamera = cameras.get(position);
			
			if (alreadyAddedCamera.get(mCamera.getDevName()) != null) {
				ToastUtils.show(R.string.camera_already_added);
				return;
			}
			
			if (TextUtils.isEmpty(mCamera.getPassword())) {
				ToastUtils.show(R.string.not_configuration_password);
				return;
			}
			
			alreadyAddedCamera.put(mCamera.getDevName(), clickMonitorId);
			
			switch (clickMonitorId) {
			case R.id.atyMonitoringLeftUpRelative:
				connectCamera(leftUpCamera, leftUpMonitor, mCamera);
				leftUpRelative.addView(leftUpMonitor);
				break;
			case R.id.atyMonitoringRightUpRelative:
				if (rightUpCamera == null || rightUpMonitor == null) {
					rightUpCamera = new Camera();
					initCamera(rightUpCamera, mCamera);
					
					rightUpMonitor = new Monitor(MonitoringActivity.this, null);
					initMonitor(rightUpCamera, rightUpMonitor, 1);
				} else {
					connectCamera(rightUpCamera, rightUpMonitor, mCamera);
				}
				rightUpRelative.addView(rightUpMonitor);
				break;
			case R.id.atyMonitoringLeftDownRelative:
				if (leftDownCamera == null || leftDownMonitor == null) {
					leftDownCamera = new Camera();
					initCamera(leftDownCamera, mCamera);
					
					leftDownMonitor = new Monitor(MonitoringActivity.this, null);
					initMonitor(leftDownCamera, leftDownMonitor, 2);
				} else {
					connectCamera(rightUpCamera, rightUpMonitor, mCamera);
				}
				leftDownRelative.addView(leftDownMonitor);
				break;
			case R.id.atyMonitoringRightDownRelative:
				if (rightDownCamera == null || rightDownMonitor == null) {
					rightDownCamera = new Camera();
					initCamera(rightDownCamera, mCamera);
					
					rightDownMonitor = new Monitor(MonitoringActivity.this, null);
					initMonitor(rightDownCamera, rightDownMonitor, 3);
				} else {
					connectCamera(rightUpCamera, rightUpMonitor, mCamera);
				}
				rightDownRelative.addView(rightDownMonitor);
				break;
			}
			
		}
	};
	
	private void initMonitor(Camera camera, Monitor monitor, int monitorId) {
		if (monitor != null) {
			monitor.setId(monitorId);
			monitor.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			monitor.setMaxZoom(3.0f);
			monitor.enableDither(camera.mEnableDither);
			monitor.attachCamera(camera, 0);
			monitor.cleanFrameQueue();
			monitor.SetOnMonitorClickListener(new MyMonitorClickListener(monitor));
		}
	}
	
	private void initCamera(Camera camera, Device mCamera) {
		camera.registerIOTCListener(iotcListener);
		camera.SetCameraListener(cameraListener);
		camera.connect(mCamera.getUid());
		camera.start(Camera.DEFAULT_AV_CHANNEL, "admin", mCamera.getPassword());
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETISOPTZOOM_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetIsOptZoomReq.parseContent(0));
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetVideoModeReq.parseContent(0));
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(0, (byte) 1));
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_Temperature_Humidity_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetTemperatureHumidityReq.parseContent(Camera.DEFAULT_AV_CHANNEL));
		camera.startShow(0, true, true);
		
	}
	
	private void getAllCamera() {
		cameras = CacheUtils.getDeviceByTypeName(AxalentUtils.TYPE_CAMERA);
		final int size = cameras.size();
		cameraNames = new String[size];
		for (int i = 0; i < size; i++) {
			cameraNames[i] = cameras.get(i).getDevName();
		}
	}
	
	private boolean removeAlreadyAddCamera(int removeId) {
		for (Entry<String, Integer> entry : alreadyAddedCamera.entrySet()) {
			if (removeId == entry.getValue()) {
				alreadyAddedCamera.remove(entry.getKey());
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public void onDestroy() {
		disconnectCamera(leftUpCamera);
		disconnectCamera(rightUpCamera);
		disconnectCamera(leftDownCamera);
		disconnectCamera(rightDownCamera);
		
		setResult(0);
		
		super.onDestroy();
	}
	
	
	private void disconnectCamera(Camera camera) {
		if (camera != null) {
			camera.disconnect();
		}
	}
	
	private void connectCamera(Camera camera, Monitor monitor, Device mCamera) {
		if (camera != null && mCamera != null) {
			camera.disconnect();
			if (monitor != null){
				monitor.deattachCamera();
			}
			camera.connect(mCamera.getUid());
			camera.start(Camera.DEFAULT_AV_CHANNEL, "admin", mCamera.getPassword());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(0, (byte) 1));
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETGUARD_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetGuardReq.parseContent(0));
			camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_NAME_REQ, AVIOCTRLDEFs.SMsgAVIoctrlCamGetNameReq.parseContent(0));
			if (monitor != null){
				monitor.enableDither(camera.mEnableDither);
				monitor.attachCamera(camera, 0);
			}
		
		}
	}

}
