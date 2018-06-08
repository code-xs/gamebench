package com.aphrome.gamebench.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import android.util.Log;
public class DataUtils {
    private static final String TAG = "DataUtils";

    public static  ApplicationInfo getApplicationInfo(Context context, String pkg, int flag){
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getApplicationInfo(pkg, flag);
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e);
        }
        return null;
    }

    public static  String getAppName(Context context, String pkg, int flag){
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = getApplicationInfo(context, pkg, flag);
            if(info != null) return info.loadLabel(pm).toString();
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e);
        }
        return null;
    }

    public static  Drawable getAppIcon(Context context, String pkg, int flag){
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = getApplicationInfo(context, pkg, flag);
            if(info != null) return info.loadIcon(pm);
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e);
        }
        return null;
    }
}
