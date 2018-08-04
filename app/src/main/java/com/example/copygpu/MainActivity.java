package com.example.copygpu;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;

import com.example.copygpu.view.MySurfaceView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private final static String TAG = "GPUActivity";
    private SurfaceTexture mSurfaceTexture = new SurfaceTexture(0);
    private MySurfaceView mSurfaceView;
    private Camera mCamera;
    private int mCameraId = 0;
    private Camera.Parameters mParameters;
    private final static int PERMISSION_REQUEST_CODE = 1234;
    private boolean mIsPause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        initView();
        checkPermiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        mIsPause = false;
        openCamera();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        closeCamera();
        mIsPause = true;
        super.onPause();
    }

    private void initView() {
        mSurfaceView = (MySurfaceView)findViewById(R.id.surfaceView);
        mSurfaceView.addRenderer(new MyGLRenderer(this, mSurfaceTexture));
    }

    private void checkPermiss() {
        ArrayList<String> list = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.CAMERA);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!list.isEmpty()) {
            requestPermissions(list.toArray(new String[list.size()]), PERMISSION_REQUEST_CODE);
        }
    }

    private boolean openCamera() {
        if(mIsPause || mCamera != null) {
            return false;
        }
        try {
            Log.i(TAG, "openCamera E");
            mCamera = Camera.open(mCameraId);
            mCamera.setPreviewTexture(mSurfaceTexture);
            mParameters = mCamera.getParameters();
            mParameters.setPreviewSize(1920, 1080);
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(mParameters);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            Log.i(TAG, "openCamera X");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    private void closeCamera() {
        if(mCamera == null) {
            return;
        }
        mCamera.release();
        mCamera = null;
    }
}
