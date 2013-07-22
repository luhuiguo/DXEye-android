package com.daxun.dxeye;

import android.graphics.Bitmap;

public class Extractor {


    public static native void initAll();
    public static native int extractFrame(Payload payload,Bitmap bitmap);
    public static native void releaseAll();
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("extractor");

    }
}
