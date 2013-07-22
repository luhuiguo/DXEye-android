package com.daxun.dxeye;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import static com.daxun.dxeye.Constants.TOKEN_LEN;

/**
 * Created by luhuiguo on 13-7-7.
 */
public class LogoutRequestEncoder extends SNVRMessageEncoder<LogoutRequest> {
    @Override
    protected void encodeBody(IoSession session, LogoutRequest message, IoBuffer out) throws Exception {
        out.putString(message.getToken(), TOKEN_LEN, UTF8_ENCODER);
    }
}
