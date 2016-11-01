package com.axalent.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.os.Environment;

public class DownLoadManager {
	public static File getFileFromServer(String path, ProgressDialog progressDialog) throws Exception {
		// sdCard
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			// get file size
			progressDialog.setMax(httpURLConnection.getContentLength());
			InputStream inputStream = httpURLConnection.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while((len = bufferedInputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, len);
				total += len;
				progressDialog.setProgress(total);
			}
			fileOutputStream.close();
			bufferedInputStream.close();
			inputStream.close();
			return file;
		} else {
			return null;
		}
	} 
}
