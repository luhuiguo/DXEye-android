package com.daxun.dxeye;


import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

/**
 * Created by luhuiguo on 13-6-29.
 */
public class SNVRCodecFactory extends DemuxingProtocolCodecFactory {
    public SNVRCodecFactory() {
        super.addMessageEncoder(LoginRequest.class, LoginRequestEncoder.class);
        super.addMessageEncoder(LogoutRequest.class, LogoutRequestEncoder.class);
        super.addMessageEncoder(ChannelRequest.class, ChannelRequestEncoder.class);
        super.addMessageEncoder(PtzRequest.class, PtzRequestEncoder.class);
        super.addMessageEncoder(PreviewRequest.class, PreviewRequestEncoder.class);

        super.addMessageDecoder(LoginResponseDecoder.class);
        super.addMessageDecoder(LogoutResponseDecoder.class);
        super.addMessageDecoder(ChannelResponseDecoder.class);
        super.addMessageDecoder(PtzResponseDecoder.class);
        super.addMessageDecoder(PreviewResponseDecoder.class);
        super.addMessageDecoder(PreviewDataDecoder.class);
    }
}
