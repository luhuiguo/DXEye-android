package com.daxun.dxeye;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import static com.daxun.dxeye.Constants.HEADER_LEN;

/**
 * Created by luhuiguo on 13-6-29.
 */
public abstract class SNVRMessageEncoder<T extends SNVRMessage> implements MessageEncoder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SNVRMessageEncoder.class);

    public static final CharsetEncoder UTF8_ENCODER = Charset.forName("UTF-8").newEncoder();


    @Override
    public void encode(IoSession session, T message, ProtocolEncoderOutput out) throws Exception {


        IoBuffer buf = IoBuffer.allocate(HEADER_LEN);


        buf.setAutoExpand(true);

        buf.putInt(message.getSize());
        buf.putInt(message.getType());
        buf.putInt(message.getNetwork());
        buf.putInt(message.getSource());
        buf.putInt(message.getTarget());

        encodeBody(session, message, buf);
        buf.flip();
        LOGGER.debug("buf: {}", buf.getHexDump());
        out.write(buf);
    }

    protected abstract void encodeBody(IoSession session, T message, IoBuffer out) throws Exception;


}
