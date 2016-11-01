package com.axalent.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;
import com.axalent.application.MyApplication;

public class InstallationUtils {

	public static final String INSTALLATION = "INSTALLATION";
    
    public static String getImei() {
    	try {
    		File installation = new File(MyApplication.getInstance().getFilesDir(), INSTALLATION);
    		if (!installation.exists()) {
    			writeInstallationFile(installation);
    		}
    		return readInstallationFile(installation);
    	} catch (Exception e) {
    		return "null";
    	}
    }
    
    public static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }
    
    public static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

}
