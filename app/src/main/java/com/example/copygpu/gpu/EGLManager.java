package com.example.copygpu.gpu;
/*
 * Author : jinrong on 2017-7-21 9:16
 * Email : jin.rong1@nubia.com
 * Company : NUBIA TECHNOLOGY CO., LTD.
 */

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.view.SurfaceHolder;

public class EGLManager {
	
	
	private EGLDisplay mEglDisplay;
	private EGLConfig mEGLConfig;
	private EGLContext mEGLContext;
	private EGLSurface mEglWindowSurface;
	private EGLSurface mEglSnapshotSurface = null;
	private State mState = State.UNINIT;
	public enum State{
		UNINIT,
		INITED,
		SURFACED,
		REGISTERD,
		UNREGISTERD,
	}
	
	public EGLManager() throws Exception {
		mEglDisplay = EGLUtil.getEGLDisplay();
		mEGLConfig = EGLUtil.getEGLConfig(mEglDisplay);
		mEGLContext = EGLUtil.getEGLContext(mEglDisplay, mEGLConfig);
		mState = State.INITED;
	}
	
	public void release() {
		if (mState.ordinal() > State.UNINIT.ordinal()) {
			EGL14.eglDestroyContext(mEglDisplay, mEGLContext);
			mEGLContext = null;
			if(mEglSnapshotSurface != null){
				EGL14.eglDestroySurface(mEglDisplay, mEglSnapshotSurface);
				mEglSnapshotSurface=null;
			}
			EGL14.eglTerminate(mEglDisplay);
		}
		
		mEglDisplay = null;
		mEGLConfig = null;
		
		mState = State.UNINIT;
	}
	
	public void swapBuffer(){
		EGL14.eglSwapBuffers(mEglDisplay, mEglWindowSurface);
	}
	
	public boolean registerContext(){
		return EGL14.eglMakeCurrent(mEglDisplay, mEglWindowSurface,
				mEglWindowSurface, mEGLContext);
	}
	
	public void unregisterContext(){
		EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE,
				EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
	}
	
	public EGLSurface createWindowSurface(SurfaceHolder surface) throws Exception {
		int[] surfaceAttribs = { EGL14.EGL_NONE };
		mEglWindowSurface = EGL14.eglCreateWindowSurface(mEglDisplay, mEGLConfig, surface, surfaceAttribs, 0);
		int error = EGL14.eglGetError();
		if(error != EGL14.EGL_SUCCESS){
			throw new Exception("fail to get window surface  error: "+error);
		}
		return mEglWindowSurface;
	}

    public void releaseWindowSurface() {
        if(mEglWindowSurface != null) {
            EGL14.eglDestroySurface(mEglDisplay, mEglWindowSurface);
            mEglWindowSurface=null;
        }
    }

}
