/**
 * File Name                   : ����ͷ��ʷ��¼����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.axalent.R;
import com.axalent.adapter.IncidentAdapter;
import com.axalent.application.BaseActivity;
import com.axalent.application.MyRequestQueue;
import com.axalent.model.Device;
import com.axalent.util.AxalentUtils;
import com.axalent.util.ListUtils;
import com.axalent.util.ToastUtils;
import com.axalent.view.widget.LoadingDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class EventActivity extends BaseActivity {

	private Device currentDevice = null;
	private int incidentCount = 0;
	private static final String SERVER_URL = "www.myidoorbell.com";
	private List<IncidentInfo> incidentInfos = new ArrayList<IncidentInfo>();
	private LoadingDialog loadingDialog = null;
	private ListView incidentList = null;
	private IncidentAdapter incidentAdapter = null;
	private String[] ipAddess = null;
	private SwipeRefreshLayout incidentSwipeRefresh = null;
	private int searchItem = 0;
	private static final int ALL = -1;
	private boolean loadDataSuccess = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incident);
		
		initActionBar();
		
		currentDevice = getIntent().getParcelableExtra(AxalentUtils.KEY_DEVICE);
		if (currentDevice == null 
				|| currentDevice.getUid() == null 
				|| currentDevice.getPassword() == null) {
			return;
		}
		
		
//		Camera camera = new Camera();
//		camera.registerIOTCListener(iRegisterIOTCListener);
//		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_REQ, SMsgAVIoctrlListEventReq.parseConent(0, startTime, stopTime, (byte) eventType, (byte) 0));
		
		incidentSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.atyIncidentSwipeRefresh);
		incidentSwipeRefresh.setColorScheme(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		incidentSwipeRefresh.setOnRefreshListener(onRefreshListener);
		incidentList = (ListView) findViewById(R.id.atyIncidentList);
//		incidentList.setOnItemClickListener(onItemClickListener);

		loadingDialog = new LoadingDialog(this);
		loadingDialog.show(R.string.load_data);
		getData(ALL);
	}
	
	private void initActionBar() {
		View content = findViewById(R.id.barEventContent);
		View back = content.findViewById(R.id.barIncidentBack);
		back.setOnClickListener(onClickListener);
		View search = content.findViewById(R.id.barIncidentSearch);
		search.setOnClickListener(onClickListener);
		
	}
	
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.barIncidentBack:
				// �˳�
				finish();
				break;
			case R.id.barIncidentSearch:
				// ��ʾ������
				showSearchDialog();
				break;
			}
		}
		
	};
	
	private void showSearchDialog() {
		new AlertDialog.Builder(EventActivity.this)
		.setIcon(R.drawable.app_logo_1)
		.setTitle(R.string.search_incident)
		.setSingleChoiceItems(R.array.array_search_items, searchItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// �������������
				searchItem = which;
			}
		})
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ���ȷ��������
				loadingDialog.show(R.string.load_data);
				getData(getSearchType());
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		})
		.create()
		.show();
	}
	
//	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			
//		}
//	};
//	
//	private Handler handler = new Handler() {
//		
//		public void handleMessage(Message msg) {
//			
//		};
//		
//	};
	
//	private IRegisterIOTCListener iRegisterIOTCListener = new IRegisterIOTCListener() {
//		
//		@Override
//		public void receiveSessionInfo(Camera arg0, int arg1) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void receiveIOCtrlData(Camera arg0, int arg1, int arg2, byte[] arg3) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void receiveFrameInfo(Camera arg0, int arg1, long arg2, int arg3, int arg4, int arg5, int arg6) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void receiveFrameDataForMediaCodec(Camera arg0, int arg1, byte[] arg2, int arg3, int arg4, byte[] arg5,
//				boolean arg6, int arg7) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void receiveFrameData(Camera arg0, int arg1, Bitmap arg2) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void receiveChannelInfo(Camera arg0, int arg1, int arg2) {
//			// TODO Auto-generated method stub
//			
//		}
//	};
	
	private OnRefreshListener onRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			getData(ALL);
		}
	};
	
	private String[] getIPAddess() {
		String[] ipAddress = null;
		try {
			InetAddress[] inetAddress = InetAddress.getAllByName(SERVER_URL);
			ipAddress = new String[inetAddress.length];
			String tempAddres = "";
			
			for(int i = 0; i < inetAddress.length; i++){
				byte[] address = inetAddress[i].getAddress();
				for (int j = 0; j < address.length; j++) {
					if (j > 0) {
						tempAddres += ".";
					}
					tempAddres += address[j] & 0xFF;
				}
				ipAddress[i] = tempAddres;
				tempAddres = "";
			}

			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        return ipAddress;
	}
	
	public class IncidentInfo {
		
		public String date;
		public String link;
		public String sensor;
		
		public IncidentInfo(String date, String link, String sensor) {
			this.date = date;
			this.link = link;
			this.sensor = sensor;
		}
		
	}
	
	/**
	 * ����¼���Ϣ
	 * @param type
	 */
	private void getData(final int type) {
		
		new Thread() {
			public void run() {
				
				if (ipAddess == null) {
					ipAddess = getIPAddess();
				}
				
				if (getReqMax() == 0) {
					disposeFailure(R.string.load_failure);
					return;
				}
				
				for (String ip : ipAddess) {
					String url = "http://" + ip + "/apns/acquire.php?uid=" + currentDevice.getUid() + "&" + "filter=" + type;
					StringRequest stringRequest = new StringRequest(url, new Listener<String>() {
						@Override
						public void onResponse(String response) {
							incidentCount ++;
							
							try {
								String data = response.substring(3);
								JSONObject object = new JSONObject(data);
								JSONArray array = object.getJSONArray("list");
								int length = array.length();
								// ����ȡ��ݳɹ��Ű�֮ǰ��������
								if (length > 0 && !loadDataSuccess) {
									incidentInfos.clear();
									loadDataSuccess = true;
								}
								for (int i = 0; i < length; i++) {
									JSONObject contentObject = array.getJSONObject(i);
									incidentInfos.add(new IncidentInfo(			
											contentObject.optString("Time"),
											contentObject.optString("Link"),
											contentObject.optString("Sensor")));
								}
								
							} catch (JSONException e) {
								disposeFailure(R.string.load_failure);
							}
							
							disposeResult();
							
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							incidentCount ++;
							disposeResult();
						}
					});
					MyRequestQueue.addToRequestQueue(stringRequest);
				}
				
				
			};
		}.start();
	}
	
	private void disposeResult() {
//		LogUtils.i("incidentCount:"+incidentCount);
		if (incidentCount == getReqMax()) {
			if (incidentInfos.isEmpty()) {
				disposeFailure(R.string.not_alarm_Incident);
			} else {
				// ���ʱ������
				ListUtils.sort(incidentInfos, new DateSortComparator());
				if (incidentAdapter == null) {
					incidentAdapter = new IncidentAdapter(this, incidentInfos);
					incidentList.setAdapter(incidentAdapter);
				} else {
					incidentAdapter.notifyDataSetChanged();
				}
				
			}
			disposeEnd();
		}
	}
	

	
	/**
	 * ��������������
	 * @return
	 */
	private int getReqMax() {
		return ipAddess == null ? 0 : ipAddess.length;
	}
	
	private void disposeFailure(int id) {
		if (incidentCount == getReqMax()) {
			if (loadDataSuccess) {
				// ���سɹ���֤��û��ʧ��,��id��Ϊ-1����ʾʧ����Ϣ
				id = -1;
				ListUtils.sort(incidentInfos, new DateSortComparator());
				incidentAdapter.notifyDataSetChanged();
			}
			disposeEnd();
			if (id != -1) {
				ToastUtils.show(id);
			}
		}
	}
	
	private void disposeEnd() {
		incidentSwipeRefresh.setRefreshing(false);
		incidentCount = 0;
		loadDataSuccess = false;
		loadingDialog.close();
	}
	
	class DateSortComparator implements Comparator<IncidentInfo> {

		@Override
		public int compare(IncidentInfo lhs, IncidentInfo rhs) {
			int result = lhs.date.compareTo(rhs.date);
			if (result < 0) {
				return 1;
			} else if (result > 0) {
				return -1;
			} else {
				return 0;
			}
		}
		
	}
	
	/**
	 * �������������
	 * @return
	 */
	private int getSearchType() {
		switch (searchItem) {
		case 0:
		case 1:
		case 2:
		case 3:
			return searchItem;
		default:
			// 4
			return -1;
		}
	}
	
	@Override
	protected void onDestroy() {
		setResult(0);
		super.onDestroy();
	}

}
