package com.remi.navidrawer.ui.contact;

import com.remi.navidrawer.Cards;

public class ContactCards extends Cards {
    private String mText2;
    private String mText3;

    public ContactCards(){
        super();
    }

    public void setText2(String text){
        mText2 = text;
    }

    public String getText2(){
        return mText2;
    }

    public void setText3(String text){
        mText3 = text;
    }

    public String getText3(){
        return mText3;
    }
}
