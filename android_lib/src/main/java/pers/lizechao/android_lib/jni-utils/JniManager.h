#ifndef __ANDROID_JNI_MANAGER_H__
#define __ANDROID_JNI_MANAGER_H__

#include <jni.h>
#include <string>
#include <vector>
#include <unordered_map>
#include <functional>
#include <android/log.h>
#include <ctime>

#define  LOG_TAG    "JniManager"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
namespace JniUtils {
    typedef struct JniMethodInfo_ {
        JNIEnv *env;
        jclass classID;
        jmethodID methodID;
    } JniMethodInfo;


    class Bundle {
    private:
        jobject bundleJ;
        std::string className;
    public:
        Bundle(jobject bundleJ);

        ~Bundle();

        int getInt(const char *key);

        float getFloat(const char *key);

        double getDouble(const char *key);

        bool getBool(const char *key);

        std::string getString(const char *key);

        jobject getObj(const char *key);
    };

    class JniManager {
    public:

        typedef std::unordered_map<JNIEnv *, std::vector<jobject>> LocalRefMapType;
        struct CallbackStore {
            std::unordered_map<std::string, void *> callbacksSuccess;
            std::unordered_map<std::string, void *> callbacksFail;
            std::unordered_map<std::string, void *> callbacksFlow;
        };

        typedef enum {
            Int, Float, Double, Bool, String, Object, JniBundle
        } DataType;


        typedef std::function<void(std::string)> StringCallback;
        typedef std::function<void(int)> IntCallback;
        typedef std::function<void(double)> DoubleCallback;
        typedef std::function<void(jobject)> ObjCallback;
        typedef std::function<void(Bundle *)> BundleCallback;
        typedef std::function<void(bool)> BoolCallback;
        typedef std::function<void(float)> FloatCallback;

        static void logI(const char *tag, const char *fmt, ...);

        static void logE(const char *tag, const char *fmt, ...);

        static long getCurrentMillisTimes();

        static void setJavaVM(JavaVM *javaVM);

        static JavaVM *getJavaVM();

        static JNIEnv *getEnv();

        static bool setClassLoaderFrom(jobject activityInstance);

        static bool getStaticMethodInfo(JniMethodInfo &methodinfo,
                                        const char *className,
                                        const char *methodName,
                                        const char *paramCode);

        static bool getMethodInfo(JniMethodInfo &methodinfo,
                                  const char *className,
                                  const char *methodName,
                                  const char *paramCode);

        static std::string jstring2string(jstring str);

        static jmethodID loadclassMethod_methodID;
        static jobject classloader;
        static std::function<void()> classloaderCallback;
        static std::string basePackageName;
        static std::unordered_map<long, CallbackStore *> functionCallbacks;

        static void setBasePackage(std::string name) {
            basePackageName = name;
            JniManager::initJniFunc();
        }

        static bool
        registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod sMethodTable[],
                              int size) {
            jclass clazz = env->FindClass(className);
            if (clazz == NULL) {
                return false;
            }
            if (JNI_OK != env->RegisterNatives(clazz, sMethodTable, size)) {
                if (env->ExceptionCheck()) {
                    env->ExceptionClear();
                }
                return false;
            }
            return true;
        }

        /**
        @brief Call of Java static void method
        @if no such method will log error
        */
        template<typename... Ts>
        static void callStaticVoidMethod(const std::string &className,
                                         const std::string &methodName,
                                         Ts... xs) {
            JniMethodInfo t;
            std::string signature = "(" + std::string(getJNISignature(xs...)) + ")V";
            if (getStaticMethodInfo(t, className.c_str(), methodName.c_str(), signature.c_str())) {
                LocalRefMapType localRefs;
                t.env->CallStaticVoidMethod(t.classID, t.methodID, convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                deleteLocalRefs(t.env, localRefs);
            } else {
                reportError(className, methodName, signature);
            }
        }

        /**
        @brief Call of Java static boolean method
        @return value from Java static boolean method if there are proper JniMethodInfo; otherwise false.
        */
        template<typename... Ts>
        static bool callStaticBooleanMethod(const std::string &className,
                                            const std::string &methodName,
                                            Ts... xs) {
            long time1 = getCurrentMillisTimes();
            jboolean jret = JNI_FALSE;
            JniMethodInfo t;
            std::string signature = "(" + std::string(getJNISignature(xs...)) + ")Z";
            if (getStaticMethodInfo(t, className.c_str(), methodName.c_str(), signature.c_str())) {
                LocalRefMapType localRefs;
                LOGI("lzc use time %ld", (getCurrentMillisTimes() - time1));
                jret = t.env->CallStaticBooleanMethod(t.classID, t.methodID,
                                                      convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                deleteLocalRefs(t.env, localRefs);
            } else {
                reportError(className, methodName, signature);
            }
            return (jret == JNI_TRUE);
        }

        /**
        @brief Call of Java static int method
        @return value from Java static int method if there are proper JniMethodInfo; otherwise 0.
        */
        template<typename... Ts>
        static int callStaticIntMethod(const std::string &className,
                                       const std::string &methodName,
                                       Ts... xs) {
            jint ret = 0;
            JniMethodInfo t;
            std::string signature = "(" + std::string(getJNISignature(xs...)) + ")I";
            if (getStaticMethodInfo(t, className.c_str(), methodName.c_str(), signature.c_str())) {
                LocalRefMapType localRefs;
                ret = t.env->CallStaticIntMethod(t.classID, t.methodID,
                                                 convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                deleteLocalRefs(t.env, localRefs);
            } else {
                reportError(className, methodName, signature);
            }
            return ret;
        }

        /**
        @brief Call of Java static float method
        @return value from Java static float method if there are proper JniMethodInfo; otherwise 0.
        */
        template<typename... Ts>
        static float callStaticFloatMethod(const std::string &className,
                                           const std::string &methodName,
                                           Ts... xs) {
            jfloat ret = 0.0;
            JniMethodInfo t;
            std::string signature = "(" + std::string(getJNISignature(xs...)) + ")F";
            if (getStaticMethodInfo(t, className.c_str(), methodName.c_str(), signature.c_str())) {
                LocalRefMapType localRefs;
                ret = t.env->CallStaticFloatMethod(t.classID, t.methodID,
                                                   convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                deleteLocalRefs(t.env, localRefs);
            } else {
                reportError(className, methodName, signature);
            }
            return ret;
        }

        /**
        @brief Call of Java static float* method
        @return address of JniMethodInfo if there are proper JniMethodInfo; otherwise nullptr.
        */
        template<typename... Ts>
        static float *callStaticFloatArrayMethod(const std::string &className,
                                                 const std::string &methodName,
                                                 Ts... xs) {
            static float ret[32];
            JniMethodInfo t;
            std::string signature = "(" + std::string(getJNISignature(xs...)) + ")[F";
            if (getStaticMethodInfo(t, className.c_str(), methodName.c_str(), signature.c_str())) {
                LocalRefMapType localRefs;
                jfloatArray array = (jfloatArray) t.env->CallStaticObjectMethod(t.classID,
                                                                                t.methodID,
                                                                                convert(localRefs,
                                                                                        t, xs)...);
                jsize len = t.env->GetArrayLength(array);
                if (len <= 32) {
                    jfloat *elems = t.env->GetFloatArrayElements(array, 0);
                    if (elems) {
                        memcpy(ret, elems, sizeof(float) * len);
                        t.env->ReleaseFloatArrayElements(array, elems, 0);
                    };
                }
                t.env->DeleteLocalRef(t.classID);
                deleteLocalRefs(t.env, localRefs);
                return &ret[0];
            } else {
                reportError(className, methodName, signature);
            }
            return nullptr;
        }

        /**
        @brief Call of Java static int* method
        @return address of JniMethodInfo if there are proper JniMethodInfo; otherwise nullptr.
        */
        template<typename... Ts>
        static int *callStaticIntArrayMethod(const std::string &className,
                                             const std::string &methodName,
                                             Ts... xs) {
            static int ret[32];
            JniMethodInfo t;
            std::string signature = "(" + std::string(getJNISignature(xs...)) + ")[I";
            if (getStaticMethodInfo(t, className.c_str(), methodName.c_str(), signature.c_str())) {
                LocalRefMapType localRefs;
                jintArray array = (jintArray) t.env->CallStaticObjectMethod(t.classID, t.methodID,
                                                                            convert(localRefs, t,
                                                                                    xs)...);
                jsize len = t.env->GetArrayLength(array);
                if (len <= 32) {
                    jint *elems = t.env->GetIntArrayElements(array, 0);
                    if (elems) {
                        memcpy(ret, elems, sizeof(int) * len);
                        t.env->ReleaseIntArrayElements(array, elems, 0);
                    };
                }
                t.env->DeleteLocalRef(t.classID);
                deleteLocalRefs(t.env, localRefs);
                return &ret[0];
            } else {
                reportError(className, methodName, signature);
            }
            return nullptr;
        }

        /**
        @brief Call of Java static double method
        @return value from Java static double method if there are proper JniMethodInfo; otherwise 0.
        */
        template<typename... Ts>
        static double callStaticDoubleMethod(const std::string &className,
                                             const std::string &methodName,
                                             Ts... xs) {
            jdouble ret = 0.0;
            JniMethodInfo t;
            std::string signature = "(" + std::string(getJNISignature(xs...)) + ")D";
            if (getStaticMethodInfo(t, className.c_str(), methodName.c_str(), signature.c_str())) {
                LocalRefMapType localRefs;
                ret = t.env->CallStaticDoubleMethod(t.classID, t.methodID,
                                                    convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                deleteLocalRefs(t.env, localRefs);
            } else {
                reportError(className, methodName, signature);
            }
            return ret;
        }

        /**
        @brief Call of Java static string method
        @return JniMethodInfo of string type if there are proper JniMethodInfo; otherwise empty string.
        */
        template<typename... Ts>
        static std::string callStaticStringMethod(const std::string &className,
                                                  const std::string &methodName,
                                                  Ts... xs) {
            std::string ret;

            JniMethodInfo t;
            std::string signature =
                    "(" + std::string(getJNISignature(xs...)) + ")Ljava/lang/String;";
            if (getStaticMethodInfo(t, className.c_str(), methodName.c_str(), signature.c_str())) {
                LocalRefMapType localRefs;
                jstring jret = (jstring) t.env->CallStaticObjectMethod(t.classID, t.methodID,
                                                                       convert(localRefs, t,
                                                                               xs)...);
                ret = jstring2string(jret);
                t.env->DeleteLocalRef(t.classID);
                t.env->DeleteLocalRef(jret);
                deleteLocalRefs(t.env, localRefs);
            } else {
                reportError(className, methodName, signature);
            }
            return ret;
        }


        template<typename... Ts>
        static jobject callStaticObjectMethod(const std::string &className,
                                              const std::string &methodName,
                                              const std::string &returnName,
                                              Ts... xs) {
            jobject ret = nullptr;

            JniMethodInfo t;
            std::string signature =
                    "(" + std::string(getJNISignature(xs...)) + ")L" + returnName + ";";
            if (getStaticMethodInfo(t, className.c_str(), methodName.c_str(), signature.c_str())) {
                LocalRefMapType localRefs;
                ret = t.env->CallStaticObjectMethod(t.classID, t.methodID,
                                                    convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                deleteLocalRefs(t.env, localRefs);
            } else {
                reportError(className, methodName, signature);
            }
            return ret;
        }

        static void reportError(const std::string &className, const std::string &methodName,
                                const std::string &signature);

        static void deleteLocalRefs(JNIEnv *env, LocalRefMapType &localRefs);


        static JNIEnv *cacheEnv();

        static bool getMethodInfo_DefaultClassLoader(JniMethodInfo &methodinfo,
                                                     const char *className,
                                                     const char *methodName,
                                                     const char *paramCode);

//    static JavaVM* _psJavaVM;


        static jstring convert(LocalRefMapType &localRefs, JniMethodInfo &t, const char *x);

        static jstring convert(LocalRefMapType &localRefs, JniMethodInfo &t, const std::string &x);

        inline static jint convert(LocalRefMapType &, JniMethodInfo &,
                                   int32_t value) { return static_cast<jint>(value); }

        inline static jlong convert(LocalRefMapType &, JniMethodInfo &,
                                    int64_t value) { return static_cast<jlong>(value); }

        inline static jfloat convert(LocalRefMapType &, JniMethodInfo &,
                                     float value) { return static_cast<jfloat>(value); }

        inline static jdouble convert(LocalRefMapType &, JniMethodInfo &,
                                      double value) { return static_cast<jdouble>(value); }

        inline static jboolean convert(LocalRefMapType &, JniMethodInfo &,
                                       bool value) { return static_cast<jboolean>(value); }

        inline static jbyte convert(LocalRefMapType &, JniMethodInfo &,
                                    int8_t value) { return static_cast<jbyte>(value); }

        inline static jchar convert(LocalRefMapType &, JniMethodInfo &,
                                    uint8_t value) { return static_cast<jchar>(value); }

        inline static jshort convert(LocalRefMapType &, JniMethodInfo &,
                                     int16_t value) { return static_cast<jshort>(value); }

        template<typename T>
        static T convert(LocalRefMapType &localRefs, JniMethodInfo &, T x) {
            return x;
        }


        static std::string getJNISignature() {
            return "";
        }

        static std::string getJNISignature(bool) {
            return "Z";
        }

        static std::string getJNISignature(jobject) {
            return "Ljava/lang/Object;";
        }

        static std::string getJNISignature(char) {
            return "C";
        }

        static std::string getJNISignature(short) {
            return "S";
        }

        static std::string getJNISignature(int) {
            return "I";
        }

        static std::string getJNISignature(long) {
            return "J";
        }

        static std::string getJNISignature(float) {
            return "F";
        }

        static std::string getJNISignature(double) {
            return "D";
        }

        static std::string getJNISignature(const char *) {
            return "Ljava/lang/String;";
        }

        static std::string getJNISignature(const std::string &) {
            return "Ljava/lang/String;";
        }

        template<typename T>
        static std::string getJNISignature(T x) {
            // This template should never be instantiated
            static_assert(sizeof(x) == 0, "Unsupported argument type");
            return "";
        }

        template<typename T, typename... Ts>
        static std::string getJNISignature(T x, Ts... xs) {
            return getJNISignature(x) + getJNISignature(xs...);
        }


    private:


        static void dispatchFunctionCallback(
                Bundle *bundle,
                void *funP,
                DataType dataType) {
            const static char *SingleDataKey = "SingleDataKey";
            if (!funP || !bundle)
                return;
            if (dataType == Int) {
                (*static_cast<IntCallback *>(funP))(bundle->getInt(SingleDataKey));
            } else if (dataType == Float) {
                (*static_cast<FloatCallback *>(funP))(bundle->getFloat(SingleDataKey));
            } else if (dataType == Double) {
                (*static_cast<DoubleCallback *>(funP))(bundle->getDouble(SingleDataKey));
            } else if (dataType == Bool) {
                (*static_cast<BoolCallback *>(funP))(bundle->getBool(SingleDataKey));
            } else if (dataType == String) {
                (*static_cast<StringCallback *>(funP))(bundle->getString(SingleDataKey));
            } else if (dataType == Object) {
                (*static_cast<ObjCallback *>(funP))(bundle->getObj(SingleDataKey));
            } else if (dataType == JniBundle) {
                (*static_cast<BundleCallback *>(funP))(bundle);
            }
            delete bundle;
        }

        static void dispatchFunctionCallbackSucceed(JNIEnv *env, jobject instance, jlong nativeObj,
                                                    jstring key, jint type, jobject bundle) {
            CallbackStore *pStore = functionCallbacks[nativeObj];
            std::string keyStr = JniManager::jstring2string(key);
            dispatchFunctionCallback(new Bundle(bundle),
                                     pStore->callbacksSuccess[keyStr],
                                     DataType((int) type));
            pStore->callbacksSuccess.erase(keyStr);
            pStore->callbacksFail.erase(keyStr);
        }

        static void dispatchFunctionCallbackFail(JNIEnv *env, jobject instance, jlong nativeObj,
                                                 jstring key, jint type, jobject bundle) {
            CallbackStore *pStore = functionCallbacks[nativeObj];
            std::string keyStr = JniManager::jstring2string(key);
            dispatchFunctionCallback(new Bundle(bundle),
                                     pStore->callbacksFail[keyStr],
                                     DataType((int) type));
            pStore->callbacksSuccess.erase(keyStr);
            pStore->callbacksFail.erase(keyStr);
        }

        static void dispatchFlow(JNIEnv *env, jobject instance, jlong nativeObj,
                                 jstring key, jint type, jobject bundle) {
            CallbackStore *pStore = functionCallbacks[nativeObj];
            std::string keyStr = JniManager::jstring2string(key);
            dispatchFunctionCallback(new Bundle(bundle),
                                     pStore->callbacksFlow[keyStr],
                                     DataType((int) type));
        }

        static void initJniFunc() {
            typedef void(JniManager::*CallbackS)(JNIEnv *env, jobject instance,
                                                 jstring key, jobject data, jint type);
            JNINativeMethod sMethodTable[] = {
                    {"dispatchFunctionCallbackSucceed", "(JLjava/lang/String;ILjava/lang/Object;)V", (void *) (dispatchFunctionCallbackSucceed)},
                    {"dispatchFunctionCallbackFail",    "(JLjava/lang/String;ILjava/lang/Object;)V", (void *) (dispatchFunctionCallbackFail)},
                    {"dispatchFlow",                    "(JLjava/lang/String;ILjava/lang/Object;)V", (void *) (dispatchFlow)},
            };
            bool succeed = JniManager::registerNativeMethods(JniManager::getEnv(),
                                                             (JniManager::basePackageName +
                                                              "/JniManager").c_str(),
                                                             sMethodTable,
                                                             sizeof(sMethodTable) /
                                                             sizeof(JNINativeMethod));
            assert(succeed);
        }

    };

    std::unordered_map<long, JniManager::CallbackStore *> JniManager::functionCallbacks;
    std::string JniManager::basePackageName;


    class JniProxy {
    private:
        jobject javaObj;
        std::string className;
        JniManager::CallbackStore callbackStore;

    public:
        JniProxy(jobject javaObj, std::string className) {
            this->javaObj = JniManager::getEnv()->NewGlobalRef(javaObj);
            this->className = className;
            JniManager::functionCallbacks[(long) this] = &callbackStore;
            callVoidMethod("createFromNative", (long) this);
        }

        ~JniProxy() {
            auto env = JniManager::getEnv();
            if (env)
                env->DeleteGlobalRef(javaObj);
            auto callback = JniManager::functionCallbacks[(long) this];
            if (callback) {
                callback->callbacksFail.clear();
                callback->callbacksFlow.clear();
                callback->callbacksSuccess.clear();
            }
            JniManager::functionCallbacks.erase((long) this);
            callVoidMethod("destroyFromNative", (long) this);
        }

        template<typename... Ts>
        void callVoidMethod(const std::string &methodName,
                            Ts... xs) {
            JniMethodInfo t;
            std::string signature = "(" + std::string(JniManager::getJNISignature(xs...)) + ")V";
            if (JniManager::getMethodInfo(t, className.c_str(), methodName.c_str(),
                                          signature.c_str())) {
                JniManager::LocalRefMapType localRefs;
                t.env->CallVoidMethod(javaObj, t.methodID,
                                      JniManager::convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                JniManager::deleteLocalRefs(t.env, localRefs);
            } else {
                JniManager::reportError(className, methodName, signature);
            }
        }

        template<typename... Ts>
        int callIntMethod(const std::string &methodName,
                          Ts... xs) {
            JniMethodInfo t;
            std::string signature = "(" + std::string(JniManager::getJNISignature(xs...)) + ")I";
            if (JniManager::getMethodInfo(t, className.c_str(), methodName.c_str(),
                                          signature.c_str())) {
                JniManager::LocalRefMapType localRefs;
                int result = t.env->CallIntMethod(javaObj, t.methodID,
                                                  JniManager::convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                JniManager::deleteLocalRefs(t.env, localRefs);
                return result;
            } else {
                JniManager::reportError(className, methodName, signature);
            }
            return 0;
        }

        template<typename... Ts>
        bool callBoolMethod(const std::string &methodName,
                            Ts... xs) {
            JniMethodInfo t;
            std::string signature = "(" + std::string(JniManager::getJNISignature(xs...)) + ")Z";
            if (JniManager::getMethodInfo(t, className.c_str(), methodName.c_str(),
                                          signature.c_str())) {
                JniManager::LocalRefMapType localRefs;
                bool result = t.env->CallBooleanMethod(javaObj, t.methodID,
                                                       JniManager::convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                JniManager::deleteLocalRefs(t.env, localRefs);
                return result;
            } else {
                JniManager::reportError(className, methodName, signature);
            }
            return false;
        }


        template<typename... Ts>
        float callFloatMethod(const std::string &methodName,
                              Ts... xs) {
            JniMethodInfo t;
            std::string signature = "(" + std::string(JniManager::getJNISignature(xs...)) + ")F";
            if (JniManager::getMethodInfo(t, className.c_str(), methodName.c_str(),
                                          signature.c_str())) {
                JniManager::LocalRefMapType localRefs;
                float result = t.env->CallFloatMethod(javaObj, t.methodID,
                                                      JniManager::convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                JniManager::deleteLocalRefs(t.env, localRefs);
                return result;
            } else {
                JniManager::reportError(className, methodName, signature);
            }
            return 0.0f;
        }

        template<typename... Ts>
        double callDoubleMethod(const std::string &methodName,
                                Ts... xs) {
            JniMethodInfo t;
            std::string signature = "(" + std::string(JniManager::getJNISignature(xs...)) + ")D";
            if (JniManager::getMethodInfo(t, className.c_str(), methodName.c_str(),
                                          signature.c_str())) {
                JniManager::LocalRefMapType localRefs;
                double result = t.env->CallDoubleMethod(javaObj, t.methodID,
                                                        JniManager::convert(localRefs, t, xs)...);
                t.env->DeleteLocalRef(t.classID);
                JniManager::deleteLocalRefs(t.env, localRefs);
                return result;
            } else {
                JniManager::reportError(className, methodName, signature);
            }
            return 0.0;
        }

        template<typename... Ts>
        std::string callStringMethod(const std::string &methodName,
                                     Ts... xs) {
            JniMethodInfo t;
            std::string signature =
                    "(" + std::string(JniManager::getJNISignature(xs...)) + ")Ljava/lang/String;";
            if (JniManager::getMethodInfo(t, className.c_str(), methodName.c_str(),
                                          signature.c_str())) {
                JniManager::LocalRefMapType localRefs;
                jstring back = static_cast<jstring>(t.env->CallObjectMethod(javaObj, t.methodID,
                                                                            JniManager::convert(
                                                                                    localRefs,
                                                                                    t,
                                                                                    xs)...));
                std::string result = JniManager::jstring2string(back);
                t.env->DeleteLocalRef(t.classID);
                JniManager::deleteLocalRefs(t.env, localRefs);
                return result;
            } else {
                JniManager::reportError(className, methodName, signature);
            }
            return "";
        }

        template<typename... Ts>
        jobject callObjectMethod(const std::string &methodName,
                                 const std::string &returnName,
                                 Ts... xs) {
            JniMethodInfo t;
            std::string signature =
                    "(" + std::string(JniManager::getJNISignature(xs...)) + ")L" + returnName + ";";
            if (JniManager::getMethodInfo(t, className.c_str(), methodName.c_str(),
                                          signature.c_str())) {
                JniManager::LocalRefMapType localRefs;
                jobject result = t.env->CallObjectMethod(javaObj, t.methodID,
                                                         JniManager::convert(
                                                                 localRefs,
                                                                 t,
                                                                 xs)...);
                t.env->DeleteLocalRef(t.classID);
                JniManager::deleteLocalRefs(t.env, localRefs);
                return result;
            } else {
                JniManager::reportError(className, methodName, signature);
            }
            return nullptr;
        }

        template<typename T1, typename T2>
        void registerCallback(const char *key, std::function<void(T1)> succeed,
                              std::function<void(T2)> fail) {
            staticCheckParams<T1>();
            staticCheckParams<T2>();
            JniManager::CallbackStore *store = JniManager::functionCallbacks[(long) this];
            store->callbacksSuccess[key] = &succeed;
            store->callbacksFail[key] = &fail;
        }

        template<typename T>
        void registerFlow(const char *key, std::function<void(T)> flow) {
            staticCheckParams<T>();
            JniManager::CallbackStore *store = JniManager::functionCallbacks[(long) this];
            store->callbacksFlow[key] = &flow;
        }

        template<typename T>
        void staticCheckParams() {
            static constexpr bool value =
                    std::is_same<T, int>::value ||
                    std::is_same<T, float>::value ||
                    std::is_same<T, long>::value ||
                    std::is_same<T, double>::value ||
                    std::is_same<T, jobject>::value ||
                    std::is_same<T, Bundle *>::value ||
                    std::is_same<T, bool>::value ||
                    std::is_same<T, long>::value ||
                    std::is_same<T, std::string>::value;
            static_assert(value);
        }

    };

}
#endif