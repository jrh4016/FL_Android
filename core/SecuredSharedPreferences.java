package com.skeds.android.phone.business.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import net.iharder.utils.Base64;

import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

class SecuredSharedPreferences implements SharedPreferences {
    static final String PREFERENCES_NAME = "secured_preferences";

    private String mSecret;
    private SharedPreferences mDelegate;
    private Context mContext;

    SecuredSharedPreferences(Context context, SharedPreferences delegate) {
        mDelegate = delegate;
        mContext = context;
        mSecret = context.getPackageName();
    }

    class Editor implements SharedPreferences.Editor {
        private SharedPreferences.Editor mDelegate;

        Editor() {
            mDelegate = SecuredSharedPreferences.this.mDelegate.edit();
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            mDelegate.putString(key, encrypt(Boolean.toString(value)));
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            mDelegate.putString(key, encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            mDelegate.putString(key, encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            mDelegate.putString(key, encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public Editor putString(String key, String value) {
            mDelegate.putString(key, encrypt(value));
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            throw new UnsupportedOperationException(); // TODO: implement me
        }

        @TargetApi(9)
        @Override
        public void apply() {
            mDelegate.apply();
        }

        @Override
        public Editor clear() {
            mDelegate.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return mDelegate.commit();
        }

        @Override
        public Editor remove(String s) {
            mDelegate.remove(s);
            return this;
        }
    }

    @Override
    public SharedPreferences.Editor edit() {
        return new Editor();
    }


    @Override
    public Map<String, ?> getAll() {
        // TODO: implement me
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        final String v = mDelegate.getString(key, null);
        return v != null ? Boolean.parseBoolean(decrypt(v)) : defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        final String v = mDelegate.getString(key, null);
        return v != null ? Float.parseFloat(decrypt(v)) : defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        final String v = mDelegate.getString(key, null);
        return v != null ? Integer.parseInt(decrypt(v)) : defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        final String v = mDelegate.getString(key, null);
        return v != null ? Long.parseLong(decrypt(v)) : defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        final String v = mDelegate.getString(key, null);
        return v != null ? decrypt(v) : defValue;
    }

    @Override
    public Set<String> getStringSet(final String key, final Set<String> defValues) {
        throw new UnsupportedOperationException(); // TODO: implement me
    }

    @Override
    public boolean contains(String s) {
        return mDelegate.contains(s);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        mDelegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        mDelegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    protected String encrypt(String value) {

        try {
            final byte[] bytes = value != null ? value.getBytes() : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(mSecret.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID).getBytes(), 20));
            return Base64.encodeBytes(pbeCipher.doFinal(bytes), Base64.DO_BREAK_LINES);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected String decrypt(String value) {
        try {
            final byte[] bytes = value != null ? Base64.decode(value, Base64.NO_OPTIONS) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(mSecret.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID).getBytes(), 20));
            return new String(pbeCipher.doFinal(bytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
