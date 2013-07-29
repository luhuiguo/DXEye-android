/*
 * Extractor.h
 *
 *  Created on: 2013-7-28
 *      Author: luhuiguo
 */
#include <jni.h>
#ifdef __cplusplus
extern "C" {
#endif

#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"

#ifdef __cplusplus
}
#endif

#ifndef EXTRACTOR_H_
#define EXTRACTOR_H_

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

#define LOG    "Extractor"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG,__VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG,__VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG,__VA_ARGS__)


typedef struct tagPayload {
	int size;
	int magic1;
	int magic2;
	int length;
	int width;
	int height;
	char streamType;
	char subStreamType;
	char frameType;
	char audioChannelCount;
	char audioBits;
	int timeStamp1;
	int timeStamp2;
	int audioSamples;
	char lineCount;
	char lineWidth;
	char* data;
} Payload;

class Extractor {
public:
	Extractor();
	virtual ~Extractor();

	void setupScaler();
	void convertFrameToRGB();

	int extractFrame(JNIEnv* env, jobject payload,jobject bitmap);


private:
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


};

#endif /* EXTRACTOR_H_ */
