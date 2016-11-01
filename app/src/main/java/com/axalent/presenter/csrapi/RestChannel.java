/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.presenter.csrapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.csr.csrmesh2.MeshService;

public class RestChannel {

    public enum RestMode {
        GATEWAY,
        CLOUD
    }


    // PRODUCTION server (test)
    private static final String CLOUD_HOST = "csrmesh-test.csrlbs.com";
    public static final String REST_APPLICATION_CODE = "c007b500-cbbd-437d-8594-59b5fec32d81"; // Used for server csrmesh-test.csrlbs.com

/*
    // DEV server (test2)
    private static final String CLOUD_HOST = "csrmesh-test2.csrlbs.com";
    public static final String REST_APPLICATION_CODE = "733c2dce-91dd-429e-ba23-ada10c855ddf"; // Used for server csrmesh-test2.csrlbs.com
*/

    private static final String CLOUD_PORT = "443";
    public static final String GATEWAY_HOST = "192.168.1.1";
    public static final String GATEWAY_PORT = "80";
    public static final String BASE_PATH_AAA = "/csrmesh/aaa"; //"/cgi-bin/csrmesh/aaa";
    public static final String BASE_PATH_CNC = "/csrmesh/cnc"; ///cgi-bin/csrmesh/cnc";
    public static final String BASE_PATH_CONFIG = "/csrmesh/config"; ///cgi-bin/csrmesh/config";

    public static final String TENANT_NAME = "tenantid_123";
    public static final String SITE_NAME = "siteid_123";
    public static final String MESH_ID = "mesg456";
    public static final String DEVICE_ID = "12";

    public static final String BASE_PATH_CGI = "/cgi-bin";
    public static final String URI_SCHEMA_HTTP = "http";
    public static final String URI_SCHEMA_HTTPS = "https";
    public static final String BASE_CSRMESH = "/csrmesh";

//    public static String getCloudHost(Context context) {
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        String storedCloudUrl = sharedPref.getString(context.getString(R.string.pref_cloud_key_url), "");
//        if(storedCloudUrl.equals("")) {
//            return CLOUD_HOST;
//        } else {
//            return storedCloudUrl;
//        }
//    }

//    public static String getCloudPort(Context context) {
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        String storedCloudPort = sharedPref.getString(context.getString(R.string.pref_cloud_key_port), "");
//        if(storedCloudPort.equals("")) {
//            return CLOUD_PORT;
//        } else {
//            return storedCloudPort;
//        }
//    }

    public static void setRestParameters(MeshService.ServerComponent serverComponent, String host, String port, String basePath, String uriScheme) {
        MeshLibraryManager.getInstance().getMeshService().setRestParams(serverComponent, host, port, basePath, uriScheme);
    }

    public static void setTenant(String tenantId) {
        MeshLibraryManager.getInstance().getMeshService().setTenantId(tenantId);
    }

    public static void setSite(String siteId) {
        MeshLibraryManager.getInstance().getMeshService().setSiteId(siteId);
    }
}
