package com.example.reminder;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class Reminder implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;


    private String nameOfTheReminder;
    private String dateOfTheReminder;
    private String timeOfTheReminder;
    private ArrayList<String> timeToGetToDestination;
    private long timeToNotifyForTheReminder;
    private String modelAverageOrWorse;
    private String typeOfTransfer;
    private boolean isLocationFeatureActive;


    public Reminder(String nameOfTheReminder, String dateOfTheReminder, String timeOfTheReminder, ArrayList<String> timeToGetToDestination, long timeToNotifyForTheReminder, boolean isLocationFeatureActive, String modelAverageOrWorse, String typeOfTransfer){
        this.nameOfTheReminder = nameOfTheReminder;
        this.dateOfTheReminder = dateOfTheReminder;
        this.timeOfTheReminder = timeOfTheReminder;
        this.timeToGetToDestination= timeToGetToDestination;
        this.timeToNotifyForTheReminder = timeToNotifyForTheReminder;
        this.isLocationFeatureActive = isLocationFeatureActive;
        this.modelAverageOrWorse = modelAverageOrWorse;
        this.typeOfTransfer = typeOfTransfer;
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

    public void setNameOfTheReminder(String nameOfTheReminder){ this.nameOfTheReminder = nameOfTheReminder;}

    public String getNameOfTheReminder() {
        return nameOfTheReminder;
    }

    public String getDateOfTheReminder() {
        return dateOfTheReminder;
    }

    public void setDateOfTheReminder(String dateOfTheReminder){ this.dateOfTheReminder = dateOfTheReminder; }

    public void setTimeOfTheReminder(String timeOfTheReminder){ this.timeOfTheReminder = timeOfTheReminder;}

    public String getTimeOfTheReminder() {
        return timeOfTheReminder;
    }

    public ArrayList<String> getTimeToGetToDestination() {
        return timeToGetToDestination;
    }


    public long getTimeToNotifyForTheReminder() {
        return timeToNotifyForTheReminder;
    }

    public void setTimeToNotifyForTheReminder(long timeToNotifyForTheReminder) {
        this.timeToNotifyForTheReminder = timeToNotifyForTheReminder;
    }

    public boolean isLocationFeatureActive() {
        return isLocationFeatureActive;
    }

    public void setLocationFeatureActive(boolean locationFeatureActive) {
        isLocationFeatureActive = locationFeatureActive;
    }

    public String getModelAverageOrWorse() {
        return modelAverageOrWorse;
    }

    public void setModelAverageOrWorse(String modelAverageOrWorse) {
        this.modelAverageOrWorse = modelAverageOrWorse;
    }

    public int getId(){return id;}

    public void setId(int id){ this.id = id;}


    public String getTypeOfTransfer() {
        return typeOfTransfer;
    }

    public void setTypeOfTransfer(String typeOfTransfer) {
        this.typeOfTransfer = typeOfTransfer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Reminder(Parcel in) {
        id = in.readInt();
        nameOfTheReminder = in.readString();
        dateOfTheReminder = in.readString();
        timeOfTheReminder = in.readString();
        timeToGetToDestination = in.createStringArrayList();
        timeToNotifyForTheReminder = in.readLong();
        isLocationFeatureActive = in.readByte() != 0;
        modelAverageOrWorse = in.readString();
        typeOfTransfer = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nameOfTheReminder);
        dest.writeString(dateOfTheReminder);
        dest.writeString(timeOfTheReminder);
        dest.writeStringList(timeToGetToDestination);
        dest.writeLong(timeToNotifyForTheReminder);
        dest.writeByte((byte) (isLocationFeatureActive ? 1 : 0));
        dest.writeString(modelAverageOrWorse);
        dest.writeString(typeOfTransfer);

    }
}
