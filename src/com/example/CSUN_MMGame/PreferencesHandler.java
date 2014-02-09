package com.example.CSUN_MMGame;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Cory on 2/8/14.
 */

public class PreferencesHandler {

    public static final int multicastPort = 12333;
    public static final int normalPort = 12344;

//    public static final int sensorRefreshRateNanoS = 10000000;
//    public static final int socketTimeout = 4;

    public static final int sensorRefreshRateNanoS = 5000000;
    public static final int socketTimeout = 2;

    public static void setLastIP(Context context, String IP) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("lastIP", IP);
        edit.commit();
    }

    public static String getLastIP(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("lastIP", "192.168.1.1");
    }
}

