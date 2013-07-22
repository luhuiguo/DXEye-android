package com.daxun.dxeye;

/**
 * Created by luhuiguo on 13-7-21.
 */
public enum VideoStatus {
    Success,
    Failure,
    InvalidPacket,
    NotVideoPacket,
    UnsupportedCodec,
    OpenDecoderFailed,
    WaitKeyFrame,
    Frame,
    VideoDecodeFailed,

}
