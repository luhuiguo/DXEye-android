package com.daxun.dxeye;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * Created by luhuiguo on 13-6-29.
 */
public class LoginResponseDecoder extends SNVRMessageDecoder {

    //private static final Logger LOGGER = LoggerFactory.getLogger(LoginResponseDecoder.class);

    public LoginResponseDecoder() {
        super(Constants.CMD_LOGIN_RESPONSE);
    }

    @Override
    protected SNVRMessage decodeBody(IoSession session, IoBuffer in) throws Exception {


        LoginResponse m = new LoginResponse();


        m.setStatus(in.getInt());
        m.setToken(in.getString(Constants.TOKEN_LEN, UTF8_DECODER));
        m.setUserInfo(in.getString(in.remaining(), UTF8_DECODER));

        return m;
    }


}
