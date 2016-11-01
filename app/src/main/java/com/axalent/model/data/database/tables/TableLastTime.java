package com.axalent.model.data.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.axalent.model.data.model.Time;

/**
 * File Name                   : TableLastTime
 * Author                      : franky
 * Version                     : 1.0.0
 * Date                        : 2016/8/11
 * Revision History            : 16:57
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd.
 * All rights reserved.
 */
public class TableLastTime implements BaseColumns {

    public static final String TABLE_NAME = "time";

    public static final String COLUMN_NAME_TIMES = "times";

    public static String[] getColumnsNames() {

        String[] columns = {
                TableLastTime._ID,
                TableLastTime.COLUMN_NAME_TIMES,
        };
        return columns;
    }

    public static ContentValues createContentValues(Time time) {
        // _ID not included since it is Auto-incremental
        ContentValues values = new ContentValues();
        values.put(TableLastTime.COLUMN_NAME_TIMES, time.getTime());
        return values;
    }

    public static Time getTimeFromCursor(Cursor cursor) {
        Time time = new Time();
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableLastTime._ID));
        String strTime = cursor.getString(cursor.getColumnIndexOrThrow(TableLastTime.COLUMN_NAME_TIMES));
        time.setId(id);
        time.setTime(strTime);
        return time;
    }

}
