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
                kStatusRetired = -3,  // Retired, should not be used. Use the newly cloned one instead.
                kStatusErrorResolved = -2,
                kStatusErrorUnresolved = -1,
                kStatusNotReady = 0,
                kStatusIdx = 1,  // Loaded, DEX idx in super_class_type_idx_ and interfaces_type_idx_.
                kStatusLoaded = 2,  // DEX idx values resolved.
                kStatusResolving = 3,  // Just cloned from temporary class object.
                kStatusResolved = 4,  // Part of linking.
                kStatusVerifying = 5,  // In the process of being verified.
                kStatusRetryVerificationAtRuntime = 6,  // Compile time verification failed, retry at runtime.
                kStatusVerifyingAtRuntime = 7,  // Retrying verification at runtime.
                kStatusVerified = 8,  // Logically part of linking; done pre-init.
                kStatusInitializing = 9,  // Class init in progress.
                kStatusInitialized = 10,  // Ready to go.
                kStatusMax = 11,
            };

            static constexpr uint32_t kClassWalkSuper = 0xC0000000;
            static constexpr uint32_t kPrimitiveTypeSizeShiftShift = 16;
            static constexpr uint32_t kPrimitiveTypeMask = (1u << kPrimitiveTypeSizeShiftShift) - 1;

            uint32_t class_loader_;
            uint32_t component_type_;
            uint32_t dex_cache_;
            uint32_t ext_data_;

            uint32_t iftable_;
            uint32_t name_;
            uint32_t super_class_;

            uint32_t vtable_;

            uint64_t ifields_;
            uint64_t methods_;

            uint64_t sfields_;

            uint32_t access_flags_;
            uint32_t class_flags_;
            uint32_t class_size_;
            pid_t clinit_thread_id_;
            int32_t dex_class_def_idx_;
            int32_t dex_type_idx_;
            uint32_t num_reference_instance_fields_;
            uint32_t num_reference_static_fields_;
            uint32_t object_size_;

            uint32_t object_size_alloc_fast_path_;

            uint32_t primitive_type_;

            uint32_t reference_instance_offsets_;
            Status status_;
            uint16_t copied_methods_offset_;
            uint16_t virtual_methods_offset_;
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

            // Field order required by test "ValidateFieldOrderOfJavaCppUnionClasses".
            // The class we are a part of.
            uint32_t declaring_class_;
            // Access flags; low 16 bits are defined by spec.
            uint32_t access_flags_;
            /* Dex file fields. The defining dex file is available via declaring_class_->dex_cache_ */
            // Offset to the CodeItem.
            uint32_t dex_code_item_offset_;
            // Index into method_ids of the dex file associated with this method.
            uint32_t dex_method_index_;
            /* End of dex file fields. */
            // Entry within a dispatch table for this method. For static/direct methods the index is into
            // the declaringClass.directMethods, for virtual methods the vtable and for interface methods the
            // ifTable.
            uint16_t method_index_;

            // The hotness we measure for this method. Incremented by the interpreter. Not atomic, as we allow
            // missing increments: if the method is hot, we will see it eventually.
            uint16_t hotness_count_;
            // Fake padding field gets inserted here.
            // Must be the last fields in the method.
            // PACKED(4) is necessary for the correctness of
            // RoundUp(OFFSETOF_MEMBER(ArtMethod, ptr_sized_fields_), pointer_size).
            struct PtrSizedFields {
                // Short cuts to declaring_class_->dex_cache_ member for fast compiled code access.
                ArtMethod **dex_cache_resolved_methods_;

                // Pointer to JNI function registered to this method, or a function to resolve the JNI function,
                // or the profiling data for non-native methods, or an ImtConflictTable, or the
                // single-implementation of an abstract/interface method.
                void *entry_point_from_jni_;

                // Method dispatch from quick compiled code invokes this pointer which may cause bridging into
                // the interpreter.
                void *entry_point_from_quick_compiled_code_;
            } ptr_sized_fields_;

        };
    }
}
