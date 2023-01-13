package com.remi.navidrawer;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class RawPPGIValue implements Parcelable {
    private Float mValue;
    private long mCurrentTime;
    private boolean validity = true;
    private boolean tested = false;

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

    public String printRawValue(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSSZ", Locale.GERMAN);
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
        calendar.setTimeInMillis(mCurrentTime);
        String tmp = sdf.format(calendar.getTime());
        String date = tmp.substring(11,23);

        String msg;
        msg ="Time: " + date + " Value: " + Float.toString(mValue) + "\n";
        return msg;
    }

    public void setValidity(boolean value) {
        this.validity = value;
    }

    public boolean getValidity() {
        return validity;
    }

    public void setTested(){
        tested = true;
    }

    public boolean ifTested() {
        return tested;
    }
}
