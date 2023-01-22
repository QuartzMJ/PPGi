package com.remi.navidrawer.ui.gallery;


import android.os.Environment;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.remi.navidrawer.Cards;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GalleryViewModel extends ViewModel {

    MutableLiveData<ArrayList<Cards>> galleryCardLiveData;
    ArrayList<Cards> galleryCards = new ArrayList<>();


    public GalleryViewModel() throws IOException {
        galleryCardLiveData = new MutableLiveData<>();
        initialize();
    }

    public void initialize() throws IOException {
        populateList();
        galleryCardLiveData.setValue(galleryCards);
    }

    public LiveData<ArrayList<Cards>> getGalleryCardLiveData() {
        return galleryCardLiveData;
    }

    public void populateList() throws IOException {
        String path = Environment.getExternalStoragePublicDirectory("ppgi").getAbsolutePath();

        File dir = new File(path);
        File[] files = dir.listFiles();

        try{
        for (File file : files) {
            if (ifVideo(file.getName()) == true) {
                Cards card = new Cards();
                card.setText(file.getName());
                card.setType(Cards.cardType.gallery);
                card.setFile(file);
                galleryCards.add(card);
            }
        }
        }
        catch (Exception e){
            if(e instanceof java.lang.NullPointerException){
               Log.d("Files Error","No files found");
            }
        }
    }

    public boolean ifVideo(String filename) {
        if (filename.length() <= 3)
            return false;

        int length = filename.length();
        String subString = filename.substring(length - 3, length);
        if (subString.equals("mp4") || subString.equals("3gp") || subString.equals("avi")) {
            return true;
        } else
            return false;
    }

}