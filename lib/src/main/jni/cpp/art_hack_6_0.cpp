
#include <time.h>

#include "art.h"
#include "art_6_0.h"
#include "common.h"

void set_clazz_hookable_art_6_0(JNIEnv *env, jclass type, jobject any_method) {

    art::mirror::ArtMethod *method =
            (art::mirror::ArtMethod *) env->FromReflectedMethod(any_method);

    art::mirror::Class *clazz = reinterpret_cast<art::mirror::Class *>(method->declaring_class_);

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking class flag in art 6.0 begin");

    clazz->access_flags_ &= ~ACC_FINAL; // remove final
    clazz->access_flags_ &= ~ACC_PRIVATE; // remove private
    clazz->access_flags_ |= ACC_PUBLIC; // add public

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking class flag in art 6.0 end");//
}

void set_method_hookable_6_0(JNIEnv *env, jobject any_method) {
    art::mirror::ArtMethod *method =
            (art::mirror::ArtMethod *) env->FromReflectedMethod(any_method);

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking method flag in art 6.0 begin");

    method->access_flags_ = method->access_flags_ & (~0x0002) | 0x0001;

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking method flag in art 6.0 end");//
}
