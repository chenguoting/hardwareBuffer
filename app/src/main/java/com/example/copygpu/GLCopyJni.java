package com.example.copygpu;

/*
 * Author : chenguoting on 2018-8-1 17:23
 * Email : chen.guoting@nubia.com
 * Company : NUBIA TECHNOLOGY CO., LTD.
 */
public class GLCopyJni {
    static {
        System.loadLibrary("NubiaGLCopy");
    }
    private long mHandler;

    public GLCopyJni(int width, int height, int texture) {
        mHandler = initHardwareBuffer(width, height, texture);
    }

    public void release() {
        releaseHardwareBuffer(mHandler);
    }

    public byte[] getBuffer() {
        return getBuffer(mHandler);
    }

    public void setBuffer(byte[] buffer) {
        setBuffer(mHandler, buffer);
    }

    private static native long initHardwareBuffer(int width, int height, int texture);
    private static native void releaseHardwareBuffer(long handler);
    private static native byte[] getBuffer(long handler);
    private static native void setBuffer(long handler, byte[] buffer);
}
