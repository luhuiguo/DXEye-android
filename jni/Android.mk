LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libffmpeg
LOCAL_SRC_FILES := libffmpeg.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

PATH_TO_FFMPEG_SOURCE:=$(LOCAL_PATH)/ffmpeg
LOCAL_C_INCLUDES += $(PATH_TO_FFMPEG_SOURCE)  
LOCAL_LDLIBS += -llog -ljnigraphics -lffmpeg

LOCAL_MODULE    := extractor
LOCAL_SRC_FILES := extractor.c

include $(BUILD_SHARED_LIBRARY)
