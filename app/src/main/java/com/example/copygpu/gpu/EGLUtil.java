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
import android.util.Log;

public class EGLUtil {
	private final static String TAG = "EGLUtil";
	public static final int EGL_RECORDABLE_ANDROID = 0x3142;

	public static void arraycopy(float[] src, int srcPos, float[] dst, int dstPos, int length) {
		for(int s=srcPos,d=dstPos,i=0; s<src.length&&d<dst.length&&i<length; s++,d++,i++) {
			dst[d] = src[s];
		}
	}

	public static void arraycopy(int[] src, int srcPos, int[] dst, int dstPos, int length) {
		for(int s=srcPos,d=dstPos,i=0; s<src.length&&d<dst.length&&i<length; s++,d++,i++) {
			dst[d] = src[s];
		}
	}

	public static void checkEGLDisplay(EGLDisplay display) throws Exception {
		if(display == EGL14.EGL_NO_DISPLAY || EGL14.eglGetError() != EGL14.EGL_SUCCESS) {
			throw new Exception("no display");
		}else{
			Log.i(TAG,"eglGetDisplay success: "+ display.toString());
		}
	}
	

	public static EGLConfig getEGLConfig(EGLDisplay display) throws Exception {
		EGLConfig[] configs = new EGLConfig[1];
		int[] configSpec = new int[] {
				EGL14.EGL_RED_SIZE, 8,
				EGL14.EGL_GREEN_SIZE, 8,
				EGL14.EGL_BLUE_SIZE, 8,
				EGL14.EGL_ALPHA_SIZE, 8 ,
//				EGL14.EGL_DEPTH_SIZE, 16 , 
				EGL14.EGL_RENDERABLE_TYPE,
				EGL14.EGL_OPENGL_ES2_BIT,
				EGL_RECORDABLE_ANDROID, 1, 
				EGL14.EGL_NONE };
		
		int[] numConfig = new int[]{0};
		EGL14.eglChooseConfig(display, configSpec, 0, configs, 0, 1,
				numConfig, 0);
		if(EGL14.eglGetError()!= EGL14.EGL_SUCCESS || numConfig[0]==0){
			throw new Exception("get display config failed");
		}

		return  configs[0];
	}
	
	
	public static  void getExtensions(EGLDisplay display){
		String info = EGL14.eglQueryString(display, EGL14.EGL_EXTENSIONS);
		if(info==null || EGL14.eglGetError()!= EGL14.EGL_SUCCESS){
			Log.e("info","getExtensions failed: "+info);
		}else{
			Log.e("info","getExtensions success: "+info);
		}
	}
	
	public static  void getVendor(EGLDisplay display){
		String info = EGL14.eglQueryString(display, EGL14.EGL_VENDOR);
		if(info==null || EGL14.eglGetError()!= EGL14.EGL_SUCCESS){
			Log.e("info","getVendor failed: "+info);
		}else{
			Log.e("info","getVendor success: "+info);
		}
	}
	
	public static  void getVersion(EGLDisplay display){
		String info = EGL14.eglQueryString(display, EGL14.EGL_VERSION);
		if(info==null || EGL14.eglGetError()!= EGL14.EGL_SUCCESS){
			Log.e("info","getVersion2 failed: "+info);
		}else{
			Log.e("info","getVersion2 success: "+info);
		}
	}
	
	public static EGLContext getEGLContext(EGLDisplay display, EGLConfig config) throws Exception {
		int[] attrib_list = { EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE };
		EGLContext context =  EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, attrib_list, 0);
		int error = EGL14.eglGetError();
		if( EGL14.eglGetError() != EGL14.EGL_SUCCESS){
			throw new Exception("fail to get EGLContext error: "+error);
		}
		return context;
	}
	
	public static EGLDisplay getEGLDisplay() throws Exception {
		EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
		checkEGLDisplay(display);
		int[] version = new int[2];
		EGL14.eglInitialize(display, version, 0, version, 1);
		return display;
	}

}
