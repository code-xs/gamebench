package com.lody.virtual.client;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import com.android.dx.stock.ProxyBuilder;
import android.view.WindowManager;
import android.content.Context;
import android.view.InputEvent;

/**
 * 需要提前反射出来备用的Class和Field
 */
public class ReflectCommon {
  public static final String ANDROID_VIEW_WINDOW_MANAGER_GLOBAL =
      "android.view.WindowManagerGlobal";
  public static final String ANDROID_VIEW_WINDOW_MANAGER_IMPL = "android.view.WindowManagerImpl";
  public static final String ANDROID_VIEW_VIEW_ROOT_IMPL = "android.view.ViewRootImpl";
  public static final String COM_ANDROID_INTERNAL_POLICY_DECOR_CONTEXT =
      "com.android.internal.policy.DecorContext";
  public static final String M_PHONE_WINDOW = "mPhoneWindow";
  public static final String ANDROID_VIEW_WINDOW = "android.view.Window";
  public static final String ANDROID_VIEW_VIEW_ROOT_IMPL$_TRAVERSAL_RUNNABLE =
      "android.view.ViewRootImpl$TraversalRunnable";
  public static final String M_TRAVERSAL_RUNNABLE = "mTraversalRunnable";
  public static final String M_CONTEXT = "mContext";
  public static final String M_APP_NAME = "mAppName";
  public static final String ANDROID_APP_ACTIVITY_MANAGER_NATIVE =
      "android.app.ActivityManagerNative";
  public static final String G_DEFAULT = "gDefault";
  public static final String ANDROID_UTIL_SINGLETON = "android.util.Singleton";
  public static final String M_INSTANCE = "mInstance";
  public static final String ANDROID_APP_IACTIVITY_MANAGER = "android.app.IActivityManager";
  public static final String ANDROID_APP_ACTIVITY_MANAGER = "android.app.ActivityManager";
  public static final String ANDROID_APP_ACTIVITY_THREAD = "android.app.ActivityThread";
  public static final String S_CURRENT_ACTIVITY_THREAD = "sCurrentActivityThread";
  public static final String M_H = "mH";
  public static final String M_CALLBACK = "mCallback";
  public static final String M_GLOBAL = "mGlobal";
  public static final String M_ROOTS = "mRoots";
  public static final String I_ACTIVITY_MANAGER_SINGLETON = "IActivityManagerSingleton";
  public static final String ANDROID_VIEW_VIEW_ROOT_IMPL$_QUEUED_INPUT_EVENT = "android.view.ViewRootImpl$QueuedInputEvent";
  public static final String ANDROID_VIEW_VIEW_ROOT_IMPL$_QUEUED_INPUT_EVENT_RECEIVER = "android.view.ViewRootImpl$WindowInputEventReceiver";
  public static final String ANDROID_VIEW_VIEW_ROOT_IMPL$_NATIVE_PREIME_INPUT_STAGE= "android.view.ViewRootImpl$NativePreImeInputStage";
  public static final String ANDROID_VIEW_VIEW_ROOT_IMPL$_INPUT_STAGE = "android.view.ViewRootImpl$InputStage";

  public static Class<?> sCLASS_WINDOW_MANAGER_GLOBAL;
  public static Class<?> sCLASS_WINDOW_MANAGER_IMPL;
  public static Class<?> sCLASS_VIEW_ROOT_IMPL;
  public static Class<?> sCLASS_DECOR_CONTEXT;
  public static Class<?> sCLASS_WINDOW;
  public static Class<?> sCLASS_VIEW_ROOT_IMPL$TRAVERSAL_RUNNABLE;

  public static Class<?> sCLASS_ACTIVITY_MANAGER_NATIVE;
  public static Class<?> sCLASS_SINGLETON;
  public static Class<?> sCLASS_I_ACTIVITY_MANAGER;

  public static Class<?> sCLASS_ACTIVITY_THREAD;

  public static Class<?> sCLASS_QUEUED_INPUT_EVENT;
  public static Class<?> sCLASS_WINDOW_INPUT_EVENT_RECEIVER;
  public static Class<?> sCLASS_NATIVE_PRE_IME_INPUT_STAGE;
  public static Class<?> sCLASS_INPUT_STAGE;
  public static Class<?> sCLASS_VIEW;

  public static Method sMethod_onInputEvent_WINDOW_INPUT_EVENT_RECEIVER;
  
  public static Field sFIELD_mTraversalRunnable_CLASS_VIEW_ROOT_IMPL;
  public static Field sFIELD_mContext_CLASS_VIEW_ROOT_IMPL;
  public static Field sFIELD_mPhoneWindow_CLASS_DECOR_CONTEXT;
  public static Field sFIELD_mAppNameField_CLASS_WINDOW;
  public static Field sFIELD_gDefault_CLASS_ACTIVITY_MANAGER_NATIVE;
  // for 8.0 android IActivityManagerSingleton
  public static Field sFIELD_IActivityManagerSingleton_CLASS_ACTIVITY_MANAGER;
  public static Field sFIELD_mInstance_CLASS_SINGLETON;
  public static Field sFIELD_sCurrentActivityThread_CLASS_ACTIVITY_THREAD;
  public static Field sFIELD_mH_CLASS_ACTIVITY_THREAD;
  public static Field sFIELD_mCallback_CLASS_HANDLER;
  public static Field sFIELD_mGlobal_CLASS_WINDOW_MANAGER_IMPL;
  public static Field sFIELD_mRoots_CLASS_WINDOW_MANAGER_GLOBAL;
  public static Field sFIELD_sDefaultWindowManager_CLASS_WINDOW_MANAGER_GLOBAL;
  public static Field sFIELD_mWindowControllerCallback_CLASS_WINDOW;
  public static Field sFIELD_mFirstInputStage_VIEWROOT_IMPL;


  private static boolean sIsInited = false;

  public static void init(Context context) {
    if (sIsInited) {
      return;
    }

    WindowManager windowManager =
        (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

    try {
      sCLASS_WINDOW_MANAGER_GLOBAL = Class.forName(ANDROID_VIEW_WINDOW_MANAGER_GLOBAL);

      sFIELD_mRoots_CLASS_WINDOW_MANAGER_GLOBAL =
          sCLASS_WINDOW_MANAGER_GLOBAL.getDeclaredField(M_ROOTS);
      sFIELD_mRoots_CLASS_WINDOW_MANAGER_GLOBAL.setAccessible(true);

      sFIELD_sDefaultWindowManager_CLASS_WINDOW_MANAGER_GLOBAL =
          ReflectCommon.sCLASS_WINDOW_MANAGER_GLOBAL.getDeclaredField("sDefaultWindowManager");
      sFIELD_sDefaultWindowManager_CLASS_WINDOW_MANAGER_GLOBAL.setAccessible(true);

      sCLASS_WINDOW_MANAGER_IMPL = Class.forName(ANDROID_VIEW_WINDOW_MANAGER_IMPL);
      sFIELD_mGlobal_CLASS_WINDOW_MANAGER_IMPL =
          sCLASS_WINDOW_MANAGER_IMPL.getDeclaredField(M_GLOBAL);
      sFIELD_mGlobal_CLASS_WINDOW_MANAGER_IMPL.setAccessible(true);

        sCLASS_VIEW_ROOT_IMPL = Class.forName(ANDROID_VIEW_VIEW_ROOT_IMPL);
        ClassHack.setClassUnfinal(sCLASS_VIEW_ROOT_IMPL);
        for (Constructor constructor : sCLASS_VIEW_ROOT_IMPL.getDeclaredConstructors()) {
            ClassHack.setConstructorHookable(constructor);
            constructor.setAccessible(true);
        }

     sCLASS_QUEUED_INPUT_EVENT = Class.forName(ANDROID_VIEW_VIEW_ROOT_IMPL$_QUEUED_INPUT_EVENT);
     ClassHack.setClassUnfinal(sCLASS_QUEUED_INPUT_EVENT);
     for (Constructor constructor : sCLASS_QUEUED_INPUT_EVENT.getDeclaredConstructors()) {
        ClassHack.setConstructorHookable(constructor);
        constructor.setAccessible(true);
     }

     sCLASS_WINDOW_INPUT_EVENT_RECEIVER = Class.forName(ANDROID_VIEW_VIEW_ROOT_IMPL$_QUEUED_INPUT_EVENT_RECEIVER);
     ClassHack.setClassUnfinal(sCLASS_WINDOW_INPUT_EVENT_RECEIVER);
     for (Constructor constructor : sCLASS_WINDOW_INPUT_EVENT_RECEIVER.getDeclaredConstructors()) {
        ClassHack.setConstructorHookable(constructor);
        constructor.setAccessible(true);
     }

    sMethod_onInputEvent_WINDOW_INPUT_EVENT_RECEIVER = sCLASS_WINDOW_INPUT_EVENT_RECEIVER.getDeclaredMethod("onInputEvent", InputEvent.class);
    sMethod_onInputEvent_WINDOW_INPUT_EVENT_RECEIVER.setAccessible(true);

    sCLASS_NATIVE_PRE_IME_INPUT_STAGE = Class.forName(ANDROID_VIEW_VIEW_ROOT_IMPL$_NATIVE_PREIME_INPUT_STAGE);
    ClassHack.setClassUnfinal(sCLASS_NATIVE_PRE_IME_INPUT_STAGE);
    for (Constructor constructor : sCLASS_NATIVE_PRE_IME_INPUT_STAGE.getDeclaredConstructors()) {
      ClassHack.setConstructorHookable(constructor);
      constructor.setAccessible(true);
    }

    sCLASS_INPUT_STAGE = Class.forName(ANDROID_VIEW_VIEW_ROOT_IMPL$_INPUT_STAGE);
    ClassHack.setClassUnfinal(sCLASS_INPUT_STAGE);
    for (Constructor constructor : sCLASS_INPUT_STAGE.getDeclaredConstructors()) {
      ClassHack.setConstructorHookable(constructor);
      constructor.setAccessible(true);
    }    

    sFIELD_mFirstInputStage_VIEWROOT_IMPL = sCLASS_VIEW_ROOT_IMPL.getDeclaredField("mFirstInputStage");
    sFIELD_mFirstInputStage_VIEWROOT_IMPL.setAccessible(true);
            
    sCLASS_VIEW = Class.forName("android.view.View");

      sCLASS_WINDOW = Class.forName(ANDROID_VIEW_WINDOW);
      
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        sCLASS_DECOR_CONTEXT = Class.forName(COM_ANDROID_INTERNAL_POLICY_DECOR_CONTEXT);

        sFIELD_mPhoneWindow_CLASS_DECOR_CONTEXT =
            sCLASS_DECOR_CONTEXT.getDeclaredField(M_PHONE_WINDOW);
        sFIELD_mPhoneWindow_CLASS_DECOR_CONTEXT.setAccessible(true);

        sFIELD_mWindowControllerCallback_CLASS_WINDOW =
            ReflectCommon.sCLASS_WINDOW.getDeclaredField("mWindowControllerCallback");
        sFIELD_mWindowControllerCallback_CLASS_WINDOW.setAccessible(true);
      }

      sCLASS_VIEW_ROOT_IMPL$TRAVERSAL_RUNNABLE =
          Class.forName(ANDROID_VIEW_VIEW_ROOT_IMPL$_TRAVERSAL_RUNNABLE);

      sFIELD_mTraversalRunnable_CLASS_VIEW_ROOT_IMPL =
          sCLASS_VIEW_ROOT_IMPL.getDeclaredField(M_TRAVERSAL_RUNNABLE);
      sFIELD_mTraversalRunnable_CLASS_VIEW_ROOT_IMPL.setAccessible(true);

      sFIELD_mContext_CLASS_VIEW_ROOT_IMPL = sCLASS_VIEW_ROOT_IMPL.getDeclaredField(M_CONTEXT);
      sFIELD_mContext_CLASS_VIEW_ROOT_IMPL.setAccessible(true);


      sFIELD_mAppNameField_CLASS_WINDOW = sCLASS_WINDOW.getDeclaredField(M_APP_NAME);
      sFIELD_mAppNameField_CLASS_WINDOW.setAccessible(true);

      sCLASS_ACTIVITY_MANAGER_NATIVE = Class.forName(ANDROID_APP_ACTIVITY_MANAGER_NATIVE);

      if (false) {
        Class sClass_ACTIVITY_MANAGER = Class.forName(ANDROID_APP_ACTIVITY_MANAGER);
        sFIELD_IActivityManagerSingleton_CLASS_ACTIVITY_MANAGER =
            sClass_ACTIVITY_MANAGER.getDeclaredField(I_ACTIVITY_MANAGER_SINGLETON);
        sFIELD_IActivityManagerSingleton_CLASS_ACTIVITY_MANAGER.setAccessible(true);
      } else {
        sFIELD_gDefault_CLASS_ACTIVITY_MANAGER_NATIVE =
            sCLASS_ACTIVITY_MANAGER_NATIVE.getDeclaredField(G_DEFAULT);
        sFIELD_gDefault_CLASS_ACTIVITY_MANAGER_NATIVE.setAccessible(true);
      }

      sCLASS_SINGLETON = Class.forName(ANDROID_UTIL_SINGLETON);

      sFIELD_mInstance_CLASS_SINGLETON = sCLASS_SINGLETON.getDeclaredField(M_INSTANCE);
      sFIELD_mInstance_CLASS_SINGLETON.setAccessible(true);

      sCLASS_I_ACTIVITY_MANAGER = Class.forName(ANDROID_APP_IACTIVITY_MANAGER);

      sCLASS_ACTIVITY_THREAD = Class.forName(ANDROID_APP_ACTIVITY_THREAD);

      sFIELD_sCurrentActivityThread_CLASS_ACTIVITY_THREAD =
          ReflectCommon.sCLASS_ACTIVITY_THREAD.getDeclaredField(S_CURRENT_ACTIVITY_THREAD);
      sFIELD_sCurrentActivityThread_CLASS_ACTIVITY_THREAD.setAccessible(true);

      sFIELD_mH_CLASS_ACTIVITY_THREAD = ReflectCommon.sCLASS_ACTIVITY_THREAD.getDeclaredField(M_H);
      sFIELD_mH_CLASS_ACTIVITY_THREAD.setAccessible(true);

      sFIELD_mCallback_CLASS_HANDLER = Handler.class.getDeclaredField(M_CALLBACK);
      sFIELD_mCallback_CLASS_HANDLER.setAccessible(true);
    } catch (Throwable throwable) {
    }
    sIsInited = true;
  }

  public static boolean isInited() {
    return sIsInited;
  }
}
