package com.lody.virtual.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.os.Build;
import android.util.Log;

//import com.getkeepsafe.relinker.ReLinker;
//import com.kuaishou.performance.PerformanceMonitor;
//import com.kuaishou.performance.context.ContextManager;

/**
 * hack虚拟机里面的类定义，让类变得可以被动态代理
 */
public class ClassHack {
  private static final String TAG = "ClassHack";
  private static final String VM_VERSION = System.getProperty("java.vm.version");
  private static final boolean IS_ART = VM_VERSION != null && VM_VERSION.startsWith("2.");

  private static boolean sIsSetupSuccess = false;

  static {
    //ReLinker.recursively().loadLibrary(ContextManager.getInstance().getContext(), "method-hook-lib",
        //ContextManager.getInstance().getVersionCode());
        System.loadLibrary("method-hook-lib");
  }

  private static native boolean setup(boolean isArt, int apilevel);

  private static native long hack_set_clazz_hook_able_dalvik(String targetClazzName);

  private static native long hack_set_clazz_hook_able_art(Constructor constructor);

  private static native long hack_set_constructor_hook_able(Constructor constructor);

  private static native long hack_set_method_hook_able(Method method);

  /**
   * 本质上就是让这个class变成非final
   * 
   * @param clazz "android/view/ViewRootImpl$TraversalRunnable"
   */
  public static void setClassUnfinal(Class clazz) {
    if (IS_ART) {
      try {
        Constructor constructor = clazz.getDeclaredConstructors()[0];
        Log.d(TAG, "art, setClassUnfinal is art  !!!  constructor:"+constructor);
        if (constructor != null) {
          // Art上用method去拿ClassObj
          hack_set_clazz_hook_able_art(constructor);
        } else {
          throw new Exception("can not get constructor from a class!!!");
        }
      } catch (Exception e) {
        //PerformanceMonitor.getInstance().getPerformancePlatformConfig()
        //    .onThrowableError("set_class_unfinal_error", e);
        Log.e(TAG, "art, setClassUnfinal failed!", e);
      }
    } else {
      // dalvik上用/的类名去在native层反射ClassObj
      Log.d(TAG, "art, setClassUnfinal is dalvik !!!");
      hack_set_clazz_hook_able_dalvik(clazz.getName().replace(".", "/"));
    }
  }

  /**
   * 将构造函数变成public，非final
   */
  public static void setConstructorHookable(Constructor constructor) {
    hack_set_constructor_hook_able(constructor);
  }

  /**
   * 将普通函数变成public，非final
   */
  public static void setMethodHookable(Method method) {
    hack_set_method_hook_able(method);
  }

  public static boolean setup() {
    if (sIsSetupSuccess) {
      return true;
    }
    try {
      int apiLevel = Build.VERSION.SDK_INT;
      sIsSetupSuccess = setup(IS_ART, apiLevel);
      return sIsSetupSuccess;
    } catch (Exception e) {
      //PerformanceMonitor.getInstance().getPerformancePlatformConfig()
      //    .onThrowableError("class hack setup failed！", e);
      Log.e(TAG, "setup env: ", e);
      return false;
    }
  }

  public static boolean isSetupSuccessed() {
    return sIsSetupSuccess;
  }
}
