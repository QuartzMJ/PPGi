package com.remi.navidrawer.ui.gallery;

import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.remi.navidrawer.Cards;
import com.remi.navidrawer.R;

import java.io.File;
import java.util.ArrayList;

public class GalleryViewModel extends ViewModel {

    MutableLiveData<ArrayList<Cards>> galleryCardLiveData;
    ArrayList<Cards> galleryCards;


    public GalleryViewModel() {
        galleryCardLiveData = new MutableLiveData<>();
        initialize();
    }

    public void initialize(){
        populateList();
        //galleryCardLiveData.setValue(galleryCards);
    }

    public void populateList(){
        String path = Environment.getExternalStoragePublicDirectory("ppgi").getAbsolutePath();
        Log.d("Path test", path);

        File dir = new File(path);
        File[] files = dir.listFiles();
        Log.d("files number", Integer.toString(files.length));

        for (File file : files) {
            Log.d("Output files:", file.getName());
        }
    }

    public LiveData<ArrayList<Cards>> getGalleryCardLiveData(){
        return galleryCardLiveData;
    }
}