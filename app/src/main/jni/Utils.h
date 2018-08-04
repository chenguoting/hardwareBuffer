#ifndef UTILS_H
#define UTILS_H

#define CLAMP(x , min , max) ((x) > (max) ? (max) : ((x) < (min) ? (min) : x))

void printGLString(const char *name, GLenum s);
void checkGlError(const char* op);
void checkEglError(const char* op);
void printOpenGLInfo();
float calcAngle(float x, float y);
float calcAngleByVec2(float *a, float *b);
float calcAngleByVec3(float *a, float *b);
void depth2Bump(int width, int height, 
        const uint8_t *pDepthBuffer, uint32_t *pBumpBuffer,
        float param1, float param2);
void dump(const char *pFileName, void *pBufferSrc, int bufferLen);
void unDump(const char *pFileName, void *pBufferSrc, int bufferLen);

typedef struct {
    float yaw;
    float pitch;
    float roll;
} EulerAngle;

class Quaternion
{
public:
    float x , y , z , w;

    Quaternion(void) : x(0.0f) , y(0.0f) , z(0.0f) , w(1.0f) {}
    ~Quaternion(void) {}
    void setEulerAngle(const EulerAngle& ea);
    void getEulerAngle(EulerAngle& ea);
};
#endif