package com.example.vcserver.iqapture.bean;

/**
 * Created by VCServer on 2018/3/22.
 */

public class Address {
    private String name;
    private String url;
    private boolean isChecked;      //是否选中

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
