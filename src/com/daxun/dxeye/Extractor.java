package com.daxun.dxeye;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;

public class Extractor {

	private int mNativeContext; // accessed by native methods

	public Extractor() {
		super();
		native_setup(new WeakReference<Extractor>(this));
	}

	@Override
	protected void finalize() {
		native_finalize();
	}
	
	protected int extract(Payload payload, Bitmap bitmap) {
		return native_extract(payload,bitmap);
	}

	public static native void native_init();

	public native void native_setup(Object extractor_this);

	public native int native_extract(Payload payload, Bitmap bitmap);

	public native void native_finalize();

	static {
		System.loadLibrary("ffmpeg");
		System.loadLibrary("eye");
		native_init();
	}
}
