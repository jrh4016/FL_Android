package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

public class RESTTermsOfService {

    public static void post() throws NonfatalException {
        RestConnector.getInstance().httpGetCheckSuccess(
                "accepttos/" + UserUtilitiesSingleton.getInstance().user.getId());
    }
}