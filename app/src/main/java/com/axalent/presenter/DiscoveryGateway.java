package com.axalent.presenter;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.axalent.application.MyRequestQueue;
import com.axalent.model.Gateway;
import com.axalent.presenter.ssdp.SSDPSearchMsg;
import com.axalent.presenter.ssdp.SSDPSocket;
import com.axalent.util.LogUtils;
import com.axalent.util.XmlUtils;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.SystemClock;

public abstract class DiscoveryGateway {
	
	private Context context;
	private Thread thread;
	private boolean flag;
	private List<String> urls;
	
	public DiscoveryGateway(Context context) {
		this.context = context;
		this.flag = true;
		this.urls = new ArrayList<String>();
		initLock();
	}
	
	private void initLock() {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiManager.MulticastLock multicastLock = wm.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					SSDPSearchMsg searchProduct = new SSDPSearchMsg("ST:upnp:rootdevice");
					SSDPSocket sock = null;
					String message = null;
					try {
						sock = new SSDPSocket();
						message = searchProduct.toString();
						while(flag) {
							sock.send(message);
							DatagramPacket dp = sock.receive(); 
							parserResult(new String(dp.getData()));
							SystemClock.sleep(800);
						}
					} catch (IOException e) {
					}
				}
			});
			thread.start();
		}
	}
	
	public void stop() {
		flag = false;
	}
	
	public abstract void onResp(String url);
	
	private void parserResult(String result) {
		final String[] LOCATIONS = result.split("LOCATION: ");
		if (LOCATIONS.length < 2) {
			return;
		}
		String content = LOCATIONS[1];
		int start = 0;
		int end = content.indexOf("\r\n");
		String url = content.substring(start, end);
		if (urls.isEmpty() || !urls.contains(url)) {
			LogUtils.i("url:" + url);
			urls.add(url);
			findGateway(url);
		}
	}
	
	private void findGateway(final String url) {
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
						LogUtils.i("find gateway ok...!");
						int start = 0;
						int end = url.lastIndexOf(":");
						String newUrl = url.substring(start, end) + ":8087/zdk/services/zamapi/";
						onResp(newUrl);
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
	
	
}
