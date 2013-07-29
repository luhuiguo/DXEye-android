/*
 * Extractor.cpp
 *
 *  Created on: 2013-7-28
 *      Author: luhuiguo
 */

#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>
#include "Extractor.h"

struct fields_t {
	jfieldID context;
	jfieldID size;
	jfieldID magic1;
	jfieldID magic2;
	jfieldID length;
	jfieldID width;
	jfieldID height;
	jfieldID streamType;
	jfieldID subStreamType;
	jfieldID frameType;
	jfieldID audioChannelCount;
	jfieldID audioBits;
	jfieldID timeStamp1;
	jfieldID timeStamp2;
	jfieldID audioSamples;
	jfieldID lineCount;
	jfieldID lineWidth;
	jfieldID data;
};
static fields_t fields;

int lock_call_back(void ** mutex, enum AVLockOp op) {
	switch (op) {
	case AV_LOCK_CREATE:
		*mutex = (pthread_mutex_t *) malloc(sizeof(pthread_mutex_t));
		pthread_mutex_init((pthread_mutex_t *) (*mutex), NULL);
		break;
	case AV_LOCK_OBTAIN:
		pthread_mutex_lock((pthread_mutex_t *) (*mutex));
		break;
	case AV_LOCK_RELEASE:
		pthread_mutex_unlock((pthread_mutex_t *) (*mutex));
		break;
	case AV_LOCK_DESTROY:
		pthread_mutex_destroy((pthread_mutex_t *) (*mutex));
		free(*mutex);
		break;
	}

	return 0;
}

#ifdef __cplusplus
extern "C" {
#endif

int jniThrowException(JNIEnv* env, const char* className, const char* msg) {
	jclass exceptionClass = env->FindClass(className);
	if (exceptionClass == NULL) {
		LOGE("Unable to find exception class %s", className);
		env->DeleteLocalRef(exceptionClass);
		return -1;
	}

	if (env->ThrowNew(exceptionClass, msg) != JNI_OK) {
		LOGE("Failed throwing '%s' '%s'", className, msg);
	}
	env->DeleteLocalRef(exceptionClass);
	return 0;
}

int jniRegisterNativeMethods(JNIEnv* env, const char* className,
		const JNINativeMethod* gMethods, int numMethods) {
	jclass clazz;

	LOGI("Registering %s natives\n", className);
	clazz = env->FindClass(className);
	if (clazz == NULL) {
		LOGE("Native registration unable to find class '%s'\n", className);
		env->DeleteLocalRef(clazz);
		return -1;
	}
	if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
		LOGE("RegisterNatives failed for '%s'\n", className);
		env->DeleteLocalRef(clazz);
		return -1;
	}
	env->DeleteLocalRef(clazz);
	return 0;

}

#ifdef __cplusplus
}
#endif

static void fill_bitmap(AndroidBitmapInfo* info, void *pixels,
		AVFrame *pFrame) {
	uint8_t *frameLine;

	int yy;
	for (yy = 0; yy < info->height; yy++) {
		uint8_t* line = (uint8_t*) pixels;
		frameLine = (uint8_t *) pFrame->data[0] + (yy * pFrame->linesize[0]);

		int xx;
		for (xx = 0; xx < info->width; xx++) {
			int out_offset = xx * 4;
			int in_offset = xx * 3;

			line[out_offset] = frameLine[in_offset];
			line[out_offset + 1] = frameLine[in_offset + 1];
			line[out_offset + 2] = frameLine[in_offset + 2];
			line[out_offset + 3] = 0;
		}
		pixels = (char*) pixels + info->stride;
	}
}

Extractor::Extractor() {
	LOGD("Extractor::Extractor() ");

	avcodec_register_all();
	pFrame = avcodec_alloc_frame();
	pFrameRGB = avcodec_alloc_frame();
	img_convert_ctx = sws_alloc_context();

}

Extractor::~Extractor() {
	LOGD("Extractor::~Extractor()");
	sws_freeContext(img_convert_ctx);
	//avpicture_free(&picture);
	av_free_packet(&packet);
	av_free(pFrame);
	av_free(pFrameRGB);

	if (pCodecCtx) {
		avcodec_close(pCodecCtx);
	}

}

void Extractor::setupScaler() {
	LOGD("setupScaler");
	if (outWidth > 0 && outHeight > 0) {

		av_free(pFrameRGB);
		sws_freeContext(img_convert_ctx);

		pFrameRGB = avcodec_alloc_frame();

		int numBytes = avpicture_get_size(PIX_FMT_RGB24, pCodecCtx->width,
				pCodecCtx->height);

		uint8_t *buffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));

		avpicture_fill((AVPicture *) pFrameRGB, buffer, PIX_FMT_RGB24,
				pCodecCtx->width, pCodecCtx->height);

		// Setup scaler
		static int sws_flags = SWS_FAST_BILINEAR;
		img_convert_ctx = sws_getContext(pCodecCtx->width, pCodecCtx->height,
				pCodecCtx->pix_fmt, outWidth, outHeight, PIX_FMT_RGB24,
				sws_flags, NULL, NULL, NULL);

	}

}

void Extractor::convertFrameToRGB() {
	LOGD("convertFrameToRGB");
	sws_scale(img_convert_ctx, (const uint8_t **) pFrame->data,
			pFrame->linesize, 0, pCodecCtx->height, pFrameRGB->data,
			pFrameRGB->linesize);
}

int Extractor::extractFrame(JNIEnv* env, jobject payload, jobject bitmap) {
	LOGD("Extractor::extractFrame");
	AndroidBitmapInfo info;
	void* pixels;
	int ret;

	jint size = env->GetIntField(payload, fields.size);
	jint magic1 = env->GetIntField(payload, fields.magic1);
	jint magic2 = env->GetIntField(payload, fields.magic2);
	jint length = env->GetIntField(payload, fields.length);
	jint width = env->GetIntField(payload, fields.width);
	jint height = env->GetIntField(payload, fields.height);

	jbyte streamType = env->GetByteField(payload,
			fields.streamType);
	jbyte subStreamType = env->GetByteField(payload,
			fields.subStreamType);
	jbyte frameType = env->GetByteField(payload, fields.frameType);
	jbyte audioChannelCount = env->GetByteField(payload,
			fields.audioChannelCount);
	jbyte audioBits = env->GetByteField(payload, fields.audioBits);

	jint timeStamp1 = env->GetIntField(payload,
			fields.timeStamp1);
	jint timeStamp2 = env->GetIntField(payload,
			fields.timeStamp2);
	jint audioSamples = env->GetIntField(payload,
			fields.audioSamples);

	jbyte lineCount = env->GetByteField(payload, fields.lineCount);
	jbyte lineWidth = env->GetByteField(payload, fields.lineWidth);

	jbyteArray data = (jbyteArray)env->GetObjectField(payload,
			fields.data);


	jbyte *arr = env->GetByteArrayElements(data,0);
	char *buf = (char *)arr;

	env->ReleaseByteArrayElements(data,arr,0);
	env->DeleteLocalRef(data);

	if (magic1 != MAGIC_ID1 || magic2 != MAGIC_ID2) {
		LOGD("Invalid packet");
		return -1;
	} else if (!(streamType == STREAM_TYPE_MJPEG
			|| streamType == STREAM_TYPE_MPEG4
			|| streamType == STREAM_TYPE_H264)) {
		LOGD("Not a video packet");
		return -1;
	} else {

		if (!pCodec || lastStreamType != streamType
				|| (lastWidth != width && width != 0)
				|| (lastHeight != height && height != 0)) {
			waitKeyFrame = 1;

			switch (streamType) {
			case STREAM_TYPE_MJPEG:
				pCodec = avcodec_find_decoder(AV_CODEC_ID_MJPEG);
				break;
			case STREAM_TYPE_MPEG4:
				pCodec = avcodec_find_decoder(AV_CODEC_ID_MPEG4);
				break;
			case STREAM_TYPE_H264:
				pCodec = avcodec_find_decoder(AV_CODEC_ID_H264);
				break;
			default:
				break;
			}
			if (pCodec == NULL) {
				//av_log(NULL, AV_LOG_ERROR, "Unsupported codec!\n");
				LOGD("Unsupported codec");
				return -1;

			}

			pCodecCtx = avcodec_alloc_context3(pCodec);
			pCodecCtx->width = width;
			pCodecCtx->height = height;
			pCodecCtx->pix_fmt = PIX_FMT_YUV420P;

			if ((ret = avcodec_open2(pCodecCtx, pCodec, NULL)) < 0) {
				//av_log(NULL, AV_LOG_ERROR, "Cannot open video decoder\n");
				LOGD("Cannot open video decoder");
				return ret;
			}

			if (pCodecCtx->width > 0 && pCodecCtx->height > 0) {
				if (outWidth != pCodecCtx->width
						|| outHeight != pCodecCtx->height) {
					outWidth = pCodecCtx->width;
					outHeight = pCodecCtx->height;
					setupScaler();
				}

			}

		}

		lastStreamType = streamType;
		lastHeight = height;
		lastWidth = width;

		if (waitKeyFrame) {
			if (!(frameType == FRAME_TYPE_UNDEFINED
					|| frameType == FRAME_TYPE_JPEG
					|| frameType == FRAME_TYPE_I
					|| frameType == FRAME_TYPE_H264_IDR
					|| frameType == FRAME_TYPE_H264_SPS
					|| frameType == FRAME_TYPE_H264_PPS)) {
				return -1;
			}
			waitKeyFrame = 0;
		}
		av_init_packet(&packet);

		packet.size = length;
		packet.data = (uint8_t *)buf;

		int got_picture = 0;

		if ((ret = avcodec_decode_video2(pCodecCtx, pFrame, &got_picture,
				&packet)) < 0) {
			return ret;
		}
		if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
			LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
			return ret;
		}

		if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
			LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		}
		convertFrameToRGB();
		fill_bitmap(&info, pixels, pFrameRGB);
		AndroidBitmap_unlockPixels(env, bitmap);
	}
	return 0;
}

static Extractor* getExtractor(JNIEnv* env, jobject thiz) {
	return (Extractor*) env->GetIntField(thiz, fields.context);
}

static Extractor* setExtractor(JNIEnv* env, jobject thiz,
		Extractor* extractor) {
	Extractor* old = (Extractor*) env->GetIntField(thiz, fields.context);
	if (old != NULL) {
		LOGI("freeing old extractor object");
		free(old);
	}
	env->SetIntField(thiz, fields.context, (int) extractor);
	return old;
}

static void native_init(JNIEnv *env) {
	LOGI("native_init");
	jclass clazz;
	clazz = env->FindClass("com/daxun/dxeye/Extractor");
	if (clazz == NULL) {
		jniThrowException(env, "java/lang/RuntimeException",
				"Can't find android/media/MediaPlayer");
		return;
	}

	fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
	if (fields.context == NULL) {
		jniThrowException(env, "java/lang/RuntimeException",
				"Can't find MediaPlayer.mNativeContext");
		return;
	}
}

static void native_setup(JNIEnv *env, jobject thiz, jobject weak_this) {
	LOGI("native_setup");
	Extractor* extractor = new Extractor();
	if (extractor == NULL) {
		jniThrowException(env, "java/lang/RuntimeException", "Out of memory");
		return;
	}

	// Stow our new C++ Extractor in an opaque field in the Java object.
	setExtractor(env, thiz, extractor);
}

static void native_finalize(JNIEnv *env, jobject thiz) {
	LOGI("native_finalize");
}

jint native_extract(JNIEnv *env, jobject thiz, jobject payload,
		jobject bitmap) {
	LOGI("native_extract");
	Extractor* extractor = getExtractor(env, thiz);
	if (extractor == NULL) {
		jniThrowException(env, "java/lang/IllegalStateException", NULL);
		return -1;
	}
	return extractor->extractFrame(env,payload,bitmap);
}

static void nativeClassInit(JNIEnv *env) {
	jclass clazz = env->FindClass("com/daxun/dxeye/Payload");

	fields.size = env->GetFieldID(clazz, "size", "I");
	fields.magic1 = env->GetFieldID(clazz, "magic1", "I");
	fields.magic2 = env->GetFieldID(clazz, "magic2", "I");
	fields.length = env->GetFieldID(clazz, "length", "I");
	fields.width = env->GetFieldID(clazz, "width", "I");
	fields.height = env->GetFieldID(clazz, "height", "I");

	fields.streamType = env->GetFieldID(clazz, "streamType", "B");
	fields.subStreamType = env->GetFieldID(clazz, "subStreamType", "B");
	fields.frameType = env->GetFieldID(clazz, "frameType", "B");
	fields.audioChannelCount = env->GetFieldID(clazz, "audioChannelCount", "B");
	fields.audioBits = env->GetFieldID(clazz, "audioBits", "B");

	fields.timeStamp1 = env->GetFieldID(clazz, "timeStamp1", "I");
	fields.timeStamp2 = env->GetFieldID(clazz, "timeStamp2", "I");
	fields.audioSamples = env->GetFieldID(clazz, "audioSamples", "I");

	fields.lineCount = env->GetFieldID(clazz, "lineCount", "B");
	fields.lineWidth = env->GetFieldID(clazz, "lineWidth", "B");

	fields.data = env->GetFieldID(clazz, "data", "[B");

	env->DeleteLocalRef(clazz);

}

static JNINativeMethod methods[] = {
		{ "native_init", "()V", (void*) native_init },
		{ "native_setup", "(Ljava/lang/Object;)V", (void*) native_setup },
		{ "native_finalize", "()V", (void *) native_finalize },
		{ "native_extract", "(Lcom/daxun/dxeye/Payload;Landroid/graphics/Bitmap;)I", (void*) native_extract },

};

int register_native_methods(JNIEnv *env) {

	return jniRegisterNativeMethods(env, "com/daxun/dxeye/Extractor", methods,
			sizeof(methods) / sizeof(methods[0]));
}

static JavaVM *sVm;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	LOGI("JNI_OnLoad");
	JNIEnv* env = NULL;
	jint result = JNI_ERR;

	sVm = vm;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK)
		return result;

	nativeClassInit(env);
	if (register_native_methods(env) != JNI_OK)
		return result;

	av_lockmgr_register(&lock_call_back);
	avcodec_register_all();

	return JNI_VERSION_1_4;
}
void JNI_OnUnload(JavaVM* vm, void* reserved) {
	LOGI("JNI_OnUnload");
	av_lockmgr_register(NULL);

}

