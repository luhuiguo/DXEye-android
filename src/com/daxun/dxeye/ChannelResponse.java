package com.daxun.dxeye;

import java.util.List;

import static com.daxun.dxeye.Constants.CMD_CHANNEL_RESPONSE;

/**
 * Created by luhuiguo on 13-7-3.
 */
public class ChannelResponse extends SNVRMessage {

    private int status;

    private List<Channel> channels;

    public ChannelResponse() {
        super(CMD_CHANNEL_RESPONSE);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "ChannelResponse{" +
                "size=" + size +
                ", type=" + Integer.toHexString(type) +
                ", status=" + status +
                ", channels=" + channels +
                '}';
    }

}
