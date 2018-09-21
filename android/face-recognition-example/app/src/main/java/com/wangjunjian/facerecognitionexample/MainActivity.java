package com.wangjunjian.facerecognitionexample;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.wangjunjian.facerecognition.FaceRecognition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.Parameters params;

    private static final int SRC_FRAME_WIDTH = 1280;
    private static final int SRC_FRAME_HEIGHT = 720;
    private static final int IMAGE_FORMAT = ImageFormat.NV21;

    private String imageFilename;
    private Rect faceRect = new Rect();
    private FaceRecognition faceRecognition = new FaceRecognition();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageFilename = this.getBaseContext().getFilesDir() + "/image.jpg";

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFixedSize(SRC_FRAME_WIDTH, SRC_FRAME_HEIGHT);
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        TextView tv = findViewById(R.id.textView);
        tv.setText(imageFilename);
    }

    @Override
    protected void onDestroy() {
        releaseCamera();

        super.onDestroy();
    }

    private void openCamera(SurfaceHolder holder) {
        releaseCamera();
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        params = camera.getParameters();
        setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, camera);
        params.setPreviewSize(SRC_FRAME_WIDTH, SRC_FRAME_HEIGHT);
        params.setPreviewFormat(IMAGE_FORMAT); // setting preview format：YV12
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        camera.setParameters(params); // setting camera parameters
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        camera.setPreviewCallback(this);
        camera.startPreview();
    }

    private synchronized void releaseCamera() {
        if (camera != null) {
            try {
                camera.setPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                camera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                camera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            camera = null;
        }
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int displayDegree;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayDegree = (info.orientation + degrees) % 360;
            displayDegree = (360 - displayDegree) % 360;  // compensate the mirror
        } else {
            displayDegree = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(displayDegree);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        YuvImage image = new YuvImage(bytes, IMAGE_FORMAT,
                size.width, size.height, null);
        Rect rect = new Rect();
        rect.bottom = size.height;
        rect.top = 0;
        rect.left = 0;
        rect.right = size.width;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compressToJpeg(rect, 90, out);
        try {
            out.writeTo(new FileOutputStream(imageFilename));
            out.close();
            if (new File(imageFilename).exists()) {
                Rect r = new Rect();
                double startTime = System.currentTimeMillis();
                faceRecognition.detect(imageFilename, r);
                double endTime = System.currentTimeMillis();

                if (!r.isEmpty()) {
                    faceRect = r;

                    String str = String.format("%d, %d, %d, %d -- %f",
                            faceRect.left, faceRect.top, faceRect.right, faceRect.bottom,
                            endTime-startTime);
                    TextView tv = (TextView) findViewById(R.id.textView);
                    tv.setText(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera.addCallbackBuffer(bytes);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        openCamera(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

}
