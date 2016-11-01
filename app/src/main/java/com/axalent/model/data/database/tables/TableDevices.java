/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package com.axalent.model.data.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.axalent.model.data.model.devices.CSRDevice;
import com.axalent.model.data.model.devices.DeviceFactory;
import com.axalent.view.material.MaterialUtils;

/**
 *
 */
public class TableDevices implements BaseColumns {

    public static final String TABLE_NAME = "devices";

    public static final String COLUMN_NAME_DEVICE_HASH = "deviceHash";
    public static final String COLUMN_NAME_DEVICE_ID = "deviceID";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_APPEARANCE = "appearance";
    public static final String COLUMN_NAME_MODEL_HIGH = "modelHigh";
    public static final String COLUMN_NAME_MODEL_LOW = "modelLow";
    public static final String COLUMN_NAME_N_GROUPS = "nGroups";
    public static final String COLUMN_NAME_GROUPS = "groups";
    public static final String COLUMN_NAME_UUID = "uuid";
    public static final String COLUMN_NAME_AUTH_CODE = "authCode";
    public static final String COLUMN_NAME_MODEL = "model";
    public static final String COLUMN_NAME_PLACE_ID = "placeID";
    public static final String COLUMN_NAME_IS_FAVOURITE = "isFavourite";
    public static final String COLUMN_NAME_IS_ASSOCIATED = "isAssociated";


    public static String[] getColumnsNames() {

        String[] columns = {
                TableDevices._ID,
                TableDevices.COLUMN_NAME_DEVICE_HASH,
                TableDevices.COLUMN_NAME_DEVICE_ID,
                TableDevices.COLUMN_NAME_NAME,
                TableDevices.COLUMN_NAME_APPEARANCE,
                TableDevices.COLUMN_NAME_MODEL_HIGH,
                TableDevices.COLUMN_NAME_MODEL_LOW,
                TableDevices.COLUMN_NAME_N_GROUPS,
                TableDevices.COLUMN_NAME_GROUPS,
                TableDevices.COLUMN_NAME_UUID,
                TableDevices.COLUMN_NAME_AUTH_CODE,
                TableDevices.COLUMN_NAME_MODEL,
                TableDevices.COLUMN_NAME_PLACE_ID,
                TableDevices.COLUMN_NAME_IS_FAVOURITE,
                TableDevices.COLUMN_NAME_IS_ASSOCIATED
        };

        return columns;
    }


    public static ContentValues createContentValues(CSRDevice CSRDevice) {

        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TableDevices.COLUMN_NAME_DEVICE_HASH, CSRDevice.getDeviceHash());
        values.put(TableDevices.COLUMN_NAME_DEVICE_ID, CSRDevice.getDeviceID());
        values.put(TableDevices.COLUMN_NAME_NAME, CSRDevice.getName());
        values.put(TableDevices.COLUMN_NAME_APPEARANCE, CSRDevice.getAppearance());
        values.put(TableDevices.COLUMN_NAME_MODEL_HIGH, CSRDevice.getModelHigh());
        values.put(TableDevices.COLUMN_NAME_MODEL_LOW, CSRDevice.getModelLow());
        values.put(TableDevices.COLUMN_NAME_N_GROUPS, CSRDevice.getNumGroups());
        values.put(TableDevices.COLUMN_NAME_GROUPS, CSRDevice.getGroupsByteArray());
        values.put(TableDevices.COLUMN_NAME_UUID, CSRDevice.getUuid());
        values.put(TableDevices.COLUMN_NAME_AUTH_CODE, CSRDevice.getAuthCode());
        values.put(TableDevices.COLUMN_NAME_MODEL, CSRDevice.getModel());
        values.put(TableDevices.COLUMN_NAME_PLACE_ID, CSRDevice.getPlaceID());
        values.put(TableDevices.COLUMN_NAME_IS_FAVOURITE, CSRDevice.isFavourite());
        values.put(TableDevices.COLUMN_NAME_IS_ASSOCIATED, CSRDevice.isAssociated());

        return values;
    }

    public static CSRDevice getDeviceFromCursor(Cursor cursor) {

        CSRDevice CSRDevice = null;

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices._ID));
        int deviceHash = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_DEVICE_HASH));
        int deviceID = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_DEVICE_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_NAME));
        int appearance = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_APPEARANCE));
        int modelHigh = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_MODEL_HIGH));
        int modelLow = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_MODEL_LOW));
        int numberGroups = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_N_GROUPS));
        byte[] groups = cursor.getBlob(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_GROUPS));
        byte[] uuid = cursor.getBlob(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_UUID));
        long authCode = cursor.getLong(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_AUTH_CODE));
        int model = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_MODEL));
        int placeID = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_PLACE_ID));
        int favouriteValue = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_IS_FAVOURITE));
        int associatedValue = cursor.getInt(cursor.getColumnIndexOrThrow(TableDevices.COLUMN_NAME_IS_ASSOCIATED));

        // Create an instance of the appropriate type of device based on the appearance value
        CSRDevice = DeviceFactory.getDevice(appearance);

        CSRDevice.setId(id);
        CSRDevice.setDeviceHash(deviceHash);
        CSRDevice.setDeviceID(deviceID);
        CSRDevice.setName(name);
        CSRDevice.setAppearance(appearance);
        CSRDevice.setModelHigh(modelHigh);
        CSRDevice.setModelLow(modelLow);
        CSRDevice.setNumGroups(numberGroups);
        CSRDevice.setGroups(MaterialUtils.byteArrayToIntArray(groups));
        CSRDevice.setUuid(uuid);
        CSRDevice.setAuthCode(authCode);
        CSRDevice.setModel(model);
        CSRDevice.setPlaceID(placeID);
        CSRDevice.setFavourite((favouriteValue == 1) ? true : false);
        CSRDevice.setAssociated((associatedValue == 1) ? true : false);

        return CSRDevice;
    }
}
