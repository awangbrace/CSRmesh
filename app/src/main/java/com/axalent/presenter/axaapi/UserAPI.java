/**
 * File Name                   : 用户管理类
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2016/1/22
 * Revision History            :
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */
package com.axalent.presenter.axaapi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.axalent.application.MyCacheData;
import com.axalent.application.MyRequestQueue;
import com.axalent.application.XmlRequest;
import com.axalent.model.User;
import com.axalent.util.AxalentUtils;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

public class UserAPI {

    /**
     * 登录
     *
     * @param user
     * @param success
     * @param error
     */
    public static void userLogin(final User user, Listener<XmlPullParser> success,
                                 ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "userLogin", success,
                error) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("appId", user.getAppId());
                map.put("name", user.getUsername());
                map.put("password", user.getPassword());
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }

    /**
     * system account login
     *
     * @param success
     * @param error
     */
    public static void systemLogin(Listener<XmlPullParser> success,
                                 ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "systemLogin", success,
                error) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", AxalentUtils.SYSTEMACCOUNT);
                map.put("password", AxalentUtils.SYSTEMPASSWORD);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }

    /**
     * system account login
     *
     * @param success
     * @param error
     */
    public static void getUserFromDeviceId(final String sysSecToken, final String deviceId, Listener<XmlPullParser> success,
                                   ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "getUserFromDeviceId", success,
                error) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("sysSecToken", sysSecToken);
                map.put("deviceID", deviceId);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }

    /**
     * system account login
     *
     * @param success
     * @param error
     */
    public static void getUserValueList(final String sysSecToken, final String userId, Listener<XmlPullParser> success,
                                           ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "getUserValueList", success,
                error) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("secToken", sysSecToken);
                map.put("userId", userId);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }

    /**
     * 注册用户
     *
     * @param user
     * @param success
     * @param error
     */
    public static void registerUser(final User user, Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "registerUser", success,
                error) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("applicationId", user.getUserId());
                map.put("email", user.getUsername());
                map.put("username", user.getUsername());
                map.put("password", user.getPassword());
                map.put("appId", user.getUserId());
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }


    /**
     * 激活用户
     *
     * @param activateCode
     * @param success
     * @param error
     */
    public static void activateUser(final String activateCode, Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "activateUser", success, error) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("activationCode", activateCode);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }

    /**
     * 更新密码
     *
     * @param user
     * @param newPassword
     * @param success
     * @param error
     */
    public static void updatePassword(final User user, final String newPassword, Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "updatePassword", success,
                error) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("secToken", MyCacheData.getInstance().getCacheUser().getSecurityToken());
                map.put("username", user.getUsername());
                map.put("oldPassword", user.getPassword());
                map.put("newPassword", newPassword);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }

    /**
     * 回收密码
     *
     * @param emailAddress
     * @param success
     * @param error
     */
    public static void requestPasswordRecoveryCode(final String emailAddress, Listener<XmlPullParser> success,
                                                   ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "requestPasswordRecoveryCode", success,
                error) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("email", emailAddress);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }

    /**
     * 重置密码
     *
     * @param user
     * @param success
     * @param error
     */
    public static void updateUserPassword(final User user, final String recoveryCode, Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "updateUserPassword", success, error) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("recoveryCode", recoveryCode);
                map.put("username", user.getUsername());
                map.put("password", user.getPassword());
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }


    /**
     * 设置用户属性
     *
     * @param name
     * @param value
     * @param success
     * @param error
     */
    public static void setUserAttribute(final String name, final String value, Listener<String> success,
                                        ErrorListener error) {
        StringRequest req = new StringRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "setUserAttribute", success,
                error) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userId", MyCacheData.getInstance().getCacheUser().getUserId());
                map.put("secToken", MyCacheData.getInstance().getCacheUser().getSecurityToken());
                map.put("name", name);
                map.put("value", value);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);

    }

    /**
     * 获得用户属性
     * @param success
     * @param error
     */
    public static void getUserValueList(Listener<XmlPullParser> success, ErrorListener error) {
        getUserValueList(null, success, error);
    }

    /**
     * 获得用户属性
     * @param success
     * @param error
     */
    public static void getUserValueList(String tag, Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "getUserValueList", success,
                error) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userId", MyCacheData.getInstance().getCacheUser().getUserId());
                map.put("userName", MyCacheData.getInstance().getCacheUser().getUsername());
                map.put("secToken", MyCacheData.getInstance().getCacheUser().getSecurityToken());
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req, tag);
    }


    /**
     * 注册移动手机
     * @param nativeId
     * @param operatingSystem
     * @param nativeSecurityToken
     * @param friendlyName
     * @param mobileAppName
     * @param success
     * @param error
     */
    public static void registerMobilePhone(final String nativeId, final String operatingSystem,
                                           final String nativeSecurityToken, final String friendlyName, final String mobileAppName, Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "registerMobilePhone", success,
                error) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("secToken", MyCacheData.getInstance().getCacheUser().getSecurityToken());
                map.put("userId", MyCacheData.getInstance().getCacheUser().getUserId());
                map.put("nativeId", nativeId);
                map.put("nativeSecurityToken", nativeSecurityToken);
                map.put("operatingSystem", operatingSystem);
                map.put("friendlyName", friendlyName);
                map.put("mobileAppName", mobileAppName);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }


    /**
     * 获得移动手机根据用户ID
     * @param success
     * @param error
     */
    public static void getMobilePhonesByUserId(Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "getMobilePhonesByUserId", success,
                error) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("secToken", MyCacheData.getInstance().getCacheUser().getSecurityToken());
                map.put("userId", MyCacheData.getInstance().getCacheUser().getUserId());
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }

    /**
     * 更新移动手机
     * @param phoneId
     * @param nativeSecurityToken
     * @param friendlyName
     * @param success
     * @param error
     */
    public static void updateMobilePhone(final String phoneId, final String nativeSecurityToken,
                                         final String friendlyName, Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "updateMobilePhone", success,
                error) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("secToken", MyCacheData.getInstance().getCacheUser().getSecurityToken());
                map.put("userId", MyCacheData.getInstance().getCacheUser().getUserId());
                map.put("nativeSecurityToken", nativeSecurityToken);
                map.put("friendlyName", friendlyName);
                map.put("phoneId", phoneId);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }

    /**
     * 解绑移动手机根据id
     * @param phoneId
     * @param success
     * @param error
     */
    public static void unregisterMobilePhone(final String phoneId, Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "unregisterMobilePhone", success,
                error) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("secToken", MyCacheData.getInstance().getCacheUser().getSecurityToken());
                map.put("userId", MyCacheData.getInstance().getCacheUser().getUserId());
                map.put("phoneId", phoneId);
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }


    /**
     * 获得服务器的时间
     * @param success
     * @param error
     */
   public static void getServerTime(Listener<XmlPullParser> success, ErrorListener error) {
        XmlRequest req = new XmlRequest(Method.POST, AxalentUtils.SERVER_ADDRESS_REMOTELY + "getServerTime", success,
                error) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("secToken", MyCacheData.getInstance().getCacheUser().getSecurityToken());
                return map;
            }
        };
        MyRequestQueue.addToRequestQueue(req);
    }




}
