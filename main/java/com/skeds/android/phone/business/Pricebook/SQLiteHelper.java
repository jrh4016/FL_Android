package com.skeds.android.phone.business.Pricebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.skeds.android.phone.business.Pricebook.tables.GCManufacturersTable;
import com.skeds.android.phone.business.Pricebook.tables.GroupCodesTable;
import com.skeds.android.phone.business.Pricebook.tables.ManufacturersTable;
import com.skeds.android.phone.business.Pricebook.tables.ProductCostsTable;
import com.skeds.android.phone.business.Pricebook.tables.ProductsTable;
import com.skeds.android.phone.business.Pricebook.tables.ServicePlansTable;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pricebookdatabase.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(getClass().getSimpleName(), "onCreate helper");
        GCManufacturersTable.onCreate(db);
        GroupCodesTable.onCreate(db);
        ManufacturersTable.onCreate(db);
        ProductCostsTable.onCreate(db);
        ProductsTable.onCreate(db);
        ServicePlansTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        GCManufacturersTable.onUpgrade(db, oldVersion, newVersion);
        GroupCodesTable.onUpgrade(db, oldVersion, newVersion);
        ManufacturersTable.onUpgrade(db, oldVersion, newVersion);
        ProductCostsTable.onUpgrade(db, oldVersion, newVersion);
        ProductsTable.onUpgrade(db, oldVersion, newVersion);
        ServicePlansTable.onUpgrade(db, oldVersion, newVersion);
    }
}
