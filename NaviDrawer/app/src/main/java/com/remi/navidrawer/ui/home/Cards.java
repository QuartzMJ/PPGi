package com.remi.navidrawer.ui.home;

public class Cards {
    private String mDescription;
    private int mPic;

    public enum cardType {intro, guide};
    private cardType mType;

    public String getDescription() {
        return mDescription;
    }

    public int getPic() {
        return mPic;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setPic(int pic) {
        mPic = pic;
    }

    public cardType getType() {
        return mType;
    }

    public void setType(cardType type) {
        mType = type;
    }
}
