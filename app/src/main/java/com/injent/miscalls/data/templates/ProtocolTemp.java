package com.injent.miscalls.data.templates;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProtocolTemp implements Parcelable {

    public ProtocolTemp() {
    }

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "content")
    private String content;

    protected ProtocolTemp(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        content = in.readString();
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

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(content);
    }
}
