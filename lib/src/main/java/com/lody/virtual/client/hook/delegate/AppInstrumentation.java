package com.lody.virtual.client.hook.delegate;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.view.ViewGroup;
import android.view.Window;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.fixer.ActivityFixer;
import com.lody.virtual.client.fixer.ContextFixer;
import com.lody.virtual.client.interfaces.IInjector;
import com.lody.virtual.client.ipc.ActivityClientRecord;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.compat.BundleCompat;
import com.lody.virtual.os.VUserHandle;
import com.lody.virtual.server.interfaces.IUiCallback;
import com.lody.virtual.client.FloatSurfaceView;
import mirror.android.app.ActivityThread;
import android.util.Log;
import com.lody.virtual.client.NativeEngine;
/**
 * @author Lody
 */
public final class AppInstrumentation extends InstrumentationDelegate implements IInjector {

    private static final String TAG = AppInstrumentation.class.getSimpleName();

    private static AppInstrumentation gDefault;

    private AppInstrumentation(Instrumentation base) {
        super(base);
    }

    public static AppInstrumentation getDefault() {
        if (gDefault == null) {
            synchronized (AppInstrumentation.class) {
                if (gDefault == null) {
                    gDefault = create();
                }
            }
        }
        return gDefault;
    }

    private static AppInstrumentation create() {
        Instrumentation instrumentation = ActivityThread.mInstrumentation.get(VirtualCore.mainThread());
        if (instrumentation instanceof AppInstrumentation) {
            return (AppInstrumentation) instrumentation;
        }
        return new AppInstrumentation(instrumentation);
    }


    @Override
    public void inject() throws Throwable {
        base = ActivityThread.mInstrumentation.get(VirtualCore.mainThread());
        ActivityThread.mInstrumentation.set(VirtualCore.mainThread(), this);
    }

    @Override
    public boolean isEnvBad() {
        return !(ActivityThread.mInstrumentation.get(VirtualCore.mainThread()) instanceof AppInstrumentation);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        if (icicle != null) {
            BundleCompat.clearParcelledData(icicle);
        }
        Log.d(TAG, "callActivityOnCreate activity:"+activity);
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        VirtualCore.get().getComponentDelegate().beforeActivityCreate(activity);
        IBinder token = mirror.android.app.Activity.mToken.get(activity);
        ActivityClientRecord r = VActivityManager.get().getActivityRecord(token);
        if (r != null) {
            r.activity = activity;
        }
        ContextFixer.fixContext(activity);
        ActivityFixer.fixActivity(activity);
        ActivityInfo info = null;
        if (r != null) {
            info = r.info;
        }
        if (info != null) {
            if (info.theme != 0) {
                activity.setTheme(info.theme);
            }
            if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    && info.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                activity.setRequestedOrientation(info.screenOrientation);
            }
        }
        super.callActivityOnCreate(activity, icicle);
        VirtualCore.get().getComponentDelegate().afterActivityCreate(activity);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        if (icicle != null) {
            BundleCompat.clearParcelledData(icicle);
        }
        Log.d(TAG, " enter :"+activity);
        Log.d(TAG, Log.getStackTraceString(new Throwable()));        
        super.callActivityOnCreate(activity, icicle, persistentState);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        VirtualCore.get().getComponentDelegate().beforeActivityResume(activity);
        VActivityManager.get().onActivityResumed(activity);
        super.callActivityOnResume(activity);
        VirtualCore.get().getComponentDelegate().afterActivityResume(activity);
        Intent intent = activity.getIntent();
        Log.d(TAG, " getIntent:"+intent);
        Log.d(TAG, Log.getStackTraceString(new Throwable()));        
        if(intent != null 
            && intent.getComponent() != null 
            && intent.getComponent().getPackageName().equals("com.tencent.tmgp.pubgmhd")
            && intent.getComponent().getClassName().equals("com.epicgames.ue4.GameActivity")){
            //&& intent.getComponent().getPackageName().equals("com.tencent.mobileqq")){
            //&& intent.getComponent().getPackageName().equals("com.UCMobile")){
            //&& intent.getComponent().getPackageName().equals("com.tencent.mtt")){
            //&& intent.getComponent().getPackageName().equals("com.baidu.searchbox")){
            FloatSurfaceView surfaceView = new FloatSurfaceView(activity);
            ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            rootView.addView(surfaceView);
            //NativeEngine.loadHookSo();
//            NativeEngine.loadUrl();  
        }

        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("_VA_|_sender_");
            Log.d(TAG, " getBundleExtra:"+bundle);
            if (bundle != null) {
                IBinder callbackToken = BundleCompat.getBinder(bundle, "_VA_|_ui_callback_");
                IUiCallback callback = IUiCallback.Stub.asInterface(callbackToken);
                Log.d(TAG, " getCallback:"+callback);
                if (callback != null) {
                    try {
                        callback.onAppOpened(VClientImpl.get().getCurrentPackage(), VUserHandle.myUserId());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void callActivityOnDestroy(Activity activity) {
        VirtualCore.get().getComponentDelegate().beforeActivityDestroy(activity);
        super.callActivityOnDestroy(activity);
        Log.d(TAG, "callActivityOnDestroy activity:"+activity);
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        VirtualCore.get().getComponentDelegate().afterActivityDestroy(activity);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        VirtualCore.get().getComponentDelegate().beforeActivityPause(activity);
        super.callActivityOnPause(activity);
        Log.d(TAG, "callActivityOnPause activity:"+activity);
        Log.d(TAG, Log.getStackTraceString(new Throwable()));        
        VirtualCore.get().getComponentDelegate().afterActivityPause(activity);
    }


    @Override
    public void callApplicationOnCreate(Application app) {
        super.callApplicationOnCreate(app);
        Log.d(TAG, "callApplicationOnCreate app:"+app);
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        
    }

}
