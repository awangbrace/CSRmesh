/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.model.data.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.axalent.model.data.model.Place;

/**
 *
 */
public class TablePlaces implements BaseColumns {

    public static final String TABLE_NAME = "places";

    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_NETWORK_KEY = "networkKey";
    public static final String COLUMN_NAME_CLOUD_SITE_ID_KEY = "cloudSiteID";
    public static final String COLUMN_NAME_ICON_ID = "iconID";
    public static final String COLUMN_NAME_COLOR = "color";
    public static final String COLUMN_NAME_HOST_CONTROLLER_ID = "hostControllerID";
    public static final String COLUMN_NAME_SETTINGS_ID = "settingsID";


    public static String[] getColumnsNames() {

        String[] columns = {
                TablePlaces._ID,
                TablePlaces.COLUMN_NAME_NAME,
                TablePlaces.COLUMN_NAME_NETWORK_KEY,
                TablePlaces.COLUMN_NAME_CLOUD_SITE_ID_KEY,
                TablePlaces.COLUMN_NAME_ICON_ID,
                TablePlaces.COLUMN_NAME_COLOR,
                TablePlaces.COLUMN_NAME_HOST_CONTROLLER_ID,
                TablePlaces.COLUMN_NAME_SETTINGS_ID
        };

        return columns;
    }

    public static ContentValues createContentValues(Place place) {

        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TablePlaces.COLUMN_NAME_NAME, place.getName());
        values.put(TablePlaces.COLUMN_NAME_NETWORK_KEY, place.getNetworkKey());
        values.put(TablePlaces.COLUMN_NAME_CLOUD_SITE_ID_KEY, place.getCloudSiteID());
        values.put(TablePlaces.COLUMN_NAME_ICON_ID, place.getIconID());
        values.put(TablePlaces.COLUMN_NAME_COLOR, place.getColor());
        values.put(TablePlaces.COLUMN_NAME_HOST_CONTROLLER_ID, place.getHostControllerID());
        values.put(TablePlaces.COLUMN_NAME_SETTINGS_ID, place.getSettingsID());

        return values;
    }

    public static Place getPlaceFromCursor(Cursor cursor) {

        Place place = new Place();

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TablePlaces._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(TablePlaces.COLUMN_NAME_NAME));
        String networkKey = cursor.getString(cursor.getColumnIndexOrThrow(TablePlaces.COLUMN_NAME_NETWORK_KEY));
        String placeCloudId = cursor.getString(cursor.getColumnIndexOrThrow(TablePlaces.COLUMN_NAME_CLOUD_SITE_ID_KEY));
        int iconID = cursor.getInt(cursor.getColumnIndexOrThrow(TablePlaces.COLUMN_NAME_ICON_ID));
        long color = cursor.getLong(cursor.getColumnIndexOrThrow(TablePlaces.COLUMN_NAME_COLOR));
        int hostControllerID = cursor.getInt(cursor.getColumnIndexOrThrow(TablePlaces.COLUMN_NAME_HOST_CONTROLLER_ID));
        int settingsId = cursor.getInt(cursor.getColumnIndexOrThrow(TablePlaces.COLUMN_NAME_SETTINGS_ID));

        place.setId(id);
        place.setName(name);
        place.setNetworkKey(networkKey);
        place.setCloudSiteID(placeCloudId);
        place.setIconID(iconID);
        place.setColor(color);
        place.setHostControllerID(hostControllerID);
        place.setSettingsID(settingsId);

        return place;
    }
}