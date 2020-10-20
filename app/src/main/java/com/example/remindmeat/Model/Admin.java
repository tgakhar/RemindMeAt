package com.example.remindmeat.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Admin implements Parcelable {
    String email;
    String UId;
    Integer disabled;

    public Admin(String email, String UId, Integer disabled) {
        this.email = email;
        this.UId = UId;
        this.disabled = disabled;
    }


    protected Admin(Parcel in) {
        email = in.readString();
        UId = in.readString();
        if (in.readByte() == 0) {
            disabled = null;
        } else {
            disabled = in.readInt();
        }
    }

    public static final Creator<Admin> CREATOR = new Creator<Admin>() {
        @Override
        public Admin createFromParcel(Parcel in) {
            return new Admin(in);
        }

        @Override
        public Admin[] newArray(int size) {
            return new Admin[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(UId);
        if (disabled == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(disabled);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUId() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public static Creator<Admin> getCREATOR() {
        return CREATOR;
    }
}
