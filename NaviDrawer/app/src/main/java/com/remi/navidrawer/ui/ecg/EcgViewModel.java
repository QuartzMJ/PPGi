package com.remi.navidrawer.ui.ecg;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EcgViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EcgViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ecg fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}