package com.skeds.android.phone.business.Cache;

import com.skeds.android.phone.business.Aspects.IResponseCache;
import com.skeds.android.phone.business.Aspects.NotFoundValueExeption;

/**
 * Created by Mikhail on 31.07.2014.
 */
public class DatabaseCache implements IResponseCache {

    //  SQLiteOpenHelper sqLiteOpenHelper = new CupboardSQLiteOpenHelper();

    @Override
    public void putObject(String key, Object obj) {
//        cupboard().withDatabase().put();
    }

    @Override
    public Object getObject(String key) throws NotFoundValueExeption {
//
//        List<EditEstimateResponse> android = cupboard()
//                .withDatabase(sqLiteOpenHelper.getReadableDatabase())
//                .query(EditEstimateResponse.class)
//                .withSelection("title = ?", "Android")
//                .list();

        return null;
    }
}
