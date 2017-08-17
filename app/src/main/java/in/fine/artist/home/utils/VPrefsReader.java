package in.fine.artist.home.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import in.fine.artist.home.ZApplication;
import in.fine.artist.home.data.NameValuePair;

/**
 * Created by apoorvarora on 04/04/17.
 */
public class VPrefsReader {

    private Context context;
    private SharedPreferences prefs, oneTimePrefs;
    private static volatile VPrefsReader sInstance;
    private ZApplication zapp;

    public static VPrefsReader getInstance() {
        if (sInstance == null) {
            synchronized (VPrefsReader.class) {
                if (sInstance == null) {
                    sInstance = new VPrefsReader();
                }
            }
        }
        return sInstance;
    }

    public void setContext(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(CommonLib.APP_SETTINGS, 0);
        oneTimePrefs = context.getSharedPreferences(CommonLib.ONE_LAUNCH_SETTINGS, 0);
        if (context instanceof ZApplication) {
            zapp = (ZApplication) context;
        }
    }

    public String getPref(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public int getPref(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    public float getPref(String key, float defaultValue) {
        return prefs.getFloat(key, defaultValue);
    }

    public boolean getPref(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public long getPref(String key, long defaultValue) {
        return prefs.getLong(key, defaultValue);
    }

    public void setPref(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }

    public void setPref(String key, int value) {
        prefs.edit().putInt(key, value).commit();
    }

    public void setPref(String key, float value) {
        prefs.edit().putFloat(key, value).commit();
    }

    public void setPref(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    public void setPref(String key, long value) {
        prefs.edit().putLong(key, value).commit();
    }

    public void removePref(String key) {
        if (prefs.contains(key))
            prefs.edit().remove(key).commit();
    }

    public String getOneTimePref(String key, String defaultValue) {
        return oneTimePrefs.getString(key, defaultValue);
    }

    public int getOneTimePref(String key, int defaultValue) {
        return oneTimePrefs.getInt(key, defaultValue);
    }

    public float getOneTimePref(String key, float defaultValue) {
        return oneTimePrefs.getFloat(key, defaultValue);
    }

    public boolean getOneTimePref(String key, boolean defaultValue) {
        return oneTimePrefs.getBoolean(key, defaultValue);
    }

    public long getOneTimePref(String key, long defaultValue) {
        return oneTimePrefs.getLong(key, defaultValue);
    }

    public void setOneTimePref(String key, String value) {
        oneTimePrefs.edit().putString(key, value).commit();
    }

    public void setOneTimePref(String key, int value) {
        oneTimePrefs.edit().putInt(key, value).commit();
    }

    public void setOneTimePref(String key, float value) {
        oneTimePrefs.edit().putFloat(key, value).commit();
    }

    public void setOneTimePref(String key, long value) {
        oneTimePrefs.edit().putLong(key, value).commit();
    }

    public void setOneTimePref(String key, boolean value) {
        oneTimePrefs.edit().putBoolean(key, value).commit();
    }

    public void removeOneTimePref(String key) {
        if (oneTimePrefs.contains(key))
            oneTimePrefs.edit().remove(key).commit();
    }

    public void setPrefs(List<NameValuePair> pairs) {
        if (pairs == null)
            return;
        SharedPreferences.Editor editor = prefs.edit();
        for (NameValuePair pair:pairs) {
            Object value = pair.getValue();
            if (value instanceof Integer)
                editor.putInt(pair.getKey(), (int)value);
            else if (value instanceof Float)
                editor.putFloat(pair.getKey(), (float) value);
            else if (value instanceof Double)
                editor.putFloat(pair.getKey(), ((Double) value).floatValue());
            else if (value instanceof Boolean)
                editor.putBoolean(pair.getKey(), (boolean) value);
            else if (value instanceof Long)
                editor.putLong(pair.getKey(), (long) value);
            else
                editor.putString(pair.getKey(), String.valueOf(value));

        }
        editor.commit();
    }

    public void setOneTimePrefs(List<NameValuePair> pairs) {
        if (pairs == null)
            return;
        SharedPreferences.Editor editor = oneTimePrefs.edit();
        for (NameValuePair pair:pairs) {
            Object value = pair.getValue();
            if (value instanceof Integer)
                editor.putInt(pair.getKey(), (int)value);
            else if (value instanceof Float)
                editor.putFloat(pair.getKey(), (float) value);
            else if (value instanceof Double)
                editor.putFloat(pair.getKey(), ((Double) value).floatValue());
            else if (value instanceof Boolean)
                editor.putBoolean(pair.getKey(), (boolean) value);
            else if (value instanceof Long)
                editor.putLong(pair.getKey(), (long) value);
            else
                editor.putString(pair.getKey(), String.valueOf(value));
        }
        editor.commit();
    }

    public void clearPrefs() {
        prefs.edit().clear().commit();
    }

    public void clearOneTimePrefs() {
        oneTimePrefs.edit().clear().commit();
    }

}
