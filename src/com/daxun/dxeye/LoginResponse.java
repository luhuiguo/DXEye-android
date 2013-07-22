package com.daxun.dxeye;

/**
 * Created by luhuiguo on 13-6-29.
 */

import static com.daxun.dxeye.Constants.CMD_LOGIN_RESPONSE;

public class LoginResponse extends SNVRMessage {

    private int status;

    private String token;

    private String userInfo;

    public LoginResponse() {
        super(CMD_LOGIN_RESPONSE);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "size=" + size +
                ", type=" + Integer.toHexString(type) +
                ", status=" + status +
                ", token='" + token + '\'' +
                ", userInfo='" + userInfo + '\'' +
                '}';
    }
}
