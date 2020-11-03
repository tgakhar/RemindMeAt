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
 * This java class is the model class for Reminder
 */
public class Reminder implements Parcelable {

    /**
     * String to store reminder id
     */
    String reminderId;

    /**
     * String to store reminder title
     */
    String reminderTitle;

    /**
     * String to store location of the set reminder
     */
    String reminderLocation;

    /**
     * String to store description of reminder
     */
    String reminderDescription;

    /**
     * String to store date selected for each reminder
     */
    String reminderDate;

    /**
     * Integer for repeat reminder
     */
    Integer reminderRepeat;

    /**
     * Integer for range of the reminder
     */
    Integer reminderRange;

    /**
     * Integer for status of reminder
     * active/inactive
     */
    Integer reminderStatus;

    /**
     * Double to store latitude of the reminder location
     */
    Double reminderLat;

    /**
     * Double to store longitude of the reminder location
     */
    Double reminderLong;

    /**
     * Integer for user id
     */
    Integer Uid;


    public Reminder() {
    }

    /**
     * Constructor
     * @param reminderId
     * @param reminderTitle
     * @param reminderLocation
     * @param reminderDescription
     * @param reminderDate
     * @param reminderRepeat
     * @param reminderRange
     * @param reminderStatus
     * @param reminderLat
     * @param reminderLong
     * @param Uid
     */
    public Reminder(String reminderId, String reminderTitle, String reminderLocation, String reminderDescription, String reminderDate, Integer reminderRepeat, Integer reminderRange, Integer reminderStatus, Double reminderLat, Double reminderLong,Integer Uid) {
        this.reminderId = reminderId;
        this.reminderTitle = reminderTitle;
        this.reminderLocation = reminderLocation;
        this.reminderDescription = reminderDescription;
        this.reminderDate = reminderDate;
        this.reminderRepeat = reminderRepeat;
        this.reminderRange = reminderRange;
        this.reminderStatus = reminderStatus;
        this.reminderLat = reminderLat;
        this.reminderLong = reminderLong;
        this.Uid=Uid;
    }

    /**
     * getter method for reminder id
     * @return
     */
    public String getReminderId() {
        return reminderId;
    }

    /**
     * setter method for reminder id
     * @param reminderId
     */
    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    /**
     * getter method for reminder title
     * @return
     */
    public String getReminderTitle() {
        return reminderTitle;
    }

    /**
     * setter method for reminder title
     * @param reminderTitle
     */
    public void setReminderTitle(String reminderTitle) {
        this.reminderTitle = reminderTitle;
    }

    /**
     * getter method for reminder location
     * @return
     */
    public String getReminderLocation() {
        return reminderLocation;
    }

    /**
     * setter method for location of the reminder
     * @param reminderLocation
     */
    public void setReminderLocation(String reminderLocation) {
        this.reminderLocation = reminderLocation;
    }

    /**
     * getter method for reminder description
     * @return
     */
    public String getReminderDescription() {
        return reminderDescription;
    }

    /**
     * setter method for description of reminder
     * @param reminderDescription
     */
    public void setReminderDescription(String reminderDescription) {
        this.reminderDescription = reminderDescription;
    }

    /**
     * getter method for reminder date
     * @return
     */
    public String getReminderDate() {
        return reminderDate;
    }

    /**
     * setter method for reminder's date
     * @param reminderDate
     */
    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    /**
     * getter method for repeat reminder
     * @return
     */
    public Integer getReminderRepeat() {
        return reminderRepeat;
    }

    /**
     * setter method for repeat mode of reminder
     * @param reminderRepeat
     */
    public void setReminderRepeat(Integer reminderRepeat) {
        this.reminderRepeat = reminderRepeat;
    }

    /**
     * getter method for range of the reminder
     * @return
     */
    public Integer getReminderRange() {
        return reminderRange;
    }

    /**
     * setter method for radius range of reminder
     * @param reminderRange
     */
    public void setReminderRange(Integer reminderRange) {
        this.reminderRange = reminderRange;
    }

    /**
     * getter method for reminder status
     * @return
     */
    public Integer getReminderStatus() {
        return reminderStatus;
    }

    /**
     * setter method for status of reminder
     * @param reminderStatus
     */
    public void setReminderStatus(Integer reminderStatus) {
        this.reminderStatus = reminderStatus;
    }

    /**
     * getter method for latitude of the reminder location
     * @return
     */
    public Double getReminderLat() {
        return reminderLat;
    }

    /**
     * setter method for latitude of the reminder location
     * @param reminderLat
     */
    public void setReminderLat(Double reminderLat) {
        this.reminderLat = reminderLat;
    }

    /**
     * getter method for longitude of the reminder location
     * @return
     */
    public Double getReminderLong() {
        return reminderLong;
    }

    /**
     * setter method for longitude of the reminder location
     * @param reminderLong
     */
    public void setReminderLong(Double reminderLong) {
        this.reminderLong = reminderLong;
    }

    /**
     * getter method for user id
     * @return
     */
    public Integer getUid() {
        return Uid;
    }

    /**
     * setter method for user id
     * @param uid
     */
    public void setUid(Integer uid) {
        Uid = uid;
    }

    public static Creator<Reminder> getCREATOR() {
        return CREATOR;
    }

    protected Reminder(Parcel in) {
        reminderId = in.readString();
        reminderTitle = in.readString();
        reminderLocation = in.readString();
        reminderDescription = in.readString();
        reminderDate = in.readString();
        if (in.readByte() == 0) {
            reminderRepeat = null;
        } else {
            reminderRepeat = in.readInt();
        }
        if (in.readByte() == 0) {
            reminderRange = null;
        } else {
            reminderRange = in.readInt();
        }
        if (in.readByte() == 0) {
            reminderStatus = null;
        } else {
            reminderStatus = in.readInt();
        }
        if (in.readByte() == 0) {
            reminderLat = null;
        } else {
            reminderLat = in.readDouble();
        }
        if (in.readByte() == 0) {
            reminderLong = null;
        } else {
            reminderLong = in.readDouble();
        }
        if (in.readByte() == 0) {
            Uid = null;
        } else {
            Uid = in.readInt();
        }
    }


    /**
     *
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reminderId);
        dest.writeString(reminderTitle);
        dest.writeString(reminderLocation);
        dest.writeString(reminderDescription);
        dest.writeString(reminderDate);
        if (reminderRepeat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(reminderRepeat);
        }
        if (reminderRange == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(reminderRange);
        }
        if (reminderStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(reminderStatus);
        }
        if (reminderLat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(reminderLat);
        }
        if (reminderLong == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(reminderLong);
        }
        if (Uid == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Uid);
        }
    }


    /**
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

}
