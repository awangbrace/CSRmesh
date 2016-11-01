/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.model.data.model.devices;

/**
 *
 */
public class ScanCSRDevice extends CSRDevice implements Comparable<ScanCSRDevice> {

    private static final long TIME_SCANINFO_VALID = 5 * 1000; // 5 secs

    public String uuid;
    public int rssi;
    public int uuidHash;
    public long timeStamp;
    public int ttl;
    public AppearanceDevice appearance;

    // Constructor
    public ScanCSRDevice(String uuid, int rssi, int uuidHash, int ttl) {
        this.uuid = uuid;
        this.rssi = rssi;
        this.uuidHash = uuidHash;
        this.ttl = ttl;
        updated();
    }

    public void updated() {
        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        if (appearance != null) {
            return appearance.getShortName();
        }
        return null;
    }

    @Override
    public int getType() {
        return TYPE_UNKNOWN;
    }

    @Override
    public boolean isFavourite() {
        return false;
    }

    @Override
    public void setFavourite(boolean favourite) { // Empty
    }

    public int getUuidHash() {
        return this.uuidHash;
    }

    /**
     * This method check if the timeStamp of the last update is still valid or not (time<TIME_SCANINFO_VALID).
     *
     * @return true if the info is still valid
     */
    public boolean isInfoValid() {
        return ((System.currentTimeMillis() - this.timeStamp) < TIME_SCANINFO_VALID);
    }

    @Override
    public int compareTo(ScanCSRDevice info) {
        final int LESS_THAN = -1;
        final int GREATER_THAN = 1;
        final int EQUAL = 0;

        // Compare to is used for sorting the list in ascending order.
        // Smaller number of hops (highest TTL) should be at the top of the list.
        // For items with the same TTL, largest signal strength (highest RSSI) should be at the top of the list.
        if (this.ttl > info.ttl) {
            return LESS_THAN;
        }
        else if (this.ttl < info.ttl) {
            return GREATER_THAN;
        }
        else if (this.rssi > info.rssi) {
            return LESS_THAN;
        }
        else if (this.rssi < info.rssi) {
            return GREATER_THAN;
        }
        else {
            return EQUAL;
        }
    }


    public void setAppearance(AppearanceDevice scanAppearance) {
        appearance = scanAppearance;
    }

    public boolean hasAppearance() {
        return appearance != null;
    }

}
