package com.example.remindmeat.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Reminder implements Parcelable {

    String reminderId;
    String reminderTitle;
    String reminderLocation;
    String reminderDescription;
    String reminderDate;
    Integer reminderRepeat;
    Integer reminderRange;
    Integer reminderStatus;
    Double reminderLat;
    Double reminderLong;

    public Reminder() {
    }

    public Reminder(String reminderId, String reminderTitle, String reminderLocation, String reminderDescription, String reminderDate, Integer reminderRepeat, Integer reminderRange, Integer reminderStatus, Double reminderLat, Double reminderLong) {
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
    }

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
    }

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

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public String getReminderTitle() {
        return reminderTitle;
    }

    public void setReminderTitle(String reminderTitle) {
        this.reminderTitle = reminderTitle;
    }

    public String getReminderLocation() {
        return reminderLocation;
    }

    public void setReminderLocation(String reminderLocation) {
        this.reminderLocation = reminderLocation;
    }

    public String getReminderDescription() {
        return reminderDescription;
    }

    public void setReminderDescription(String reminderDescription) {
        this.reminderDescription = reminderDescription;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public Integer getReminderRepeat() {
        return reminderRepeat;
    }

    public void setReminderRepeat(Integer reminderRepeat) {
        this.reminderRepeat = reminderRepeat;
    }

    public Integer getReminderRange() {
        return reminderRange;
    }

    public void setReminderRange(Integer reminderRange) {
        this.reminderRange = reminderRange;
    }

    public Integer getReminderStatus() {
        return reminderStatus;
    }

    public void setReminderStatus(Integer reminderStatus) {
        this.reminderStatus = reminderStatus;
    }

    public Double getReminderLat() {
        return reminderLat;
    }

    public void setReminderLat(Double reminderLat) {
        this.reminderLat = reminderLat;
    }

    public Double getReminderLong() {
        return reminderLong;
    }

    public void setReminderLong(Double reminderLong) {
        this.reminderLong = reminderLong;
    }


}
