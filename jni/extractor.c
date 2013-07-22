#include <string.h>
#include <jni.h>
#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <ffmpeg/libavcodec/avcodec.h>
#include <ffmpeg/libavformat/avformat.h>
#include <ffmpeg/libswscale/swscale.h>

#define JNI_PAYLOAD_CLASS "com/daxun/dxeye/Payload"

#define MAGIC_ID1               0x11AFCAA9
#define MAGIC_ID2               0xEE102FBD

#define STREAM_TYPE_UNDEFINED    0
#define STREAM_TYPE_MJPEG        1
#define STREAM_TYPE_MPEG2        2
#define STREAM_TYPE_MPEG4        3
#define STREAM_TYPE_H264         4

#define STREAM_TYPE_PCM         128
#define STREAM_TYPE_G711_ULAW   129
#define STREAM_TYPE_G711_ALAW   130
#define STREAM_TYPE_G726        131
#define STREAM_TYPE_G722        132
#define STREAM_TYPE_ADPCM       133
#define STREAM_TYPE_MP3         134
#define STREAM_TYPE_G729        135
#define STREAM_TYPE_G721        136
#define STREAM_TYPE_AAC         137

#define SUBSTREAM_TYPE_UNDEFINED        0
#define SUBSTREAM_TYPE_8000HZ8BITS      1
#define SUBSTREAM_TYPE_8000HZ16BITS     2
#define SUBSTREAM_TYPE_16000HZ8BITS     3
#define SUBSTREAM_TYPE_16000HZ16BITS    4

#define FRAME_TYPE_UNDEFINED    0
#define FRAME_TYPE_JPEG         1
#define FRAME_TYPE_I            2
#define FRAME_TYPE_P            3
#define FRAME_TYPE_B            4
#define FRAME_TYPE_A            5
#define FRAME_TYPE_H264_SLICE   0x11
#define FRAME_TYPE_H264_IDR     0x15
#define FRAME_TYPE_H264_SPS     0x17
#define FRAME_TYPE_H264_PPS     0x18

#define LOG    "FFMPEG"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG,__VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG,__VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG,__VA_ARGS__)

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

struct PayloadOffsets {
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
} gPayloadOffsets;

typedef struct tagPayload {
	jint size;
	jint magic1;
	jint magic2;
	jint length;
	jint width;
	jint height;
	jbyte streamType;
	jbyte subStreamType;
	jbyte frameType;
	jbyte audioChannelCount;
	jbyte audioBits;
	jint timeStamp1;
	jint timeStamp2;
	jint audioSamples;
	jbyte lineCount;
	jbyte lineWidth;
	jbyteArray data;
} Payload;

static void nativeClassInit(JNIEnv *env) {
	jclass payloadClass = (*env)->FindClass(env, JNI_PAYLOAD_CLASS);

	gPayloadOffsets.size = (*env)->GetFieldID(env, payloadClass, "size", "I");
	gPayloadOffsets.magic1 = (*env)->GetFieldID(env, payloadClass, "magic1",
			"I");
	gPayloadOffsets.magic2 = (*env)->GetFieldID(env, payloadClass, "magic2",
			"I");
	gPayloadOffsets.length = (*env)->GetFieldID(env, payloadClass, "length",
			"I");
	gPayloadOffsets.width = (*env)->GetFieldID(env, payloadClass, "width", "I");
	gPayloadOffsets.height = (*env)->GetFieldID(env, payloadClass, "height",
			"I");

	gPayloadOffsets.streamType = (*env)->GetFieldID(env, payloadClass,
			"streamType", "B");
	gPayloadOffsets.subStreamType = (*env)->GetFieldID(env, payloadClass,
			"subStreamType", "B");
	gPayloadOffsets.frameType = (*env)->GetFieldID(env, payloadClass,
			"frameType", "B");
	gPayloadOffsets.audioChannelCount = (*env)->GetFieldID(env, payloadClass,
			"audioChannelCount", "B");
	gPayloadOffsets.audioBits = (*env)->GetFieldID(env, payloadClass,
			"audioBits", "B");

	gPayloadOffsets.timeStamp1 = (*env)->GetFieldID(env, payloadClass,
			"timeStamp1", "I");
	gPayloadOffsets.timeStamp2 = (*env)->GetFieldID(env, payloadClass,
			"timeStamp2", "I");
	gPayloadOffsets.audioSamples = (*env)->GetFieldID(env, payloadClass,
			"audioSamples", "I");

	gPayloadOffsets.lineCount = (*env)->GetFieldID(env, payloadClass,
			"lineCount", "B");
	gPayloadOffsets.lineWidth = (*env)->GetFieldID(env, payloadClass,
			"lineWidth", "B");

	gPayloadOffsets.data = (*env)->GetFieldID(env, payloadClass, "data", "[B");

}

static int register_payload(JNIEnv *env) {
	nativeClassInit(env);
	return JNI_OK;
}

static void fill_bitmap(AndroidBitmapInfo* info, void *pixels, AVFrame *pFrame) {
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

AVCodecContext *pCodecCtx;
AVCodec *pCodec;
AVFrame *pFrame;
AVFrame *pFrameRGB;
AVPacket packet;
//AVPicture picture;
struct SwsContext *img_convert_ctx;

int lastWidth;
int lastHeight;
int lastStreamType;
int waitKeyFrame;
int outWidth;
int outHeight;

void Java_com_daxun_dxeye_Extractor_initAll(JNIEnv *env, jobject obj) {

	av_lockmgr_register(&lock_call_back);
	avcodec_register_all();
	pFrame = avcodec_alloc_frame();
}

void Java_com_daxun_dxeye_Extractor_releaseAll(JNIEnv *env, jobject obj) {
	sws_freeContext(img_convert_ctx);
	//avpicture_free(&picture);
	av_free_packet(&packet);
	av_free(pFrame);
	av_free(pFrameRGB);

	if (pCodecCtx) {
		avcodec_close(pCodecCtx);
	}

	av_lockmgr_register(NULL);

}

jint Java_com_daxun_dxeye_Extractor_extractFrame(JNIEnv *env, jobject obj,
		jobject payload, jobject bitmap) {
	AndroidBitmapInfo info;
	void* pixels;
	int ret;

	Payload p;

	p.size = (*env)->GetIntField(env, payload, gPayloadOffsets.size);
	p.magic1 = (*env)->GetIntField(env, payload, gPayloadOffsets.magic1);
	p.magic2 = (*env)->GetIntField(env, payload, gPayloadOffsets.magic2);
	p.length = (*env)->GetIntField(env, payload, gPayloadOffsets.length);
	p.width = (*env)->GetIntField(env, payload, gPayloadOffsets.width);
	p.height = (*env)->GetIntField(env, payload, gPayloadOffsets.height);

	p.streamType = (*env)->GetByteField(env, payload,
			gPayloadOffsets.streamType);
	p.subStreamType = (*env)->GetByteField(env, payload,
			gPayloadOffsets.subStreamType);
	p.frameType = (*env)->GetByteField(env, payload, gPayloadOffsets.frameType);
	p.audioChannelCount = (*env)->GetByteField(env, payload,
			gPayloadOffsets.audioChannelCount);
	p.audioBits = (*env)->GetByteField(env, payload, gPayloadOffsets.audioBits);

	p.timeStamp1 = (*env)->GetIntField(env, payload,
			gPayloadOffsets.timeStamp1);
	p.timeStamp2 = (*env)->GetIntField(env, payload,
			gPayloadOffsets.timeStamp2);
	p.audioSamples = (*env)->GetIntField(env, payload,
			gPayloadOffsets.audioSamples);

	p.lineCount = (*env)->GetByteField(env, payload, gPayloadOffsets.lineCount);
	p.lineWidth = (*env)->GetByteField(env, payload, gPayloadOffsets.lineWidth);

	p.data = (jbyteArray)(*env)->GetObjectField(env, payload,
			gPayloadOffsets.data);

	if (p.magic1 != MAGIC_ID1 || p.magic2 != MAGIC_ID2) {
		LOGD("Invalid packet");
		return -1;
	} else if (!(p.streamType == STREAM_TYPE_MJPEG
			|| p.streamType == STREAM_TYPE_MPEG4
			|| p.streamType == STREAM_TYPE_H264)) {
		LOGD("Not a video packet");
		return -1;
	} else {

		if (!pCodec || lastStreamType != p.streamType
				|| (lastWidth != p.width && p.width != 0)
				|| (lastHeight != p.height && p.height != 0)) {
			waitKeyFrame = 1;

			switch (p.streamType) {
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
			pCodecCtx->width = p.width;
			pCodecCtx->height = p.height;
			pCodecCtx->pix_fmt = PIX_FMT_YUV420P;

			if (ret = avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
				//av_log(NULL, AV_LOG_ERROR, "Cannot open video decoder\n");
				LOGD("Cannot open video decoder");
				return ret;
			}

			if (pCodecCtx->width > 0 && pCodecCtx->height > 0) {
				if (outWidth != pCodecCtx->width
						|| outHeight != pCodecCtx->height) {
					outWidth = pCodecCtx->width;
					outHeight = pCodecCtx->height;

					//avpicture_free(&picture);
					av_free(pFrameRGB);
					sws_freeContext(img_convert_ctx);

					pFrameRGB = avcodec_alloc_frame();
					LOGI("Video size is [%d x %d]", pCodecCtx->width,
							pCodecCtx->height);

					int numBytes = avpicture_get_size(PIX_FMT_RGB24,
							pCodecCtx->width, pCodecCtx->height);
					uint8_t *buffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));

					avpicture_fill((AVPicture *) pFrameRGB, buffer,
							PIX_FMT_RGB24, pCodecCtx->width, pCodecCtx->height);

					// Allocate RGB picture
//					avpicture_alloc(&picture, PIX_FMT_RGB24,
//							outWidth, outHeight);

					static int sws_flags = SWS_FAST_BILINEAR;
					img_convert_ctx = sws_getContext(pCodecCtx->width,
							pCodecCtx->height, pCodecCtx->pix_fmt, outWidth,
							outHeight, PIX_FMT_RGB24, sws_flags, NULL, NULL,
							NULL);

				}

			}

		}

		lastStreamType = p.streamType;
		lastHeight = p.height;
		lastWidth = p.width;

		if (waitKeyFrame) {
			if (!(p.frameType == FRAME_TYPE_UNDEFINED
					|| p.frameType == FRAME_TYPE_JPEG
					|| p.frameType == FRAME_TYPE_I
					|| p.frameType == FRAME_TYPE_H264_IDR
					|| p.frameType == FRAME_TYPE_H264_SPS
					|| p.frameType == FRAME_TYPE_H264_PPS)) {
				return -1;
			}
			waitKeyFrame = 0;
		}

		av_init_packet(&packet);

		packet.size = p.length;
		packet.data = (char*) (*env)->GetByteArrayElements(env, p.data, 0);

		int got_picture = 0;

		if (ret = avcodec_decode_video2(pCodecCtx, pFrame, &got_picture,
				&packet) < 0) {
			return ret;
		}
		if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
			LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
			return ret;
		}

		if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
			LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		}

		sws_scale(img_convert_ctx, (const uint8_t **) pFrame->data,
				pFrame->linesize, 0, pCodecCtx->height, pFrameRGB->data,
				pFrameRGB->linesize);
		fill_bitmap(&info, pixels, pFrameRGB);

		AndroidBitmap_unlockPixels(env, bitmap);

	}

	return 0;

}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = JNI_ERR;

	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return result;
	}

	register_payload(env);

	return JNI_VERSION_1_4;
}
void JNI_OnUnload(JavaVM* vm, void* reserved) {

}
