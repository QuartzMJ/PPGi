package com.remi.navidrawer.ui.ecg;

import com.remi.navidrawer.Cards;

public class EcgCards extends Cards {
    private String mDeviceName;
    private String mDeviceAddress;

    public EcgCards() {
        super();
    }

    public String getDeviceName() {
        return  mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        this.mDeviceName = deviceName;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.mDeviceAddress = deviceAddress;
    }
}
