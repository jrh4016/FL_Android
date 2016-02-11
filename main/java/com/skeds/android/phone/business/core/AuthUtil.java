package com.skeds.android.phone.business.core;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.data.UserAccount;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class AuthUtil {

    static final String LOG_TAG = AuthUtil.class.getSimpleName();

    // MPX user id (long value)
    private static final String KEY_USER_ID = "7eleven.user_id";
    // User id exported from associated social network
    private static final String KEY_SOCIAL_ID = "7eleven.social_user_id";
    // full name of the logged in user (string)
    private static final String KEY_USER_NAME = "7eleven.user_name";
    // user's sex
    private static final String KEY_GENDER = "7eleven.gender";
    // postal code of logged in user
    private static final String KEY_ZIP_CODE = "7eleven.zip_code";
    // logged in user's birthday
    private static final String KEY_BIRTHDAY = "7eleven.birthday";
    // flag indicating that account was changed on a client side
    // but changes wasn't sent to the server
    private static final String KEY_IS_DIRTY = "7eleven.is_dirty";
    // flag indicating that account was logged in via FB
    private static final String KEY_IS_FACEBOOK = "7eleven.is_facebook";

    private static final String BIRTHDAY_DATE_FORMAT = "dd.MM.yyyy";

    private AuthUtil() {
        // NO-OP
    }

    /**
     * Checks if user is authenticated to this application.
     *
     * @return <code>true</code> if user is authenticated, <code>false</code> - otherwise
     */
    public static boolean isAuthenticated() {
        SkedsApplication app = SkedsApplication.getInstance();
        AccountManager am = AccountManager.get(app);
        Account[] accounts = am.getAccountsByType(app.getString(R.string.account_type));
        return (accounts != null && accounts.length > 0);
    }

    /**
     * Returns logged in user account.
     *
     * @return logged in user account or <code>null</code> when user is not logged in
     */
    public static UserAccount getAccount() {
        SkedsApplication app = SkedsApplication.getInstance();
        AccountManager am = AccountManager.get(app);
        Account[] accounts = am.getAccountsByType(app.getString(R.string.account_type));
        UserAccount account = null;
        if (accounts != null && accounts.length > 0) {
            // only one account supported
            Account acc = accounts[0];
            account = new UserAccount();
            account.setEmail(acc.name);
            account.setPassword(am.getPassword(acc)); // password is md5 encoded

            // get additional user's data
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(app);
            account.setUserId(prefs.getLong(KEY_USER_ID, -1));
            account.setSocialId(prefs.getString(KEY_SOCIAL_ID, ""));
            account.setUserName(prefs.getString(KEY_USER_NAME, ""));
            account.setZipCode(prefs.getString(KEY_ZIP_CODE, ""));
            account.setDirty(prefs.getBoolean(KEY_IS_DIRTY, false));
            account.setFacebookAccount(prefs.getBoolean(KEY_IS_FACEBOOK, false));
//            String gender = prefs.getString(KEY_GENDER, "");
//            if (!TextUtils.isEmpty(gender)) {
//                account.setGender(Gender.valueOf(gender));
//            }
//            String birthday = prefs.getString(KEY_BIRTHDAY, "");
//            if (!TextUtils.isEmpty(birthday)) {
//                DateFormat formatter = new SimpleDateFormat(BIRTHDAY_DATE_FORMAT,
//                        Locale.getDefault());
//                try {
//                    account.setBirthday(formatter.parse(birthday));
//                } catch (ParseException e) {
//                    // should never occur
//                    Log.wtf(LOG_TAG, "Unable to parse birthday", e);
//                }
//            }
        }

        return account;
    }

    public static Bundle addAccount(UserAccount account) {
        Bundle result = null;
        SkedsApplication app = SkedsApplication.getInstance();

        Account acc = new Account(account.getEmail(), app.getString(R.string.account_type));
        AccountManager am = AccountManager.get(app);

        // password is md5 encoded so it is safe to store it without additional encryption
        if (am.addAccountExplicitly(acc, account.getPassword(), null)) {
            // save user profile data to shared preferences
            // we can't save this data to AccountManager because there is
            // a bug in AccountManager on many HTC devices.
            // Description can be found here:
            // http://stackoverflow.com/questions/10657658/accountmanager-getuserdata-returning-null-despite-it-being-set
            saveUserData(account, app);

            result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, acc.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, acc.type);
        }

        return result;
    }

    public static void updateAccount(UserAccount account) {
        SkedsApplication app = SkedsApplication.getInstance();

        AccountManager am = AccountManager.get(app);
        Account[] accounts = am.getAccountsByType(app.getString(R.string.account_type));
        if (accounts != null && accounts.length > 0) {
            saveUserData(account, app);
        }
    }

    public static void removeAccount() {
        final SkedsApplication app = SkedsApplication.getInstance();
        AccountManager am = AccountManager.get(app);
        Account[] accounts = am.getAccountsByType(app.getString(R.string.account_type));

        // only one account supported so for has one loop
        for (Account account : accounts) {
            am.removeAccount(account, null, null);
        }
        // perform cleanup of user data even if account not found.
        // removeAccount must be called from Dashboard to clean previously
        // logged in user data if user removed account from "Accounts and Sync" menu
        // and then launched application
        cleanUserData(app);
    }

    private static void saveUserData(UserAccount account, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor prefEditor = prefs.edit();
        prefEditor.putLong(KEY_USER_ID, account.getUserId())
                .putString(KEY_SOCIAL_ID, account.getSocialId())
                .putString(KEY_USER_NAME, account.getUserName())
                .putString(KEY_ZIP_CODE, account.getZipCode())
                .putBoolean(KEY_IS_DIRTY, account.isDirty())
                .putBoolean(KEY_IS_FACEBOOK, account.isFacebookAccount());
        Date birthday = account.getBirthday();
        if (birthday != null) {
            DateFormat formatter = new SimpleDateFormat(BIRTHDAY_DATE_FORMAT, Locale.getDefault());
            prefEditor.putString(KEY_BIRTHDAY, formatter.format(birthday));
        }
        // remove info about offers and vouchers sync time, since user can sync
        // offers when he was not logged in (synced public offers)
        prefEditor.commit();
    }

    private static void cleanUserData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove(KEY_USER_ID).remove(KEY_SOCIAL_ID).remove(KEY_USER_NAME)
                .remove(KEY_ZIP_CODE).remove(KEY_GENDER).remove(KEY_BIRTHDAY).remove(KEY_IS_DIRTY)
                .remove(KEY_IS_FACEBOOK).commit();
    }
}
