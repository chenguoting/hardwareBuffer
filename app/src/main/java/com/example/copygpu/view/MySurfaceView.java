package com.example.copygpu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.copygpu.gpu.RenderThread;

import java.util.ArrayList;

/*
 * Author : chenguoting on 2018-8-4 10:42
 * Email : chen.guoting@nubia.com
 * Company : NUBIA TECHNOLOGY CO., LTD.
 */
public class MySurfaceView extends SurfaceView {
    private final static String TAG = "MySurfaceView";
    private RenderThread mRenderThread;
    private ArrayList<Renderer> mRendererList = new ArrayList<Renderer>();

    public interface Renderer {
        public void onSurfaceCreated();
        public void onSurfaceChanged(int width, int height);
        public void onSurfaceDraw();
        public void onSurfaceDestroyed();
    }

    public MySurfaceView(Context context) {
        this(context, null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getHolder().addCallback(Callback);
    }

    public void addRenderer(Renderer renderer) {
        mRendererList.add(renderer);
    }

    public void removeRenderer(Renderer renderer) {
        mRendererList.remove(renderer);
    }

    SurfaceHolder.Callback Callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.i(TAG, "surfaceCreated");
            mRenderThread = new RenderThread(surfaceHolder, mRendererList);
            mRenderThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.i(TAG, "surfaceChanged "+i+" "+i1+" "+i2);
            mRenderThread.setSurfaceSize(i1, i2);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.i(TAG, "surfaceDestroyed");
            mRenderThread.finish();
        }
    };
}
