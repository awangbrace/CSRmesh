/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package com.axalent.model.data.model.places;

import com.axalent.model.data.model.Place;

/**
 *
 */
public class PlaceItemEntry implements PlaceItem {

    private final String mPlaceName;
    private final String mOwner;
    private final Place mPlace;
    private final boolean mIsCreateNewButton;


    public PlaceItemEntry(String placeName, boolean isCreateNewButton) {
        this.mPlaceName = placeName;
        this.mOwner = "";
        this.mPlace = null;
        this.mIsCreateNewButton = isCreateNewButton;
    }

    public PlaceItemEntry(Place place) {
        this.mPlace = place;
        this.mOwner = "";
        this.mIsCreateNewButton = false;
        this.mPlaceName = place.getName();
    }

    public PlaceItemEntry(String placeName) {
        this.mPlace = null;
        this.mPlaceName = placeName;
        this.mOwner = "";
        this.mIsCreateNewButton = false;
    }

    public PlaceItemEntry(String placeName, String owner) {
        this.mPlace = null;
        this.mPlaceName = placeName;
        this.mOwner = owner;
        this.mIsCreateNewButton = false;
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public String getOwner() {
        return mOwner;
    }

    public Place getPlace() {
        return mPlace;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    public boolean isCreateNewButton() {
        return mIsCreateNewButton;
    }
}
