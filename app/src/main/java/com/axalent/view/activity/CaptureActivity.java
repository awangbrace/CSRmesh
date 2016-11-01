/**
 * File Name                   : ɨ���豸����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.presenter.AddDevice;
import com.axalent.presenter.AddDevice.OnAddDeviceListener;
import com.axalent.presenter.controller.impl.DeviceManagerImpl;
import com.axalent.model.Device;
import com.axalent.model.HintError;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.view.widget.LoadingDialog;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CaptureActivity extends BaseActivity implements Callback {

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private LinearLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private LoadingDialog loadingDialog;
	private int count = 0;
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// ��ʼ�� CameraManager
		initActionBar();
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		mContainer = (LinearLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(2000);
		mQrLineView.startAnimation(animation);
		loadingDialog = new LoadingDialog(this);
	}

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// �������
			CameraManager.get().openLight();
		} else {
			flag = true;
			// �������
			CameraManager.get().offLight();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		addScanDevice(result);
	}
	
	private void initActionBar() {
		View customView = findViewById(R.id.barAddContent);
		TextView titleTxt = (TextView) customView.findViewById(R.id.barAddTitleTxt);
		titleTxt.setText(R.string.search_qr_code);
		RelativeLayout back = (RelativeLayout) customView.findViewById(R.id.barAddBack);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void addScanDevice(String result) {
		LogUtils.i("msg:"+result+" length:"+result.length());
		if (TextUtils.isEmpty(result)) {
			ToastUtils.show(R.string.not_detection_scan_data);
		} else {
			loadingDialog.show(R.string.is_the_add);
			count = 0;
			if (result.contains("uuid")) {
				final List<Device> gateways = CacheUtils.getDeviceByTypeName(AxalentUtils.TYPE_GATEWAY);
				if (!gateways.isEmpty()) {
					try {
						JSONObject obj = new JSONObject(result);
						String uuid = obj.optString("uuid");
						String ac = obj.optString("ac");
						String value = TextUtils.isEmpty(ac) ? uuid : uuid + " " +ac;
						LogUtils.i("value:"+value);
						for (Device gateway : gateways) {
							if (gateway.getState() != null && gateway.getState().equals("1")) {
								LogUtils.i("��������������豸����أ�"+gateway.getDevName());
								count++;
								AddDevice addDevice = new AddDevice(new DeviceManagerImpl());
								addDevice.setTimeOut(60000);
								addDevice.setOnAddDeviceListener(new OnAddDeviceListener() {
									@Override
									public void onAddDevice(Device device, HintError error) {
										LogUtils.i("��ӳɹ�");
										loadingDialog.close();
										if (device != null) {
											sendAddBroadcast(device);
											ToastUtils.showByTask(R.string.add_success);
											finish();
										} else {
											count++;
											if (count == gateways.size()) {
												String errorCode = error.getErrorCode();
												if ("346".equals(errorCode) || "110".equals(errorCode)) {
													ToastUtils.showByTask(R.string.device_already_add);
												} else if ("809".equals(errorCode)) {
													ToastUtils.showByTask(R.string.device_already_add);
												} else if ("992".equals(errorCode)) {
													ToastUtils.showByTask(R.string.format_error);
												} else {
													ToastUtils.showByTask(R.string.add_failure);
												}
											}
											sendAgainScan();
										}
									}
								});
								addDevice.addGatewayDevice(
										gateway, AxalentUtils.ATTRIBUTE_PERMITJOIN, value);
							}
						
						}
						
						if (count == 0) {
							LogUtils.i("û���ҵ����");
							sendAgainScan();
							loadingDialog.close();
							ToastUtils.showByTask(R.string.add_failure);
						}
						
					} catch (Exception e) {
						LogUtils.i("�׳��쳣��"+e.getMessage());
						sendAgainScan();
						loadingDialog.close();
						ToastUtils.showByTask(R.string.add_failure);
					}
				} else {
					LogUtils.i("��ά���ʽ����");
					sendAgainScan();
					loadingDialog.close();
					ToastUtils.showByTask(R.string.add_failure);
				}
			} else {
				AddDevice addDevice = new AddDevice(new DeviceManagerImpl());
				addDevice.setTimeOut(60000);
				addDevice.setOnAddDeviceListener(new OnAddDeviceListener() {
					@Override
					public void onAddDevice(Device device, HintError error) {
						loadingDialog.close();
						if (device != null) {
							sendAddBroadcast(device);
							ToastUtils.showByTask(R.string.add_success);
							finish();
						} else {
							String errorCode = error.getErrorCode();
							if ("346".equals(errorCode) || "110".equals(errorCode)) {
								ToastUtils.showByTask(R.string.device_already_add);
							} else if ("809".equals(errorCode)) {
								ToastUtils.showByTask(R.string.device_already_add);
							} else if ("992".equals(errorCode)) {
								ToastUtils.showByTask(R.string.format_error);
							} else {
								ToastUtils.showByTask(R.string.add_failure);
							}
							sendAgainScan();
						}
					}
				});
				addDevice.addScanDevice(result);
			}
			
		}
	}
	
	private void sendAddBroadcast(Device scanDevice) {
		Intent intent = new Intent("com.axalent.HomeActivity");
		intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.ADD_DEVICE);
		intent.putExtra(AxalentUtils.KEY_DEVICE, scanDevice);
		sendBroadcast(intent);
	}
	
	
	private void sendAgainScan() {
		// ����ɨ�裬�����ʹ���Ϣɨ��һ�ν����Ͳ����ٴ�ɨ��
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, 2500);
		}
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width
					/ mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height
					/ mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}
