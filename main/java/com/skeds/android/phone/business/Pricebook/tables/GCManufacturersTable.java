package com.skeds.android.phone.business.Pricebook.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class GCManufacturersTable implements BaseColumns {

    public static final String TABLE_GK_MANUFACTURERS = "group_code_manufacturers";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_GROUP_CODE_ID = "service_plan_id";
    public static final String COLUMN_MANUFACTURER_ID = "manufacturer_id";

    // Table creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_GK_MANUFACTURERS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_GROUP_CODE_ID
            + " integer, " + COLUMN_MANUFACTURER_ID + " integer);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(GCManufacturersTable.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GK_MANUFACTURERS);
        onCreate(db);
    }

}
