package com.injent.miscalls.data.templates;

import android.os.Parcel;
import android.os.Parcelable;

public class ProtocolTemp implements Parcelable {

    private String name;

    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    protected ProtocolTemp(Parcel in) {
    }

    public static final Creator<ProtocolTemp> CREATOR = new Creator<ProtocolTemp>() {
        @Override
        public ProtocolTemp createFromParcel(Parcel in) {
            return new ProtocolTemp(in);
        }

        @Override
        public ProtocolTemp[] newArray(int size) {
            return new ProtocolTemp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
