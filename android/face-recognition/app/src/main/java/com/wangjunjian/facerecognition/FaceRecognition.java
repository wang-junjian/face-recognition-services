package com.wangjunjian.facerecognition;

import android.graphics.Rect;

public class FaceRecognition {
    static {
        System.loadLibrary("face-recognition");
    }

    public native void detect(String filename, Rect rect);

}
