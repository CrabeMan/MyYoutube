package com.esgi.myyoutube;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Util {

    public static class ExtraKey {
        public static final String VIDEO = "EXTRA_VIDEO";
    }

    public static class ActResult {
        public static final int VIDEO = 42;
    }

    public static class Pref {
        public static final String VIDEO_FAV = "VIDEO_FAV";
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
