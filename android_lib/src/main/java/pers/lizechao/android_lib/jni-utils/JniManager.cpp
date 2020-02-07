#include <JniManager.h>
#include <string.h>
#include <pthread.h>
#include <chrono>
#include "ccUTF8.h"

namespace JniUtils {
    static pthread_key_t g_key;

    jclass _getClassID(const char *className) {
        if (nullptr == className) {
            return nullptr;
        }

        JNIEnv *env = JniUtils::JniManager::getEnv();

        jstring _jstrClassName = env->NewStringUTF(className);

        jclass _clazz = (jclass) env->CallObjectMethod(JniUtils::JniManager::classloader,
                                                       JniUtils::JniManager::loadclassMethod_methodID,
                                                       _jstrClassName);

        if (nullptr == _clazz) {
            LOGE("Classloader failed to find class of %s", className);
            env->ExceptionClear();
        }

        env->DeleteLocalRef(_jstrClassName);

        return _clazz;
    }

    void _detachCurrentThread(void *a) {
        JniUtils::JniManager::getJavaVM()->DetachCurrentThread();
    }
    static JavaVM *_psJavaVM;
/**
 ==================================================================================
 * JniManager Class
 */

    jmethodID JniManager::loadclassMethod_methodID = nullptr;
    jobject JniManager::classloader = nullptr;
    std::function<void()> JniManager::classloaderCallback = nullptr;
    std::unordered_map<long, JniManager::CallbackStore *> JniManager::functionCallbacks;
    std::string JniManager::basePackageName = "";


    void JniManager::setBasePackage(std::string name) {
        basePackageName = name;
        initJniFunc();
    }

    void JniManager::initJniFunc() {
        typedef void(JniManager::*CallbackS)(JNIEnv *env, jobject instance,
                                             jstring key, jobject data, jint type);
        JNINativeMethod sMethodTable[] = {
                {"dispatchFunctionCallbackSucceed", "(JLjava/lang/String;ILjava/lang/Object;)V", (void *) (JniManager::dispatchFunctionCallbackSucceed)},
                {"dispatchFunctionCallbackFail",    "(JLjava/lang/String;ILjava/lang/Object;)V", (void *) (JniManager::dispatchFunctionCallbackFail)},
                {"dispatchFlow",                    "(JLjava/lang/String;ILjava/lang/Object;)V", (void *) (JniManager::dispatchFlow)},
        };
        bool succeed = JniManager::registerNativeMethods(JniManager::getEnv(),
                                                         (JniManager::basePackageName +
                                                          "/JniManager").c_str(),
                                                         sMethodTable,
                                                         sizeof(sMethodTable) /
                                                         sizeof(JNINativeMethod));
        assert(succeed);
    }


    JavaVM *JniManager::getJavaVM() {
        pthread_t thisthread = pthread_self();
        //LOGD("JniManager::getJavaVM(), pthread_self() = %ld", thisthread);
        return _psJavaVM;
    }

    void JniManager::setJavaVM(JavaVM *javaVM) {
        pthread_t thisthread = pthread_self();
        //LOGD("JniManager::setJavaVM(%p), pthread_self() = %ld", javaVM, thisthread);
        _psJavaVM = javaVM;

        pthread_key_create(&g_key, _detachCurrentThread);
    }


    JNIEnv *JniManager::cacheEnv() {
        JNIEnv *_env = nullptr;
        // get jni environment
        jint ret = getJavaVM()->GetEnv((void **) &_env, JNI_VERSION_1_6);

        switch (ret) {
            case JNI_OK :
                // Success!
                pthread_setspecific(g_key, _env);
                return _env;

            case JNI_EDETACHED :
                // Thread not attached
                if (getJavaVM()->AttachCurrentThread(&_env, nullptr) < 0) {
                    LOGE("Failed to get the environment using AttachCurrentThread()");

                    return nullptr;
                } else {
                    // Success : Attached and obtained JNIEnv!
                    pthread_setspecific(g_key, _env);
                    return _env;
                }

            case JNI_EVERSION :
                // Cannot recover from this error
                LOGE("JNI interface version 1.4 not supported");
            default :
                LOGE("Failed to get the environment using GetEnv()");
                return nullptr;
        }
    }

    JNIEnv *JniManager::getEnv() {
        JNIEnv *_env = (JNIEnv *) pthread_getspecific(g_key);
        if (_env == nullptr)
            _env = JniManager::cacheEnv();
        return _env;
    }


    bool JniManager::setClassLoaderFrom(jobject contextObj) {
        JniMethodInfo _getclassloaderMethod;
        if (!JniManager::getMethodInfo_DefaultClassLoader(_getclassloaderMethod,
                                                          "android/content/Context",
                                                          "getClassLoader",
                                                          "()Ljava/lang/ClassLoader;")) {
            return false;
        }

        jobject _c = getEnv()->CallObjectMethod(contextObj,
                                                _getclassloaderMethod.methodID);

        if (nullptr == _c) {
            return false;
        }

        JniMethodInfo _m;
        if (!JniManager::getMethodInfo_DefaultClassLoader(_m,
                                                          "java/lang/ClassLoader",
                                                          "loadClass",
                                                          "(Ljava/lang/String;)Ljava/lang/Class;")) {
            return false;
        }

        JniManager::classloader = getEnv()->NewGlobalRef(_c);
        JniManager::loadclassMethod_methodID = _m.methodID;
        if (JniManager::classloaderCallback != nullptr) {
            JniManager::classloaderCallback();
        }

        return true;
    }

    bool JniManager::getStaticMethodInfo(JniMethodInfo &methodinfo,
                                         const char *className,
                                         const char *methodName,
                                         const char *paramCode) {
        if ((nullptr == className) ||
            (nullptr == methodName) ||
            (nullptr == paramCode)) {
            return false;
        }

        JNIEnv *env = JniManager::getEnv();
        if (!env) {
            LOGE("Failed to get JNIEnv");
            return false;
        }

        jclass classID = _getClassID(className);
        if (!classID) {
            LOGE("Failed to find class %s", className);
            env->ExceptionClear();
            return false;
        }

        jmethodID methodID = env->GetStaticMethodID(classID, methodName, paramCode);
        if (!methodID) {
            LOGE("Failed to find static method id of %s", methodName);
            env->ExceptionClear();
            return false;
        }

        methodinfo.classID = classID;
        methodinfo.env = env;
        methodinfo.methodID = methodID;
        return true;
    }

    bool JniManager::getMethodInfo_DefaultClassLoader(JniMethodInfo &methodinfo,
                                                      const char *className,
                                                      const char *methodName,
                                                      const char *paramCode) {
        if ((nullptr == className) ||
            (nullptr == methodName) ||
            (nullptr == paramCode)) {
            return false;
        }

        JNIEnv *env = JniManager::getEnv();
        if (!env) {
            return false;
        }

        jclass classID = env->FindClass(className);
        if (!classID) {
            LOGE("Failed to find class %s", className);
            env->ExceptionClear();
            return false;
        }

        jmethodID methodID = env->GetMethodID(classID, methodName, paramCode);
        if (!methodID) {
            LOGE("Failed to find method id of %s", methodName);
            env->ExceptionClear();
            return false;
        }

        methodinfo.classID = classID;
        methodinfo.env = env;
        methodinfo.methodID = methodID;

        return true;
    }

    bool JniManager::getMethodInfo(JniMethodInfo &methodinfo,
                                   const char *className,
                                   const char *methodName,
                                   const char *paramCode) {
        if ((nullptr == className) ||
            (nullptr == methodName) ||
            (nullptr == paramCode)) {
            return false;
        }

        JNIEnv *env = JniManager::getEnv();
        if (!env) {
            return false;
        }

        jclass classID = _getClassID(className);
        if (!classID) {
            LOGE("Failed to find class %s", className);
            env->ExceptionClear();
            return false;
        }

        jmethodID methodID = env->GetMethodID(classID, methodName, paramCode);
        if (!methodID) {
            LOGE("Failed to find method id of %s %s", methodName, paramCode);
            env->ExceptionClear();
            return false;
        }

        methodinfo.classID = classID;
        methodinfo.env = env;
        methodinfo.methodID = methodID;

        return true;
    }

    std::string JniManager::jstring2string(jstring jstr) {
        if (jstr == nullptr) {
            return "";
        }

        JNIEnv *env = JniManager::getEnv();
        if (!env) {
            return "";
        }

        std::string strValue = StringUtils::getStringUTFCharsJNI(env, jstr);

        return strValue;
    }

    jstring
    JniManager::convert(LocalRefMapType &localRefs, JniMethodInfo &t, const char *x) {
        jstring ret = StringUtils::newStringUTFJNI(t.env, x ? x : "");
        localRefs[t.env].push_back(ret);
        return ret;
    }

    jstring JniManager::convert(LocalRefMapType &localRefs, JniMethodInfo &t,
                                const std::string &x) {
        return convert(localRefs, t, x.c_str());
    }

    void JniManager::deleteLocalRefs(JNIEnv *env, LocalRefMapType &localRefs) {
        if (!env) {
            return;
        }

        for (const auto &ref : localRefs[env]) {
            env->DeleteLocalRef(ref);
        }
        localRefs[env].clear();
    }

    void JniManager::reportError(const std::string &className, const std::string &methodName,
                                 const std::string &signature) {
        LOGE("Failed to find static java method. Class name: %s, method name: %s, signature: %s ",
             className.c_str(), methodName.c_str(), signature.c_str());
        assert(false);
    }

    void JniManager::logI(const char *tag, const char *fmt, ...) {
        va_list ap;
        va_start(ap, fmt);
        __android_log_vprint(ANDROID_LOG_INFO, tag, fmt, ap);
        va_end (ap);
    }

    void JniManager::logE(const char *tag, const char *fmt, ...) {
        va_list ap;
        va_start(ap, fmt);
        __android_log_vprint(ANDROID_LOG_ERROR, tag, fmt, ap);
        va_end (ap);
    }

    using namespace std::chrono;
    typedef duration<int, std::ratio<60 * 60 * 24>> days_type;

    long long int JniManager::getCurrentMillisTimes() {
        system_clock::duration d = system_clock::now().time_since_epoch();
        milliseconds mil = duration_cast<milliseconds>(d);
        return mil.count();
    }

    long long int JniManager::getCurrentSecTimes() {
        system_clock::duration d = system_clock::now().time_since_epoch();
        seconds sec = duration_cast<seconds>(d);
        return sec.count();
    }

    long long int JniManager::getCurrentNanoTimes() {
        system_clock::duration d = system_clock::now().time_since_epoch();
        nanoseconds nan = duration_cast<nanoseconds>(d);
        return nan.count();
    }

    /**
     ==================================================================================
     * Bundle Class
     */

    Bundle::Bundle(jobject bundleJ) {
        this->bundleJ = JniManager::getEnv()->NewGlobalRef(bundleJ);
        this->className = JniManager::basePackageName + "/JniBundle";
    }

    Bundle::~Bundle() {
        JNIEnv *env = JniManager::getEnv();
        if (env) {
            JniManager::callStaticVoidMethod(className, "clear", bundleJ);
            env->DeleteGlobalRef(bundleJ);
        }
    }

    int Bundle::getInt(const char *key) const {
        return JniManager::callStaticIntMethod(className, "getInt", bundleJ, key);
    }

    bool Bundle::getBool(const char *key) const {
        return JniManager::callStaticBooleanMethod(className, "getBool", bundleJ, key);
    }

    double Bundle::getDouble(const char *key) const {
        return JniManager::callStaticDoubleMethod(className, "getDouble", bundleJ, key);
    }

    float Bundle::getFloat(const char *key) const {
        return JniManager::callStaticFloatMethod(className, "getFloat", bundleJ, key);
    }

    jobject Bundle::getObj(const char *key) const {
        return JniManager::callStaticObjectMethod(className, "getObj", "java/lang/Objects", bundleJ,
                                                  key);
    }

    std::string Bundle::getString(const char *key) const {
        return JniManager::callStaticStringMethod(className, "getString", bundleJ, key);
    }

    long long Bundle::getLong(const char *key) const {
        return JniManager::callStaticLongMethod(className, "getLong", bundleJ, key);
    }
}
