package com.example.remindmeat.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Patel Dhruv
 * @author Gakhar Tanvi
 * @author Kaur Sarbjit
 * @author Kaur Kamaljit
 * @author Varma Akshay
 * @author Dankhara Chintan
 * @author Karthik Modubowna
 * This java class is the model class for Admin
 */
public class Admin implements Parcelable {

    /**
     * String to store email
     */
    String email;

    /**
     * String for user id
     */
    String UId;

    /**
     * Integer for disable user function
     */
    Integer disabled;

    /**
     * Constructor
     * @param email
     * @param UId
     * @param disabled
     */
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

    /**
     * parcelable creator method
     */
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

    /**
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *
     * @param parcel
     * @param i
     */
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

    /**
     * getter method for user email
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * setter method for user's email
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * getter method for user id
     * @return
     */
    public String getUId() {
        return UId;
    }

    /**
     * setter method for user id
     * @param UId
     */
    public void setUId(String UId) {
        this.UId = UId;
    }

    /**
     * getter method for disable functionality
     * @return
     */
    public Integer getDisabled() {
        return disabled;
    }

    /**
     * setter method for disabling functionality
     * @param disabled
     */
    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public static Creator<Admin> getCREATOR() {
        return CREATOR;
    }
}
