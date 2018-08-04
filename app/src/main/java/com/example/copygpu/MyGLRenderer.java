package com.example.copygpu;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.example.copygpu.gpu.GLUtil;
import com.example.copygpu.view.MySurfaceView;

import java.util.Arrays;

public class MyGLRenderer implements MySurfaceView.Renderer {
    private final static String TAG = "GPURenderer";
    private Context mContext;
    private int mPreviewTextureId = -1;
    private SurfaceTexture mPreviewTexture;
    private GLCopyJni mGLCopyJni;
    private int mBufferTextureId = -1;
    private Picture mPicture;
    private Picture mPicture2;
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
        mPicture = new Picture(mContext.getResources(), R.raw.texture_vertex_shader, R.raw.texture_oes_fragment_shader,
                mPreviewTextureId, GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        mBufferTextureId = GLUtil.getOneTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mGLCopyJni = new GLCopyJni(1080, 1920, mBufferTextureId);

        m2DTextureId = GLUtil.getOneTexture(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m2DTextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 1080, 1920, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        mPicture2 = new Picture(mContext.getResources(), R.raw.texture_vertex_shader, R.raw.texture_fragment_shader,
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

        //mPicture.draw();
        //byte[] bytes = new byte[1080*1920*3/2];
        //Arrays.fill(bytes, (byte)127);
        //mGLCopyJni.setBuffer(bytes);
        //mPicture2.draw();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        beginRenderTarget(GLES20.GL_TEXTURE_2D, m2DTextureId);
        mPicture.draw();
        endRenderTarget();

        mPicture2.draw();
    }

    @Override
    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
        mPreviewTexture.detachFromGLContext();
        GLES20.glDeleteTextures(1, new int[]{mPreviewTextureId}, 0);
        mPreviewTextureId = -1;

        mGLCopyJni.release();
        GLES20.glDeleteTextures(1, new int[]{mBufferTextureId}, 0);
        mBufferTextureId = -1;

        if(mFrameBuffer != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
        }
    }

    public void beginRenderTarget(int target, int textureId){
        if(mFrameBuffer == null){
            mFrameBuffer = new int[1];
            GLES20.glGenFramebuffers(1, mFrameBuffer, 0);
            checkFramebufferStatus();
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
        checkFramebufferStatus();

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                target, textureId, 0);
        checkFramebufferStatus();
    }

    public void endRenderTarget(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLUtil.checkGlError();
    }

    public static void checkFramebufferStatus() {
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            String msg = "";
            switch (status) {
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS";
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
                    break;
                case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
                    msg = "GL_FRAMEBUFFER_UNSUPPORTED";
                    break;
            }
            throw new RuntimeException(msg + " : 0x" + Integer.toHexString(status));
        }
    }

}
