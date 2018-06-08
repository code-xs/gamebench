#include <time.h>

#include "art.h"
#include "art_4_4.h"
#include "common.h"

void set_clazz_hookable_art_4_4(JNIEnv *env, jclass type, jobject any_method) {
    art::mirror::ArtMethod *method =
            (art::mirror::ArtMethod *) env->FromReflectedMethod(any_method);

    art::mirror::Class *clazz = reinterpret_cast<art::mirror::Class *>(method->declaring_class_);

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking class flag in art 4.4 begin");
    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", ">original accessFlags is %x",clazz->access_flags_ & 0xffff);//

    clazz->access_flags_ &= ~ACC_FINAL; // remove final
    clazz->access_flags_ &= ~ACC_PRIVATE; // remove private
    clazz->access_flags_ |= ACC_PUBLIC; // add public

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", ">new accessFlags is %x",clazz->access_flags_ & 0xffff);
    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking class flag in art 4.4 end");//
}

void set_method_hookable_4_4(JNIEnv *env, jobject any_method) {
    art::mirror::ArtMethod *method =
            (art::mirror::ArtMethod *) env->FromReflectedMethod(any_method);

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking method flag in art 4.4 begin");
    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", ">original accessFlags is %x",method->access_flags_ & 0xffff);//

    method->access_flags_ = method->access_flags_ & (~0x0002) | 0x0001;

    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", ">new accessFlags is %x",method->access_flags_ & 0xffff);
    //__android_log_print(ANDROID_LOG_ERROR, "hjw-test", "hooking method flag in art 4.4 end");//
}

