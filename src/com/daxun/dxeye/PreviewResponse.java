package com.daxun.dxeye;

/**
 * Created by luhuiguo on 13-7-3.
 */

import static com.daxun.dxeye.Constants.CMD_PREVIEW_RESPONSE;

public class PreviewResponse extends SNVRMessage {
    private int status;
    private int brightness;
    private int contrast;
    private int saturation;
    private int hue;


    public PreviewResponse() {
        super(CMD_PREVIEW_RESPONSE);

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getContrast() {
        return contrast;
    }

    public void setContrast(int contrast) {
        this.contrast = contrast;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    @Override
    public String toString() {
        return "PreviewResponse{" +
                "size=" + size +
                ", type=" + Integer.toHexString(type) +
                ", status=" + status +
                ", brightness=" + brightness +
                ", contrast=" + contrast +
                ", saturation=" + saturation +
                ", hue=" + hue +
                '}';
    }
}