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
    ArrayList<EcgCards> ecgCards;

    public EcgViewModel() {
        ecgCards = new ArrayList<>();
        ecgCardsLiveData = new MutableLiveData<>();
    }

    public void updateCards(DetectedDevice device) {
        for(EcgCards ecgCards : ecgCards){
            if(ecgCards.getDeviceAddress() == device.getDeviceAddress())
                return;
        }
        EcgCards card = new EcgCards();
        card.setDeviceName(device.getDeviceName());
        card.setDeviceAddress(device.getDeviceAddress());
        BluetoothClass btClass = device.getDeviceBluetoothClass();

        String deviceClassName = "";
        
        switch (btClass.getDeviceClass()){
            case BluetoothClass.Device.COMPUTER_DESKTOP:
                deviceClassName += "Desktop";
                card.setPic(R.drawable.ic_desktop);
                break;
            case BluetoothClass.Device.PHONE_SMART:
                deviceClassName += "Phone";
                card.setPic(R.drawable.ic_smartphone);
                break;
            case BluetoothClass.Device.COMPUTER_LAPTOP:
                deviceClassName += "Laptop";
                card.setPic(R.drawable.ic_desktop);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES:
                deviceClassName += "Headset";
                card.setPic(R.drawable.ic_headset_mic);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET:
                deviceClassName += "Headset";
                card.setPic(R.drawable.ic_headset_mic);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO:
                deviceClassName += "Headset";
                card.setPic(R.drawable.ic_headset_mic);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE:
                deviceClassName += "Headset";
                card.setPic(R.drawable.ic_headset_mic);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO:
                deviceClassName += "Headset";
                card.setPic(R.drawable.ic_headset_mic);
                break;
            case BluetoothClass.Device.PERIPHERAL_KEYBOARD:
                deviceClassName += "Keyboard";
                card.setPic(R.drawable.ic_headset_mic);
                break;
            case BluetoothClass.Device.TOY_CONTROLLER:
                deviceClassName += "Gamepad";
                card.setPic(R.drawable.ic_headset_mic);
                break;
            case BluetoothClass.Device.PERIPHERAL_KEYBOARD_POINTING:
                deviceClassName += "Keyboard";
                card.setPic(R.drawable.ic_headset_mic);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER:
                deviceClassName += "TV";
                card.setPic(R.drawable.ic_tv);
                break;
            case BluetoothClass.Device.HEALTH_BLOOD_PRESSURE:
                deviceClassName += "Vital sign monitor";
                card.setPic(R.drawable.ic_others);
                break;
            case BluetoothClass.Device.HEALTH_DATA_DISPLAY:
                deviceClassName += "Vital sign monitor";
                card.setPic(R.drawable.ic_others);
                break;
            case BluetoothClass.Device.HEALTH_THERMOMETER:
                deviceClassName += "Vital sign monitor";
                card.setPic(R.drawable.ic_others);
                break;
            default:
                deviceClassName += "Others";
                card.setPic(R.drawable.ic_others);
                break;
        }
        
        card.setText(deviceClassName);
        ecgCards.add(card);
        ecgCardsLiveData.setValue(ecgCards);
    }

    public void freeList() {
        ecgCards.clear();
        ecgCardsLiveData.setValue(ecgCards);
    }

    public LiveData<ArrayList<EcgCards>> getLiveEcgCards() {
        return ecgCardsLiveData;
    }

}