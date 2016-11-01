/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.model.data.model;

/**
 *
 */
public class Place {

    private int id = -1;
    private String name = "";
    private String networkKey = "";
    private String cloudSiteID = "";
    private int iconID;
    private long color;
    private int hostControllerID;
    private int settingsID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNetworkKey() {
        return networkKey;
    }

    public void setNetworkKey(String networkKey) {
        this.networkKey = networkKey;
    }

    public String getCloudSiteID() {
        return cloudSiteID;
    }

    public void setCloudSiteID(String cloudSiteID) {
        this.cloudSiteID = cloudSiteID;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public long getColor() {
        return color;
    }

    public void setColor(long color) {
        this.color = color;
    }

    public int getHostControllerID() {
        return hostControllerID;
    }

    public void setHostControllerID(int hostControllerID) {
        this.hostControllerID = hostControllerID;
    }

    public int getSettingsID() {
        return settingsID;
    }

    public void setSettingsID(int settingsID) {
        this.settingsID = settingsID;
    }
}
