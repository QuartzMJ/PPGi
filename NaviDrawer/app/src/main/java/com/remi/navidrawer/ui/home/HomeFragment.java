package com.remi.navidrawer.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codingending.popuplayout.PopupLayout;
import com.remi.navidrawer.Cards;
import com.remi.navidrawer.MainActivity;
import com.remi.navidrawer.R;
import com.remi.navidrawer.Runtime_measureactivity;
import com.remi.navidrawer.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeCardAdapter mHomeCardAdapter;
    private HomeCardAdapter mHomeCardAdapter2;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        RecyclerView introRecyclerView = binding.introCards;
        mHomeCardAdapter = new HomeCardAdapter();
        introRecyclerView.setAdapter(mHomeCardAdapter);
        introRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mHomeCardAdapter.setOnItemClickListener(new HomeCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                View mView = View.inflate(getContext(), R.layout.detail_page, null);
                PopupLayout popupLayout = PopupLayout.init(getContext(), mView);
                if(getResources().getConfiguration().orientation == 1){ // in case of portrait
                    popupLayout.setHeight(750,true);
                    popupLayout.setWidth(500,true);
                    popupLayout.show(PopupLayout.POSITION_CENTER);
                }else{// in case of landscape
                    popupLayout.show(PopupLayout.POSITION_BOTTOM);
                }
                popupLayout.show();
            }
        });


        RecyclerView guideRecyclerView = binding.userGuideCards;
        mHomeCardAdapter2 = new HomeCardAdapter();
        guideRecyclerView.setAdapter(mHomeCardAdapter2);
        guideRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mHomeCardAdapter2.setOnItemClickListener(new HomeCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                View mView = View.inflate(getContext(), R.layout.popout_layout, null);
                PopupLayout popupLayout = PopupLayout.init(getContext(), mView);


                if(getResources().getConfiguration().orientation == 1){ // in case of portrait
                popupLayout.setHeight(120, true);
                popupLayout.show(PopupLayout.POSITION_BOTTOM);
                }else{
                    popupLayout.setHeight(120, true);    // in case of landscape
                    popupLayout.show(PopupLayout.POSITION_BOTTOM);
                }
                popupLayout.show();
            }
        });

        homeViewModel.getIntroCardLiveData().observe(getViewLifecycleOwner(),introCardsUpdateObserver);
        homeViewModel.getGuideCardLiveData().observe(getViewLifecycleOwner(),guideCardsUpdateObserver);

        return root;

    }

    Observer<ArrayList<Cards>>  introCardsUpdateObserver = new Observer<ArrayList<Cards>>() {
        @Override
        public void onChanged(@NonNull ArrayList<Cards> cards) {
            mHomeCardAdapter.updateCardsList(cards);
        }
    };

    Observer<ArrayList<Cards>>  guideCardsUpdateObserver = new Observer<ArrayList<Cards>>() {
        @Override
        public void onChanged(@NonNull ArrayList<Cards> cards) {
            mHomeCardAdapter2.updateCardsList(cards);
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