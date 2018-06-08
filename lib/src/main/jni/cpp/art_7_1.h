#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <string>
#include <memory>
#include <sys/mman.h>

#include <fcntl.h>
#include <dlfcn.h>

#include <stdint.h>    /* C99 */

namespace art {
    namespace mirror {
        class Object {
        public:
            // The number of vtable entries in java.lang.Object.
            static constexpr size_t kVTableLength = 11;
            static uint32_t hash_code_seed;
            uint32_t klass_;

            uint32_t monitor_;
        };

        class Class : public Object {
        public:
            enum Status {
                kStatusRetired = -2, // Retired, should not be used. Use the newly cloned one instead.
                kStatusError = -1,
                kStatusNotReady = 0,
                kStatusIdx = 1, // Loaded, DEX idx in super_class_type_idx_ and interfaces_type_idx_.
                kStatusLoaded = 2,  // DEX idx values resolved.
                kStatusResolving = 3,  // Just cloned from temporary class object.
                kStatusResolved = 4,  // Part of linking.
                kStatusVerifying = 5,  // In the process of being verified.
                kStatusRetryVerificationAtRuntime = 6, // Compile time verification failed, retry at runtime.
                kStatusVerifyingAtRuntime = 7,  // Retrying verification at runtime.
                kStatusVerified = 8,  // Logically part of linking; done pre-init.
                kStatusInitializing = 9,  // Class init in progress.
                kStatusInitialized = 10,  // Ready to go.
                kStatusMax = 11,
            };

            static constexpr uint32_t kClassWalkSuper = 0xC0000000;

            static constexpr size_t kImtSize = 0;    //IMT_SIZE;

            uint32_t class_loader_;

            uint32_t component_type_;

            uint32_t dex_cache_;

            // 7.1.1
            uint32_t dex_cache_string_;

            uint32_t iftable_;

            uint32_t name_;

            uint32_t super_class_;

            uint32_t verify_error_class_;

            uint32_t vtable_;

            uint32_t access_flags_;

            uint64_t direct_methods_;

            uint64_t ifields_;

            uint64_t sfields_;

            uint64_t virtual_methods_;

            uint32_t class_size_;


            pid_t clinit_thread_id_;

            // TODO: really 16bits
            int32_t dex_class_def_idx_;

            // TODO: really 16bits
            int32_t dex_type_idx_;

            uint32_t num_direct_methods_;

            uint32_t num_instance_fields_;

            uint32_t num_reference_instance_fields_;

            uint32_t num_reference_static_fields_;

            uint32_t num_static_fields_;

            uint32_t num_virtual_methods_;

            uint32_t object_size_;

            uint32_t primitive_type_;

            uint32_t reference_instance_offsets_;

            Status status_;

            static uint32_t java_lang_Class_;
        };

        class ArtField {
        public:
            uint32_t declaring_class_;
            uint32_t access_flags_;
            uint32_t field_dex_idx_;
            uint32_t offset_;
        };

        class ArtMethod {
        public:
            uint32_t declaring_class_;

            uint32_t access_flags_;

            uint32_t dex_code_item_offset_;

            uint32_t dex_method_index_;

            uint16_t method_index_;

            uint16_t hotness_count_;

            struct PtrSizedFields {
                // Short cuts to declaring_class_->dex_cache_ member for fast compiled code access.
                ArtMethod **dex_cache_resolved_methods_;

                // Short cuts to declaring_class_->dex_cache_ member for fast compiled code access.
                void *dex_cache_resolved_types_;

                // Pointer to JNI function registered to this method, or a function to resolve the JNI function,
                // or the profiling data for non-native methods, or an ImtConflictTable.
                void *entry_point_from_jni_;

                // Method dispatch from quick compiled code invokes this pointer which may cause bridging into
                // the interpreter.
                void *entry_point_from_quick_compiled_code_;
            } ptr_sized_fields_;
        };

    }

}
