package com.skeds.android.phone.business.Aspects;

/**
 * Created by Mikhail on 31.07.2014.
 */
public interface IResponseCache {

    void putObject(String key, Object obj);

    Object getObject(String key) throws NotFoundValueExeption;
}
