/**
 * File Name                   : 设置界面
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.application.MyRequestQueue;
import com.axalent.model.Gateway;
import com.axalent.presenter.ssdp.SSDPSearchMsg;
import com.axalent.presenter.ssdp.SSDPSocket;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DiscoveryGatewayActivity extends BaseActivity implements OnItemClickListener{

	private GatewayAdapter adapter;
	private ListView gatewayList;
	private List<Gateway> searchResults = new ArrayList<Gateway>();
	private boolean stop = false;
	private HashMap<String, String> filterMap = new HashMap<String, String>();
	private ProgressBar progressBar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway_discovery);
		initActionBar();
		initView();
		initMulticastLock();
		sendSearchMessage();
	}

	private void initActionBar() {
		View customView = findViewById(R.id.barSearchContent);
		TextView titleTxt = (TextView) customView.findViewById(R.id.barSearchTitleTxt);
		titleTxt.setText(R.string.discovery_gateway_title);
		RelativeLayout back = (RelativeLayout) customView.findViewById(R.id.barSearchBack);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		progressBar = (ProgressBar) customView.findViewById(R.id.barSearchBar);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void initView() {
		gatewayList = (ListView) findViewById(R.id.atyGatewaySetupList);
		adapter = new GatewayAdapter();
		gatewayList.setAdapter(adapter);
		gatewayList.setOnItemClickListener(this);
//		gatewayList.setOnItemLongClickListener(this);
	}

	private void initMulticastLock() {
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiManager.MulticastLock multicastLock = wm.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();
	}

	private void sendSearchMessage() {
		new Thread(searchRunnable).start();
	}

	private Runnable searchRunnable = new Runnable() {
		public void run() {
			SSDPSearchMsg searchProduct = new SSDPSearchMsg("ST:upnp:rootdevice");
			SSDPSocket sock = null;
			String message = null;
			try {
				sock = new SSDPSocket();
				message = searchProduct.toString();
				sock.send(message);
				while(!stop) {
					DatagramPacket dp = sock.receive();
					parserResult(new String(dp.getData()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private void parserResult(String result) {
		final String[] LOCATIONS = result.split("LOCATION: ");
		if (LOCATIONS.length < 2) {
			return;
		}
		String content = LOCATIONS[1];
		int start = 0;
		int end = content.indexOf("\r\n");
		String url = content.substring(start, end);
		if (filterMap.get(url) == null) {
			filterMap.put(url, url);
			findGateway(url);
		}
	}

	private void findGateway(final String url) {
		LogUtils.i("url:"+url);
		StringRequest req = new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String str) {
				try {
					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					XmlPullParser pull = factory.newPullParser();
					pull.setInput(new StringReader(str));
					Gateway gateway = XmlUtils.getGateway(pull);
					String manufacturer = gateway.getManufacturer();

					if (manufacturer != null && manufacturer.contains("Axalent")) {
						LogUtils.i("find gateway ok!");
						int start = 0;
						int end = url.lastIndexOf(":");
						String newUrl = url.substring(start, end);

						gateway.setUrl(newUrl);
						searchResults.add(gateway);
						adapter.notifyDataSetChanged();

					}

				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
			}
		});

		MyRequestQueue.addToRequestQueue(req);

	}


	private class GatewayAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return searchResults.size();
		}

		@Override
		public Object getItem(int position) {
			return searchResults.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = View.inflate(DiscoveryGatewayActivity.this, R.layout.adapter_gateway_config, null);
				holder = new  ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Gateway gateway = searchResults.get(position);
			String id = gateway.getId();
			String name = gateway.getModelName();
			holder.nameTxt.setText(id == null ? name : id);

			return convertView;
		}

		private class ViewHolder {

			TextView nameTxt;

			public ViewHolder(View view) {
				nameTxt = (TextView) view.findViewById(R.id.adapterGatewaySetupNameTxt);
			}
		}
	}

	@Override
	protected void onPause() {
		stop = true;
		progressBar.setVisibility(View.GONE);
		super.onPause();
	}


//	@Override
//	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//		Intent intent = new Intent(this, SwitchGatewayWifiActivity.class);
//		intent.putExtra("url", searchResults.get(position).getUrl());
//		startActivityForResult(intent, 0);
//		return true;
//	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg1 == 0x99) {
			finish();
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.local_control);
		builder.setMessage(R.string.gatewat_control_local);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Gateway gateway = searchResults.get(position);
				String localUrl = gateway.getUrl() + ":8087/zdk/services/zamapi/";
				sharedPreferences.edit().putString("localUrl", localUrl).commit();
				LogUtils.i("localUrl:"+localUrl);

				Intent intent = new Intent();
				intent.putExtra("url", localUrl);
				setResult(AxalentUtils.SAVE_GATEWAY_IP, intent);
				ToastUtils.show(R.string.success);
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
	}



}
	
