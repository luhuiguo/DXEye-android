package com.daxun.dxeye;

/**
 * Created by luhuiguo on 13-7-14.
 */
public class Constants {

    public static final String CHANNEL_KEY = "Channel";

    public static final int CHANNEL_REQUEST = 1;

    public static final int HEADER_LEN = 20;

    public static final int USERNAME_LEN = 20;
    public static final int PASSWORD_LEN = 40;
    public static final int VERSION_LEN = 4;
    public static final int RESERVED_LEN = 24;
    public static final int TOKEN_LEN = 128;
    public static final int CHANNELS_LEN = 4096;

    public static final int STREAM_LEN = 4;

    public static final int DEVICE_LEN = 8;
    public static final int CHANNEL_LEN = 2;
    public static final int COMMAND_LEN = 1;
    public static final int OPTION_LEN = 1;

    public static final int LOGIN_REQUEST_LEN = HEADER_LEN + USERNAME_LEN + PASSWORD_LEN + VERSION_LEN + RESERVED_LEN;
    public static final int LOGOUT_REQUEST_LEN = HEADER_LEN + TOKEN_LEN;
    public static final int CHANNEL_REQUEST_LEN = HEADER_LEN + TOKEN_LEN;
    public static final int PTZ_REQUEST_LEN = HEADER_LEN + TOKEN_LEN + DEVICE_LEN + CHANNEL_LEN + COMMAND_LEN + OPTION_LEN;
    public static final int PREVIEW_REQUEST_LEN = HEADER_LEN + TOKEN_LEN + DEVICE_LEN + CHANNEL_LEN + STREAM_LEN;



    public static final int CMD_LOGIN_REQUEST = 0x00010001;
    public static final int CMD_LOGIN_RESPONSE = 0x80010001;
    public static final int CMD_LOGOUT_REQUEST = 0x00010003;
    public static final int CMD_LOGOUT_RESPONSE = 0x80010003;
    public static final int CMD_CHANNEL_REQUEST = 0x00010004;
    public static final int CMD_CHANNEL_RESPONSE = 0x80010004;
    public static final int CMD_PREVIEW_REQUEST = 0x00050101;
    public static final int CMD_PREVIEW_RESPONSE = 0x80050101;
    public static final int CMD_PREVIEW_DATA = 0x80050102;
    public static final int CMD_PTZ_REQUEST = 0x00090002;
    public static final int CMD_PTZ_RESPONSE = 0x80090001;

    public static final int MAGIC_ID1 = 0x11AFCAA9;
    public static final int MAGIC_ID2 = 0xEE102FBD;

    public static final int STREAM_TYPE_UNDEFINED = 0;
    public static final int STREAM_TYPE_MJPEG = 1;
    public static final int STREAM_TYPE_MPEG2 = 2;
    public static final int STREAM_TYPE_MPEG4 = 3;
    public static final int STREAM_TYPE_H264 = 4;

    public static final int STREAM_TYPE_PCM = 128;
    public static final int STREAM_TYPE_G711_ULAW = 129;
    public static final int STREAM_TYPE_G711_ALAW = 130;
    public static final int STREAM_TYPE_G726 = 131;
    public static final int STREAM_TYPE_G722 = 132;
    public static final int STREAM_TYPE_ADPCM = 133;
    public static final int STREAM_TYPE_MP3 = 134;
    public static final int STREAM_TYPE_G729 = 135;
    public static final int STREAM_TYPE_G721 = 136;
    public static final int STREAM_TYPE_AAC = 137;

    public static final int SUBSTREAM_TYPE_UNDEFINED = 0;
    public static final int SUBSTREAM_TYPE_8000HZ8BITS = 1;
    public static final int SUBSTREAM_TYPE_8000HZ16BITS = 2;
    public static final int SUBSTREAM_TYPE_16000HZ8BITS = 3;
    public static final int SUBSTREAM_TYPE_16000HZ16BITS = 4;

    public static final int FRAME_TYPE_UNDEFINED = 0;
    public static final int FRAME_TYPE_JPEG = 1;
    public static final int FRAME_TYPE_I = 2;
    public static final int FRAME_TYPE_P = 3;
    public static final int FRAME_TYPE_B = 4;
    public static final int FRAME_TYPE_A = 5;
    public static final int FRAME_TYPE_H264_SLICE = 0x11;
    public static final int FRAME_TYPE_H264_IDR = 0x15;
    public static final int FRAME_TYPE_H264_SPS = 0x17;
    public static final int FRAME_TYPE_H264_PPS = 0x18;


}
