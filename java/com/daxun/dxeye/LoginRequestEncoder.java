package com.daxun.dxeye;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import static com.daxun.dxeye.Constants.PASSWORD_LEN;
import static com.daxun.dxeye.Constants.RESERVED_LEN;
import static com.daxun.dxeye.Constants.USERNAME_LEN;

/**
 * Created by luhuiguo on 13-6-29.
 */
public class LoginRequestEncoder extends SNVRMessageEncoder<LoginRequest> {
    @Override
    protected void encodeBody(IoSession session, LoginRequest message, IoBuffer out) throws Exception {

        out.putString(message.getUsername(), USERNAME_LEN, UTF8_ENCODER);
        out.putString(message.getPassword(), PASSWORD_LEN, UTF8_ENCODER);
        out.putInt(message.getVersion());
        out.putString(message.getReserved(), RESERVED_LEN, UTF8_ENCODER);


    }
}
