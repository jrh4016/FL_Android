package com.skeds.android.phone.business.Pricebook.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class ProductCostsTable implements BaseColumns {

    public static final String TABLE_PRODUCT_COSTS = "product_costs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_PRICE = "price";

    private static final String DATABASE_NAME = "pricebook.db";
    private static final int DATABASE_VERSION = 1;

    // Table creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PRODUCT_COSTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_PRODUCT_ID
            + " integer, " + COLUMN_NAME + " text not null, " + COLUMN_TYPE
            + " integer, " + COLUMN_PRICE + " real);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ProductCostsTable.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_COSTS);
        onCreate(db);
    }

}
