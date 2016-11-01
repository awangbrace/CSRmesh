package com.axalent.view.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.axalent.R;
import com.axalent.application.BaseActivity;
import com.axalent.application.MyRequestQueue;
import com.axalent.application.XmlRequest;
import com.axalent.presenter.DiscoveryGateway;
import com.axalent.presenter.WifiAdmin;
import com.axalent.presenter.controller.DeviceManager;
import com.axalent.presenter.controller.impl.DeviceManagerImpl;
import com.axalent.model.APWifi;
import com.axalent.model.Device;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;
import com.axalent.util.XmlUtils;
import com.axalent.view.widget.LoadingDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SwitchGatewayWifiActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener,
		OnItemClickListener, Comparator<APWifi> {

	private List<APWifi> wifis = new ArrayList<APWifi>();
	private SwipeRefreshLayout refreshLayout = null;
	private ListView wifiList = null;
	private WifiAdapter adapter = null;
	private LoadingDialog dialog = null;
	private String localUrl = null;
	private Handler handler = new Handler();
	private DiscoveryGateway discoveryGateway;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch_gateway_wifi);

		initActionbar();

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.atySwitchGatewayWifiRefreshLayout);
		refreshLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		refreshLayout.setOnRefreshListener(this);

		wifiList = (ListView) findViewById(R.id.atyWifiList);
		adapter = new WifiAdapter();
		wifiList.setAdapter(adapter);
		wifiList.setOnItemClickListener(this);

		localUrl = getIntent().getStringExtra("localUrl");

		LogUtils.i("localUrl:" + localUrl);

		dialog = new LoadingDialog(this);
		dialog.show(R.string.load_data);

		loadData();
	}

	private void initActionbar() {
		View content = findViewById(R.id.barWifiSettingContent);
		View back = content.findViewById(R.id.barSettingBack);
		back.setOnClickListener(this);
	}


	private void switchGatewayWifi(final APWifi wifi) {
		dialog.show(R.string.please_wait);
		XmlRequest req = new XmlRequest(Method.POST, localUrl + "setWifiMode", new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser arg0) {
				LogUtils.i("setWifiMode success...");
				sharedPreferences.edit().putString("wifiName", wifi.getSsid()).commit();
				networkRestart(wifi);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LogUtils.e("setWifiMode error...");
				dialog.close();
				ToastUtils.show(R.string.failure);
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("mode", "sta");
				map.put("ssid", wifi.getSsid());
				map.put("password", wifi.getPassword());
				map.put("encryption", wifi.getEncryption());
				return map;
			}
		};
		MyRequestQueue.addToRequestQueue(req);
	}

	private void networkRestart(final APWifi wifi) {
		XmlRequest req = new XmlRequest(Method.POST, localUrl + "networkRestart", new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser arg0) {
				LogUtils.i("networkRestart success...");
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LogUtils.e("networkRestart error...");
			}
		});
		MyRequestQueue.addToRequestQueue(req);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				LogUtils.i("start switch phone wifi...");
				WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
				wifiAdmin.connectWiFi(wifi.getSsid(), wifi.getPassword(), 3);
				wifiAdmin.connectWiFi(wifi.getSsid(), wifi.getPassword(), 3);
				wifiAdmin.connectWiFi(wifi.getSsid(), wifi.getPassword(), 3);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						LogUtils.i("start find gateway...");
						discoveryGateway = new DiscoveryGateway(getApplicationContext()) {
							@Override
							public void onResp(String url) {
								LogUtils.i("find gateway ok...");
								handler.removeCallbacks(timeout);
								discoveryGateway.stop();
								sharedPreferences.edit()
										.putString("localUrl", url)
										.putString("wifiName", wifi.getSsid())
										.commit();

								Intent intent = new Intent();
								intent.putExtra("ssid", wifi.getSsid());
								intent.putExtra("url", url);
								setResult(AxalentUtils.SWITCH_GATEWAY_WIFI, intent);

								ToastUtils.show(R.string.success);
								dialog.close();
								finish();
							}
						};
						discoveryGateway.start();
						handler.postDelayed(timeout, 50000);
					}
				}, 10000);
			}
		}, 10000);

	}

	private Runnable timeout = new Runnable() {
		@Override
		public void run() {
			LogUtils.e("find gateway timeout...");
			discoveryGateway.stop();
//			dialog.close();
//			finish();
//			ToastUtils.show(R.string.failure);
			ToastUtils.show(R.string.time_out);
			dialog.close();
			confirmTryAgainSwitch();
		}
	};

	private void confirmTryAgainSwitch() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.please_try_again));
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void loadData() {
		XmlRequest req = new XmlRequest(Method.POST, localUrl + "getApList", new Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser pull) {
				LogUtils.i("getApList success...");
				XmlUtils.parserAPWifiList(pull, wifis);
				Collections.sort(wifis, SwitchGatewayWifiActivity.this);
				dialog.close();
				refreshLayout.setRefreshing(false);
				adapter.notifyDataSetChanged();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LogUtils.e("getApList error...");
				dialog.close();
				refreshLayout.setRefreshing(false);
				ToastUtils.show(R.string.load_failure);
			}
		});

		MyRequestQueue.addToRequestQueue(req);
	}

	private class WifiAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return wifis.size();
		}

		@Override
		public Object getItem(int position) {
			return wifis.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.adapter_apwifi, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			APWifi wifi = wifis.get(position);
			holder.wifiName.setText(wifi.getSsid());
			holder.wifiSignal.setText(wifi.getSignal());

			return convertView;
		}

		private class ViewHolder {

			TextView wifiName;
			TextView wifiSignal;

			public ViewHolder(View view) {
				wifiName = (TextView) view.findViewById(R.id.adapterWifiNameTxt);
				wifiSignal = (TextView) view.findViewById(R.id.adapterWifiSignalTxt);
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.barSettingBack:
				finish();
				break;
		}
	}


	@Override
	public void onRefresh() {
		wifis.clear();
		adapter.notifyDataSetChanged();
		loadData();
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(SwitchGatewayWifiActivity.this);
		builder.setIcon(R.drawable.app_logo_1);
		builder.setTitle(R.string.switch_wifi);
		View v = getLayoutInflater().inflate(R.layout.dialog_input, null);
		builder.setView(v);
		final EditText codeEdit = (EditText) v.findViewById(R.id.dialogInputEdit);
		codeEdit.setHint(R.string.password);
		codeEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String password = codeEdit.getText().toString().trim();
				if (TextUtils.isEmpty(password)) {
					ToastUtils.show(R.string.password_not_null);
				} else if (password.length() < 8) {
					ToastUtils.show(R.string.password_length_eng);
				}
				else {
					APWifi wifi = wifis.get(position);
					wifi.setPassword(password);
					switchGatewayWifi(wifis.get(position));
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();


	}



	@Override
	public int compare(APWifi lhs, APWifi rhs) {
		int signalOne = Integer.parseInt(lhs.getSignal());
		int signalTwo = Integer.parseInt(rhs.getSignal());
		if (signalOne > signalTwo) {
			return 1;
		} else {
			return 0;
		}

	}
}
