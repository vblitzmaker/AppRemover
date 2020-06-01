package com.vblitzmaker.appremover;

import android.graphics.drawable.Drawable;

public class AppItem {

    public String a_name,p_name;
    public Drawable a_icon;

    public AppItem(String a_name, String p_name) {
        this.a_name = a_name;
        this.p_name = p_name;
    }

    public AppItem(String a_name, String p_name,Drawable a_icon) {
        this.a_name = a_name;
        this.p_name = p_name;
        this.a_icon = a_icon;
    }

    public String getA_name() {
        return a_name;
    }

    public void setA_name(String a_name) {
        this.a_name = a_name;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }


    public Drawable getA_icon() {
        return a_icon;
    }

    public void setA_icon(Drawable a_icon) {
        this.a_icon = a_icon;
    }
}
