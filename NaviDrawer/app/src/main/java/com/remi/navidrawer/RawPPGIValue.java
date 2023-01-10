package com.remi.navidrawer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class RawPPGIValue implements Parcelable {
    private Float mValue;
    private long mCurrentTime;

    public RawPPGIValue(Float value, Calendar current){
        mValue = value;
        mCurrentTime = current.getInstance().getTimeInMillis();
    }
    public RawPPGIValue(Float value, long currentTime)
    {
        this.mValue = value;
        this.mCurrentTime = currentTime;
    }

    protected RawPPGIValue(Parcel in) {
        if (in.readByte() == 0) {
            mValue = null;
        } else {
            mValue = in.readFloat();
        }
        mCurrentTime = in.readLong();
    }

    public static final Creator<RawPPGIValue> CREATOR = new Creator<RawPPGIValue>() {
        @Override
        public RawPPGIValue createFromParcel(Parcel in) {
            return new RawPPGIValue(in);
        }

        @Override
        public RawPPGIValue[] newArray(int size) {
            return new RawPPGIValue[size];
        }
    };

    public void setCurrentTime(long mCurrentTime) {
        this.mCurrentTime = mCurrentTime;
    }

    public void setCurrentTime(Calendar current) {
        this.mCurrentTime = current.getInstance().getTimeInMillis();
    }

    public void setValue(Float value) {
        mValue = value;
    }

    public Float getValue() {
    return mValue;}

    public long getCurrentTime() {
        return mCurrentTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(mValue);
        parcel.writeLong(mCurrentTime);
    }
}
