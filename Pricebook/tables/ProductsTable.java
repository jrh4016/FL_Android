package com.skeds.android.phone.business.Pricebook.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class ProductsTable implements BaseColumns {

    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_TAXABLE = "taxable";
    public static final String COLUMN_DELETED = "deleted";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_MANUFACTURER_ID = "manufacturer_id";
    public static final String COLUMN_GROUPCODE_ID = "groupcode_id";


    // Table creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PRODUCTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_PRODUCT_ID
            + " integer, " + COLUMN_TAXABLE + " integer, " + COLUMN_DELETED + " integer, " + COLUMN_NAME + " text not null, "
            + COLUMN_DESCRIPTION + " text not null, " + COLUMN_MANUFACTURER_ID + " integer, " + COLUMN_GROUPCODE_ID + " integer);";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ProductsTable.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

}
