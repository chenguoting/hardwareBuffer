//
// Author : chenguoting on 2018-8-1 18:38
// Email : chen.guoting@nubia.com
// Company : NUBIA TECHNOLOGY CO., LTD.
//

#ifndef NEWCAMERA_NUBIAGLCOPY_H
#define NEWCAMERA_NUBIAGLCOPY_H
#include <jni.h>
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_example_copygpu_GLCopyJni_initHardwareBuffer(
        JNIEnv*env, jclass type, jint, jint, jint);

JNIEXPORT void JNICALL
Java_com_example_copygpu_GLCopyJni_releaseHardwareBuffer(
        JNIEnv*env, jclass type, jlong handler);

JNIEXPORT jbyteArray JNICALL
Java_com_example_copygpu_GLCopyJni_getBuffer(
        JNIEnv*env, jclass type, jlong handler);

JNIEXPORT void JNICALL
Java_com_example_copygpu_GLCopyJni_setBuffer(
        JNIEnv*env, jclass type, jlong handler, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif //NEWCAMERA_NUBIAGLCOPY_H
