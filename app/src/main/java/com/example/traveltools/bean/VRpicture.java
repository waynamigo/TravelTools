package com.example.traveltools.bean;

import java.io.Serializable;

public class VRpicture implements Serializable {
    int drawableid;
    String placename;

    public int getDrawableid() {
        return drawableid;
    }

    public void setDrawableid(int drawableid) {
        this.drawableid = drawableid;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }
}
