package com.daxun.dxeye;


import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by luhuiguo on 13-6-29.
 */
public class PreviewHandler extends IoHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewHandler.class);

    private String token;

    private short channel;
    private int stream;

    public PreviewHandler(String token, short channel, int stream) {
        this.token = token;
        this.channel = channel;
        this.stream = stream;
    }


    @Override
    public void sessionOpened(IoSession session) {

        session.write(new PreviewRequest(token, channel, stream));

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

        LOGGER.warn("exceptionCaught", cause);
        super.exceptionCaught(session, cause);
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        //LOGGER.debug("messageReceived: {}",message);
        super.messageReceived(session, message);
        if (message instanceof PreviewResponse) {

        } else if (message instanceof PreviewData) {

        }

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        LOGGER.debug("messageSent: {}", message);
        super.messageSent(session, message);
    }
}
