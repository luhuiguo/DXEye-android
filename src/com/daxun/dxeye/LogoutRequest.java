package com.daxun.dxeye;

import static com.daxun.dxeye.Constants.CMD_LOGOUT_REQUEST;
import static com.daxun.dxeye.Constants.LOGOUT_REQUEST_LEN;

/**
 * Created by luhuiguo on 13-7-3.
 */
public class LogoutRequest extends SNVRMessage {

    private String token;


    public LogoutRequest() {
        super(LOGOUT_REQUEST_LEN, CMD_LOGOUT_REQUEST);
    }

    public LogoutRequest(String token) {
        super(LOGOUT_REQUEST_LEN, CMD_LOGOUT_REQUEST);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LogoutRequest{" +
                "size=" + size +
                ", type=" + Integer.toHexString(type) +
                ", token='" + token + '\'' +
                '}';
    }
}
