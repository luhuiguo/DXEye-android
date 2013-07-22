package com.daxun.dxeye;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.List;

import static com.daxun.dxeye.Constants.CMD_CHANNEL_RESPONSE;

/**
 * Created by luhuiguo on 13-7-7.
 */
public class ChannelResponseDecoder extends SNVRMessageDecoder {

    //private static final Logger LOGGER = LoggerFactory.getLogger(ChannelResponseDecoder.class);

    public ChannelResponseDecoder() {
        super(CMD_CHANNEL_RESPONSE);
    }

    @Override
    protected SNVRMessage decodeBody(IoSession session, IoBuffer in) throws Exception {

        ChannelResponse m = new ChannelResponse();

        m.setStatus(in.getInt());

        String str = in.getString(in.remaining(), UTF8_DECODER);


        String[] arr = StringUtils.split(str, ";");

        List<Channel> channels = new ArrayList<Channel>(arr.length);

        for (String s : arr) {

            Channel c = new Channel();
            c.setId(NumberUtils.toInt(StringUtils.substringBefore(s, ",")));
            c.setName(StringUtils.substringAfter(s, ","));
            channels.add(c);

        }

        m.setChannels(channels);

        return m;
    }


}