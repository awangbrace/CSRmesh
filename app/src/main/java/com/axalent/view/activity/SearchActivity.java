package com.axalent.view.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.presenter.AddDevice;
import com.axalent.presenter.AddDevice.OnAddDeviceListener;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.presenter.controller.impl.DeviceManagerImpl;
import com.axalent.model.Device;
import com.axalent.model.DeviceAttribute;
import com.axalent.model.HintError;
import com.axalent.util.AxalentUtils;
import com.axalent.util.CacheUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.LoadingDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	private ListView searchList = null;
	private List<Device> searchDevices = new ArrayList<Device>();
	private DeviceManager deviceManager = new DeviceManagerImpl();
	private int errorCount = 0;
	private int end = 0;
	private SearchDeviceAdapter searchDeviceAdapter = null;
	private List<SelectGatewayAttribute> currentThreads = new ArrayList<SelectGatewayAttribute>();
	private ProgressBar progressBar = null;
	private LoadingDialog dialog = null;
	private List<String> successUUIDs = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		initActionbar();
		
		searchList = (ListView) findViewById(R.id.activitySearchList);
		searchDeviceAdapter = new SearchDeviceAdapter();
		searchList.setAdapter(searchDeviceAdapter);
		searchList.setOnItemClickListener(this);
		
		dialog = new LoadingDialog(this);
		
		searchDevice();
	
	}
	
	private void initActionbar() {
		View customView = findViewById(R.id.barSearchContent);
		TextView titleTxt = (TextView) customView.findViewById(R.id.barSearchTitleTxt);
		titleTxt.setText(R.string.search_device);
		RelativeLayout back = (RelativeLayout) customView.findViewById(R.id.barSearchBack);
		back.setOnClickListener(this);
		progressBar = (ProgressBar) customView.findViewById(R.id.barSearchBar);
	}
	
	private class SearchDeviceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return searchDevices.size();
		}

		@Override
		public Object getItem(int position) {
			return searchDevices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.adapter_search, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			Device device = searchDevices.get(position);
			
			holder.nameTxt.setText("name : "+device.getDevName());
			holder.uuidTxt.setText("uuid : "+device.getUid());
			
			return convertView;
		}
		
	}
	
	private class ViewHolder {
		
		TextView nameTxt;
		TextView uuidTxt;
		
		public ViewHolder(View view) {
			nameTxt = (TextView) view.findViewById(R.id.adapterSearchNameTxt);
			uuidTxt = (TextView) view.findViewById(R.id.adapterSearchUuidTxt);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.barSearchBack:
			finish();
			break;
		}
	}
	
	
	private void searchDevice() {
		final List<Device> gateways = CacheUtils.getDeviceByTypeName(AxalentUtils.TYPE_GATEWAY);
		if (gateways.isEmpty()) {
			ToastUtils.show(R.string.not_find_gateway);
			return;
		}
	
		errorCount = 0;
		end = gateways.size();
		
		for (final Device gateway : gateways) {
			LogUtils.i("�ҷ��͵����Ϊ��"+gateway.getDevName());
			deviceManager.setDeviceAttribute(gateway.getDevId(), 
					AxalentUtils.ATTRIBUTE_SCAN_DEVICE,
					"1", new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					String reqCode = XmlUtils.converRequestCode(response);
					if (reqCode.equals(AxalentUtils.REQUEST_OK)) {
						SelectGatewayAttribute selectGatewayAttribute = 
								new SelectGatewayAttribute(gateway);
						currentThreads.add(selectGatewayAttribute);
						selectGatewayAttribute.start();
					} else {
						assertError();
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					assertError();
				}
			});
		}
	}
	
	private void assertError() {
		errorCount++;
		if (errorCount == end) {
			progressBar.setVisibility(View.GONE);
			ToastUtils.show(R.string.search_failure);
		}
	}
	
	private void stopSearchDevice() {
		
		destroyThread();
		
		final List<Device> gateways = CacheUtils.getDeviceByTypeName(AxalentUtils.TYPE_GATEWAY);
		if (gateways.isEmpty()) {
			ToastUtils.show(R.string.not_find_gateway);
			return;
		}
		
		for (final Device gateway : gateways) {
			deviceManager.setDeviceAttribute(gateway.getDevId(), 
					AxalentUtils.ATTRIBUTE_SCAN_DEVICE,
					"0", new Listener<XmlPullParser>() {
				@Override
				public void onResponse(XmlPullParser response) {
					LogUtils.i("ֹͣ�����ɹ�...");
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					LogUtils.i("ֹͣ����ʧ��...");
				}
			});
		}
		
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.add_device);
		View edit = View.inflate(this, R.layout.dialog_input, null);
		builder.setView(edit);
		final EditText acEdit = (EditText) edit.findViewById(R.id.dialogInputEdit);
		acEdit.setHint(R.string.authorization_code);
		InputFilter[] filters = {new InputFilter.LengthFilter(19)};
		acEdit.setFilters(filters);
		acEdit.addTextChangedListener(new AuthorizationCodeWatcher(acEdit));
		builder.setNegativeButton(R.string.cancel, null);
		builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				runOnUiThread(new Runnable() {
					public void run() {
						progressBar.setVisibility(View.GONE);
					}
				});
				
				SearchDevice dev = (SearchDevice) searchDevices.get(position);
				dev.setAuthorizationCode(acEdit.getText().toString().trim());
				addSearchDevice(dev);
			}
		});
		builder.create().show();
	}
	
	private class AuthorizationCodeWatcher implements TextWatcher {
		
		EditText edit;
		
		int beforeLen = 0;  
	    int afterLen = 0;
	    
		public AuthorizationCodeWatcher(EditText edit) {
			this.edit = edit;
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			String txt = edit.getText().toString();
			afterLen = txt.length();
			if (afterLen > beforeLen) {
				if (txt.length() == 5 || txt.length() == 10 || txt.length() == 15) {
					edit.setText(new StringBuffer(txt).insert(txt.length() - 1, "-").toString());
					edit.setSelection(edit.getText().length());
				}
			} else {
				if (txt.startsWith("-")) {
					edit.setText(new StringBuffer(txt).delete(afterLen - 1, afterLen).toString());
					edit.setSelection(edit.getText().length());
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			beforeLen = arg0.length();
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

		}
		
	}
	
	
	private void addSearchDevice(final SearchDevice dev) {
		dialog.show(R.string.is_the_add);
		AddDevice addDevice = new AddDevice(deviceManager);
		addDevice.setTimeOut(60000);
		addDevice.setOnAddDeviceListener(new OnAddDeviceListener() {
			@Override
			public void onAddDevice(Device device, HintError error) {
				dialog.close();
				if (device != null) {
					successUUIDs.add(dev.getUid());
					Intent intent = new Intent("com.axalent.HomeActivity");
					intent.putExtra(AxalentUtils.KEY_SKIP, AxalentUtils.ADD_DEVICE);
					intent.putExtra(AxalentUtils.KEY_DEVICE, device);
					sendBroadcast(intent);
					ToastUtils.showByTask(R.string.add_success);
				} else {
					String errorCode = error.getErrorCode();
					if ("346".equals(errorCode) || "110".equals(errorCode)) {
						ToastUtils.showByTask(R.string.device_already_add);
					} else if ("809".equals(errorCode)) {
						ToastUtils.showByTask(R.string.device_already_add);
					} else {
						ToastUtils.showByTask(R.string.add_failure);
					}
				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						searchDevices.clear();
					    searchDeviceAdapter.notifyDataSetChanged();
						progressBar.setVisibility(View.VISIBLE);
					}
				});
				
			}
		});
		
		String value = TextUtils.isEmpty(dev.getAuthorizationCode()) ? dev.getUid() : dev.getUid() + " " +dev.getAuthorizationCode();
		LogUtils.i("send value is:"+value);
		addDevice.addSearchDevice(dev.getGateway(), value);
	}
	
	private class SelectGatewayAttribute extends Thread implements Listener<XmlPullParser>,  ErrorListener{
		
		private boolean isChanged = false;
		private Device gateway = null;
		private String tempValue = "";
		
		public SelectGatewayAttribute(Device gateway) {
			LogUtils.i("�̣߳�"+gateway.getCustomName());
			this.gateway = gateway;
		}
		
		@Override
		public void run() {
			while (!isChanged) {
				LogUtils.i("���ڲ�ѯ����");
				deviceManager.getDeviceAttribute(gateway.getDevId(), AxalentUtils.ATTRIBUTE_SCAN_DEVICE,this, this);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onResponse(XmlPullParser response) {
			DeviceAttribute deviceAttribute = XmlUtils.converDeviceAttribute(response);
			String value = deviceAttribute.getValue();
			if (searchDevices.isEmpty()) tempValue = "";
			if (!TextUtils.isEmpty(value) && !tempValue.equals(value)) {
				tempValue = value;
				parserInfo(gateway, value);
			}
		}

		@Override
		public void onErrorResponse(VolleyError arg0) {
			LogUtils.i("ʧ��");
			isChanged = true;
		
		}
	}

	
	private void parserInfo(Device gateway, String value) {
		
		try {
			JSONArray array = new JSONArray(value);
			final int length = array.length();
			if (length > 0) {
				searchDevices.clear();
			}
			for (int i = 0; i < length; i++) {
				JSONObject obj = array.getJSONObject(i);
				String uuid = obj.optString("uuid");
				String name = obj.optString("name");
				
				boolean isFiltration = false;
				for (int j = 0; j < successUUIDs.size(); j++) {
					if (successUUIDs.get(j).equals(uuid)) {
						isFiltration = true;
						break;
					}
				}
				
				if (!isFiltration) {
					SearchDevice device = new SearchDevice();
					device.setUid(uuid);
					device.setDevName(name);
					device.setGateway(gateway);
					searchDevices.add(device);
					searchDeviceAdapter.notifyDataSetChanged();
				}
			}
			
		} catch (JSONException e) {
			
		}
	}
	
	private class SearchDevice extends Device {
		
		private Device gateway;
		private String authorizationCode;
		
		public void setGateway(Device gateway) {
			this.gateway = gateway;
		}

		public String getAuthorizationCode() {
			return authorizationCode;
		}

		public void setAuthorizationCode(String authorizationCode) {
			this.authorizationCode = authorizationCode;
		}

		public Device getGateway() {
			return gateway;
		}
		
		
		
	}
	
	private void destroyThread() {
		final int size = currentThreads.size();
		for (int i = 0; i < size; i++) {
			currentThreads.get(i).isChanged = true;
		}
		currentThreads.clear();
	}
	
	@Override
	protected void onDestroy() {
		stopSearchDevice();
		super.onDestroy();
	}
	
	
}
