#include "GPUIPCommon.h"
#include <math.h>
#include <EGL/egl.h>
#include "Utils.h"

void printGLString(const char *name, GLenum s)
{
    const char *str = (const char *) glGetString(s);
    LOGI("GL %s = %s\n", name, str);
    return;
}

void checkGlError(const char* op) 
{
    for (GLint error = glGetError(); error; error = glGetError()) 
    {
        LOGE("after %s() glError (0x%x)\n", op, error);
    }

    return;
}

void checkEglError(const char* op) {
    GLint error = eglGetError();
    if(error != EGL_SUCCESS) {
        LOGE("after %s() eglError (0x%x)\n", op, error);
    }
    return;
}

void printOpenGLInfo() 
{
    printGLString("Version", GL_VERSION);
    printGLString("Vendor", GL_VENDOR);
    printGLString("Renderer", GL_RENDERER);
    printGLString("Extensions", GL_EXTENSIONS);
    return;
}

float calcAngle(float x, float y)
{
    if (x == 0.0f)
    {
        if (y > 0.0f)
        {
            return 90.0f;
        }
        else
        {
            return 270.0f;
        }
    }

    float tana = y / x;
    float angle = atan(tana) * 180.0f / PI;

    if (x < 0.0f)
    {
        angle += 180.0f;
    }

    if (angle < 0.0f)
    {
        angle += 360.0f;
    } 
    else if (angle > 360.0f)
    {
        angle -= 360.0f;
    }

    return angle;
}


float calcAngleByVec2(float *a, float *b)
{
    float aLength = sqrt(a[0] * a[0] + a[1] * a[1]);
    float bLength = sqrt(b[0] * b[0] + b[1] * b[1]);

    float cosa = (a[0] * b[0] + a[1] * b[1]) / (aLength * bLength);
    return acos(cosa) * 180.0f / PI;
}

float calcAngleByVec3(float *a, float *b)
{
    float aLength = sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    float bLength = sqrt(b[0] * b[0] + b[1] * b[1] + b[2] * b[2]);

    float cosa = (a[0] * b[0] + a[1] * b[1] + a[2] * b[2]) / (aLength * bLength);
    return acos(cosa) * 180.0f / PI;
}

void depth2Bump(int width, int height, 
        const uint8_t *pDepthBuffer, uint32_t *pBumpBuffer,
        float param1, float param2)
{
    int i, j;
    int value, valueUp, valueRight;
    int vecInt[3];
    float vec[3], vecUp[3], vecRight[3];
    float module;

    vecUp[0] = 1.0f;
    vecUp[1] = 0.0f;
    vecRight[0] = 0.0f;
    vecRight[1] = 1.0f;

    for (j = 0; j < height; j++)
    {
        for (i = 0; i < width; i++)
        {
            value = pDepthBuffer[j * width + i];

            if ((j == 0) || (i == width - 1))
            {
                pBumpBuffer[j * width + i] = 0x00ff8080 | (value << 24);
                continue;
            }

            valueUp = pDepthBuffer[(j - 1) * width + i];
            valueRight = pDepthBuffer[j * width + (i + 1)];

            vecUp[2] = (valueUp - value) / 255.0f * param1;
            vecRight[2] = (valueRight - value) / 255.0f * param2;

            //cross product and normal
            vec[0] = vecUp[1] * vecRight[2] - vecRight[1] * vecUp[2];
            vec[1] = vecUp[2] * vecRight[0] - vecRight[2] * vecUp[0];
            vec[2] = vecUp[0] * vecRight[1] - vecRight[0] * vecUp[1];
            module = sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
            vec[0] /= module;
            vec[1] /= module;
            vec[2] /= module;

            vecInt[0] = (int)(vec[0] * 128.0f) + 128;
            vecInt[1] = (int)(vec[1] * 128.0f) + 128;
            vecInt[2] = (int)(vec[2] * 128.0f) + 128;
            vecInt[0] = (vecInt[0] > 255) ? 255 : vecInt[0];
            vecInt[1] = (vecInt[1] > 255) ? 255 : vecInt[1];
            vecInt[2] = (vecInt[2] > 255) ? 255 : vecInt[2];
            //ABGR
            //pBumpBuffer[j * width + i] = 0xff000000 | (vecInt[2] << 16) | (vecInt[1] << 8) | vecInt[0];
            pBumpBuffer[j * width + i] = (value << 24) | (vecInt[2] << 16) | (vecInt[1] << 8) | vecInt[0];
        }
    }
}

void dump(const char *pFileName, void *pBufferSrc, int bufferLen)
{
    FILE *pFile = fopen(pFileName, "w+");

    if (pFile != NULL)
    {
        int countWrited = 0;
        int countRemind = bufferLen;
        uint8_t *pBufferSrcEx = (uint8_t *)pBufferSrc;
        uint8_t *pBuffer = pBufferSrcEx;

        do
        {
            countWrited += fwrite(pBuffer, 1, countRemind, pFile);
            pBuffer = pBufferSrcEx + countWrited;
            countRemind = bufferLen - countWrited;
        } while(countRemind > 0);

        fclose(pFile);
    }
    else
    {
        LOGE("dump open file %s error!", pFileName);
    }
}

void unDump(const char *pFileName, void *pBufferSrc, int bufferLen)
{
    FILE *pFile = fopen(pFileName, "r");

    if (pFile != NULL)
    {
        int countRead = 0;
        int countRemind = bufferLen;
        uint8_t *pBufferSrcEx = (uint8_t *)pBufferSrc;
        uint8_t *pBuffer = pBufferSrcEx;

        do
        {
            countRead += fread(pBuffer, 1, countRemind, pFile);
            pBuffer = pBufferSrcEx + countRead;
            countRemind = bufferLen - countRead;
        } while(countRemind > 0);

        fclose(pFile);
    }
    else
    {
        LOGI("unDump open file %s error!", pFileName);
    }
}

void Quaternion::setEulerAngle(const EulerAngle &ea)
{
    float cr = cos(ea.roll / 57.3f / 2.0f);
    float sr = sin(ea.roll / 57.3f / 2.0f);
    float cp = cos(ea.pitch / 57.3f / 2.0f);
    float sp = sin(ea.pitch / 57.3f / 2.0f);
    float cy = cos(ea.yaw / 57.3f / 2.0f);
    float sy = sin(ea.yaw / 57.3f / 2.0f);

    w = cr * cp * cy + sr * sp * sy;
    x = cr * sp * cy + sr * cp * sy;
    y = cr * cp * sy - sr * sp * cy;
    z = sr * cp * cy - cr * sp * sy;
}

void Quaternion::getEulerAngle(EulerAngle &ea) 
{
    ea.roll = atan2(2.0f * (w * z + x * y) , 1.0f - 2.0f * (z * z + x * x));
    ea.roll *= 57.3f;
    ea.pitch = asin(CLAMP(2.0f * (w * x - y * z) , -1.0f , 1.0f));
    ea.pitch *= 57.3f;
    ea.yaw = atan2(2.0f * (w * y + z * x) , 1.0f - 2.0f * (x * x + y * y));
    ea.yaw *= 57.3f;
    return;
}