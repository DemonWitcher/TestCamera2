package com.witcher.testcamera2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_PERMISSIONS = 888;

    private Button mBtOpen;
    private Button mBtClose;
    private Button mBtSwitch;
    private SurfaceView mSvPreview;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtOpen = findViewById(R.id.bt_open);
        mBtClose = findViewById(R.id.bt_close);
        mBtSwitch = findViewById(R.id.bt_switch);
        mSvPreview = findViewById(R.id.sv_preview);

        mBtOpen.setOnClickListener(this);
        mBtClose.setOnClickListener(this);
        mBtSwitch.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_open: {
                openCamera();
            }
            break;
            case R.id.bt_close: {
                closeCamera();
            }
            break;
            case R.id.bt_switch: {
                switchCamera();
            }
            break;
        }

    }

    private void switchCamera() {
        L.t(this,"todo");
    }

    private void open() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                CameraManager cameraMgr = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                if (cameraMgr != null) {
                    cameraMgr.openCamera("" + CameraCharacteristics.LENS_FACING_BACK, stateCallback, null);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            //camera1
        }
    }

    private void startPerview() {
        if (mCameraDevice == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                mCameraDevice.createCaptureSession(Arrays.asList(mSvPreview.getHolder().getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mCameraCaptureSession = session;
                        sendRepeatPreviewRequest();
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendRepeatPreviewRequest() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                CaptureRequest.Builder builder;
                builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(mSvPreview.getHolder().getSurface());
                builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//                builder.setTag(RequestTag.Preview);
//                addBaselineCaptureKeysToRequest(builder);
                mCameraCaptureSession.setRepeatingRequest(builder.build(),
                        new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                super.onCaptureStarted(session, request, timestamp, frameNumber);
                            }
                        },
                        null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }


    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            L.i("onOpened");
            mCameraDevice = camera;
            startPerview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            L.i("onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            L.i("onError");
        }
    };

    private void closeCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(mCameraDevice!=null){
                mCameraDevice.close();
            }
        }
    }

    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,}, REQUEST_PERMISSIONS);
        } else {
            open();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                open();
            } else {
                Toast.makeText(this, "没权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
