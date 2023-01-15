package com.remi.navidrawer.ui.contact;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.remi.navidrawer.R;


import java.util.ArrayList;

public class ContactViewModel extends ViewModel {

    MutableLiveData<ArrayList<ContactCards>> contactCardLiveData;
    ArrayList<ContactCards> contactCards = new ArrayList<>();


    public ContactViewModel() {
        contactCardLiveData = new MutableLiveData<>();
        initialize();
    }

    public void initialize(){
        populateList();
        contactCardLiveData.setValue(contactCards);
        Log.d("initialize finished", "continue");
    }

    public LiveData<ArrayList<ContactCards>> getContactCardsLiveData(){
        return contactCardLiveData;
    }

    public void populateList(){
        ContactCards c1 = new ContactCards();
        c1.setText("Phone");
        c1.setText2("+(0)49-152-58-444-945");
        c1.setText3("(Private)");
        c1.setPic(R.drawable.ic_phone_foreground);

        ContactCards c2 = new ContactCards();
        c2.setText("Phone");
        c2.setText2("+(0)49-241-80-23-210");
        c2.setText3("(Institute)");
        c2.setPic(R.drawable.ic_phone_foreground);

        ContactCards c3 = new ContactCards();
        c3.setText("Mail");
        c3.setText2("damengwu@outlook.com");
        c3.setText3("(Personal)");
        c3.setPic(R.drawable.ic_mail_foreground);

        ContactCards c4 = new ContactCards();
        c4.setText("Mail");
        c4.setText2("medit@hia.rwth-aachen.de");
        c4.setText3("(Institue)");
        c4.setPic(R.drawable.ic_mail_foreground);

        ContactCards c5 = new ContactCards();
        c5.setText("Web");
        c5.setText2("https://game.granbluefantasy.jp/#profile/17630849");
        c5.setPic(R.drawable.ic_website_foreground);

        contactCards.add(c3);
        contactCards.add(c1);
        contactCards.add(c4);
        contactCards.add(c2);
        contactCards.add(c5);




    }
}