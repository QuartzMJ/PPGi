package com.remi.navidrawer.ui.ecg;

import android.bluetooth.BluetoothClass;

public class DetectedDevice {
    private String deviceName;
    private String deviceAddress;
    private BluetoothClass deviceClass;
    private int deviceType;
    public DetectedDevice(){

    }

    public DetectedDevice(String deviceName, String deviceAddress, BluetoothClass deviceClass,int deviceType) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.deviceClass = deviceClass;
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName){
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress){
        this.deviceAddress = deviceAddress;
    }

    public int getDeviceType(){
        return deviceType;
    }

    public void setDeviceType(int deviceType){
        this.deviceType = deviceType;
    }

    public BluetoothClass getDeviceBluetoothClass(){
      return this.deviceClass;
    }

    public void setDeviceBluetoothClass(BluetoothClass deviceClass){
        this.deviceClass = deviceClass;
    }
}
