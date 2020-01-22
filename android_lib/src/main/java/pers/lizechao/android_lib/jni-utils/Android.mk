LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := jni_manager
LOCAL_MODULE_FILENAME := libjni_manager

LOCAL_SRC_FILES += JniManager.cpp
LOCAL_SRC_FILES += ccUTF8.cpp
LOCAL_SRC_FILES += ConvertUTF.c

LOCAL_C_INCLUDES += $(LOCAL_PATH)

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH) \

LOCAL_LDLIBS += -llog -landroid
LOCAL_CFLAGS += -frtti

include $(BUILD_SHARED_LIBRARY)