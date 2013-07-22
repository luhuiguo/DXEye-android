package com.daxun.dxeye;

import java.util.List;

/**
 * Created by luhuiguo on 13-7-7.
 */
public class Payload {

    private int size;
    private int magic1;
    private int magic2;
    private int length;
    private int width;
    private int height;
    private byte streamType;
    private byte subStreamType;
    private byte frameType;
    //    private byte reserved;
//    private byte reserved1[2];
    private byte audioChannelCount;
    private byte audioBits;
    private int timeStamp1;
    private int timeStamp2;
    private int audioSamples;
    //    private int reserved2;
    private byte lineCount;
    private byte lineWidth;
//    private byte reserved3[14];

    private byte[] data;


    private List<Line> lines;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMagic1() {
        return magic1;
    }

    public void setMagic1(int magic1) {
        this.magic1 = magic1;
    }

    public int getMagic2() {
        return magic2;
    }

    public void setMagic2(int magic2) {
        this.magic2 = magic2;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte getStreamType() {
        return streamType;
    }

    public void setStreamType(byte streamType) {
        this.streamType = streamType;
    }

    public byte getSubStreamType() {
        return subStreamType;
    }

    public void setSubStreamType(byte subStreamType) {
        this.subStreamType = subStreamType;
    }

    public byte getFrameType() {
        return frameType;
    }

    public void setFrameType(byte frameType) {
        this.frameType = frameType;
    }

    public byte getAudioChannelCount() {
        return audioChannelCount;
    }

    public void setAudioChannelCount(byte audioChannelCount) {
        this.audioChannelCount = audioChannelCount;
    }

    public byte getAudioBits() {
        return audioBits;
    }

    public void setAudioBits(byte audioBits) {
        this.audioBits = audioBits;
    }

    public int getTimeStamp1() {
        return timeStamp1;
    }

    public void setTimeStamp1(int timeStamp1) {
        this.timeStamp1 = timeStamp1;
    }

    public int getTimeStamp2() {
        return timeStamp2;
    }

    public void setTimeStamp2(int timeStamp2) {
        this.timeStamp2 = timeStamp2;
    }

    public int getAudioSamples() {
        return audioSamples;
    }

    public void setAudioSamples(int audioSamples) {
        this.audioSamples = audioSamples;
    }

    public byte getLineCount() {
        return lineCount;
    }

    public void setLineCount(byte lineCount) {
        this.lineCount = lineCount;
    }

    public byte getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(byte lineWidth) {
        this.lineWidth = lineWidth;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "PreviewData{" +
                "size=" + size +
                ", magic1=" + magic1 +
                ", magic2=" + magic2 +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", streamType=" + streamType +
                ", subStreamType=" + subStreamType +
                ", frameType=" + frameType +
//                ", audioChannelCount=" + audioChannelCount +
//                ", audioBits=" + audioBits +
//                ", timeStamp1=" + timeStamp1 +
//                ", timeStamp2=" + timeStamp2 +
//                ", audioSamples=" + audioSamples +
                ", lineCount=" + lineCount +
                ", lineWidth=" + lineWidth +
//                ", data=" + Arrays.toString(data) +
                ", lines=" + lines +
                '}';
    }


}
