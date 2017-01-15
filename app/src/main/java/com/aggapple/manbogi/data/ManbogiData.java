package com.aggapple.manbogi.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ManbogiData implements Parcelable {
    private int id = -1;
    private long date = 0l;
    private long walk = 0l;
    private double distance = 0.0d;
    private boolean runState = false;

    public ManbogiData() {
    }

    public ManbogiData(long date, long walk, double distance) {
        this.date = date;
        this.walk = walk;
        this.distance = distance;
        this.runState = false;
    }

    public ManbogiData(int id, long date, long walk, double distance) {
        this.id = id;
        this.date = date;
        this.walk = walk;
        this.distance = distance;
        this.runState = false;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getWalk() {
        return walk;
    }

    public void setWalk(long walk) {
        this.walk = walk;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isRunning() {
        return runState;
    }

    public void setRunState(boolean isRunning) {
        this.runState = isRunning;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(date);
        dest.writeLong(walk);
        dest.writeDouble(distance);
        dest.writeInt(runState == true ? 0 : 1);
    }

    public ManbogiData readFromParcel(Parcel in) {
        id = in.readInt();
        date = in.readLong();
        walk = in.readLong();
        distance = in.readDouble();
        runState = in.readInt() == 0 ? true : false;
        return this;
    }

    public static final Parcelable.Creator<ManbogiData> CREATOR = new Parcelable.Creator<ManbogiData>() {

        @Override
        public ManbogiData createFromParcel(Parcel in) {
            return new ManbogiData().readFromParcel(in);
        }

        @Override
        public ManbogiData[] newArray(int size) {
            return new ManbogiData[size];
        }
    };
}
