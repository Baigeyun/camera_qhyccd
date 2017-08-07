package com.starrysky.dto;

import java.util.List;


public class SettingMenuItem {
    private String name;
    private String currentSettingValue;
    private List<SettingMenuDetailItem> subMenus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentSettingValue() {
        return currentSettingValue;
    }

    public void setCurrentSettingValue(String currentSettingValue) {
        this.currentSettingValue = currentSettingValue;
    }

    public List<SettingMenuDetailItem> getSubMenus() {
        return subMenus;
    }

    public void setSubMenus(List<SettingMenuDetailItem> subMenus) {
        this.subMenus = subMenus;
    }
}
