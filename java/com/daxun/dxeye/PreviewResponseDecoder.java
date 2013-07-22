package com.daxun.dxeye;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import static com.daxun.dxeye.Constants.CMD_PREVIEW_RESPONSE;

/**
 * Created by luhuiguo on 13-7-7.
 */
public class PreviewResponseDecoder extends SNVRMessageDecoder {

    //private static final Logger LOGGER = LoggerFactory.getLogger(PreviewResponseDecoder.class);

    public PreviewResponseDecoder() {
        super(CMD_PREVIEW_RESPONSE);
    }

    @Override
    protected SNVRMessage decodeBody(IoSession session, IoBuffer in) throws Exception {


        PreviewResponse m = new PreviewResponse();


        m.setStatus(in.getInt());
        m.setBrightness(in.getInt());
        m.setContrast(in.getInt());
        m.setSaturation(in.getInt());
        m.setHue(in.getInt());

        return m;
    }


}