package com.remi.navidrawer.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.remi.navidrawer.Cards;

import com.remi.navidrawer.R;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    MutableLiveData<ArrayList<Cards>> introCardLiveData;
    MutableLiveData<ArrayList<Cards>> guideCardLiveData;
    ArrayList<Cards> introCards;
    ArrayList<Cards> guideCards;

    public HomeViewModel() {
        introCardLiveData = new MutableLiveData<>();
        guideCardLiveData = new MutableLiveData<>();
        initialize();
    }

    public LiveData<ArrayList<Cards>> getIntroCardLiveData() {
        return introCardLiveData;
    }
    public LiveData<ArrayList<Cards>> getGuideCardLiveData() {return guideCardLiveData;}


    public void initialize(){
        populateList();
        introCardLiveData.setValue(introCards);
        guideCardLiveData.setValue(guideCards);
    }
    public void populateList(){
        Cards mCards = new Cards();
        String description = "The PPGI is a contactless measurement system for the functional registration of blood perfusion in the upper skin layers. As an enhancement of the classical photoplethysmography (PPG), it is also based on measuring the optical damping of skin, which varies according to changing blood volumes. But for PPGI instead of a single PPG sensor, a high sensitive CCD camera is used. This enables detecting the skin perfusion with spatial resolution.";
        mCards.setText( description);
        mCards.setType(Cards.cardType.intro);
        mCards.setPic(R.drawable.intro_heartrate);

        Cards mCards2 = new Cards();
        String howTo = "With PPGI also low-frequency blood volume shifts (<0.25 Hz) can be analysed. During such examinations on several subjects’ forehead, local bounded “clouds” of phase synchronous oscillations in blood volume were detected. These continuous regions of same phase appear at frequencies around 0.1 Hz. It could be monitored that these clouds move along the forehead, sometimes vanish or arise new. An example of a video sequence of such measurement can be downloaded here.";
        mCards2.setText( howTo);
        mCards2.setType(Cards.cardType.intro);
        mCards2.setPic(R.drawable.man_on_camera);

        Cards mCards3 = new Cards();
        String placeholder ="OpenCV (Open Source Computer Vision Library) is a library of programming functions mainly aimed at real-time computer vision. By using this library,We are able to analyze the runtime behaviors of current frames under different channels and extract the hidden information behind those visual parts.";
        mCards3.setText(placeholder);
        mCards3.setType(Cards.cardType.intro);
        mCards3.setPic(R.drawable.opencv_logo);

        Cards mCards4 = new Cards();
        String placeholder2 = "Among the RGB Channels,the green channel featuring the strongest plethysmographic signal, corresponding to an absorption peak by (oxy-) hemoglobin, the red and blue channels also contained plethysmographic information. The results show that ambient light photo-plethysmography may be useful for medical purposes such as characterization of vascular skin lesions (e.g., port wine stains) and remote sensing of vital signs (e.g., heart and respiration rates) for triage or sports purposes.";
        mCards4.setText(placeholder2);
        mCards4.setType(Cards.cardType.intro);
        mCards4.setPic(R.drawable.rgb);

        introCards = new ArrayList<>();
        introCards.add(mCards);
        introCards.add(mCards2);
        introCards.add(mCards3);
        introCards.add(mCards4);

        Cards uCards = new Cards();
        String description1 = "The PPGI is a contactless measurement system for the functional registration of blood perfusion in the upper skin layers. As an enhancement of the classical photoplethysmography (PPG), it is also based on measuring the optical damping of skin, which varies according to changing blood volumes. But for PPGI instead of a single PPG sensor, a high sensitive CCD camera is used. This enables detecting the skin perfusion with spatial resolution.";
        uCards.setText( description1);
        uCards.setType(Cards.cardType.guide);
        uCards.setPic(R.drawable.intro_heartrate);

        Cards uCards2 = new Cards();
        String howTo1 = "With PPGI also low-frequency blood volume shifts (<0.25 Hz) can be analysed. During such examinations on several subjects’ forehead, local bounded “clouds” of phase synchronous oscillations in blood volume were detected. These continuous regions of same phase appear at frequencies around 0.1 Hz. It could be monitored that these clouds move along the forehead, sometimes vanish or arise new. An example of a video sequence of such measurement can be downloaded here.";
        uCards2.setText( howTo1);
        uCards2.setType(Cards.cardType.guide);
        uCards2.setPic(R.drawable.man_on_camera);

        Cards uCards3 = new Cards();
        String placeholder1 ="OpenCV (Open Source Computer Vision Library) is a library of programming functions mainly aimed at real-time computer vision. By using this library,We are able to analyze the runtime behaviors of current frames under different channels and extract the hidden information behind those visual parts.";
        uCards3.setText(placeholder1);
        uCards3.setType(Cards.cardType.guide);
        uCards3.setPic(R.drawable.opencv_logo);

        guideCards = new ArrayList<>();
        guideCards.add(uCards);
        guideCards.add(uCards2);
        guideCards.add(uCards3);

    }
}