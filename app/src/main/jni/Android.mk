LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)
    LOCAL_MODULE := libNubiaGLCopy

    LOCAL_SRC_FILES += GraphicBufferEx.cpp \
                       GPUIPCommon.cpp \
                       Utils.cpp \
                       NubiaGLCopy.cpp

    LOCAL_C_INCLUDES += $(LOCAL_PATH)/GPUIP \
                        $(LOCAL_PATH)/GPUIP/Utils \
                        $(NDK_INCLUDE)

    LOCAL_LDLIBS += -llog -lEGL -lGLESv3 -lnativewindow
    LOCAL_CPPFLAGS += -DGL_GLEXT_PROTOTYPES -DEGL_EGLEXT_PROTOTYPES -Wno-unused-parameter
include $(BUILD_SHARED_LIBRARY)
