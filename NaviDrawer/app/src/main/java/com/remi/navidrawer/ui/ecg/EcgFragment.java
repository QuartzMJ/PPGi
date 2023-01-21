package com.remi.navidrawer.ui.ecg;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.remi.navidrawer.databinding.FragmentEcgBinding;

import java.util.Set;

public class EcgFragment extends Fragment {

    private FragmentEcgBinding binding;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("I am inside", "Here");
        EcgViewModel ecgViewModel =
                new ViewModelProvider(this).get(EcgViewModel.class);
        binding = FragmentEcgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textEcg;
        ecgViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            ecgViewModel.setText("There is no Bluetooth adapter on your device");
        } else {
            ecgViewModel.setText("Bluetooth adapter is available!");
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d("Bluetooth device:",deviceName);
                Log.d("deviceHardwareAddress:",deviceHardwareAddress);
            }
        }

        mIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BLUETOOTH CHECK"," onRecieve"); //do something with intent
            }
        };

        int requestCode = 1;
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, requestCode);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }

}