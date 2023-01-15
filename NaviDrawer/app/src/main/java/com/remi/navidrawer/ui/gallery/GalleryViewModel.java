package com.remi.navidrawer.ui.gallery;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.remi.navidrawer.Cards;

import java.io.File;
import java.util.ArrayList;

public class GalleryViewModel extends ViewModel {

    MutableLiveData<ArrayList<Cards>> galleryCardLiveData;
    ArrayList<Cards> galleryCards = new ArrayList<>();


    public GalleryViewModel() {
        galleryCardLiveData = new MutableLiveData<>();
        initialize();
    }

    public void initialize(){
        populateList();
        galleryCardLiveData.setValue(galleryCards);
        Log.d("initialize finished", "continue");
    }

    public LiveData<ArrayList<Cards>> getGalleryCardLiveData(){
        return galleryCardLiveData;
    }

    public void populateList(){
        String path = Environment.getExternalStoragePublicDirectory("ppgi").getAbsolutePath();
        Log.d("Path test", path);

        File dir = new File(path);
        File[] files = dir.listFiles();
        Log.d("files number", Integer.toString(files.length));

        for (File file : files) {
            if( ifVideo(file.getName()) == true )
            {

                Log.d("ifVideo true:", file.getPath());
                Cards card = new Cards();
                card.setText(file.getName());
                card.setType(Cards.cardType.gallery);
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                card.setPic(bitmap);
                galleryCards.add(card);
            }
        }
    }

    public boolean ifVideo(String filename)
    {
        if (filename.length() <= 3)
            return false;

        int length = filename.length();
        String subString = filename.substring(length - 3, length);


        if(subString.equals("mp4") || subString.equals("3gp") || subString.equals("avi")){
            Log.d("Filename extension", subString);
            return true;
        }
        else
            return false;
    }
}