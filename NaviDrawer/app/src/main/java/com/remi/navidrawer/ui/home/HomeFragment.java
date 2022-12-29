package com.remi.navidrawer.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.remi.navidrawer.MainActivity;
import com.remi.navidrawer.R;
import com.remi.navidrawer.databinding.FragmentHomeBinding;
import com.remi.navidrawer.ui.CardAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public CardAdapter mCardAdapter;
    public CardAdapter mCardAdapter2;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        RecyclerView introRecyclerView = binding.introCards;
        mCardAdapter = new CardAdapter();
        introRecyclerView.setAdapter(mCardAdapter);
        introRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));


        RecyclerView guideRecyclerView = binding.userGuideCards;
        mCardAdapter2 = new CardAdapter();
        guideRecyclerView.setAdapter(mCardAdapter2);
        guideRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        homeViewModel.getIntroCardLiveData().observe(getViewLifecycleOwner(),introCardsUpdateObserver);
        homeViewModel.getGuideCardLiveData().observe(getViewLifecycleOwner(),guideCardsUpdateObserver);

        return root;

    }

    Observer<ArrayList<Cards>>  introCardsUpdateObserver = new Observer<ArrayList<Cards>>() {
        @Override
        public void onChanged(@NonNull ArrayList<Cards> cards) {
            mCardAdapter.updateCardsList(cards);
        }
    };

    Observer<ArrayList<Cards>>  guideCardsUpdateObserver = new Observer<ArrayList<Cards>>() {
        @Override
        public void onChanged(@NonNull ArrayList<Cards> cards) {
            mCardAdapter2.updateCardsList(cards);
        }
    };




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity) getActivity()).getFloatingActionButton().show();
    }
}