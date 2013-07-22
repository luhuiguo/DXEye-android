package com.daxun.dxeye;

/**
 * Created by luhuiguo on 13-6-29.
 */
public abstract class SNVRMessage {

    protected int size;

    protected int type;

    protected int network;

    protected int source;

    protected int target;


    protected SNVRMessage(int type) {
        this.type = type;
    }

    protected SNVRMessage(int size, int type) {
        this.size = size;
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNetwork() {
        return network;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "SNVRMessage{" +
                "size=" + size +
                ", type=" + Integer.toHexString(type) +
                ", network=" + network +
                ", source=" + source +
                ", target=" + target +
                '}';
    }
}
