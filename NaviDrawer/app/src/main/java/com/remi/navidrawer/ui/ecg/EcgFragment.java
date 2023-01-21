package com.remi.navidrawer.ui.ecg;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.remi.navidrawer.databinding.FragmentEcgBinding;

public class EcgFragment extends Fragment {

    private FragmentEcgBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("I am inside","Here");
        EcgViewModel ecgViewModel =
                new ViewModelProvider(this).get(EcgViewModel.class);
        binding = FragmentEcgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textEcg;


        ecgViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
        /*((MainActivity) getActivity()).getFloatingActionButton().show();*/
    }
}