#ifndef GPUIP_COMMON_H
#define GPUIP_COMMON_H
#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <android/log.h>
#include <GLES3/gl3.h>
#include <android/log.h>

#define PI			3.1415926f
#define MAX_BINARY_PROGRAM_SIZE (50 * 1024)

#undef LOG_TAG
#define LOG_TAG		"[libGPUIP]"
#define LOGI(...)	((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGW(...)	((void)__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))
#define LOGE(...)	((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))
#define PRINTF		LOGI

//#define JNI_INTERFACE_BOKEH
//#define JNI_INTERFACE_ROTATION
//#define JNI_INTERFACE_ZOOMBLUR

#define USE_GRAPHIC_BUFFER
//#define USE_HARDWARE_BUFFER

//#define SHADER_PROGRAM_BINARY

typedef enum
{
/*8 bit Y plane followed by 8 bit 2x2 subsampled UV planes*/
    PIX_FORMAT_NV12,
/*8 bit Y plane followed by 8 bit 2x2 subsampled VU planes*/
    PIX_FORMAT_NV21,
/*8 bit Y plane followed by 8 bit 2x2 subsampled U and V planes*/
    PIX_FORMAT_I420
}PIX_FORMAT_SUPPORT;

struct yuvBuffer
{
public:
    int w;
    int h;
    int pixformat;
    int yPitch;
    int uvPitch;
    int len;
    void *Y;
    void *U;
    void *V;

public:
    yuvBuffer()
        : w(0)
        , h(0)
        , pixformat(PIX_FORMAT_NV21)
        , yPitch(0)
        , uvPitch(0)
        , len(0)
        , Y(NULL)
        , U(NULL)
        , V(NULL) {
    }
};

struct GPUIPBuffer
{
public:
    int width;
    int height;
    int format;
    int stride;
    int length;
    void *pY;
    void *pU;
    void *pV;

public:
    GPUIPBuffer()
        : width(0)
        , height(0)
        , format(PIX_FORMAT_NV21)
        , stride(0)
        , length(0)
        , pY(NULL)
        , pU(NULL)
        , pV(NULL) {
    }
};

void GPUIPBuffer_NV21_COPY(GPUIPBuffer *srcBuffer, GPUIPBuffer *dstBuffer);
void GPUIPBuffer_RGB_COPY(GPUIPBuffer *srcBuffer, GPUIPBuffer *dstBuffer);
void GPUIPBuffer_RGBA_COPY(GPUIPBuffer *srcBuffer, GPUIPBuffer *dstBuffer);
void GPUIPBuffer_Y8U8V8_NV21(GPUIPBuffer *srcBuffer, GPUIPBuffer *dstBuffer);


#endif