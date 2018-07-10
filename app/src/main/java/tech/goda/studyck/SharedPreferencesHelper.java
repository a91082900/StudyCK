package tech.goda.studyck;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jerry on 2018/7/8.
 */


public class SharedPreferencesHelper {

    private static final String SHARED_PREF_NAME = "KEYSTORE_SETTING";

    private static final String PREF_KEY_AES = "PREF_KEY_AES";
    private static final String PREF_KEY_IV = "PREF_KEY_IV";
    private static final String PREF_KEY_INPUT = "PREF_KEY_INPUT";
    public static final String PREF_AC = "PREF_AC";

    private SharedPreferences sharedPreferences;



    SharedPreferencesHelper(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }


    String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    void putString(String key, String value) {
        sharedPreferences.edit()
                .putString(key, value)
                .apply();
    }

    private boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    private void putBoolean(String key, boolean value) {
        sharedPreferences.edit()
                .putBoolean(key, value)
                .apply();
    }




    void setIV(String value) {
        putString(PREF_KEY_IV, value);
    }

    String getIV() {
        return getString(PREF_KEY_IV);
    }

    void setAESKey(String value) {
        putString(PREF_KEY_AES, value);
    }

    String getAESKey() {
        return getString(PREF_KEY_AES);
    }

    public void setInput(String value) {
        putString(PREF_KEY_INPUT, value);
    }

    String getInput() {
        return getString(PREF_KEY_INPUT);
    }

}