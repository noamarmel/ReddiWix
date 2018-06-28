package com.project.interviews.reddiwix.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListingData {
    //region Data Members
    @SerializedName("children")
    private ArrayList<T3post> mPosts;

    @SerializedName("dist")
    private Integer mDist;

    @SerializedName("after")
    private String mNextListing;

    @SerializedName("befor")
    private String mPrevListing;
    //endregion

    //region Properties
    public ArrayList<T3post> getPosts() {
        return mPosts;
    }

    public void setPosts(ArrayList<T3post> mPosts) {
        this.mPosts = mPosts;
    }

    public Integer getDist() {
        return mDist;
    }

    public void setDist(Integer mDist) {
        this.mDist = mDist;
    }

    public String getNextListing() {
        return mNextListing;
    }

    public void setNextListing(String mNextListing) {
        this.mNextListing = mNextListing;
    }

    public String getPrevListing() {
        return mPrevListing;
    }

    public void setPrevListing(String mPrevListing) {
        this.mPrevListing = mPrevListing;
    }
    //endregion
}
