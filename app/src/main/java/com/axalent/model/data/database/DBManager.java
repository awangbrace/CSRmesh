/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.model.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.axalent.R;
import com.axalent.model.data.database.tables.TableAreas;
import com.axalent.model.data.database.tables.TableDevices;
import com.axalent.model.data.database.tables.TableGateways;
import com.axalent.model.data.database.tables.TableLastTime;
import com.axalent.model.data.database.tables.TablePlaces;
import com.axalent.model.data.database.tables.TableSettings;
import com.axalent.model.data.model.Area;
import com.axalent.model.data.model.Gateway;
import com.axalent.model.data.model.Place;
import com.axalent.model.data.model.Setting;
import com.axalent.model.data.model.Time;
import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.model.data.model.devices.TemperatureCSRDevice;
import com.axalent.model.data.model.devices.UnknownCSRDevice;
import com.axalent.presenter.RxBus;
import com.axalent.presenter.csrapi.MeshLibraryManager;
import com.axalent.presenter.events.MeshResponseEvent;
import com.axalent.util.ApplicationUtils;
import com.axalent.util.AxalentUtils;
import com.axalent.util.LogUtils;
import com.axalent.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    // Logcat tag
    private static final String TAG = "DBDataSource";

    private Context context;

    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private static DBManager dbManager = null;

    public static DBManager getDBManagerInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }

        return dbManager;
    }


    private DBManager(Context context) {
        this.context = context;

        mDBHelper = new DBHelper(context);
    }

    public synchronized void testDBOperations() {

        // Get the third device
        CSRDevice CSRDevice = getDevice(3);
        CSRDevice.setName("New Test Device");

        // Edit the 3rd device name
        createOrUpdateDevice(CSRDevice);

        // Create a new device from code
        TemperatureCSRDevice temperatureDevice = new TemperatureCSRDevice();
        temperatureDevice.setName("Heater 2");

        createOrUpdateDevice(temperatureDevice);

        List<CSRDevice> allDevicesList = getAllDevicesList();

        // Delete 4th device
        removeDevice(4);

        allDevicesList = getAllDevicesList();

        Log.d(TAG, allDevicesList.toString());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // DEVICES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public synchronized List<CSRDevice> getDevicesInArea(int areaId) {
        List<CSRDevice> CSRDeviceList = new ArrayList<>();

        List<CSRDevice> allCSRDevices = getAllDevicesList();
        Area area = getArea(areaId);
        if (area == null) {
            return null;
        }

        for (int i = 0; i < allCSRDevices.size(); i++) {
            CSRDevice CSRDevice = allCSRDevices.get(i);
            if (CSRDevice.getGroupsList().contains(area.getAreaID())) {
                CSRDeviceList.add(CSRDevice);
            }
        }

        return CSRDeviceList;
    }


    private Cursor getAllDevicesIDsCursor() {

        String sortOrder = TableDevices.COLUMN_NAME_DEVICE_ID + " ASC";
        String[] columns = {
                TableDevices.COLUMN_NAME_DEVICE_ID
        };

//        String queryString = context.getString(R.string.sql_where_clause_place_id);
//        String[] queryParameters = new String[]{String.valueOf(Utils.getLatestPlaceIdUsed(context))};

        try {
            return executeSelect(TableDevices.TABLE_NAME, columns, null, null, sortOrder);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Integer> getAllDevicesIDsList() {

        open();

        List<Integer> idsList = new ArrayList<>();
        Cursor cursor = getAllDevicesIDsCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                int deviceID = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_DEVICE_ID));
                idsList.add(deviceID);
            }
            cursor.close();
        }
        close();

        return idsList;
    }


    private Cursor getAllDevicesCursor() {

        String sortOrder = TableDevices._ID + " ASC";

//        String queryString = context.getString(R.string.sql_where_clause_place_id);
//        String[] queryParameters = new String[]{String.valueOf(Utils.getLatestPlaceIdUsed(context))};

        try {
            return executeSelect(TableDevices.TABLE_NAME, TableDevices.getColumnsNames(), null, null, sortOrder);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<CSRDevice> getAllDevicesList() {

        open();

        List<CSRDevice> devicesList = new ArrayList<>();

        Cursor cursor = getAllDevicesCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                CSRDevice CSRDevice = TableDevices.getDeviceFromCursor(cursor);
                devicesList.add(CSRDevice);
            }
            cursor.close();
        }
        close();

        devicesList = ApplicationUtils.sortDevicesListAlphabetically(devicesList);

        return devicesList;
    }


    private Cursor getDeviceCursor(final long id) {

        String queryString = context.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableDevices.TABLE_NAME, TableDevices.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized CSRDevice getDevice(final long id) {

        open();

        CSRDevice CSRDevice = null;
        Cursor cursor = getDeviceCursor(id);


        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE DEVICE STATES
                CSRDevice = TableDevices.getDeviceFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return CSRDevice;
    }
    // update time
    String KEY_UPDATE_TIME = "timestamp";
    // devices_list
    String KEY_DEVICES_LIST = "devices_list";
    String KEY_DEVICE_PRIM_ID = "id";
    String KEY_DEVICE_ID = "deviceID";
    String KEY_DEVICE_NAME = "name";
    String KEY_DEVICE_ASSOCIATED = "isAssociated";
    String KEY_DEVICE_HASH = "hash";
    String KEY_DEVICE_APPEARANCE = "appearance";
    String KEY_DEVICE_MODEL_LOW = "modelLow";
    String KEY_DEVICE_MODEL_HIGH = "modelHigh";
    String KEY_DEVICE_MODEL = "model";
    String KEY_DEVICE_PLACE_ID = "placeId";
    String KEY_DEVICE_UUID = "uuid";
    String KEY_DEVICE_NUM_GROUPS = "numgroups";
    String KEY_DEVICE_GROUPS = "groups";
    String KEY_DEVICE_FAVOURITE = "isFavorite";
    String KEY_DEVICE_AUTH_CODE = "authCode";

    // areas_list
    String KEY_AREAS_LIST = "areas_list";
    String KEY_AREAS_PRIM_ID = "id";
    String KEY_AREA_NAME = "name";
    String KEY_AREA_ID = "areaID";
    String KEY_AREA_PLACE_ID = "placeId";
    String KEY_AREA_FAVORITE = "isFavorite";

    // gateways_list
    String KEY_GATEWAY_LIST = "gateways_list";
    String KEY_GATEWAY_PRIM_ID = "id";
    String KEY_GATEWAY_NAME = "name";
    String KEY_GATEWAY_HOST = "host";
    String KEY_GATEWAY_PORT = "port";
    String KEY_GATEWAY_UUID = "uuid";
    String KEY_GATEWAY_BASE_PATH = "basePath";
    String KEY_GATEWAY_STATE = "state";
    String KEY_GATEWAY_DEVICE_HASH = "deviceHash";
    String KEY_GATEWAY_DEVICE_ID = "deviceID";
    String KEY_GATEWAY_PLACE_ID = "placeID";

    // places_list
    String KEY_PLACE_LIST = "places_list";
    String KEY_PLACE_PRIM_ID = "id";
    String KEY_PLACE_NAME = "name";
    String KEY_PLACE_NETWORK_KEY = "networkKey";
    String KEY_PLACE_CLOUD_SITE_ID = "cloudSiteID";
    String KEY_PLACE_ICON_ID = "iconID";
    String KEY_PLACE_COLOR = "color";
    String KEY_PLACE_CONTROLLER_ID = "hostControllerID";
    String KEY_PLACE_SETTING_ID = "settingsID";

    // setting_list
    String KEY_SETTING_LIST = "settings_list";
    String KEY_SETTING_PRIM_ID = "id";
    String KEY_SETTING_CURRENT_CONNECTION = "concurrentConnections";
    String KEY_SETTING_LISTENING_MODE = "listeningMode";
    String KEY_SETTING_RETRY_COUNT = "retryCount";
    String KEY_SETTING_RETRY_INTERVAL = "retryInterval";
    String KEY_SETTING_CLOUD_MESH_ID = "cloudMeshId";
    String KEY_SETTING_CLOUD_TENANT_ID = "cloudTenantId";


    /**
     * Get the database as a json format.
     *
     * @return json containing the database.
     */
    public synchronized String getDataBaseAsJson() {
        JSONObject objJson = new JSONObject();
        try {
            // Devices.
            JSONArray jsonDevices = new JSONArray();
            List<CSRDevice> CSRDevices = getAllDevicesList();
            for (int i = 0; i < CSRDevices.size(); i++) {
                CSRDevice CSRDevice = CSRDevices.get(i);

                JSONObject deviceJson = new JSONObject();
                deviceJson.put(KEY_DEVICE_PRIM_ID, CSRDevice.getId());
                deviceJson.put(KEY_DEVICE_ID, CSRDevice.getDeviceID());
                deviceJson.put(KEY_DEVICE_NAME, CSRDevice.getName());
                deviceJson.put(KEY_DEVICE_ASSOCIATED, CSRDevice.isAssociated());
                deviceJson.put(KEY_DEVICE_FAVOURITE, CSRDevice.isFavourite());
                deviceJson.put(KEY_DEVICE_HASH, CSRDevice.getDeviceHash());
                deviceJson.put(KEY_DEVICE_APPEARANCE, CSRDevice.getAppearance());
                deviceJson.put(KEY_DEVICE_MODEL_LOW, CSRDevice.getModelLow());
                deviceJson.put(KEY_DEVICE_MODEL_HIGH, CSRDevice.getModelHigh());
                deviceJson.put(KEY_DEVICE_MODEL, CSRDevice.getModel());
                deviceJson.put(KEY_DEVICE_PLACE_ID, CSRDevice.getPlaceID());
                deviceJson.put(KEY_DEVICE_NUM_GROUPS, CSRDevice.getNumGroups());

                // Array of groups
                JSONArray jsonGroups = new JSONArray();
                int[] groups = CSRDevice.getGroups();
                for (int j = 0 ; j < groups.length; j++) {
                    jsonGroups.put(groups[j]);
                }
                deviceJson.put(KEY_DEVICE_GROUPS, jsonGroups);
                deviceJson.put(KEY_DEVICE_AUTH_CODE, CSRDevice.getAuthCode());
                jsonDevices.put(deviceJson);
            }
            objJson.put(KEY_DEVICES_LIST, jsonDevices);

            // areas
            JSONArray jsonAreas = new JSONArray();
            List<Area> areas = getAllAreaList();
            for (int i = 0; i < areas.size(); i++) {
                Area area = areas.get(i);
                JSONObject areaJson = new JSONObject();
                areaJson.put(KEY_AREAS_PRIM_ID, area.getId());
                areaJson.put(KEY_AREA_NAME, area.getName());
                areaJson.put(KEY_AREA_ID, area.getAreaID());
                areaJson.put(KEY_AREA_PLACE_ID, area.getPlaceID());
                areaJson.put(KEY_AREA_FAVORITE, area.isFavorite());

                jsonAreas.put(areaJson);
            }
            objJson.put(KEY_AREAS_LIST, jsonAreas);

            // Gateways
            JSONArray jsonGateways = new JSONArray();
            List<Gateway> gateways = getAllGatewaysList();
            for (int i = 0; i < gateways.size(); i++) {
                Gateway gateway = gateways.get(i);
                JSONObject gatewayJson = new JSONObject();
                gatewayJson.put(KEY_GATEWAY_PRIM_ID, gateway.getId());
                gatewayJson.put(KEY_GATEWAY_NAME, gateway.getName());
                gatewayJson.put(KEY_GATEWAY_HOST, gateway.getHost());
                gatewayJson.put(KEY_GATEWAY_PORT, gateway.getPort());
                gatewayJson.put(KEY_GATEWAY_UUID, gateway.getUuid());
                gatewayJson.put(KEY_GATEWAY_BASE_PATH, gateway.getBasePath());
                gatewayJson.put(KEY_GATEWAY_STATE, gateway.getState());
                gatewayJson.put(KEY_GATEWAY_DEVICE_HASH, gateway.getDeviceHash());
                gatewayJson.put(KEY_GATEWAY_DEVICE_ID, gateway.getDeviceID());
                gatewayJson.put(KEY_GATEWAY_PLACE_ID, gateway.getPlaceID());

                jsonGateways.put(gatewayJson);
            }
            objJson.put(KEY_GATEWAY_LIST, jsonGateways);

            // places
            JSONArray jsonPlaces = new JSONArray();
            List<Place> places = getAllPlacesList();
            for (int i = 0; i < places.size(); i++) {
                Place place = places.get(i);
                JSONObject placeJson = new JSONObject();
                placeJson.put(KEY_PLACE_PRIM_ID, place.getId());
                placeJson.put(KEY_PLACE_NAME, place.getName());
                placeJson.put(KEY_PLACE_NETWORK_KEY, place.getNetworkKey());
                placeJson.put(KEY_PLACE_CLOUD_SITE_ID, place.getCloudSiteID());
                placeJson.put(KEY_PLACE_ICON_ID, place.getIconID());
                placeJson.put(KEY_PLACE_COLOR, place.getColor());
                placeJson.put(KEY_PLACE_CONTROLLER_ID, place.getHostControllerID());
                placeJson.put(KEY_PLACE_SETTING_ID, place.getSettingsID());

                jsonPlaces.put(placeJson);
            }
            objJson.put(KEY_PLACE_LIST, jsonPlaces);

            // settings
            JSONArray jsonSettings = new JSONArray();
            List<Setting> settings = getAllSettingList();
            for (int i = 0; i < settings.size(); i++) {
                Setting setting = settings.get(i);
                JSONObject settingJson = new JSONObject();
                settingJson.put(KEY_SETTING_PRIM_ID, setting.getId());
                settingJson.put(KEY_SETTING_CURRENT_CONNECTION, setting.getConcurrentConnections());
                settingJson.put(KEY_SETTING_LISTENING_MODE, setting.getListeningMode());
                settingJson.put(KEY_SETTING_RETRY_COUNT, setting.getRetryCount());
                settingJson.put(KEY_SETTING_RETRY_INTERVAL, setting.getRetryInterval());
                settingJson.put(KEY_SETTING_CLOUD_MESH_ID, setting.getCloudMeshId());
                settingJson.put(KEY_SETTING_CLOUD_TENANT_ID, setting.getCloudTenantId());

                jsonSettings.put(settingJson);
            }
            objJson.put(KEY_SETTING_LIST, jsonSettings);

            // timestamp
            Time time = getTime(1);
            objJson.put(KEY_UPDATE_TIME, time.getTime());

        }
        catch (Exception e) {
            return null;
        }

        return objJson.toString();
    }

    /**
     *  get server database attr
     *  update the local database
     * @param valueList
     */
    public void updateLocalDatabase(String valueList) {
        // sync devices data
        syncDeviceData(valueList);
        // sync areas data
        syncAreasData(valueList);
        // sync gateways data
        syncGatewayData(valueList);
        // sync places data
        syncPlacesData(valueList);
        // sync settings data
        syncSettingsData(valueList);
        ToastUtils.show("sync data success!");
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.REFRESH_PAGE));
    }

    private void syncDeviceData(String valueList) {
        List<Integer> serverIds = new ArrayList<>();
        List<Integer> localIds = new ArrayList<>();
        try {
            JSONObject objJson = new JSONObject(valueList);
            LogUtils.i("valueList:" + valueList);
            // device
            JSONArray deviceArray = objJson.getJSONArray(KEY_DEVICES_LIST);
            Log.i("TAG", deviceArray.toString());
            int length = deviceArray.length();

            List<CSRDevice> CSRDevices = getAllDevicesList();
            for (CSRDevice d : CSRDevices) {
                localIds.add(d.getId());
                Log.i(TAG, "localIds: " + d.getId());
            }

            for (int m = 0; m < length; m++) {
                JSONObject deviceObj = deviceArray.getJSONObject(m);
                serverIds.add(deviceObj.getInt(KEY_DEVICE_PRIM_ID));
                Log.i(TAG, "serverIds: " + deviceObj.getInt(KEY_DEVICE_PRIM_ID));
            }

            for (int i = 0; i < serverIds.size(); i++) {
                JSONObject deviceObj = deviceArray.getJSONObject(i);
                Log.i(TAG, "updateLocalDatabase: " + deviceObj.getInt(KEY_DEVICE_PRIM_ID));
                CSRDevice cSRDevice;
                // insert
                if (!localIds.contains(serverIds.get(i))) {
                    cSRDevice = new CSRDevice() {
                        @Override
                        public int getType() {
                            return TYPE_UNKNOWN;
                        }
                    };
                    Log.i("TAG", "insert server data to local database!");
                } else {
                    // update
                    cSRDevice = getDevice(serverIds.get(i));
                    Log.i("TAG", "update local data from server!");
                }
                setDeviceAttr(cSRDevice, deviceObj);
                CSRDevice resultCSRDevice = createOrUpdateDevice(cSRDevice);
                if (resultCSRDevice != null) { sendBroadcase(); }
            }
            // delete local data
            for (int j = 0; j < localIds.size(); j++) {
                // delete local database data
                if (!serverIds.contains(localIds.get(j))) {
                    boolean result = removeDevice(localIds.get(j));
                    if (result) { sendBroadcase(); }
                    Log.i("TAG", "remove local database data!");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncAreasData(String valueList) {
        List<Integer> serverIds = new ArrayList<>();
        List<Integer> localIds = new ArrayList<>();
        try {
            JSONObject objJson = new JSONObject(valueList);
            // device
            JSONArray areasArray = objJson.getJSONArray(KEY_AREAS_LIST);
            Log.i("TAG", areasArray.toString());
            int length = areasArray.length();

            List<Area> ares = getAllAreaList();
            for (Area a : ares) {
                localIds.add(a.getId());
                Log.i(TAG, "localIds: " + a.getId());
            }

            for (int m = 0; m < length; m++) {
                JSONObject areaObj = areasArray.getJSONObject(m);
                serverIds.add(areaObj.getInt(KEY_AREAS_PRIM_ID));
                Log.i(TAG, "serverIds: " + areaObj.getInt(KEY_AREAS_PRIM_ID));
            }

            for (int i = 0; i < serverIds.size(); i++) {
                JSONObject areaObj = areasArray.getJSONObject(i);
                Log.i(TAG, "updateLocalDatabase: " + areaObj.getInt(KEY_AREAS_PRIM_ID));
                // insert
                if (!localIds.contains(serverIds.get(i))) {
                    Area area = new Area();
                    setAreaAttr(area, areaObj);
                    Area resultArea = createOrUpdateArea(area);
                    if (resultArea != null) { sendBroadcase(); }
                    Log.i("TAG", "insert server data to local database!");
                } else {
                    // update
                    Area area = getArea(serverIds.get(i));
                    setAreaAttr(area, areaObj);
                    Area resultArea = createOrUpdateArea(area);
                    if (resultArea != null) { sendBroadcase(); }
                    Log.i("TAG", "update local data from server!");
                }
            }
            // delete local data
            for (int j = 0; j < localIds.size(); j++) {
                // delete local database data
                if (!serverIds.contains(localIds.get(j))) {
                    boolean result = removeArea(localIds.get(j));
                    if (result) { sendBroadcase(); }
                    Log.i("TAG", "remove local database data!");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncGatewayData(String valueList) {
        List<Integer> serverIds = new ArrayList<>();
        List<Integer> localIds = new ArrayList<>();
        try {
            JSONObject objJson = new JSONObject(valueList);
            // device
            JSONArray gatewaysArray = objJson.getJSONArray(KEY_GATEWAY_LIST);
            Log.i("TAG", gatewaysArray.toString());
            int length = gatewaysArray.length();

            List<Gateway> gateways = getAllGatewaysList();
            for (Gateway g : gateways) {
                localIds.add(g.getId());
                Log.i(TAG, "localIds: " + g.getId());
            }

            for (int m = 0; m < length; m++) {
                JSONObject gatewayObj = gatewaysArray.getJSONObject(m);
                serverIds.add(gatewayObj.getInt(KEY_GATEWAY_PRIM_ID));
                Log.i(TAG, "serverIds: " + gatewayObj.getInt(KEY_GATEWAY_PRIM_ID));
            }

            for (int i = 0; i < serverIds.size(); i++) {
                JSONObject gatewayObj = gatewaysArray.getJSONObject(i);
                Log.i(TAG, "updateLocalDatabase: " + gatewayObj.getInt(KEY_GATEWAY_PRIM_ID));
                // insert
                if (!localIds.contains(serverIds.get(i))) {
                    Gateway gateway = new Gateway();
                    setGatewayAttr(gateway, gatewayObj);
                    Gateway resultGateway = createOrUpdateGateway(gateway);
                    if (resultGateway != null) { sendBroadcase(); }
                    Log.i("TAG", "insert server data to local database!");
                } else {
                    // update
                    Gateway gateway = getGateway(serverIds.get(i));
                    setGatewayAttr(gateway, gatewayObj);
                    Gateway resultGateway = createOrUpdateGateway(gateway);
                    if (resultGateway != null) { sendBroadcase(); }
                    Log.i("TAG", "update local data from server!");
                }
            }
            // delete local data
            for (int j = 0; j < localIds.size(); j++) {
                // delete local database data
                if (!serverIds.contains(localIds.get(j))) {
                    boolean result = removeGateway(localIds.get(j));
                    if (result) { sendBroadcase(); }
                    Log.i("TAG", "remove local database data!");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncPlacesData(String valueList) {
        List<Integer> serverIds = new ArrayList<>();
        List<Integer> localIds = new ArrayList<>();
        try {
            JSONObject objJson = new JSONObject(valueList);
            // device
            JSONArray placesArray = objJson.getJSONArray(KEY_PLACE_LIST);
            Log.i("TAG", placesArray.toString());
            int length = placesArray.length();

            List<Place> places = getAllPlacesList();
            for (Place p : places) {
                localIds.add(p.getId());
                Log.i(TAG, "localIds: " + p.getId());
            }

            for (int m = 0; m < length; m++) {
                JSONObject placeObj = placesArray.getJSONObject(m);
                serverIds.add(placeObj.getInt(KEY_PLACE_PRIM_ID));
                Log.i(TAG, "serverIds: " + placeObj.getInt(KEY_PLACE_PRIM_ID));
            }

            for (int i = 0; i < serverIds.size(); i++) {
                JSONObject placeObj = placesArray.getJSONObject(i);
                Log.i(TAG, "updateLocalDatabase: " + placeObj.getInt(KEY_PLACE_PRIM_ID));
                // insert
                if (!localIds.contains(serverIds.get(i))) {
                    Place place = new Place();
                    setPlaceAttr(place, placeObj);
                    Place resultPlace = createOrUpdatePlace(place);
                    if (resultPlace != null) { sendBroadcase(); }
                    Log.i("TAG", "insert server data to local database!");
                } else {
                    // update
                    Place place = getPlace(serverIds.get(i));
                    setPlaceAttr(place, placeObj);
                    Place resultPlace = createOrUpdatePlace(place);
                    if (resultPlace != null) { sendBroadcase(); }
                    Log.i("TAG", "update local data from server!");
                }
            }
            // delete local data
            for (int j = 0; j < localIds.size(); j++) {
                // delete local database data
                if (!serverIds.contains(localIds.get(j))) {
                    boolean result = removePlace(localIds.get(j));
                    if (result) { sendBroadcase(); }
                    Log.i("TAG", "remove local database data!");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncSettingsData(String valueList) {
        List<Integer> serverIds = new ArrayList<>();
        List<Integer> localIds = new ArrayList<>();
        try {
            JSONObject objJson = new JSONObject(valueList);
            // device
            JSONArray settingsArray = objJson.getJSONArray(KEY_SETTING_LIST);
            Log.i("TAG", settingsArray.toString());
            int length = settingsArray.length();

            List<Setting> settings = getAllSettingList();
            for (Setting s : settings) {
                localIds.add(s.getId());
                Log.i(TAG, "localIds: " + s.getId());
            }

            for (int m = 0; m < length; m++) {
                JSONObject settingObj = settingsArray.getJSONObject(m);
                serverIds.add(settingObj.getInt(KEY_SETTING_PRIM_ID));
                Log.i(TAG, "serverIds: " + settingObj.getInt(KEY_SETTING_PRIM_ID));
            }

            for (int i = 0; i < serverIds.size(); i++) {
                JSONObject settingObj = settingsArray.getJSONObject(i);
                Log.i(TAG, "updateLocalDatabase: " + settingObj.getInt(KEY_SETTING_PRIM_ID));
                // insert
                if (!localIds.contains(serverIds.get(i))) {
                    Setting setting = new Setting();
                    setSettingAttr(setting, settingObj);
                    Setting resultSetting = createOrUpdateSetting(setting);
                    if (resultSetting != null) { sendBroadcase(); }
                    Log.i("TAG", "insert server data to local database!");
                } else {
                    // update
                    Setting setting = getSetting(serverIds.get(i));
                    setSettingAttr(setting, settingObj);
                    Setting resultSetting = createOrUpdateSetting(setting);
                    if (resultSetting != null) { sendBroadcase(); }
                    Log.i("TAG", "update local data from server!");
                }
            }
            // delete local data
            for (int j = 0; j < localIds.size(); j++) {
                // delete local database data
                if (!serverIds.contains(localIds.get(j))) {
                    boolean result = removeSetting(localIds.get(j));
                    if (result) { sendBroadcase(); }
                    Log.i("TAG", "remove local database data!");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcase() {
        Log.i("TAG", "send broadCase");
        Intent intent = new Intent(AxalentUtils.CAST_FIFTER);
        intent.putExtra("action", AxalentUtils.UPDATE_PAGE);
        context.sendBroadcast(intent);
    }

    private void setDeviceAttr(CSRDevice CSRDevice, JSONObject deviceObj) {
        try {
            CSRDevice.setAppearance(deviceObj.getInt(KEY_DEVICE_APPEARANCE));
            CSRDevice.setAssociated(deviceObj.getBoolean(KEY_DEVICE_ASSOCIATED));
            CSRDevice.setAuthCode(deviceObj.getLong(KEY_DEVICE_AUTH_CODE));
            CSRDevice.setDeviceHash(deviceObj.getInt(KEY_DEVICE_HASH));
            CSRDevice.setDeviceID(deviceObj.getInt(KEY_DEVICE_ID));
            CSRDevice.setFavourite(deviceObj.getBoolean(KEY_DEVICE_FAVOURITE));
            CSRDevice.setModel(deviceObj.getInt(KEY_DEVICE_MODEL));
            CSRDevice.setModelHigh(deviceObj.getLong(KEY_DEVICE_MODEL_HIGH));
            CSRDevice.setModelLow(deviceObj.getLong(KEY_DEVICE_MODEL_LOW));
            CSRDevice.setName(deviceObj.getString(KEY_DEVICE_NAME));
            CSRDevice.setPlaceID(deviceObj.getInt(KEY_DEVICE_PLACE_ID));
            CSRDevice.setNumGroups(deviceObj.getInt(KEY_DEVICE_NUM_GROUPS));

            JSONArray groupsArray = deviceObj.getJSONArray(KEY_DEVICE_GROUPS);
            int[] groups = new int[groupsArray.length()];
            for (int m = 0; m < groupsArray.length(); m++) {
                groups[m] = (int)groupsArray.get(m);
            }
            CSRDevice.setGroups(groups);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAreaAttr(Area area, JSONObject areaObj) {
        try {
            area.setName(areaObj.getString(KEY_AREA_NAME));
            area.setAreaID(areaObj.getInt(KEY_AREA_ID));
            area.setIsFavorite(areaObj.getBoolean(KEY_AREA_FAVORITE));
            area.setPlaceID(areaObj.getInt(KEY_AREA_PLACE_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setGatewayAttr(Gateway gateway, JSONObject gatewayObj) {
        try {
            gateway.setName(gatewayObj.getString(KEY_GATEWAY_NAME));
            gateway.setBasePath(gatewayObj.getString(KEY_GATEWAY_BASE_PATH));
            gateway.setPlaceID(gatewayObj.getInt(KEY_GATEWAY_PLACE_ID));
            gateway.setDeviceHash(gatewayObj.getInt(KEY_GATEWAY_DEVICE_HASH));
            gateway.setDeviceID(gatewayObj.getInt(KEY_GATEWAY_DEVICE_ID));
            gateway.setHost(gatewayObj.getString(KEY_GATEWAY_HOST));
            gateway.setPort(gatewayObj.getString(KEY_GATEWAY_PORT));
            gateway.setState(gatewayObj.getInt(KEY_GATEWAY_STATE));
            gateway.setUuid(gatewayObj.getString(KEY_GATEWAY_UUID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPlaceAttr(Place place, JSONObject placeObj) {
        try {
            place.setName(placeObj.getString(KEY_PLACE_NAME));
            place.setCloudSiteID(placeObj.getString(KEY_PLACE_CLOUD_SITE_ID));
            place.setColor(placeObj.getLong(KEY_PLACE_COLOR));
            place.setHostControllerID(placeObj.getInt(KEY_PLACE_CONTROLLER_ID));
            place.setIconID(placeObj.getInt(KEY_PLACE_ICON_ID));
            place.setNetworkKey(placeObj.getString(KEY_PLACE_NETWORK_KEY));
            place.setSettingsID(placeObj.getInt(KEY_PLACE_SETTING_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSettingAttr(Setting setting, JSONObject settingObj) {
        try {
            setting.setCloudMeshId(settingObj.getString(KEY_SETTING_CLOUD_MESH_ID));
            setting.setCloudTenantId(settingObj.getString(KEY_SETTING_CLOUD_TENANT_ID));
            setting.setConcurrentConnections(settingObj.getInt(KEY_SETTING_CURRENT_CONNECTION));
            setting.setListeningMode(settingObj.getInt(KEY_SETTING_LISTENING_MODE));
            setting.setRetryCount(settingObj.getInt(KEY_SETTING_RETRY_COUNT));
            setting.setRetryInterval(settingObj.getInt(KEY_SETTING_RETRY_INTERVAL));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a list of Area objects from JSON.
     * @param json JSON string containing the description of the areas.
     * @param placeId The place id to use for the areas.
     * @return A list of Area objects.
     */
    public synchronized List<Area> getListAreasByJson(String json, int placeId) {
        List<Area> areas = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(json);

            JSONArray jsonArray = jsonObj.getJSONArray(KEY_AREAS_LIST);

            for (int areasIndex = 0; areasIndex < jsonArray.length(); areasIndex++) {
                Area area = new Area();

                String areaName = jsonArray.getJSONObject(areasIndex).getString(KEY_AREA_NAME);
                area.setName(areaName);

                int areaId = jsonArray.getJSONObject(areasIndex).getInt(KEY_AREA_ID);
                area.setAreaID(areaId);

                area.setPlaceID(placeId);

                areas.add(area);
            }
        }
        catch (JSONException e) {
            return null;
        }

        return areas;
    }

    /**
     * Create a list of Device objects from JSON.
     * @param json JSON string containing the description of the devices.
     * @param placeId The place id to use for the devices.
     * @return A list of Device objects.
     */
    public synchronized List<CSRDevice> getListDevicesByJson(String json, int placeId) {
        List<CSRDevice> CSRDevices = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(json);

            JSONArray jsonArray = jsonObj.getJSONArray(KEY_DEVICES_LIST);

            for (int devicesIndex = 0; devicesIndex < jsonArray.length(); devicesIndex++) {

                UnknownCSRDevice device = new UnknownCSRDevice();

                int deviceId = jsonArray.getJSONObject(devicesIndex).getInt(KEY_DEVICE_ID);
                device.setDeviceID(deviceId);

                String deviceName = jsonArray.getJSONObject(devicesIndex).getString(KEY_DEVICE_NAME);
                device.setName(deviceName);

                int hash = jsonArray.getJSONObject(devicesIndex).getInt(KEY_DEVICE_HASH);
                device.setDeviceHash(hash);

                int appearance = jsonArray.getJSONObject(devicesIndex).getInt(KEY_DEVICE_APPEARANCE);
                device.setAppearance(appearance);

                long modelLow = jsonArray.getJSONObject(devicesIndex).getLong(KEY_DEVICE_MODEL_LOW);
                device.setModelLow(modelLow);

                long modelHigh = jsonArray.getJSONObject(devicesIndex).getLong(KEY_DEVICE_MODEL_HIGH);
                device.setModelHigh(modelHigh);

                int numGroups = jsonArray.getJSONObject(devicesIndex).getInt(KEY_DEVICE_NUM_GROUPS);
                device.setNumGroups(numGroups);

                JSONArray jsonGroupsArray = jsonArray.getJSONObject(devicesIndex).getJSONArray(KEY_DEVICE_GROUPS);
                if (jsonGroupsArray != null) {
                    int[] groups = new int[jsonGroupsArray.length()];

                    for (int i = 0; i < jsonGroupsArray.length(); i++) {
                        groups[i] = jsonGroupsArray.getInt(i);
                    }
                    device.setGroups(groups);
                }


                /*byte[] uuid = Base64.decode(jsonArray.getJSONObject(devicesIndex).getString(KEY_DEVICE_UUID), Base64.DEFAULT);
                device.setUuid(uuid);*/

                long authCode = jsonArray.getJSONObject(devicesIndex).getLong(KEY_DEVICE_AUTH_CODE);
                device.setAuthCode(authCode);

                device.setPlaceID(placeId);

                CSRDevices.add(device);
            }


        }
        catch (JSONException e) {
            return null;
        }

        return CSRDevices;
    }

    public synchronized CSRDevice createOrUpdateDevice(CSRDevice CSRDevice) {

        open();

        boolean response = false;
        CSRDevice updatedCSRDevice = null;

        try {
            if ((CSRDevice.getId() != -1) && (getDevice(CSRDevice.getId()) != null)) {
                String queryString = context.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(CSRDevice.getId())};

                response = executeUpdate(TableDevices.TABLE_NAME, TableDevices.createContentValues(CSRDevice), queryString, queryParameters);
                if (response) {
                    updatedCSRDevice = getDevice(CSRDevice.getId());
                }

            }
            else {

                long idDeviceCreated = executeCreate(TableDevices.TABLE_NAME, TableDevices.createContentValues(CSRDevice));
                if (idDeviceCreated >= 0) {
                    updatedCSRDevice = getDevice(idDeviceCreated);
                }
            }

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return updatedCSRDevice;
    }


    public synchronized boolean removeDevice(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = context.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableDevices.TABLE_NAME, queryString, queryParameters);

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return response;
    }

    public synchronized boolean removeAllDevicesByPlaceId(int placeId) {

        open();

        boolean response = false;
        try {
            String queryString = context.getString(R.string.sql_where_clause_place_id);
            String[] queryParameters = new String[]{String.valueOf(placeId)};

            response = executeDelete(TableDevices.TABLE_NAME, queryString, queryParameters);

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // AREA
    ///////////////////////////////////////////////////////////////////////////////////////////////


    public synchronized int getNewAreaId() {
        int lastIdUsed = 0;
        List<Area> areas = getAllAreaList();
        for (int i = 0; i < areas.size(); i++) {
            lastIdUsed = Math.max(lastIdUsed, areas.get(i).getAreaID());
        }
        lastIdUsed++;

        return lastIdUsed;
    }

    private Cursor getAllAreasCursor() {

        String sortOrder = TableAreas._ID + " ASC";

//        String queryString = context.getString(R.string.sql_where_clause_place_id);
//        String[] queryParameters = new String[]{String.valueOf(Utils.getLatestPlaceIdUsed(context))};

        try {
            return executeSelect(TableAreas.TABLE_NAME, TableAreas.getColumnsNames(), null, null, sortOrder);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Area> getAllAreaList() {

        open();

        List<Area> areaList = new ArrayList<>();

        Cursor cursor = getAllAreasCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Area area = TableAreas.getAreaFromCursor(cursor);
                areaList.add(area);
            }
            cursor.close();
        }
        close();

        return areaList;
    }


    private Cursor getAreaCursor(final long id) {

        String queryString = context.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableAreas.TABLE_NAME, TableAreas.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Area getArea(final long id) {

        open();

        Area area = null;
        Cursor cursor = getAreaCursor(id);

        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                area = TableAreas.getAreaFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return area;
    }

    private Cursor getAreasOfPlaceWithIdCursor(final long placeID) {

        String queryString = context.getString(R.string.sql_where_clause_place_id);
        String[] queryParameters = new String[]{String.valueOf(placeID)};

        try {
            return executeSelect(TableAreas.TABLE_NAME, TableAreas.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Area> getAreasOfPlaceWithId(final long placeID) {

        open();

        List<Area> areaList = new ArrayList<>();

        Cursor cursor = getAreasOfPlaceWithIdCursor(placeID);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Area area = TableAreas.getAreaFromCursor(cursor);
                areaList.add(area);
            }
            cursor.close();
        }
        close();

        return areaList;
    }

    private Cursor getAreasByGroupIdCursor(final long areaId) {

        String queryString = context.getString(R.string.sql_where_clause_area_id);
//        String[] queryParameters = new String[]{String.valueOf(areaId), String.valueOf(Utils.getLatestPlaceIdUsed(context))};

//        try {
//            return executeSelect(TableAreas.TABLE_NAME, TableAreas.getColumnsNames(), queryString, queryParameters, null);
//        }
//        catch (SQLException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
        return null;
    }

    public synchronized List<Area> getAreasbyGroupsId(List<Integer> ids) {
        open();

        List<Area> areaList = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            Cursor cursor = getAreasByGroupIdCursor(ids.get(i));

            if (cursor != null && cursor.moveToNext()) {
                Area area = TableAreas.getAreaFromCursor(cursor);
                areaList.add(area);
                cursor.close();
            }
            close();
        }
        return areaList;
    }

    public synchronized Area createOrUpdateArea(Area area) {

        open();

        boolean response = false;
        Area updatedArea = null;

        try {
            if ((area.getId() != -1) && (getArea(area.getId()) != null)) {
                String queryString = context.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(area.getId())};

                response = executeUpdate(TableAreas.TABLE_NAME, TableAreas.createContentValues(area), queryString, queryParameters);
                if (response) {
                    updatedArea = getArea(area.getId());
                }

            }
            else {

                long idAreaCreated = executeCreate(TableAreas.TABLE_NAME, TableAreas.createContentValues(area));
                if (idAreaCreated >= 0) {
                    updatedArea = getArea(idAreaCreated);
                }
            }

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return updatedArea;
    }

    public synchronized boolean removeArea(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = context.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableAreas.TABLE_NAME, queryString, queryParameters);

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return response;
    }

    public synchronized boolean removeAreaOfPlaceWithId(final int placeID) {

        open();

        boolean response = false;
        try {
            String queryString = context.getString(R.string.sql_where_clause_place_id);
            String[] queryParameters = new String[]{String.valueOf(placeID)};

            response = executeDelete(TableAreas.TABLE_NAME, queryString, queryParameters);

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        return response;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // PLACE
    ///////////////////////////////////////////////////////////////////////////////////////////////


    private Cursor getAllPlacesCursor() {

        String sortOrder = TablePlaces._ID + " ASC";

        try {
            return executeSelect(TablePlaces.TABLE_NAME, TablePlaces.getColumnsNames(), null, null, sortOrder);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Place> getAllPlacesList() {

        open();

        List<Place> placeList = new ArrayList<>();

        Cursor cursor = getAllPlacesCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Place place = TablePlaces.getPlaceFromCursor(cursor);
                placeList.add(place);
            }
            cursor.close();
        }
        close();

        return placeList;
    }


    private Cursor getPlaceCursor(final long id) {

        String queryString = context.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TablePlaces.TABLE_NAME, TablePlaces.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Place getPlace(final long id) {

        open();

        Place place = null;
        Cursor cursor = getPlaceCursor(id);

        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE Place STATES
                place = TablePlaces.getPlaceFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return place;
    }


    public synchronized Place createOrUpdatePlace(Place place) {

        open();

        boolean response = false;
        Place updatedPlace = null;

        try {
            if ((place.getId() != -1) && (getPlace(place.getId()) != null)) {
                String queryString = context.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(place.getId())};

                response = executeUpdate(TablePlaces.TABLE_NAME, TablePlaces.createContentValues(place), queryString, queryParameters);
                if (response) {
                    updatedPlace = getPlace(place.getId());
                }

            }
            else {

                long idPlaceCreated = executeCreate(TablePlaces.TABLE_NAME, TablePlaces.createContentValues(place));
                if (idPlaceCreated >= 0) {
                    updatedPlace = getPlace(idPlaceCreated);
                }
            }

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return updatedPlace;
    }


    public synchronized boolean removePlace(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = context.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TablePlaces.TABLE_NAME, queryString, queryParameters);

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return response;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // SETTING
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Cursor getAllSettingsCursor() {

        String sortOrder = TableSettings._ID + " ASC";

        try {
            return executeSelect(TableSettings.TABLE_NAME, TableSettings.getColumnsNames(), null, null, sortOrder);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Setting> getAllSettingList() {

        open();

        List<Setting> settingsList = new ArrayList<>();

        Cursor cursor = getAllSettingsCursor();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Setting setting = TableSettings.getSettingFromCursor(cursor);
                settingsList.add(setting);
            }
            cursor.close();
        }
        close();

        return settingsList;
    }


    private Cursor getSettingCursor(final long id) {

        String queryString = context.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableSettings.TABLE_NAME, TableSettings.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Setting getSetting(final long id) {

        open();

        Setting setting = null;
        Cursor cursor = getSettingCursor(id);

        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                setting = TableSettings.getSettingFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return setting;
    }

    public synchronized Setting getSettingByFacebookId(String id) {

        //TODO use Facebook id.
        open();

        Setting setting = null;
        Cursor cursor = getAllSettingsCursor();

        if (cursor != null) {
            cursor.moveToFirst();
            setting = TableSettings.getSettingFromCursor(cursor);
            cursor.close();
        }
        close();

        return setting;
    }

    private Cursor getSettingsOfPlaceWithIdCursor(final long placeID) {

        String queryString = context.getString(R.string.sql_where_clause_place_id);
        String[] queryParameters = new String[]{String.valueOf(placeID)};

        try {
            return executeSelect(TableSettings.TABLE_NAME, TableSettings.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized List<Setting> getSettingsOfPlaceWithId(final long placeID) {

        open();

        List<Setting> settingList = new ArrayList<>();

        Cursor cursor = getSettingsOfPlaceWithIdCursor(placeID);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Setting setting = TableSettings.getSettingFromCursor(cursor);
                settingList.add(setting);
            }
            cursor.close();
        }
        close();

        return settingList;
    }


    public synchronized Setting createOrUpdateSetting(Setting setting) {

        open();

        boolean response = false;
        Setting updatedSetting = null;

        try {
            if ((setting.getId() != -1) && (getSetting(setting.getId()) != null)) {
                String queryString = context.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(setting.getId())};

                response = executeUpdate(TableSettings.TABLE_NAME, TableSettings.createContentValues(setting), queryString, queryParameters);
                if (response) {
                    updatedSetting = getSetting(setting.getId());
                }

            }
            else {

                long idSettingCreated = executeCreate(TableSettings.TABLE_NAME, TableSettings.createContentValues(setting));
                if (idSettingCreated >= 0) {
                    updatedSetting = getSetting(idSettingCreated);
                }
            }

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return updatedSetting;
    }

    public synchronized boolean removeSetting(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = context.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableSettings.TABLE_NAME, queryString, queryParameters);

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return response;
    }

    public synchronized boolean removeSettingOfPlaceWithId(final int placeID) {

        open();

        boolean response = false;
        try {
            String queryString = context.getString(R.string.sql_where_clause_place_id);
            String[] queryParameters = new String[]{String.valueOf(placeID)};

            response = executeDelete(TableSettings.TABLE_NAME, queryString, queryParameters);

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        return response;
    }







    ///////////////////////////////////////////////////////////////////////////////////////////////
    // GATEWAYS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Cursor getAllGatewaysIDsCursor(final boolean justFromSelectedPlace) {

//        String sortOrder = TableGateways.COLUMN_NAME_DEVICE_ID + " ASC";
//        String[] columns = {
//                TableGateways.COLUMN_NAME_DEVICE_ID
//        };
//
//        String queryString = justFromSelectedPlace ? context.getString(R.string.sql_where_clause_place_id) : null;
//        String[] queryParameters = justFromSelectedPlace ? new String[]{String.valueOf(Utils.getLatestPlaceIdUsed(context))} : null;
//
//        try {
//            return executeSelect(TableGateways.TABLE_NAME, columns, queryString, queryParameters, sortOrder);
//        }
//        catch (SQLException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
        return null;
    }

    public synchronized List<Integer> getAllGatewaysIDsList() {

        return getAllGatewaysIDsList(false);
    }

    public synchronized List<Integer> getAllGatewaysIDsFromCurrentPlace() {

        return getAllGatewaysIDsList(true);
    }

    private List<Integer> getAllGatewaysIDsList(final boolean justFromSelectedPlace) {

        open();

        List<Integer> idsList = new ArrayList<>();
        Cursor cursor = getAllGatewaysIDsCursor(justFromSelectedPlace);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                int deviceID = cursor.getInt(cursor.getColumnIndexOrThrow(TableGateways.COLUMN_NAME_DEVICE_ID));
                idsList.add(deviceID);
            }
            cursor.close();
        }
        close();

        return idsList;
    }


    private Cursor getAllGatewaysCursor(final boolean justFromSelectedPlace) {

//        String sortOrder = TableGateways._ID + " ASC";
//
//        String queryString = justFromSelectedPlace ? context.getString(R.string.sql_where_clause_place_id) : null;
//        String[] queryParameters = justFromSelectedPlace ? new String[]{String.valueOf(Utils.getLatestPlaceIdUsed(context))} : null;
//
//        try {
//            return executeSelect(TableGateways.TABLE_NAME, TableGateways.getColumnsNames(), queryString, queryParameters, sortOrder);
//        }
//        catch (SQLException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
        return null;
    }


    public synchronized List<Gateway> getAllGatewaysList() {

        return getAllGatewaysList(false);
    }

    public synchronized List<Gateway> getAllGatewaysFromCurrentPlace() {

        return getAllGatewaysList(true);
    }

    private List<Gateway> getAllGatewaysList(final boolean justFromSelectedPlace) {

        open();

        List<Gateway> gatewaysList = new ArrayList<>();

        Cursor cursor = getAllGatewaysCursor(justFromSelectedPlace);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Gateway gateway = TableGateways.getGatewayFromCursor(cursor);
                gatewaysList.add(gateway);
            }
            cursor.close();
        }
        close();

        gatewaysList = ApplicationUtils.sortGatewaysListAlphabetically(gatewaysList);

        return gatewaysList;
    }


    private Cursor getGatewayCursor(final long id) {

        String queryString = context.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableGateways.TABLE_NAME, TableGateways.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private Cursor getTimeCursor(final long id) {

        String queryString = context.getString(R.string.sql_where_clause_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableLastTime.TABLE_NAME, TableLastTime.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Gateway getGateway(final long id) {

        open();

        Gateway gateway = null;
        Cursor cursor = getGatewayCursor(id);


        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE Gateway STATES
                gateway = TableGateways.getGatewayFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return gateway;
    }

    private Cursor getGatewayWithUUIDCursor(final String uuid) {

        String queryString = context.getString(R.string.sql_where_clause_uuid);
        String[] queryParameters = new String[]{String.valueOf(uuid)};

        try {
            return executeSelect(TableGateways.TABLE_NAME, TableGateways.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Gateway getSelectedGateway() {

        open();

        Gateway gateway = null;
        Cursor cursor = getGatewayWithUUIDCursor(MeshLibraryManager.getInstance().getSelectedGatewayUUID());


        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE Gateway STATES
                gateway = TableGateways.getGatewayFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return gateway;
    }

    private Cursor getGatewayWithDeviceIdCursor(final long id) {

        String queryString = context.getString(R.string.sql_where_clause_device_id);
        String[] queryParameters = new String[]{String.valueOf(id)};

        try {
            return executeSelect(TableGateways.TABLE_NAME, TableGateways.getColumnsNames(), queryString, queryParameters, null);
        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public synchronized Gateway getGatewayWithDeviceId(final long id) {

        open();

        Gateway gateway = null;
        Cursor cursor = getGatewayWithDeviceIdCursor(id);


        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE Gateway STATES
                gateway = TableGateways.getGatewayFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return gateway;
    }



    public synchronized Gateway createOrUpdateGateway(Gateway gateway) {

        open();

        boolean response = false;
        Gateway updatedGateway = null;

        try {
            if ((gateway.getId() != -1) && (getGateway(gateway.getId()) != null)) {
                String queryString = context.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(gateway.getId())};

                response = executeUpdate(TableGateways.TABLE_NAME, TableGateways.createContentValues(gateway), queryString, queryParameters);
                if (response) {
                    updatedGateway = getGateway(gateway.getId());
                }

            }
            else {

                long idGatewayCreated = executeCreate(TableGateways.TABLE_NAME, TableGateways.createContentValues(gateway));
                if (idGatewayCreated >= 0) {
                    updatedGateway = getGateway(idGatewayCreated);
                }
            }

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return updatedGateway;
    }


    public synchronized boolean removeGateway(final int id) {

        open();

        boolean response = false;
        try {
            String queryString = context.getString(R.string.sql_where_clause_id);
            String[] queryParameters = new String[]{String.valueOf(id)};

            response = executeDelete(TableGateways.TABLE_NAME, queryString, queryParameters);

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        RxBus.getDefaultInstance().post(new MeshResponseEvent(MeshResponseEvent.ResponseEvent.DATABASE_UPDATE));
        return response;
    }

    public synchronized boolean removeAllGatewaysByPlaceId(int placeId) {

        open();

        boolean response = false;
        try {
            String queryString = context.getString(R.string.sql_where_clause_place_id);
            String[] queryParameters = new String[]{String.valueOf(placeId)};

            response = executeDelete(TableGateways.TABLE_NAME, queryString, queryParameters);

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();
        createOrUpdateTime(AxalentUtils.getCurrentTimeMillis());
        return response;
    }

    public synchronized Time getTime(final long id) {

        open();

        Time time = null;
        Cursor cursor = getTimeCursor(id);

        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();

                // RETRIEVE Place STATES
                time = TableLastTime.getTimeFromCursor(cursor);
            }
            cursor.close();
        }
        close();

        return time;
    }

    // update or create time
    public synchronized Time createOrUpdateTime(Time time) {

        open();

        boolean response = false;
        Time updatedTime = null;

        try {
            if ((time.getId() != -1) && (getTime(time.getId()) != null)) {
                String queryString = context.getString(R.string.sql_where_clause_id);
                String[] queryParameters = new String[]{String.valueOf(time.getId())};

                response = executeUpdate(TableLastTime.TABLE_NAME, TableLastTime.createContentValues(time), queryString, queryParameters);
                if (response) {
                    updatedTime = getTime(time.getId());
                }

            }
            else {

                long idTimeCreated = executeCreate(TableLastTime.TABLE_NAME, TableLastTime.createContentValues(time));
                if (idTimeCreated >= 0) {
                    updatedTime = getTime(idTimeCreated);
                }
            }

        }
        catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        close();

        return updatedTime;
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Primary DB operations
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param sql
     * @throws SQLException
     */
    private void executeSql(String sql) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }
        mDatabase.execSQL(sql);
    }

    /**
     * @param sql
     * @param whereArgs
     * @return
     * @throws SQLException
     */
    private Cursor executeSelectQuery(String sql, String[] whereArgs) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        Cursor cursor = mDatabase.rawQuery(sql, whereArgs);

        return cursor;
    }

    /**
     * @param tableName
     * @param columns
     * @param whereClause
     * @param whereArgs
     * @param orderBy
     * @return
     * @throws SQLException
     */
    private Cursor executeSelect(String tableName, String[] columns, String whereClause, String[] whereArgs, String orderBy) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        Cursor cursor = mDatabase.query(tableName, columns, whereClause, whereArgs, null, null, orderBy);

        return cursor;
    }

    /**
     * @param tableName
     * @param contentValues
     * @return
     * @throws SQLException
     */
    private long executeCreate(String tableName, ContentValues contentValues) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        long rowId = mDatabase.insert(tableName, null, contentValues);

        return rowId;
    }

    /**
     * @param tableName
     * @param contentValues
     * @return
     * @throws SQLException
     */
    private boolean executeReplace(String tableName, ContentValues contentValues) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        long rowId = mDatabase.replace(tableName, null, contentValues);

        return (rowId > 0);
    }

    /**
     * @param tableName
     * @param contentValues
     * @param whereClause
     * @param whereArgs
     * @return
     * @throws SQLException
     */
    private boolean executeUpdate(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        long numberOfRowsAffected = mDatabase.update(tableName, contentValues, whereClause, whereArgs);

        return (numberOfRowsAffected > 0);
    }

    /**
     * @param tableName
     * @param whereClause
     * @param whereArgs
     * @return
     * @throws SQLException
     */
    private boolean executeDelete(String tableName, String whereClause, String[] whereArgs) throws SQLException {

        if (!mDatabase.isOpen()) {
            mDatabase = mDBHelper.getWritableDatabase();
        }

        long numberOfRowsAffected = mDatabase.delete(tableName, whereClause, whereArgs);

        return (numberOfRowsAffected > 0);
    }

    private void open() throws SQLException {
        mDatabase = mDBHelper.getWritableDatabase();
    }

    private void close() {
        mDBHelper.close();
    }
}

