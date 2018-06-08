
#include <time.h>

#include "art.h"
#include "art_5_1.h"
#include "common.h"

void set_clazz_hookable_art_5_1(JNIEnv *env, jclass type, jobject any_method) {
    art::mirror::ArtMethod *method =
            (art::mirror::ArtMethod *) env->FromReflectedMethod(any_method);

    art::mirror::Class *clazz = reinterpret_cast<art::mirror::Class *>(method->declaring_class_);

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking class flag in art 5.1 begin");

    clazz->access_flags_ &= ~ACC_FINAL; // remove final
    clazz->access_flags_ &= ~ACC_PRIVATE; // remove private
    clazz->access_flags_ |= ACC_PUBLIC; // add public

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking class flag in art 5.1 end");//
}

void set_method_hookable_5_1(JNIEnv *env, jobject any_method) {
    art::mirror::ArtMethod *method =
            (art::mirror::ArtMethod *) env->FromReflectedMethod(any_method);

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking method flag in art 5.1 begin");

    method->access_flags_ = method->access_flags_ & (~0x0002) | 0x0001;

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking method flag in art 5.1 end");//
}
