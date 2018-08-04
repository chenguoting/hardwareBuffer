#include "GraphicBufferEx.h"
#include "Utils.h"

static const int USAGE = (AHARDWAREBUFFER_USAGE_CPU_WRITE_OFTEN
                           | AHARDWAREBUFFER_USAGE_CPU_READ_OFTEN);

GraphicBufferEx::GraphicBufferEx(
        EGLDisplay eglDisplay, EGLContext eglContext)
{
    mEGLDisplay = eglDisplay;
    mEGLContext = eglContext;
}

int GraphicBufferEx::getWidth()
{
    AHardwareBuffer_Desc outDesc;
    AHardwareBuffer_describe(mHardwareBuffer, &outDesc);
    return outDesc.width;
}

int GraphicBufferEx::getHeight()
{
    AHardwareBuffer_Desc outDesc;
    AHardwareBuffer_describe(mHardwareBuffer, &outDesc);
    return outDesc.height;
}

int GraphicBufferEx::getStride()
{
    AHardwareBuffer_Desc outDesc;
    AHardwareBuffer_describe(mHardwareBuffer, &outDesc);
    return outDesc.stride;
}

int GraphicBufferEx::getFormat()
{
    AHardwareBuffer_Desc outDesc;
    AHardwareBuffer_describe(mHardwareBuffer, &outDesc);
    return outDesc.format;
}

void GraphicBufferEx::create(int width, int height, 
        int textureId, int format)
{
    //format
    //RGBA_8888                     1
    //RGB_888                       3
    //A_8                           8
    //HAL_PIXEL_FORMAT_YCrCb_420_SP 17
    //HAL_PIXEL_FORMAT_YV12
    AHardwareBuffer_Desc buffDesc;
    buffDesc.width = width;
    buffDesc.height = height;
    buffDesc.layers = 1;
    buffDesc.format = format;
    buffDesc.usage = USAGE;
    buffDesc.stride = width;
    buffDesc.rfu0 = 0;
    buffDesc.rfu1 = 0;

    int err = AHardwareBuffer_allocate(&buffDesc, &mHardwareBuffer);

    if (0 != err)//NO_ERROR
    {
        LOGE("GraphicBufferEx HardwareBuffer create error. (NO_ERROR != err)");
        return;
    }
    else {
        LOGI("AHardwareBuffer_allocate %p", mHardwareBuffer);
    }

    EGLClientBuffer clientBuffer = eglGetNativeClientBufferANDROID(mHardwareBuffer);
    checkEglError("eglGetNativeClientBufferANDROID");
    mEGLImageKHR = eglCreateImageKHR(
            mEGLDisplay, mEGLContext,
            EGL_NATIVE_BUFFER_ANDROID, clientBuffer, 0);
    checkEglError("eglCreateImageKHR");

    if (EGL_NO_IMAGE_KHR == mEGLImageKHR)
    {
        LOGE("GraphicBufferEx create error. (EGL_NO_IMAGE_KHR == mEGLImageKHR)");
        return;
    }

    glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
    checkGlError("glBindTexture");
    glEGLImageTargetTexture2DOES(GL_TEXTURE_EXTERNAL_OES, (GLeglImageOES)mEGLImageKHR);
    checkGlError("glEGLImageTargetTexture2DOES");
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    checkGlError("glTexParameteri");
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    checkGlError("glTexParameteri");
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
}

void GraphicBufferEx::destroy()
{
    AHardwareBuffer_release(mHardwareBuffer);
    eglDestroyImageKHR(mEGLDisplay, mEGLImageKHR);
}

void GraphicBufferEx::setBuffer(GPUIPBuffer *srcBuffer)
{
    uint8_t *pDstBuffer = NULL;

    if (NULL == srcBuffer)
    {
        LOGE("GraphicBufferEx setBuffer (NULL == srcBuffer)\n");
        return;
    }

    int err = AHardwareBuffer_lock(mHardwareBuffer, USAGE, -1, NULL, (void**)(&pDstBuffer));

    if (0 != err)//NO_ERROR
    {
        LOGE("GraphicBufferEx setBuffer AHardwareBuffer_lock failed. err = %d\n", err);
        return;
    }

    GPUIPBuffer dstBuffer;
    
    dstBuffer.width = getWidth();
    dstBuffer.height = getHeight();
    dstBuffer.format = getFormat();
    dstBuffer.stride = getStride();
    dstBuffer.pY = pDstBuffer;
    dstBuffer.pU = pDstBuffer + dstBuffer.stride * dstBuffer.height;
    dstBuffer.pV = dstBuffer.pU;

    switch(getFormat())
    {
        case 1://RGBA_8888
        {
            GPUIPBuffer_RGBA_COPY(srcBuffer, &dstBuffer);
            break;
        }

        case 3://RGB_888
        {
            GPUIPBuffer_RGB_COPY(srcBuffer, &dstBuffer);
            break;
        }

        case 17://HAL_PIXEL_FORMAT_YCrCb_420_SP
        {
            GPUIPBuffer_NV21_COPY(srcBuffer, &dstBuffer);
            break;
        }

        default:
        {
            LOGE("GraphicBufferEx setBuffer do not surpport format = %d.\n", getFormat());
        }
    }

    int fence = -1;
    err = AHardwareBuffer_unlock(mHardwareBuffer, &fence);
    
    if (0 != err)//NO_ERROR
    {
        LOGE("GraphicBufferEx setBuffer AHardwareBuffer_lock failed. err = %d\n", err);
        return;
    }
}

void GraphicBufferEx::getBuffer(
        GBDataCallBackFun pCallBackFun, GPUIPBuffer *dstBuffer)
{
    uint8_t *pSrcBuffer = NULL;

    if ((NULL == pCallBackFun) || (NULL == dstBuffer))
    {
        LOGE("GraphicBufferEx getBuffer ((NULL == pCallBackFun) || (NULL == dstBuffer))\n");
        return;
    }

    int err = AHardwareBuffer_lock(mHardwareBuffer, USAGE, -1, NULL, (void**)(&pSrcBuffer));

    if (0 != err)//NO_ERROR
    {
        LOGE("GraphicBufferEx getBuffer AHardwareBuffer_lock failed. err = %d\n", err);
        return;
    }

    GPUIPBuffer srcBuffer;

    srcBuffer.width = getWidth();
    srcBuffer.height = getHeight();
    srcBuffer.format = getFormat();
    srcBuffer.stride = getStride();
    srcBuffer.pY = pSrcBuffer;
    srcBuffer.pU = pSrcBuffer + srcBuffer.stride * srcBuffer.height;
    srcBuffer.pV = srcBuffer.pU;
    pCallBackFun(&srcBuffer, dstBuffer);

    int fence = -1;
    err = AHardwareBuffer_unlock(mHardwareBuffer, &fence);

    if (0 != err)//NO_ERROR
    {
        LOGE("GraphicBufferEx getBuffer AHardwareBuffer_unlock failed. err = %d\n", err);
        return;
    }
}
