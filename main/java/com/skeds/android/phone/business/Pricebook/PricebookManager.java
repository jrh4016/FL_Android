package com.skeds.android.phone.business.Pricebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.skeds.android.phone.business.Pricebook.tables.GCManufacturersTable;
import com.skeds.android.phone.business.Pricebook.tables.GroupCodesTable;
import com.skeds.android.phone.business.Pricebook.tables.ManufacturersTable;
import com.skeds.android.phone.business.Pricebook.tables.ProductCostsTable;
import com.skeds.android.phone.business.Pricebook.tables.ProductsTable;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.Cost;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.GroupCode;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.Manufacturer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.Product;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.ServicePlan;

public class PricebookManager {

    private Context context;

    private SQLiteDatabase database;

    private SQLiteHelper helper;

    private PricebookDatabase pricebookDatabase;

    public PricebookManager(Context context) {
        this.context = context;
        helper = new SQLiteHelper(context);
        pricebookDatabase = PricebookDatabase.getInstance();
        openDatabase();
    }

    public void openDatabase() throws SQLException {
        database = helper.getWritableDatabase();
    }

    public void closeDatabase() {
        database.close();
    }


    public void addServicePlans(ServicePlan plan) {
//		ContentValues values = new ContentValues();
//		values.put(ServicePlansTable.COLUMN_NAME, plan.getName());
//		values.put(ServicePlansTable.COLUMN_PRICE_TYPE,
//				plan.getPriceType());
//		values.put(ServicePlansTable.COLUMN_SERVICE_PLAN_ID,
//				plan.getId());
//
//		// String[] allColumns = { ServicePlansSQLiteHelper.COLUMN_ID,
//		// ServicePlansSQLiteHelper.COLUMN_SERVICE_PLAN_ID };
//		// Cursor cursor = servicePlansDatabase.query(
//		// ServicePlansSQLiteHelper.TABLE_SERVICE_PLANS,
//		// allColumns,
//		// ServicePlansSQLiteHelper.COLUMN_SERVICE_PLAN_ID + " = "
//		// + plan.getId(), null, null, null, null);
//
//		int updatedAmount = database.update(
//				ServicePlansTable.TABLE_SERVICE_PLANS,
//				values,
//				ServicePlansTable.COLUMN_SERVICE_PLAN_ID + "="
//						+ plan.getId(), null);
//
//		if (updatedAmount == 0)
//			database.insert(
//					ServicePlansTable.TABLE_SERVICE_PLANS, null, values);

    }

    public void addGroupCode(GroupCode code) {
        ContentValues values = new ContentValues();
        values.put(GroupCodesTable.COLUMN_NAME, code.getName());
        values.put(GroupCodesTable.COLUMN_CODE_ID, code.getId());
        values.put(GroupCodesTable.COLUMN_DESCRIPTION,
                code.getDescription());

        int updatedAmount = database.update(
                GroupCodesTable.TABLE_CODES, values,
                GroupCodesTable.COLUMN_CODE_ID + "=" + code.getId(),
                null);

        if (updatedAmount == 0) {
            database.insert(GroupCodesTable.TABLE_CODES, null,
                    values);
        }

        database.delete(
                GCManufacturersTable.TABLE_GK_MANUFACTURERS,
                GCManufacturersTable.COLUMN_GROUP_CODE_ID + "="
                        + code.getId(), null);
        for (Integer manufId : code.getManufacturerIds()) {
            values = new ContentValues();
            values.put(GCManufacturersTable.COLUMN_MANUFACTURER_ID,
                    manufId);
            values.put(GCManufacturersTable.COLUMN_GROUP_CODE_ID,
                    code.getId());

            database.insert(
                    GCManufacturersTable.TABLE_GK_MANUFACTURERS, null,
                    values);
        }

    }

    public void addProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(ProductsTable.COLUMN_PRODUCT_ID, product.getId());
        values.put(ProductsTable.COLUMN_NAME, product.getName());
        values.put(ProductsTable.COLUMN_DESCRIPTION,
                product.getDescription());
        values.put(ProductsTable.COLUMN_MANUFACTURER_ID,
                product.getManufacturerId());
        values.put(ProductsTable.COLUMN_GROUPCODE_ID,
                product.getGroupCodeId());
        values.put(ProductsTable.COLUMN_DELETED, product.isDeleted() ? 1
                : 0);
        values.put(ProductsTable.COLUMN_TAXABLE, product.isTaxable() ? 1
                : 0);

        int updatedAmount = database.update(
                ProductsTable.TABLE_PRODUCTS, values,
                ProductsTable.COLUMN_PRODUCT_ID + "=" + product.getId(),
                null);

        if (updatedAmount == 0)
            database.insert(ProductsTable.TABLE_PRODUCTS, null,
                    values);

        database.delete(
                ProductCostsTable.TABLE_PRODUCT_COSTS,
                ProductCostsTable.COLUMN_PRODUCT_ID + "="
                        + product.getId(), null);
        for (Cost cost : product.getProductCosts()) {
            values = new ContentValues();
            values.put(ProductCostsTable.COLUMN_PRODUCT_ID,
                    product.getId());
            values.put(ProductCostsTable.COLUMN_NAME, cost.getName());
            values.put(ProductCostsTable.COLUMN_TYPE, cost.getType());
            values.put(ProductCostsTable.COLUMN_PRICE,
                    Double.parseDouble(cost.getPrice().toString()));

            database.insert(
                    ProductCostsTable.TABLE_PRODUCT_COSTS, null, values);
        }
    }

    public void addManufacturer(Manufacturer manufacturer) {

        ContentValues values = new ContentValues();
        values.put(ManufacturersTable.COLUMN_NAME,
                manufacturer.getName());
        values.put(ManufacturersTable.COLUMN_MANUFACTURER_ID,
                manufacturer.getId());
        values.put(ManufacturersTable.COLUMN_DESCRIPTION,
                manufacturer.getDescription());

        int updatedAmount = database.update(
                ManufacturersTable.TABLE_MANUFACTURERS, values,
                ManufacturersTable.COLUMN_MANUFACTURER_ID + "="
                        + manufacturer.getId(), null);

        if (updatedAmount == 0)
            database.insert(ManufacturersTable.TABLE_MANUFACTURERS,
                    null, values);
    }

}
