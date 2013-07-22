package com.daxun.dxeye;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import static com.daxun.dxeye.Constants.TOKEN_LEN;

/**
 * Created by luhuiguo on 13-7-7.
 */
public class PtzRequestEncoder extends SNVRMessageEncoder<PtzRequest> {
    @Override
    protected void encodeBody(IoSession session, PtzRequest message, IoBuffer out) throws Exception {
        out.putString(message.getToken(), TOKEN_LEN, UTF8_ENCODER);
        out.putLong(message.getDevice());
        out.putShort(message.getChannel());
        out.put(message.getCommand());
        out.put(message.getOption());
    }
}