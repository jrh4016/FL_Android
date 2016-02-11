package com.skeds.android.phone.business.Pricebook.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class ManufacturersTable implements BaseColumns {

    public static final String TABLE_MANUFACTURERS = "manufacturers";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MANUFACTURER_ID = "manufacturer_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";


    // Table creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MANUFACTURERS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_MANUFACTURER_ID
            + " integer, " + COLUMN_NAME + " text not null, "
            + COLUMN_DESCRIPTION + " text not null);";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ManufacturersTable.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANUFACTURERS);
        onCreate(db);
    }

}
