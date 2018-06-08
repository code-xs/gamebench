#include <jni.h>
#include <android/log.h>
#include "dalvik.h"
#include "art.h"

static const char *kClassMethodHookChar = "com/kuaishou/performance/cpp/ClassHack";

int apiLevel;
bool isArt;

long set_clazz_hook_able_dalvik(JNIEnv *env, jclass type, jstring targetClazzName) {

    jclass clazz = env->FindClass(env->GetStringUTFChars(targetClazzName, 0));

    ClassObject *mirrorClazz = (ClassObject *) dvmDecodeIndirectRef_fnPtr(
            dvmThreadSelf_fnPtr(), clazz);
    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking class flag in dalvik.. begin");

    mirrorClazz->accessFlags &= ~ACC_FINAL; // remove final
    mirrorClazz->accessFlags &= ~ACC_PRIVATE; // remove private
    mirrorClazz->accessFlags |= ACC_PUBLIC; // add public

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking class flag in dalvik.. end");
    return 0l;
}


long set_clazz_hook_able_art(JNIEnv *env, jclass type, jobject anyMethod) {
    if (apiLevel > 25) {
        set_clazz_hookable_art_8_0(env, type, anyMethod);
    } else if (apiLevel > 24) {
        set_clazz_hookable_art_7_1(env, type, anyMethod);
    } else if (apiLevel > 23) {
        set_clazz_hookable_art_7_0(env, type, anyMethod);
    } else if (apiLevel > 22) {
        set_clazz_hookable_art_6_0(env, type, anyMethod);
    } else if (apiLevel > 21) {
        set_clazz_hookable_art_5_1(env, type, anyMethod);
    } else if (apiLevel > 19) {
        set_clazz_hookable_art_5_0(env, type, anyMethod);
    } else {
        set_clazz_hookable_art_4_4(env, type, anyMethod);
    }
    return 0l;
}


long set_method_hookable(JNIEnv *env, jclass type, jobject anyMethod) {
    if (isArt) {
        if (apiLevel > 25) {
            set_method_hookable_8_0(env, anyMethod);
        } else if (apiLevel > 24) {
            set_method_hookable_7_1(env, anyMethod);
        } else if (apiLevel > 23) {
            set_method_hookable_7_0(env, anyMethod);
        } else if (apiLevel > 22) {
            set_method_hookable_6_0(env, anyMethod);
        } else if (apiLevel > 21) {
            set_method_hookable_5_1(env, anyMethod);
        } else if (apiLevel > 19) {
            set_method_hookable_5_0(env, anyMethod);
        } else {
            set_method_hookable_4_4(env, anyMethod);
        }
    } else {
        Method *method = (Method *) env->FromReflectedMethod(anyMethod);
//        __android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking method flag in dalvik begin");
//
//        __android_log_print(ANDROID_LOG_ERROR, "hjw-test", "name is %s", method->name);
//        __android_log_print(ANDROID_LOG_ERROR, "hjw-test", ">original accessFlags is %x",
//                            method->accessFlags & 0xffff);//

//    __android_log_print(ANDROID_LOG_ERROR, "hjw-test", "clazz is %x", method->clazz);
//    __android_log_print(ANDROID_LOG_ERROR, "hjw-test", "methodIndex is %x", method->methodIndex);
//    __android_log_print(ANDROID_LOG_ERROR, "hjw-test", "registersSize is %x", method->registersSize);
//    __android_log_print(ANDROID_LOG_ERROR, "hjw-test", "outsSize is %x", method->outsSize);
//    __android_log_print(ANDROID_LOG_ERROR, "hjw-test", "insSize is %x", method->insSize);

        method->accessFlags &= ~ACC_FINAL; // remove final
        method->accessFlags &= ~ACC_PRIVATE; // remove private
        method->accessFlags |= ACC_PUBLIC; // add public

//        __android_log_print(ANDROID_LOG_ERROR, "hjw-test", ">new accessFlags is %x",
//                            method->accessFlags & 0xffff);
//        __android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking method flag in dalvik end");//
    }
    return 0l;
}


static void *dvm_dlsym(void *hand, const char *name) {
    void *ret = dlsym(hand, name);
    char msg[1024] = {0};
    snprintf(msg, sizeof(msg) - 1, "0x%x", ret);
    LOGD("%s = %s\n", name, msg);
    return ret;
}


jboolean __attribute__ ((visibility ("hidden"))) art_setup(JNIEnv *env, int level) {
    return JNI_TRUE;
}

jboolean __attribute__ ((visibility ("hidden"))) dalvik_setup(
        JNIEnv *env, int apilevel) {
    void *dvm_hand = dlopen("libdvm.so", RTLD_NOW);
    if (dvm_hand) {
        dvmDecodeIndirectRef_fnPtr = (dvmDecodeIndirectRef_func) dvm_dlsym(dvm_hand,
                                                                           apilevel > 10 ?
                                                                           "_Z20dvmDecodeIndirectRefP6ThreadP8_jobject"
                                                                                         :
                                                                           "dvmDecodeIndirectRef");
        if (!dvmDecodeIndirectRef_fnPtr) {
            return JNI_FALSE;
        }
        dvmThreadSelf_fnPtr = (dvmThreadSelf_func) dvm_dlsym(dvm_hand,
                                                             apilevel > 10 ? "_Z13dvmThreadSelfv"
                                                                           : "dvmThreadSelf");
        if (!dvmThreadSelf_fnPtr) {
            return JNI_FALSE;
        }
        jclass clazz = env->FindClass("java/lang/reflect/Method");
        jClassMethod = env->GetMethodID(clazz, "getDeclaringClass",
                                        "()Ljava/lang/Class;");
        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}


jboolean setup(JNIEnv *env, jclass clazz, jboolean isart,
               jint apilevel) {
    isArt = isart;
    apiLevel = apilevel;

    LOGD("vm is: %s , apilevel is: %i", (isArt ? "art" : "dalvik"),
         (int) apilevel);
    if (isArt) {
        return art_setup(env, (int) apilevel);
    } else {
        return dalvik_setup(env, (int) apilevel);
    }
}


static JNINativeMethod gMethods[] = {
        {"setup",                           "(ZI)Z",                               (void *) setup},
        {"hack_set_constructor_hook_able",  "(Ljava/lang/reflect/Constructor;)J", (void *) set_method_hookable},
        {"hack_set_method_hook_able",       "(Ljava/lang/reflect/Method;)J",      (void *) set_method_hookable},
        {"hack_set_clazz_hook_able_dalvik", "(Ljava/lang/String;)J",               (void *) set_clazz_hook_able_dalvik},
        {"hack_set_clazz_hook_able_art",    "(Ljava/lang/reflect/Constructor;)J",  (void *) set_clazz_hook_able_art}
};
/*
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return JNI_FALSE;
    }
    jclass classEvaluateUtil = env->FindClass(kClassMethodHookChar);
    if (env->RegisterNatives(classEvaluateUtil, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) <
        0) {
        return JNI_FALSE;
    }
    return JNI_VERSION_1_4;
}
*/

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
/*
    return initialize(vm, [] {
        nativeEngineClass = findClassStatic("com/lody/virtual/client/NativeEngine");
        nativeEngineClass->registerNatives({
                        makeNativeMethod("nativeEnableIORedirect",
                                         jni_nativeEnableIORedirect),
                        makeNativeMethod("nativeIOWhitelist",
                                         jni_nativeIOWhitelist),
                        makeNativeMethod("nativeIOForbid",
                                         jni_nativeIOForbid),
                        makeNativeMethod("nativeIORedirect",
                                         jni_nativeIORedirect),
                        makeNativeMethod("nativeGetRedirectedPath",
                                         jni_nativeGetRedirectedPath),
                        makeNativeMethod("nativeReverseRedirectedPath",
                                         jni_nativeReverseRedirectedPath),
                        makeNativeMethod("nativeLaunchEngine",
                                         jni_nativeLaunchEngine),
                }
        );
    });
    */
    return NULL;
}


