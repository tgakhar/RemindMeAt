package com.example.remindmeat.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Admin implements Parcelable {
    String email;
    String UId;

    public Admin(String email, String UId) {
        this.email = email;
        this.UId = UId;
    }


    protected Admin(Parcel in) {
        email = in.readString();
        UId = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(UId);
    }
}
