package com.skeds.android.phone.business.Pricebook;

import android.util.Log;

import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.GroupCode;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.Manufacturer;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.Product;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.PriceBook.ServicePlan;
import com.skeds.android.phone.business.core.SkedsApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PricebookDatabase implements Serializable {

    private static PricebookDatabase instance;

    private List<ServicePlan> servicePlans;

    private List<Product> products;

    private List<Manufacturer> manufacturers;

    private List<GroupCode> groupCodes;

    private PricebookDatabase() {
    }

    public static final PricebookDatabase getInstance() {

        if (instance == null) {

            PricebookDatabase data = SkedsApplication.getInstance().getPriceBookFromFile();
            if (data != null) {
                Log.d("file_transaction",
                        "Pricebook Data Have Been Retrieved From File");
                instance = data;
                return data;
            }
            Log.e("APPLICATION_DATA", "!Pricebook Data Instance Created!");
            instance = new PricebookDatabase();
        }
        return instance;
    }

    public static final PricebookDatabase getReference() {
        return instance;
    }

    public static final void clear() {
        instance = null;
    }

    public List<ServicePlan> getServicePlans() {
        if (servicePlans == null)
            servicePlans = new ArrayList<ServicePlan>();
        return servicePlans;
    }

    public List<Product> getProducts() {
        if (products == null)
            products = new ArrayList<Product>();
        return products;
    }

    public List<Manufacturer> getManufacturers() {
        if (manufacturers == null)
            manufacturers = new ArrayList<Manufacturer>();
        return manufacturers;
    }

    public List<GroupCode> getGroupCodes() {
        if (groupCodes == null)
            groupCodes = new ArrayList<GroupCode>();
        return groupCodes;
    }
}
