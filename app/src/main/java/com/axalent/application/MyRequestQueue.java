package com.axalent.application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.text.TextUtils;

public class MyRequestQueue {
	
	public static final String VOLLEY_TAG = "VolleyPatterns";
	private static RequestQueue requestQueue;
	
	public static RequestQueue getInstance() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(MyApplication.getInstance());
		}
		return requestQueue;
	}
	
	public static <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? VOLLEY_TAG : tag);
		getInstance().add(req);
	}

	public static <T> void addToRequestQueue(Request<T> req) {
		req.setTag(VOLLEY_TAG);
		getInstance().add(req);
	}

	public static void cancelPendingRequests() {
		getInstance().cancelAll(VOLLEY_TAG);
	}

}
