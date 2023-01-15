package com.remi.navidrawer.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.remi.navidrawer.CardAdapter;
import com.remi.navidrawer.Cards;
import com.remi.navidrawer.MainActivity;
import com.remi.navidrawer.databinding.FragmentCaptureBinding;
import com.remi.navidrawer.databinding.FragmentGalleryBinding;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding viewBinding;
    private CardAdapter mCardAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        viewBinding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = viewBinding.getRoot();
        Log.d("OnCreatView Gallery", "onCreateView");

        RecyclerView mGalleryRecycler = viewBinding.galleryRecyclerView;
        mCardAdapter = new CardAdapter();
        mGalleryRecycler.setAdapter(mCardAdapter);
        mGalleryRecycler.setLayoutManager(new GridLayoutManager(getContext(),3));



        galleryViewModel.getGalleryCardLiveData().observe(getViewLifecycleOwner(),galleryCardsUpdateObserver);

        return root;
    }

    Observer<ArrayList<Cards>> galleryCardsUpdateObserver = new Observer<ArrayList<Cards>>() {
        @Override
        public void onChanged(@NonNull ArrayList<Cards> cards) {
            mCardAdapter.updateCardsList(cards);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinding = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity) getActivity()).getFloatingActionButton().show();
    }
}