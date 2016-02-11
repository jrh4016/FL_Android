package com.skeds.android.phone.business.Aspects;

/**
 * Created by Mikhail on 31.07.2014.
 */
public class NotFoundValueExeption extends Exception {

    @Override
    public String getMessage() {
        return "Object didn't found in cache, proceeding...";
    }
}
