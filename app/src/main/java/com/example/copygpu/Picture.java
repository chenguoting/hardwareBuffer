package com.example.copygpu;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.copygpu.gpu.GLUtil;

import java.nio.FloatBuffer;

/*
 * Author : chenguoting on 2018-7-10 10:24
 * Email : chen.guoting@nubia.com
 * Company : NUBIA TECHNOLOGY CO., LTD.
 */
public class Picture {
    private final int mProgram;
    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int mMVPMatrixHandle;
    private int mSTMatrixHandle;
    private int mTextureHandle;

    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];
    private final int vertexCount = rectCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordBuffer;
    private int mTextureID;
    private int mTextureTarget;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    static final float rectCoords[] = {
            -1, 1, 0,  //左上
            1, 1, 0,  //右上
            -1, -1, 0,  //左下
            1, -1, 0  //右下
    };

    static final float textureCoords[] = {
            0, 0,
            1, 0,
            0, 1,
            1, 1
    };

    public Picture(Resources res, int vertexId, int fragmentId, int textureId, int target) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.setIdentityM(mSTMatrix, 0);

        vertexBuffer = GLUtil.arrayToBuffer(rectCoords);
        textureCoordBuffer = GLUtil.arrayToBuffer(textureCoords);

        mTextureID = textureId;
        mTextureTarget = target;

        int vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER,
                GLUtil.readRawFile(res, vertexId));
        int fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER,
                GLUtil.readRawFile(res, fragmentId));
        mProgram = GLUtil.createProgram(vertexShader, fragmentShader);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        mTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTextureSampler");

    }


    public void draw() {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // Enable a handle to the rect vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Prepare the rect coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2,
                GLES20.GL_FLOAT, false,
                2*4, textureCoordBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(mTextureTarget, mTextureID);
        GLES20.glUniform1i(mTextureHandle, 3);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mSTMatrixHandle, 1, false, mSTMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
