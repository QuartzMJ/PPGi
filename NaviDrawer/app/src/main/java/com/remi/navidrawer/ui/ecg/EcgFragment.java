package com.remi.navidrawer.ui.ecg;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.remi.navidrawer.Cards;
import com.remi.navidrawer.MainActivity;
import com.remi.navidrawer.databinding.FragmentEcgBinding;

import java.util.ArrayList;
import java.util.Set;

public class EcgFragment extends Fragment {

    private FragmentEcgBinding binding;
    private IntentFilter mIntentFilter;
    private int REQUEST_ENABLE_BT = 1;
    private int BLUETOOTH_ENABLE_SUCCESS = -1;
    private EcgViewModel ecgViewModel;
    private EcgCardAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<DetectedDevice> mDetectedDevices = new ArrayList<>();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1
                );
            } else {
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
                    DetectedDevice mDetectedDevice = new DetectedDevice();
                    String deviceClass ="";
                    BluetoothClass btClass = device.getBluetoothClass();

                    if(btClass.getDeviceClass() == BluetoothClass.Device.COMPUTER_DESKTOP){
                        deviceClass += "Desktop";
                    }else if(btClass.getDeviceClass() == BluetoothClass.Device.PHONE_SMART){
                        deviceClass += "Phone";
                    }else if(btClass.getDeviceClass() == BluetoothClass.Device.COMPUTER_LAPTOP){
                        deviceClass += "Laptop";
                    }else{
                        deviceClass += "Others";
                    }


                    if (device != null) {
                        Log.d("Bluetooth device found", device.getAddress().toString() + " Class: " + deviceClass );
                        mDetectedDevice.setDeviceAddress(device.getAddress());
                        if (device.getName() != null) {
                            Log.d("Bluetooth device found", device.getName().toString() + " Class: " + deviceClass);
                            mDetectedDevice.setDeviceName(device.getName());
                        }
                        mDetectedDevice.setDeviceType(device.getType());
                        mDetectedDevice.setDeviceBluetoothClass(device.getBluetoothClass());
                        mDetectedDevices.add(mDetectedDevice);
                        ecgViewModel.updateCards(mDetectedDevices);
                        ecgViewModel.getLiveEcgCards().observe(getViewLifecycleOwner(), updateObserver);
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
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        TextView setDiscoverable = binding.startDiscover;

        setDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDiscoverable();
            }
        });


        TextView refresh = binding.refreshList;
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBluetoothDevice();
            }
        });

        RecyclerView mRecyclerView = binding.ecgRecyclerView;
        mAdapter = new EcgCardAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        initBluetooth();
        setDiscoverable();
        scanBluetoothDevice();


        return root;
    }

    Observer<ArrayList<EcgCards>> updateObserver = new Observer<ArrayList<EcgCards>>() {
        @Override
        public void onChanged(ArrayList<EcgCards> ecgCards) {
            mAdapter.updateEcgCardList(ecgCards);
        }
    };

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
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1
            );
        } else {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d("Paired device ", deviceName + " with " + deviceHardwareAddress);
                }
            }
        }
    }

    public void setDiscoverable() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

}
    public void scanBluetoothDevice() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1
            );
            mBluetoothAdapter.startDiscovery();
        } else {
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