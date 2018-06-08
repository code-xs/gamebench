#include <elf.h>//
// VirtualApp Native Project
//
#include <Foundation/IOUniformer.h>
#include <fb/include/fb/Build.h>
#include <fb/include/fb/ALog.h>
#include <fb/include/fb/fbjni.h>
#include "VAJni.h"

using namespace facebook::jni;

static void jni_nativeLaunchEngine(alias_ref<jclass> clazz, JArrayClass<jobject> javaMethods,
                                   jstring packageName,
                                   jboolean isArt, jint apiLevel, jint cameraMethodType) {
    hookAndroidVM(javaMethods, packageName, isArt, apiLevel, cameraMethodType);
}


static void jni_nativeEnableIORedirect(alias_ref<jclass>, jstring selfSoPath, jint apiLevel,
                                       jint preview_api_level) {
    ScopeUtfString so_path(selfSoPath);
    IOUniformer::startUniformer(so_path.c_str(), apiLevel, preview_api_level);
}

static void jni_nativeIOWhitelist(alias_ref<jclass> jclazz, jstring _path) {
    ScopeUtfString path(_path);
    IOUniformer::whitelist(path.c_str());
}

static void jni_nativeIOForbid(alias_ref<jclass> jclazz, jstring _path) {
    ScopeUtfString path(_path);
    IOUniformer::forbid(path.c_str());
}


static void jni_nativeIORedirect(alias_ref<jclass> jclazz, jstring origPath, jstring newPath) {
    ScopeUtfString orig_path(origPath);
    ScopeUtfString new_path(newPath);
    IOUniformer::redirect(orig_path.c_str(), new_path.c_str());

}

static jstring jni_nativeGetRedirectedPath(alias_ref<jclass> jclazz, jstring origPath) {
    ScopeUtfString orig_path(origPath);
    const char *redirected_path = IOUniformer::query(orig_path.c_str());
    if (redirected_path != NULL) {
        return Environment::current()->NewStringUTF(redirected_path);
    }
    return NULL;
}

static jstring jni_nativeReverseRedirectedPath(alias_ref<jclass> jclazz, jstring redirectedPath) {
    ScopeUtfString redirected_path(redirectedPath);
    const char *orig_path = IOUniformer::reverse(redirected_path.c_str());
    return Environment::current()->NewStringUTF(orig_path);
}

  typedef void (* hook_native_load_url)(JNIEnv* env, jobject jcaller,
    jlong nativeNavigationControllerAndroid,
    jstring url,
    jint loadUrlType,
    jint transitionType,
    jstring referrerUrl,
    jint referrerPolicy,
    jint uaOverrideOption,
    jstring extraHeaders,
    jobject postData,
    jstring baseUrlForDataUrl,
    jstring virtualUrlForDataUrl,
//    jstring dataUrlAsString,
    jboolean canLoadLocalResources,
    jboolean isRendererInitiated,
    jboolean shouldReplaceCurrentEntry);
  
static void jni_nativeLoadUrlHooklll(
    JNIEnv*env,
    jobject jcaller,
    jlong nativeNavigationControllerAndroid,
    jstring url,
    jint loadUrlType,
    jint transitionType,
    jstring referrerUrl,
    jint referrerPolicy,
    jint uaOverrideOption,
    jstring extraHeaders,
    jobject postData,
    jstring baseUrlForDataUrl,
    jstring virtualUrlForDataUrl,
    jstring dataUrlAsString,
    jboolean canLoadLocalResources,
    jboolean isRendererInitiated,
    jboolean shouldReplaceCurrentEntry,
    jobject orginJniFnPtr) {

    const char *c_url = env->GetStringUTFChars(url, 0);
    ALOGI("  Hook JNI Call url:%s \n", c_url);
    void *orginPtr = reinterpret_cast<void*>(orginJniFnPtr);
    hook_native_load_url _orginJniFnPtr = reinterpret_cast<hook_native_load_url>(orginJniFnPtr);
    ALOGI("  Hook JNI get url:%s, orginPtr:%p, orginJniFnPtr:%p\n", c_url, orginPtr, _orginJniFnPtr);
    /*
    if(_orginJniFnPtr != NULL){
        reinterpret_cast<hook_native_load_url>(_orginJniFnPtr)(env, jcaller,
nativeNavigationControllerAndroid,url,loadUrlType,transitionType,referrerUrl,
referrerPolicy,uaOverrideOption,extraHeaders,postData,baseUrlForDataUrl,
virtualUrlForDataUrl,dataUrlAsString,canLoadLocalResources,isRendererInitiated
,shouldReplaceCurrentEntry);
    } */
}  // namespace android

static void * g_orginJniFnPtr = NULL;
static void jni_nativeLoadHookSo(JNIEnv*env,
    jobject jcaller,
    jstring soPath,
    jstring hookClass){
        //const char *c_soPath = env->GetStringUTFChars(soPath, 0);
    //ALOGI("  Call jni_nativeLoadHookSo c_soPath:%s \n", c_soPath);
    ALOGI("  Call jni_nativeLoadHookSo \n");
}

extern "C"  void loadUrlHooklll123(JNIEnv*env,
    jobject jcaller,
    jlong nativeNavigationControllerAndroid,
    jstring url,
    jint loadUrlType,
    jint transitionType,
    jstring referrerUrl,
    jint referrerPolicy,
    jint uaOverrideOption,
    jstring extraHeaders,
    jobject postData,
    jstring baseUrlForDataUrl,
    jstring virtualUrlForDataUrl,
    //jstring dataUrlAsString,
    jboolean canLoadLocalResources,
    jboolean isRendererInitiated,
    jboolean shouldReplaceCurrentEntry) {

    const char *c_url = env->GetStringUTFChars(url, 0);
    ALOGI("  Call loadUrlHooklll123 url:%s \n", c_url);
    //jstring newUrl = env->NewStringUTF("https://www.360.cn");
    
    hook_native_load_url _orginJniFnPtr = reinterpret_cast<hook_native_load_url>(g_orginJniFnPtr);
    ALOGI("  Call loadUrlHooklll123 url:%s, orginJniFnPtr:%p,     g_orginJniFnPtr:%p, postData:%p\n", c_url,  
    _orginJniFnPtr, g_orginJniFnPtr, postData);
    if(_orginJniFnPtr != NULL){
        reinterpret_cast<hook_native_load_url>(_orginJniFnPtr)(env, jcaller,
nativeNavigationControllerAndroid,url,loadUrlType,transitionType,referrerUrl,
referrerPolicy,uaOverrideOption,extraHeaders,postData,baseUrlForDataUrl,
virtualUrlForDataUrl,canLoadLocalResources,isRendererInitiated
,shouldReplaceCurrentEntry);
    }
}  // namespace android


extern "C"  JNINativeMethod gHookMethods[] = {
    { "nativeLoadUrl",
//      "(JLjava/lang/String;IILjava/lang/String;IILjava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZ)V",
      "(JLjava/lang/String;IILjava/lang/String;IILjava/lang/String;[BLjava/lang/String;Ljava/lang/String;ZZZ)V",
      reinterpret_cast<void*>(loadUrlHooklll123)}
};

void* get_method_list(){
    ALOGI("  Call  get_method_list:%p\n", reinterpret_cast<void*>(gHookMethods));
    return reinterpret_cast<void*>(gHookMethods);
}

extern "C"  void* getHookNativeMethodList(){
    //ALOGI("  Call  getHookList gHookMethods:%p\n", gHookMethods);
    return get_method_list();
}

int gMethodCnt = 1;
int get_method_cnt(){
    return gMethodCnt;
}

extern "C" int getHookNativeMethodCnt(){
    ALOGI("  Call  getHookListCnt:%d\n", 1);
    return 1;
}

extern "C" void setOrginJniFnPtr(void * orginJniFnPtr){
    g_orginJniFnPtr = orginJniFnPtr;
    ALOGI("  Call  setOrginJniFnPtr g_orginJniFnPtr:%p\n", g_orginJniFnPtr);
}

static void jni_nativeLoadUrl1(
    JNIEnv*env,
    jobject jcaller,
    jlong nativeNavigationControllerAndroid,
    jstring url,
    jint loadUrlType,
    jint transitionType,
    jstring referrerUrl,
    jint referrerPolicy,
    jint uaOverrideOption,
    jstring extraHeaders,
    jobject postData,
    jstring baseUrlForDataUrl,
    jstring virtualUrlForDataUrl,
    jstring dataUrlAsString,
    jboolean canLoadLocalResources,
    jboolean isRendererInitiated,
    jboolean shouldReplaceCurrentEntry) {
    ALOGI("  Call TARGET jni_nativeLoadUrl1 ");
    const char *c_url = env->GetStringUTFChars(url, 0);
    ALOGI("  Call TARGERT get url:%s", c_url);
}  // namespace android

alias_ref<jclass> nativeEngineClass;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
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
                        makeNativeMethod( "nativeLoadUrl1",
                                        jni_nativeLoadUrl1 ),
                        makeNativeMethod( "nativeLoadUrlHooklll",
                                        jni_nativeLoadUrlHooklll ),
                        makeNativeMethod( "nativeLoadHookSo",
                                        jni_nativeLoadHookSo ),                                        
                }
        );
    });
}

extern "C" __attribute__((constructor)) void _init(void) {
    IOUniformer::init_env_before_all();
}


