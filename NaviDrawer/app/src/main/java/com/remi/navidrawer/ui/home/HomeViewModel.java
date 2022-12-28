package com.remi.navidrawer.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.remi.navidrawer.R;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    MutableLiveData<ArrayList<Cards>> cardLiveData;
    ArrayList<Cards> cards;

    public HomeViewModel() {
        cardLiveData = new MutableLiveData<>();
        initialize();
    }

    public LiveData<ArrayList<Cards>> getCardLiveData() {
        return cardLiveData;
    }


    public void initialize(){
        populateList();
        cardLiveData.setValue(cards);
    }

    public void populateList(){
        Cards mCard = new Cards();
        String description = "The PPGI is a contactless measurement system for the functional registration of blood perfusion in the upper skin layers. As an enhancement of the classical photoplethysmography (PPG), it is also based on measuring the optical damping of skin, which varies according to changing blood volumes. But for PPGI instead of a single PPG sensor, a high sensitive CCD camera is used. This enables detecting the skin perfusion with spatial resolution.";
        mCard.setDescription( description);
        mCard.setPic(R.drawable.intro_heartrate);

        Cards mCards2 = new Cards();
        String howTo = "With PPGI also low-frequency blood volume shifts (<0.25 Hz) can be analysed. During such examinations on several subjects’ forehead, local bounded “clouds” of phase synchronous oscillations in blood volume were detected. These continuous regions of same phase appear at frequencies around 0.1 Hz. It could be monitored that these clouds move along the forehead, sometimes vanish or arise new. An example of a video sequence of such measurement can be downloaded here.";
        mCards2.setDescription( howTo);
        mCards2.setPic(R.drawable.man_on_camera);

        cards = new ArrayList<>();
        cards.add(mCard);
        cards.add(mCards2);
    }
}