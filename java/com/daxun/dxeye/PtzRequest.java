package com.daxun.dxeye;

import static com.daxun.dxeye.Constants.CMD_PTZ_REQUEST;
import static com.daxun.dxeye.Constants.PTZ_REQUEST_LEN;

/**
 * Created by luhuiguo on 13-7-3.
 */
public class PtzRequest extends SNVRMessage {

    private String token;
    private long device = 0L;
    private short channel;
    private byte command;
    private byte option;

    public PtzRequest() {
        super(PTZ_REQUEST_LEN, CMD_PTZ_REQUEST);
    }

    public PtzRequest(String token, short channel, byte command, byte option) {
        super(PTZ_REQUEST_LEN, CMD_PTZ_REQUEST);
        this.token = token;
        this.device = 0L;
        this.channel = channel;
        this.command = command;
        this.option = option;
    }

    public PtzRequest(String token, long device, short channel, byte command, byte option) {
        super(PTZ_REQUEST_LEN, CMD_PTZ_REQUEST);
        this.token = token;
        this.device = device;
        this.channel = channel;
        this.command = command;
        this.option = option;
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

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public byte getOption() {
        return option;
    }

    public void setOption(byte option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "PtzRequest{" +
                "size=" + size +
                ", type=" + Integer.toHexString(type) +
                ", token='" + token + '\'' +
                ", device=" + device +
                ", channel=" + channel +
                ", command=" + command +
                ", option=" + option +
                '}';
    }
}