package com.remi.navidrawer.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.remi.navidrawer.Cards;
import com.remi.navidrawer.MainActivity;
import com.remi.navidrawer.OfflineMeasureActivity;
import com.remi.navidrawer.R;
import com.remi.navidrawer.databinding.FragmentGalleryBinding;
import com.remi.navidrawer.ui.dialogs.DirectionAlertDialogFragment;


import java.util.ArrayList;

public class GalleryFragment extends Fragment  {

    private FragmentGalleryBinding viewBinding;
    private GalleryCardAdapter mGalleryCardAdapter;



    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        viewBinding = FragmentGalleryBinding.inflate(inflater, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        View root = viewBinding.getRoot();


        RecyclerView mGalleryRecycler = viewBinding.galleryRecyclerView;
        mGalleryCardAdapter = new GalleryCardAdapter();
        mGalleryRecycler.setAdapter(mGalleryCardAdapter);
        mGalleryRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mGalleryCardAdapter.setContext(getContext());



        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.galleryFab);
        fab.setImageResource(R.drawable.ic_offline_measure_foreground);
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.ic_offline_measure_background));
        fab.setForegroundTintList(getResources().getColorStateList(R.color.galleryFab_color));

        mGalleryCardAdapter.setOnItemClickListener(new GalleryCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView tv = view.findViewById(R.id.filenames);
                String filename = tv.getText().toString();
                filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ppgi/" + filename;
                ((MainActivity) getActivity()).setFilePath(filename);

                DialogFragment newFragment = new DirectionAlertDialogFragment();
                newFragment.show(getFragmentManager(), "game");

            }
        });

        galleryViewModel.getGalleryCardLiveData().observe(getViewLifecycleOwner(), galleryCardsUpdateObserver);

        return root;
    }

    Observer<ArrayList<Cards>> galleryCardsUpdateObserver = new Observer<ArrayList<Cards>>() {
        @Override
        public void onChanged(@NonNull ArrayList<Cards> cards) {
            mGalleryCardAdapter.updateCardsList(cards);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}