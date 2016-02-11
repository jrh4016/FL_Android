package com.skeds.android.phone.business.Pricebook.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class ServicePlansTable implements BaseColumns {

    public static final String TABLE_SERVICE_PLANS = "service_plans";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SERVICE_PLAN_ID = "service_plan_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE_TYPE = "price_type";

    // Table creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SERVICE_PLANS + " (" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_SERVICE_PLAN_ID
            + " integer, " + COLUMN_NAME + " text not null, "
            + COLUMN_PRICE_TYPE + " integer);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ServicePlansTable.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE_PLANS);
        onCreate(db);
    }

}
