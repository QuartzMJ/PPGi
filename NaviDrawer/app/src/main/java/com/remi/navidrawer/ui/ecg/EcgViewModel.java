package com.remi.navidrawer.ui.ecg;

import static androidx.fragment.app.FragmentManager.TAG;

import android.bluetooth.BluetoothClass;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.remi.navidrawer.R;

import java.util.ArrayList;

public class EcgViewModel extends ViewModel {
    MutableLiveData<ArrayList<EcgCards>> ecgCardsLiveData;
    ArrayList<EcgCards> ecgCards = new ArrayList<>();

    public EcgViewModel() {
        ecgCardsLiveData = new MutableLiveData<>();
    }

    public void updateCards(ArrayList<DetectedDevice> devices) {

        for (DetectedDevice device : devices) {
            EcgCards card = new EcgCards();
            card.setDeviceName(device.getDeviceName());
            card.setDeviceAddress(device.getDeviceAddress());
            BluetoothClass btClass = device.getDeviceBluetoothClass();

            if (btClass.getDeviceClass() == BluetoothClass.Device.COMPUTER_DESKTOP || btClass.getDeviceClass() == BluetoothClass.Device.COMPUTER_LAPTOP) {
                card.setText("PC&Tablet");
                card.setPic(R.drawable.ic_desktop);
            } else if (btClass.getDeviceClass() == BluetoothClass.Device.PHONE_SMART) {
                card.setText("Phone");
                card.setPic(R.drawable.ic_smartphone);
            } else if (btClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES || btClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
                card.setText("Headset");
                card.setPic(R.drawable.ic_headset_mic);
            } else {
                card.setText("Others");
                card.setPic(R.drawable.ic_others);
            }
            ecgCards.add(card);
        }
        ecgCardsLiveData.setValue(ecgCards);
    }

    public LiveData<ArrayList<EcgCards>> getLiveEcgCards() {
        return ecgCardsLiveData;
    }

}