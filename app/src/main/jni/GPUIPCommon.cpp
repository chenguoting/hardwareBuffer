#include "GPUIPCommon.h"

void GPUIPBuffer_NV21_COPY(GPUIPBuffer *srcBuffer, GPUIPBuffer *dstBuffer)
{
    if ((srcBuffer->width != dstBuffer->width) 
            || (srcBuffer->height != dstBuffer->height))
    {
        LOGE("GPUIPBuffer_NV21_COPY error. srcW = %d, dstW = %d, srcH = %d, dstH = %d\n", 
                srcBuffer->width, dstBuffer->width, srcBuffer->height, dstBuffer->height);
        return;
    }

    if (srcBuffer->stride == dstBuffer->stride)
    {
        int size = srcBuffer->stride * srcBuffer->height;

        memcpy(dstBuffer->pY, srcBuffer->pY, size);
        memcpy(dstBuffer->pU, srcBuffer->pU, size / 2);
    }
    else
    {
        int i;
        uint8_t *pSrc;
        uint8_t *pDst;

        //LOGE("GPUIPBuffer_NV21_COPY warning! srcStride = %d, dstStride = %d",
        //       srcBuffer->stride, dstBuffer->stride);
        pSrc = (uint8_t *)srcBuffer->pY;
        pDst = (uint8_t *)dstBuffer->pY;

        for (i = 0; i < srcBuffer->height; i++)
        {
            memcpy(pDst, pSrc, srcBuffer->width);
            pSrc += srcBuffer->stride;
            pDst += dstBuffer->stride;
        }

        pSrc = (uint8_t *)srcBuffer->pU;
        pDst = (uint8_t *)dstBuffer->pU;

        for (i = 0; i < srcBuffer->height / 2; i++)
        {
            memcpy(pDst, pSrc, srcBuffer->width);
            pSrc += srcBuffer->stride;
            pDst += dstBuffer->stride;
        }
    }
}

void GPUIPBuffer_RGB_COPY(GPUIPBuffer *srcBuffer, GPUIPBuffer *dstBuffer)
{
    int size;

    if ((srcBuffer->width != dstBuffer->width) 
            || (srcBuffer->height != dstBuffer->height))
    {
        LOGE("GPUIPBuffer_RGB_COPY error. srcW = %d, dstW = %d, srcH = %d, dstH = %d\n", 
                srcBuffer->width, dstBuffer->width, srcBuffer->height, dstBuffer->height);
        return;
    }

    if (srcBuffer->stride == dstBuffer->stride)
    {
        size = srcBuffer->stride * srcBuffer->height * 3;
        memcpy(dstBuffer->pY, srcBuffer->pY, size);
    }
    else
    {
        int i;
        int srcSize;
        int dstSize;
        uint8_t *pSrcBuffer;
        uint8_t *pDstBuffer;

        LOGE("GPUIPBuffer_RGB_COPY warning! srcStride = %d, dstStride = %d", 
               srcBuffer->stride, dstBuffer->stride);
        pSrcBuffer = (uint8_t *)srcBuffer->pY;
        pDstBuffer = (uint8_t *)dstBuffer->pY;
        size = srcBuffer->width * 3;
        srcSize = srcBuffer->stride * 3;
        dstSize = dstBuffer->stride * 3;

        for (i = 0; i < srcBuffer->height; i++)
        {
            memcpy(pDstBuffer, pSrcBuffer, size);
            pSrcBuffer += srcSize;
            pDstBuffer += dstSize;
        }
    }
}

void GPUIPBuffer_RGBA_COPY(GPUIPBuffer *srcBuffer, GPUIPBuffer *dstBuffer)
{
    int size;

    if ((srcBuffer->width != dstBuffer->width) 
            || (srcBuffer->height != dstBuffer->height))
    {
        LOGE("GPUIPBuffer_RGBA_COPY error. srcW = %d, dstW = %d, srcH = %d, dstH = %d\n", 
                srcBuffer->width, dstBuffer->width, srcBuffer->height, dstBuffer->height);
        return;
    }

    if (srcBuffer->stride == dstBuffer->stride)
    {
        size = srcBuffer->stride * srcBuffer->height * 4;
        memcpy(dstBuffer->pY, srcBuffer->pY, size);
    }
    else
    {
        int i;
        int srcSize;
        int dstSize;
        uint8_t *pSrcBuffer;
        uint8_t *pDstBuffer;

        LOGE("GPUIPBuffer_RGBA_COPY warning! srcStride = %d, dstStride = %d", 
               srcBuffer->stride, dstBuffer->stride);
        pSrcBuffer = (uint8_t *)srcBuffer->pY;
        pDstBuffer = (uint8_t *)dstBuffer->pY;
        size = srcBuffer->width * 4;
        srcSize = srcBuffer->stride * 4;
        dstSize = dstBuffer->stride * 4;

        for (i = 0; i < srcBuffer->height; i++)
        {
            memcpy(pDstBuffer, pSrcBuffer, size);
            pSrcBuffer += srcSize;
            pDstBuffer += dstSize;
        }
    }
}

void GPUIPBuffer_Y8U8V8_NV21(GPUIPBuffer *srcBuffer, GPUIPBuffer *dstBuffer)
{
    int i, j;
    int width, height;
    uint8_t *pSrcBuffer;
    uint8_t *pDstBuffer;

    if ((srcBuffer->width != dstBuffer->width) 
            || (srcBuffer->height != dstBuffer->height))
    {
        LOGE("GPUIPBuffer_Y8U8V8_NV21 error. srcW = %d, dstW = %d, srcH = %d, dstH = %d\n", 
                srcBuffer->width, dstBuffer->width, srcBuffer->height, dstBuffer->height);
        return;
    }

    width = srcBuffer->width;
    height = srcBuffer->height;

    //Y8U8V8 -> YUV
    for (j = 0; j < height; j++)
    {
        pSrcBuffer = (uint8_t *)srcBuffer->pY;
        pSrcBuffer += (srcBuffer->stride * j * 3);
        pDstBuffer = (uint8_t *)dstBuffer->pY;
        pDstBuffer += (dstBuffer->stride * j);
        
        for (i = 0; i < width; i++)
        {
            *pDstBuffer = *pSrcBuffer;
            pSrcBuffer += 3;
            pDstBuffer++;
        }
    }

    for (j = 0; j < height / 2; j++)
    {
        pSrcBuffer = (uint8_t *)srcBuffer->pY;
        pSrcBuffer += (srcBuffer->stride * j * 3 * 2 + 1);
        pDstBuffer = (uint8_t *)dstBuffer->pU;
        pDstBuffer += (dstBuffer->stride * j);
                
        for (i = 0; i < width / 2; i++)
        {
            *pDstBuffer = *pSrcBuffer;
            pSrcBuffer++;
            pDstBuffer++;
       
            *pDstBuffer = *pSrcBuffer;
            pSrcBuffer += 5;
            pDstBuffer++;
        }
    }
}

