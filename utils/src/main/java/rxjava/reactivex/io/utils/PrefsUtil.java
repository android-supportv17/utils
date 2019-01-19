package rxjava.reactivex.io.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import java.util.ArrayList;

public class PrefsUtil {

    private static final String PREFS_NAME = "rxprefs";


    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.commit();
    }


    public static String getString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return prefs.getString(key, null);
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static void setLong(Context context, String key, long value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return prefs.getInt(key, 0);
    }

    public static long getLong(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return prefs.getLong(key, 0);
    }

    public static void setBoolean(Context context, String key, Boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defaut) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defaut);
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static void setStrArray(Context context, String key, ArrayList<String> values) {
        String regularEx = "#";
        String str = "";
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (values != null && values.size() > 0) {
            for (String value : values) {
                str += value;
                str += regularEx;
            }
            Editor et = sp.edit();
            et.putString(key, str);
            et.commit();
        }
    }

    public static String[] getStrArray(Context context, String key) {
        String regularEx = "#";
        String[] str = null;
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String values;
        values = sp.getString(key, "0");
        str = values.split(regularEx);

        return str;
    }

    /**
     * 清除指定 key 的记录
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }

    /**
     * 清除所有记录
     *
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

}
