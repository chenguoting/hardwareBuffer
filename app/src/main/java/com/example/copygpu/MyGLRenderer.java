package com.example.copygpu;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.example.copygpu.gpu.GLUtil;
import com.example.copygpu.view.MySurfaceView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class MyGLRenderer implements MySurfaceView.Renderer {
    private final static String TAG = "GPURenderer";
    private Context mContext;
    private int mPreviewTextureId = -1;
    private SurfaceTexture mPreviewTexture;
    private GLCopyJni mGLCopyJni;
    private int mOESTextureId = -1;
    private Picture mPreview;
    private Picture m2DPicture;
    private Picture mOesPicture;
    private Picture mOes2OesPicture;
    private int[] mFrameBuffer;
    private int m2DTextureId = -1;


    public MyGLRenderer(Context c, SurfaceTexture texture) {
        mContext = c;
        mPreviewTexture = texture;
    }

    @Override
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mPreviewTextureId = GLUtil.getOneTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mPreviewTexture.detachFromGLContext();
        mPreviewTexture.attachToGLContext(mPreviewTextureId);
        mPreview = new Picture(mContext.getResources(), R.raw.texture_vertex_shader, R.raw.texture_oes_fragment_shader,
                mPreviewTextureId, GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mOes2OesPicture = new Picture(mContext.getResources(), R.raw.texture_vertex_shader, R.raw.texture_oes2oes_fragment_shader,
                mPreviewTextureId, GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        mOESTextureId = GLUtil.getOneTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mGLCopyJni = new GLCopyJni(1080, 1920, mOESTextureId);
        mOesPicture = new Picture(mContext.getResources(), R.raw.texture_vertex_shader, R.raw.texture_oes_fragment_shader,
                mOESTextureId, GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        m2DTextureId = GLUtil.getOneTexture(GLES20.GL_TEXTURE_2D);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 1080, 1920, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        m2DPicture = new Picture(mContext.getResources(), R.raw.texture_vertex_shader, R.raw.texture_2d_fragment_shader,
                m2DTextureId, GLES20.GL_TEXTURE_2D);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged "+width+" "+height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onSurfaceDraw() {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        mPreviewTexture.updateTexImage();
        //直接将预览绘制到屏幕
        //mPreview.draw();

        //将一个YUV数据放到OES纹理，再将纹理绘制到屏幕
        /*byte[] bytes = new byte[1080*1920*3/2];
        Arrays.fill(bytes, (byte)127);
        mGLCopyJni.setBuffer(bytes);
        mOesPicture.draw();*/

        //将预览绘制到一个2D纹理，再将纹理绘制到屏幕
        /*beginRenderTarget(GLES20.GL_TEXTURE_2D, m2DTextureId);
        GLUtil.checkGlError();
        mPreview.draw();
        GLUtil.checkGlError();
        endRenderTarget();
        m2DPicture.draw();*/

        long start = System.currentTimeMillis();
        //将预览绘制到一个OES纹理，将纹理的数据拷贝出来并保存，再将纹理绘制到屏幕
        beginRenderTarget(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        GLUtil.checkGlError();
        mOes2OesPicture.draw();
        GLUtil.checkGlError();
        GLES20.glFinish();
        endRenderTarget();
        yuv = mGLCopyJni.getBuffer();
        long end = System.currentTimeMillis();
        time = time * 0.8f + (end-start) * 0.2f;
        Log.i(TAG, "time "+time);
        save(yuv);
        mOesPicture.draw();
    }
    float time = 0;
    byte[] yuv;

    @Override
    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
        mPreviewTexture.detachFromGLContext();
        GLES20.glDeleteTextures(1, new int[]{mPreviewTextureId}, 0);
        mPreviewTextureId = -1;

        mGLCopyJni.release();
        GLES20.glDeleteTextures(1, new int[]{mOESTextureId}, 0);
        mOESTextureId = -1;

        if(mFrameBuffer != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
        }
    }

    public void beginRenderTarget(int target, int textureId){
        if(mFrameBuffer == null){
            mFrameBuffer = new int[1];
            GLES20.glGenFramebuffers(1, mFrameBuffer, 0);
            GLUtil.checkGlError();
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
        GLUtil.checkGlError();

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                target, textureId, 0);
        GLUtil.checkFramebufferStatus();
    }

    public void endRenderTarget(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLUtil.checkGlError();
    }

    private static void save(byte[] yuv) {
        try {
            String pathName = "/sdcard/hw.yuv";
            FileOutputStream outputStream = new FileOutputStream(pathName);
            outputStream.write(yuv);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
