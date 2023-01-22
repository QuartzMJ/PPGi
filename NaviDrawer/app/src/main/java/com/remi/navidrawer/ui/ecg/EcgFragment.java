package com.remi.navidrawer.ui.ecg;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.remi.navidrawer.databinding.FragmentEcgBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EcgFragment extends Fragment {

    private FragmentEcgBinding binding;
    private IntentFilter mIntentFilter;
    private int REQUEST_ENABLE_BT = 1;
    private int BLUETOOTH_ENABLE_SUCCESS = -1;
    private EcgViewModel ecgViewModel;
    private BluetoothAdapter mBluetoothAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1
                );
                String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(getContext(), action, Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(getContext(), action, Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Toast.makeText(getContext(), "BluetoothAdapter.STATE_TURNING_ON", Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Toast.makeText(getContext(), "BluetoothAdapter.STATE_TURNING_OFF", Toast.LENGTH_LONG).show();
                            break;
                        default:
                    }
                } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device != null) {
                        Log.d("Bluetooth device found", device.getName().toString());
                    }else {
                        Toast.makeText(getContext(), "Device is null pointer", Toast.LENGTH_LONG).show();
                    }
                }
            }else{
                String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(getContext(), action, Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(getContext(), action, Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Toast.makeText(getContext(), "BluetoothAdapter.STATE_TURNING_ON", Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Toast.makeText(getContext(), "BluetoothAdapter.STATE_TURNING_OFF", Toast.LENGTH_LONG).show();
                            break;
                        default:
                    }
                } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("Magical Bluetooth","I am here ");
                    if(device != null) {
                        Log.d("Bluetooth device found",device.getAddress().toString());
                    }else {
                        Toast.makeText(getContext(), "Device is null pointer", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ecgViewModel =
                new ViewModelProvider(this).get(EcgViewModel.class);
        binding = FragmentEcgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textEcg;
        ecgViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initBluetooth();
        scanBluetoothDevice();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }

    public void initBluetooth() {
        if (mBluetoothAdapter == null) {
            ecgViewModel.setText("There is no Bluetooth adapter on your device");
        } else {
            ecgViewModel.setText("Bluetooth adapter is available!");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1
            );
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    }

    public void scanBluetoothDevice() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1
            );
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            mIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mReceiver, mIntentFilter);
            mBluetoothAdapter.startDiscovery();
        } else {
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            mIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mReceiver, mIntentFilter);
            mBluetoothAdapter.startDiscovery();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == BLUETOOTH_ENABLE_SUCCESS) {
                    Toast.makeText(getContext(), "Bluetooth activated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Activation deined", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 2:
                break;
            default:
        }
    }
}