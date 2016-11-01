/**
 * File Name                   : ͬ�����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.presenter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;

import com.axalent.model.User;

public class SyncLocalData extends AsyncTask<Object, Void, XmlPullParser>{
	
	public SyncDataListener syncDataListener;
	
	public void setSyncDataListener(SyncDataListener syncDataListener) {
		this.syncDataListener = syncDataListener;
	}

	@Override
	protected XmlPullParser doInBackground(Object... params) {
		try {
			URL url = new URL((String) params[0]);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(120000);
			httpURLConnection.setReadTimeout(120000);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			
			User user = (User) params[1];
			String parameter = "name=" + user.getUsername() + "&password=" + user.getPassword() + "&appId=" + user.getAppId();
			
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameter.getBytes().length));  
			httpURLConnection.setRequestProperty("Content-Language", "en-US");
			
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
			bufferedWriter.write(parameter);
			bufferedWriter.flush();
			bufferedWriter.close();
			
			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));  
				 StringBuilder sb = new StringBuilder();
				 String line = "";
				 
				 while ((line = bufferedReader.readLine()) != null) {
					 sb.append(line);
				 }
				 
				 bufferedReader.close();
				 XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
				 XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
				 xmlPullParser.setInput(new StringReader(sb.toString()));
				 return xmlPullParser;
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(XmlPullParser result) {
		if (syncDataListener != null) {
			syncDataListener.syncDataListener(result);
		}
		super.onPostExecute(result);
	}
	
	public interface SyncDataListener {
		void syncDataListener(XmlPullParser result);
	}


}
