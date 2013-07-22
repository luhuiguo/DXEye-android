package com.daxun.dxeye;

/**
 * Created by luhuiguo on 13-6-29.
 */

import static com.daxun.dxeye.Constants.CMD_LOGIN_REQUEST;
import static com.daxun.dxeye.Constants.LOGIN_REQUEST_LEN;

public class LoginRequest extends SNVRMessage {

    private String username;
    private String password;
    private int version = 1;
    private String reserved = "";

    public LoginRequest() {
        super(LOGIN_REQUEST_LEN, CMD_LOGIN_REQUEST);
    }

    public LoginRequest(String username, String password) {
        super(LOGIN_REQUEST_LEN, CMD_LOGIN_REQUEST);
        this.username = username;
        this.password = password;

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "size=" + size +
                ", type=" + Integer.toHexString(type) +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", version=" + version +
                ", reserved='" + reserved + '\'' +
                '}';
    }
}
