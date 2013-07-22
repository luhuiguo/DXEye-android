package com.daxun.dxeye;


import static com.daxun.dxeye.Constants.CMD_LOGOUT_RESPONSE;

/**
 * Created by luhuiguo on 13-7-3.
 */


public class LogoutResponse extends SNVRMessage {

    public LogoutResponse() {
        super(CMD_LOGOUT_RESPONSE);
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "size=" + size +
                ", type=" + Integer.toHexString(type) +
                '}';
    }

}
