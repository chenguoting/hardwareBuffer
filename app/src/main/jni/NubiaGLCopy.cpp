#include "NubiaGLCopy.h"
#include "GraphicBufferEx.h"
#include "Utils.h"


JNIEXPORT jlong JNICALL
Java_com_example_copygpu_GLCopyJni_initHardwareBuffer(
        JNIEnv*env, jclass type, jint width, jint height, jint textureId)
{
    GraphicBufferEx* graphicBufferEx = new GraphicBufferEx(eglGetCurrentDisplay(), EGL_NO_CONTEXT);
    int format = 17; //HAL_PIXEL_FORMAT_YCrCb_420_SP
    graphicBufferEx->create(width, height, textureId, format);
    return (jlong)graphicBufferEx;
}

JNIEXPORT void JNICALL
Java_com_example_copygpu_GLCopyJni_releaseHardwareBuffer(
        JNIEnv*env, jclass type, jlong handler)
{
    GraphicBufferEx* graphicBufferEx =  (GraphicBufferEx*)handler;
    graphicBufferEx->destroy();
    delete graphicBufferEx;
}

JNIEXPORT jbyteArray JNICALL
Java_com_example_copygpu_GLCopyJni_getBuffer(
        JNIEnv*env, jclass type, jlong handler)
{
    GraphicBufferEx* graphicBufferEx =  (GraphicBufferEx*)handler;
    jbyteArray jYuvArray = env->NewByteArray(graphicBufferEx->getWidth()*graphicBufferEx->getHeight()*3/2);
    jbyte* yuvArray = env->GetByteArrayElements(jYuvArray, NULL);
    GPUIPBuffer buffer;
    buffer.width = graphicBufferEx->getWidth();
    buffer.height = graphicBufferEx->getHeight();
    buffer.format = graphicBufferEx->getFormat();
    buffer.stride = buffer.width;
    buffer.length = buffer.stride * buffer.height * 3 / 2;
    buffer.pY = yuvArray;
    buffer.pU = yuvArray + buffer.stride * buffer.height;
    buffer.pV = buffer.pU;

    graphicBufferEx->getBuffer(GPUIPBuffer_NV21_COPY, &buffer);
    //LOGI("dump yuv");
    //dump("/sdcard/123.yuv", buffer.pY, buffer.length);
    env->ReleaseByteArrayElements(jYuvArray, yuvArray, 0);
    return jYuvArray;
}

JNIEXPORT void JNICALL
Java_com_example_copygpu_GLCopyJni_setBuffer(
        JNIEnv*env, jclass type, jlong handler, jbyteArray jbuffer)
{
    GraphicBufferEx* graphicBufferEx =  (GraphicBufferEx*)handler;
    jbyte* buffer = env->GetByteArrayElements(jbuffer, NULL);
    GPUIPBuffer inputBuffer;
    inputBuffer.width = graphicBufferEx->getWidth();
    inputBuffer.height = graphicBufferEx->getHeight();
    inputBuffer.format = 17; //HAL_PIXEL_FORMAT_YCrCb_420_SP
    inputBuffer.stride = inputBuffer.width;
    inputBuffer.length = inputBuffer.stride * inputBuffer.height * 3 / 2;
    inputBuffer.pY = buffer;
    inputBuffer.pU = buffer + inputBuffer.stride * inputBuffer.height;
    inputBuffer.pV = inputBuffer.pU;
    graphicBufferEx->setBuffer(&inputBuffer);
    env->ReleaseByteArrayElements(jbuffer, buffer, 0);
}