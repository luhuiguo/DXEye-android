package com.daxun.dxeye;

import static com.daxun.dxeye.Constants.CMD_PREVIEW_REQUEST;
import static com.daxun.dxeye.Constants.PREVIEW_REQUEST_LEN;

/**
 * Created by luhuiguo on 13-7-3.
 */
public class PreviewRequest extends SNVRMessage {

    private String token;
    private long device;
    private short channel;
    private int stream;

    public PreviewRequest() {
        super(PREVIEW_REQUEST_LEN, CMD_PREVIEW_REQUEST);
    }

    public PreviewRequest(String token, short channel, int stream) {
        super(PREVIEW_REQUEST_LEN, CMD_PREVIEW_REQUEST);
        this.token = token;
        this.device = 0L;
        this.channel = channel;
        this.stream = stream;
    }

    public PreviewRequest(String token, long device, short channel, int stream) {
        super(PREVIEW_REQUEST_LEN, CMD_PREVIEW_REQUEST);
        this.token = token;
        this.device = device;
        this.channel = channel;
        this.stream = stream;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getDevice() {
        return device;
    }

    public void setDevice(long device) {
        this.device = device;
    }

    public short getChannel() {
        return channel;
    }

    public void setChannel(short channel) {
        this.channel = channel;
    }

    public int getStream() {
        return stream;
    }

    public void setStream(int stream) {
        this.stream = stream;
    }

    @Override
    public String toString() {
        return "PreviewRequest{" +
                "size=" + size +
                ", type=" + Integer.toHexString(type) +
                ", token='" + token + '\'' +
                ", device=" + device +
                ", channel=" + channel +
                ", stream=" + stream +
                '}';
    }
}
