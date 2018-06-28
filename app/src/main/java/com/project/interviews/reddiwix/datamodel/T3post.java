package com.project.interviews.reddiwix.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class T3post implements Parcelable {
    //region Public Const's
    public static final String POST_ITEM = "t3post.item";
    //endregion

    //region Data Members
    @SerializedName("title")
    private String mTitle;

    @SerializedName("thumbnail")
    private String mThumbnail;

    @SerializedName("permalink")
    private String mPermalink;

    @SerializedName("id")
    private String mId;
    //endregion

    //region Parcelable Related
    public T3post(Parcel in) {
        mTitle = in.readString();
        mThumbnail = in.readString();
        mPermalink = in.readString();
        mId = in.readString();
    }

    public static final Parcelable.Creator<T3post> CREATOR
            = new Parcelable.Creator<T3post>() {
        public T3post createFromParcel(Parcel in) {
            return new T3post(in);
        }

        public T3post[] newArray(int size) {
            return new T3post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mThumbnail);
        parcel.writeString(mPermalink);
        parcel.writeString(mId);
    }
    //endregion

    //region Properties
    public String getTitle() {
        return mTitle;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public String getPermalink() {
        return mPermalink;
    }

    public String getId() {
        return mId;
    }
    //endregion
}
