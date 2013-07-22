package com.daxun.dxeye;

import java.io.Serializable;

/**
 * Created by luhuiguo on 13-7-3.
 */
public class Channel implements Serializable {


    private int id;

    private String name;

    public Channel() {
    }

    public Channel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }


}
