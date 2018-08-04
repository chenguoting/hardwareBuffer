package com.example.copygpu.gpu;

import android.opengl.GLES20;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.copygpu.view.MySurfaceView;

import java.util.ArrayList;


/*
 * Author : chenguoting
 * Email : chen.guoting@nubia.com
 * Company : NUBIA TECHNOLOGY CO., LTD.
 */

public class RenderThread extends Thread {
    private static final String TAG = "RenderThread";

    private boolean mFinished = false;    

    private SurfaceHolder mWindowSurfaceHolder;
    private int width, height;
    private boolean isSurfaceChanged = false;
    private boolean isEGLSurfaceCreate = false;
    private EGLManager mEGLManager;
    private ArrayList<MySurfaceView.Renderer> mRendererList = new ArrayList<MySurfaceView.Renderer>();

    
    public RenderThread(SurfaceHolder holder, ArrayList<MySurfaceView.Renderer> list){
        super("Nubia Render Thread");
        mWindowSurfaceHolder = holder;
        mRendererList = list;
    }

    public void setSurfaceSize(int w, int h) {
        width = w;
        height = h;
        isSurfaceChanged = true;
        synchronized (this) {
            notify();
        }
    }

    private void notifyRendererCreated() {
        if(mRendererList.isEmpty()) {
            return;
        }
        MySurfaceView.Renderer[] renderers = mRendererList.toArray(new MySurfaceView.Renderer[mRendererList.size()]);
        for(MySurfaceView.Renderer renderer : renderers) {
            renderer.onSurfaceCreated();
        }
    }
    private void notifyRendererChanged() {
        if(mRendererList.isEmpty()) {
            return;
        }
        MySurfaceView.Renderer[] renderers = mRendererList.toArray(new MySurfaceView.Renderer[mRendererList.size()]);
        for(MySurfaceView.Renderer renderer : renderers) {
            renderer.onSurfaceChanged(width, height);
        }
    }
    private void notifyRendererDraw() {
        if(mRendererList.isEmpty()) {
            return;
        }
        MySurfaceView.Renderer[] renderers = mRendererList.toArray(new MySurfaceView.Renderer[mRendererList.size()]);
        for(MySurfaceView.Renderer renderer : renderers) {
            renderer.onSurfaceDraw();
        }
    }
    private void notifyRendererDestroyed() {
        if(mRendererList.isEmpty()) {
            return;
        }
        MySurfaceView.Renderer[] renderers = mRendererList.toArray(new MySurfaceView.Renderer[mRendererList.size()]);
        for(MySurfaceView.Renderer renderer : renderers) {
            renderer.onSurfaceDestroyed();
        }
    }

    public void finish(){
        mFinished = true;
        mWindowSurfaceHolder = null;
        synchronized (this) {
            notify();
        }
    }
    
    @Override
    public void run() {
        Log.i(TAG, "thread "+getId()+" start");
        initEglEnv();

        notifyRendererCreated();

        while (!mFinished) {
            synchronized (this) {
                if(width == 0 || height == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
            }

            if(isSurfaceChanged) {
                notifyRendererChanged();
                isSurfaceChanged = false;
            }

            notifyRendererDraw();

            GLES20.glFinish();
            mEGLManager.swapBuffer();
        }
        notifyRendererDestroyed();

        releaseEglEnv();
        Log.i(TAG, "thread "+getId()+" end");
    }

    private void initEglEnv() {
        try {
            mEGLManager = new EGLManager();
            mEGLManager.createWindowSurface(mWindowSurfaceHolder);
            mEGLManager.registerContext();
            isEGLSurfaceCreate = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseEglEnv() {
        Log.i(TAG, "releaseEglEnv");
        if(isEGLSurfaceCreate) {
            mEGLManager.unregisterContext();
            mEGLManager.releaseWindowSurface();
            mEGLManager.release();
            isEGLSurfaceCreate = false;
        }
    }


}
