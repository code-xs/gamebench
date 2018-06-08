package com.aphrome.gamebench.home;

import android.content.Context;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.Intent;

import android.os.Handler;
import android.os.Message;

import android.net.Uri;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;
import com.aphrome.gamebench.home.models.AppInfoLite;
import android.util.Log;
import android.content.pm.ApplicationInfo;
public class DualAppManager {
    private Context mContext;
    private static final String TAG ="DualAppManager";
    private static final String PKG_NAME_ARGUMENT = "MODEL_ARGUMENT";
    private static final String KEY_INTENT = "KEY_INTENT";
    private static final String KEY_USER = "KEY_USER";

    private static DualAppManager mInstance = null;
    public  DualAppManager(){
    }

    public static DualAppManager getInstance(){
        if(mInstance == null)
            mInstance = new DualAppManager();
        return mInstance;
    }

    public static boolean installVirtuallApp(String pkg, String apkPath){
        if(VirtualCore.get().isAppInstalled(pkg)){
            Log.d(TAG, " VirtualApp had installed "+pkg);
            return true;
        }
        try {
            AppInfoLite info = new AppInfoLite(pkg, apkPath, true);
            InstallResult result = addVirtualApp(info);
            Log.d(TAG, " add VirtualApp: "+result+" ret:"+result.isSuccess);
            if (!result.isSuccess) {
                throw new IllegalStateException();
            }else{
                Log.d(TAG, " add VirtualApp: "+result+" ret:"+result.isSuccess);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean checkInstallVirtuallApp(String pkg){
        return VirtualCore.get().isAppInstalled(pkg);
    }

    public static void launch(Context context, String packageName, int userId) {
        Intent intent = VirtualCore.get().getLaunchIntent(packageName, userId);
        if (intent != null) {
            Intent loadingPageIntent = new Intent(context, LoadingActivity.class);
            loadingPageIntent.putExtra(PKG_NAME_ARGUMENT, packageName);
            loadingPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loadingPageIntent.putExtra(KEY_INTENT, intent);
            loadingPageIntent.putExtra(KEY_USER, userId);
            Log.d(TAG, "loadingPageIntent  "+loadingPageIntent);
            context.startActivity(loadingPageIntent);
        }
    }

    public static InstallResult addVirtualApp(AppInfoLite info) {
        int flags = InstallStrategy.COMPARE_VERSION | InstallStrategy.SKIP_DEX_OPT;
        if (info.fastOpen) {
            flags |= InstallStrategy.DEPEND_SYSTEM_IF_EXIST;
        }
        return VirtualCore.get().installPackage(info.path, flags);
    }

    public static boolean removeVirtualApp(String packageName, int userId) {
        return VirtualCore.get().uninstallPackageAsUser(packageName, userId);
    }

    public static  boolean installOrRemoveApp(Context context, String pkg){
        ApplicationInfo info = DataUtils.getApplicationInfo(context, pkg, 0);
        Log.d(TAG, "get info:"+info);
        if(info != null){
            Log.d(TAG, "get info.sourceDir:"+info.sourceDir);
            if(!checkInstallVirtuallApp(pkg))
                return installVirtuallApp(pkg, info.sourceDir);
        }else{
            if(checkInstallVirtuallApp(pkg))
                return removeVirtualApp(pkg, 0);
        }
        return true;
    }    
}
