package com.remi.navidrawer;

import android.graphics.Bitmap;

public class Cards {
    private String mDescription;
    private int mPic;
    private boolean mIsBitmap = false;
    private Bitmap mBitmap;

    public enum cardType {intro, guide,gallery,contact};
    private cardType mType;

    public String getText() {
        return mDescription;
    }

    public int getPic() {
        return mPic;
    }

    public void setText(String description) {
        mDescription = description;
    }

    public void setPic(int pic) {
        mPic = pic;
    }

    public void setPic(Bitmap bitmap) {
        mBitmap = bitmap;
        mIsBitmap = true;
    }

    public boolean getIsBitmap() {
        return mIsBitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public cardType getType() {
        return mType;
    }

    public void setType(cardType type) {
        mType = type;
    }
}
