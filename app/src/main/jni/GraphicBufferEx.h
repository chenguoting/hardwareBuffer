#ifndef GRAHIC_BUFFER_EX_H
#define GRAHIC_BUFFER_EX_H
#include "GPUIPCommon.h"

#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <EGL/eglplatform.h>

#include <GLES3/gl3.h>
#include <GLES2/gl2ext.h>
#include <GLES3/gl3ext.h>
#include <android/hardware_buffer.h>

typedef void (*GBDataCallBackFun)(\
        GPUIPBuffer *srcBuffer, GPUIPBuffer *dstBuffer);

class GraphicBufferEx {
private:
    EGLDisplay mEGLDisplay;
    EGLContext mEGLContext;
    EGLImageKHR mEGLImageKHR;
    AHardwareBuffer *mHardwareBuffer;

public:
    GraphicBufferEx(
            EGLDisplay eglDisplay, EGLContext eglContext);
    int getWidth();
    int getHeight();
    int getStride();
    int getFormat();
    void create(int width, int height, 
            int textureId, int format);
    void destroy();
    void setBuffer(GPUIPBuffer *Buffer);
    void getBuffer(
            GBDataCallBackFun pCallBackFun, GPUIPBuffer *dstBuffer);
};
#endif
