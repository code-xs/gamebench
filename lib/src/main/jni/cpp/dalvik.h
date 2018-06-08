#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <fcntl.h>
#include <dlfcn.h>

#include <stdint.h>    /* C99 */

#include "common.h"

typedef struct DexProto {
    u4 *dexFile; /* file the idx refers to */
    u4 protoIdx; /* index into proto_ids table of dexFile */
} DexProto;

typedef void (*DalvikBridgeFunc)(const u4 *args, void *pResult,
                                 const void *method, void *self);

struct Field {
    void *clazz; /* class in which the field is declared */
    const char *name;
    const char *signature; /* e.g. "I", "[C", "Landroid/os/Debug;" */
    u4 accessFlags;
};

struct Method;
struct ClassObject;
struct DvmDex;

typedef struct Object {
    /* ptr to class object */
    struct ClassObject *clazz;

    /*
     * A word containing either a "thin" lock or a "fat" monitor.  See
     * the comments in Sync.c for a description of its layout.
     */
    u4 lock;
} Object;

struct InitiatingLoaderList {
    /* a list of initiating loader Objects; grown and initialized on demand */
    void **initiatingLoaders;
    /* count of loaders in the above list */
    int initiatingLoaderCount;
};

enum PrimitiveType {
    PRIM_NOT = 0, /* value is a reference type, not a primitive type */
    PRIM_VOID = 1,
    PRIM_BOOLEAN = 2,
    PRIM_BYTE = 3,
    PRIM_SHORT = 4,
    PRIM_CHAR = 5,
    PRIM_INT = 6,
    PRIM_LONG = 7,
    PRIM_FLOAT = 8,
    PRIM_DOUBLE = 9,
} typedef PrimitiveType;

enum ClassStatus {
    CLASS_ERROR = -1,

    CLASS_NOTREADY = 0, CLASS_IDX = 1, /* loaded, DEX idx in super or ifaces */
    CLASS_LOADED = 2, /* DEX idx values resolved */
    CLASS_RESOLVED = 3, /* part of linking */
    CLASS_VERIFYING = 4, /* in the process of being verified */
    CLASS_VERIFIED = 5, /* logically part of linking; done pre-init */
    CLASS_INITIALIZING = 6, /* class init in progress */
    CLASS_INITIALIZED = 7, /* ready to go */
} typedef ClassStatus;

union JValue {
    u1 z;
    s1 b;
    u2 c;
    s2 s;
    s4 i;
    s8 j;
    float f;
    double d;
    Object *l;
};


/*
 * Static field.
 */
struct StaticField : Field {
    JValue value;          /* initially set from DEX for primitives */
};

/*
 * Instance field.
 */
struct InstField : Field {
    /*
     * This field indicates the byte offset from the beginning of the
     * (Object *) to the actual instance data; e.g., byteOffset==0 is
     * the same as the object pointer (bug!), and byteOffset==4 is 4
     * bytes farther.
     */
    int byteOffset;
};

/*
 * Used for iftable in ClassObject.
 */
struct InterfaceEntry {
    /* pointer to interface class */
    ClassObject *clazz;

    /*
     * Index into array of vtable offsets.  This points into the ifviPool,
     * which holds the vtables for all interfaces declared by this class.
     */
    int *methodIndexArray;
};




/*
 * This defines the amount of space we leave for field slots in the
 * java.lang.Class definition.  If we alter the class to have more than
 * this many fields, the VM will abort at startup.
 */
#define CLASS_FIELD_SLOTS   4

/*
 * Class objects have many additional fields.  This is used for both
 * classes and interfaces, including synthesized classes (arrays and
 * primitive types).
 *
 * Class objects are unusual in that they have some fields allocated with
 * the system malloc (or LinearAlloc), rather than on the GC heap.  This is
 * handy during initialization, but does require special handling when
 * discarding java.lang.Class objects.
 *
 * The separation of methods (direct vs. virtual) and fields (class vs.
 * instance) used in Dalvik works out pretty well.  The only time it's
 * annoying is when enumerating or searching for things with reflection.
 */
typedef struct ClassObject : Object {
    /* leave space for instance data; we could access fields directly if we
       freeze the definition of java/lang/Class */
    u4 instanceData[CLASS_FIELD_SLOTS];

    /* UTF-8 descriptor for the class; from constant pool, or on heap
       if generated ("[C") */
    const char *descriptor;
    char *descriptorAlloc;

    /* access flags; low 16 bits are defined by VM spec */
    u4 accessFlags;

    /* VM-unique class serial number, nonzero, set very early */
    u4 serialNumber;

    /* DexFile from which we came; needed to resolve constant pool entries */
    /* (will be NULL for VM-generated, e.g. arrays and primitive classes) */
    DvmDex *pDvmDex;

    /* state of class initialization */
    ClassStatus status;

    /* if class verify fails, we must return same error on subsequent tries */
    ClassObject *verifyErrorClass;

    /* threadId, used to check for recursive <clinit> invocation */
    u4 initThreadId;

    /*
     * Total object size; used when allocating storage on gc heap.  (For
     * interfaces and abstract classes this will be zero.)
     */
    size_t objectSize;

    /* arrays only: class object for base element, for instanceof/checkcast
       (for String[][][], this will be String) */
    ClassObject *elementClass;

    /* arrays only: number of dimensions, e.g. int[][] is 2 */
    int arrayDim;

    /* primitive type index, or PRIM_NOT (-1); set for generated prim classes */
    PrimitiveType primitiveType;

    /* superclass, or NULL if this is java.lang.Object */
    ClassObject *super;

    /* defining class loader, or NULL for the "bootstrap" system loader */
    Object *classLoader;

    /* initiating class loader list */
    /* NOTE: for classes with low serialNumber, these are unused, and the
       values are kept in a table in gDvm. */
    InitiatingLoaderList initiatingLoaderList;

    /* array of interfaces this class implements directly */
    int interfaceCount;
    ClassObject **interfaces;

    /* static, private, and <init> methods */
    int directMethodCount;
    Method *directMethods;

    /* virtual methods defined in this class; invoked through vtable */
    int virtualMethodCount;
    Method *virtualMethods;

    /*
     * Virtual method table (vtable), for use by "invoke-virtual".  The
     * vtable from the superclass is copied in, and virtual methods from
     * our class either replace those from the super or are appended.
     */
    int vtableCount;
    Method **vtable;

    /*
     * Interface table (iftable), one entry per interface supported by
     * this class.  That means one entry for each interface we support
     * directly, indirectly via superclass, or indirectly via
     * superinterface.  This will be null if neither we nor our superclass
     * implement any interfaces.
     *
     * Why we need this: given "class Foo implements Face", declare
     * "Face faceObj = new Foo()".  Invoke faceObj.blah(), where "blah" is
     * part of the Face interface.  We can't easily use a single vtable.
     *
     * For every interface a concrete class implements, we create a list of
     * virtualMethod indices for the methods in the interface.
     */
    int iftableCount;
    InterfaceEntry *iftable;

    /*
     * The interface vtable indices for iftable get stored here.  By placing
     * them all in a single pool for each class that implements interfaces,
     * we decrease the number of allocations.
     */
    int ifviPoolCount;
    int *ifviPool;

    /* instance fields
     *
     * These describe the layout of the contents of a DataObject-compatible
     * Object.  Note that only the fields directly defined by this class
     * are listed in ifields;  fields defined by a superclass are listed
     * in the superclass's ClassObject.ifields.
     *
     * All instance fields that refer to objects are guaranteed to be
     * at the beginning of the field list.  ifieldRefCount specifies
     * the number of reference fields.
     */
    int ifieldCount;
    int ifieldRefCount; // number of fields that are object refs
    InstField *ifields;

    /* bitmap of offsets of ifields */
    u4 refOffsets;

    /* source file name, if known */
    const char *sourceFile;

    /* static fields */
    int sfieldCount;
    StaticField sfields[0]; /* MUST be last item */
} ClassObject;


typedef struct Method {
    struct ClassObject *clazz;
    u4 accessFlags;

    u2 methodIndex;

    u2 registersSize; /* ins + locals */
    u2 outsSize;
    u2 insSize;

    /* method name, e.g. "<init>" or "eatLunch" */
    const char *name;

    /*
     * Method prototype descriptor string (return and argument types).
     *
     * TODO: This currently must specify the DexFile as well as the proto_ids
     * index, because generated Proxy classes don't have a DexFile.  We can
     * remove the DexFile* and reduce the size of this struct if we generate
     * a DEX for proxies.
     */
    DexProto prototype;

    /* short-form method descriptor string */
    const char *shorty;

    /*
     * The remaining items are not used for abstract or native methods.
     * (JNI is currently hijacking "insns" as a function pointer, set
     * after the first call.  For internal-native this stays null.)
     */

    /* the actual code */
    u2 *insns;

    /* cached JNI argument and return-type hints */
    int jniArgInfo;

    /*
     * Native method ptr; could be actual function or a JNI bridge.  We
     * don't currently discriminate between DalvikBridgeFunc and
     * DalvikNativeFunc; the former takes an argument superset (i.e. two
     * extra args) which will be ignored.  If necessary we can use
     * insns==NULL to detect JNI bridge vs. internal native.
     */
    DalvikBridgeFunc nativeFunc;
} Method;

typedef Object *(*dvmDecodeIndirectRef_func)(void *self, jobject jobj);

typedef void *(*dvmThreadSelf_func)();

dvmDecodeIndirectRef_func dvmDecodeIndirectRef_fnPtr;
dvmThreadSelf_func dvmThreadSelf_fnPtr;

jmethodID jClassMethod;
