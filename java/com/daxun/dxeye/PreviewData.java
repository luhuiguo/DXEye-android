package com.daxun.dxeye;

import static com.daxun.dxeye.Constants.CMD_PREVIEW_DATA;

/**
 * Created by luhuiguo on 13-7-3.
 */

public class PreviewData extends SNVRMessage {

    private int type;
    private int size;

    private Payload payload;


    public PreviewData() {
        super(CMD_PREVIEW_DATA);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "PreviewData{" +
                "type=" + type +
                ", size=" + size +
                ", payload=" + payload +
                '}';
    }
}
