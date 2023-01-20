package com.remi.navidrawer.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codingending.popuplayout.PopupLayout;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.remi.navidrawer.Cards;
import com.remi.navidrawer.MainActivity;
import com.remi.navidrawer.PagesContainer;
import com.remi.navidrawer.R;
import com.remi.navidrawer.Runtime_measureactivity;
import com.remi.navidrawer.databinding.ActivityMainBinding;
import com.remi.navidrawer.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeCardAdapter mHomeCardAdapter;
    private HomeCardAdapter mHomeCardAdapter2;
    private ArrayList<PagesContainer> mPagesContainerList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.homeFab);
        fab.setImageResource(R.drawable.ic_heartrate_analyze);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Runtime_measureactivity.class);
                startActivity(intent);
            }
        });

        populate();
        RecyclerView introRecyclerView = binding.introCards;
        mHomeCardAdapter = new HomeCardAdapter();
        introRecyclerView.setAdapter(mHomeCardAdapter);
        introRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mHomeCardAdapter.setOnItemClickListener(new HomeCardAdapter.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(View view, int position) {
                View mView = View.inflate(getContext(), R.layout.detail_page, null);
                PopupLayout popupLayout = PopupLayout.init(getContext(), mView);
                Toolbar appbar = mView.findViewById(R.id.toolbar);
                appbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupLayout.dismiss();
                    }
                });


                appbar.setTitle(mPagesContainerList.get(position).getPageTitle());

                ImageView imageView = (ImageView) mView.findViewById(R.id.detailImage);
                imageView.setImageResource(mPagesContainerList.get(position).getPagePic());

                TextView tv = (TextView) mView.findViewById(R.id.detailText);
                tv.setText(mPagesContainerList.get(position).getPageText());

                TextView tv2 = (TextView) mView.findViewById(R.id.additionalText);
                tv2.setText(mPagesContainerList.get(position).getPageAdditionalText());

                String[] links = mPagesContainerList.get(position).getPageLinks();
                TextView tv3 = (TextView) mView.findViewById(R.id.pageLinks1);
                tv3.setText(links[0]);

                TextView tv4 = (TextView) mView.findViewById(R.id.pageLinks2);
                tv4.setText(links[1]);

                if (getResources().getConfiguration().orientation == 1) {
                    popupLayout.setHeight(750, true);
                    popupLayout.setWidth(500, true);
                    popupLayout.show(PopupLayout.POSITION_CENTER);// in case of portrait
                } else {
                    popupLayout.setHeight(120, true);    // in case of landscape
                    popupLayout.show(PopupLayout.POSITION_BOTTOM);
                }
                popupLayout.show();
            }
        });


        RecyclerView guideRecyclerView = binding.userGuideCards;
        mHomeCardAdapter2 = new HomeCardAdapter();
        guideRecyclerView.setAdapter(mHomeCardAdapter2);
        guideRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mHomeCardAdapter2.setOnItemClickListener(new HomeCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                View mView = View.inflate(getContext(), R.layout.detail_page, null);
                PopupLayout popupLayout = PopupLayout.init(getContext(), mView);
                if (getResources().getConfiguration().orientation == 1) { // in case of portrait
                    popupLayout.setHeight(750, true);
                    popupLayout.setWidth(500, true);
                    popupLayout.show(PopupLayout.POSITION_CENTER);
                } else {// in case of landscape
                    popupLayout.show(PopupLayout.POSITION_BOTTOM);
                }
                popupLayout.show();
            }
        });


        homeViewModel.getIntroCardLiveData().observe(getViewLifecycleOwner(), introCardsUpdateObserver);
        homeViewModel.getGuideCardLiveData().observe(getViewLifecycleOwner(), guideCardsUpdateObserver);

        return root;

    }

    Observer<ArrayList<Cards>> introCardsUpdateObserver = new Observer<ArrayList<Cards>>() {
        @Override
        public void onChanged(@NonNull ArrayList<Cards> cards) {
            mHomeCardAdapter.updateCardsList(cards);
        }
    };

    Observer<ArrayList<Cards>> guideCardsUpdateObserver = new Observer<ArrayList<Cards>>() {
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
    public void onResume() {
        super.onResume();
        /*((MainActivity) getActivity()).getFloatingActionButton().show();*/
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }

    public void populate() {
        mPagesContainerList = new ArrayList();

        String text = getString(R.string.ppgi_description);
        String additionalText = getString(R.string.ppgi_description_wiki);
        String[] links = {getString(R.string.ppgi_link1), getString(R.string.ppgi_link1)};
        mPagesContainerList.add(new PagesContainer(R.drawable.intro_details, "PPGI", text, additionalText, links));

        text = getString(R.string.ppgi_howto);
        additionalText = getString(R.string.ppgi_description_wiki);
        String[] newLinks = {getString(R.string.ppgi_howto_reference_1), getString(R.string.ppgi_howto_reference_2)};
        mPagesContainerList.add(new PagesContainer(R.drawable.filming, "How-To", text, additionalText, newLinks));

        text = getString(R.string.opencv_description);
        additionalText = getString(R.string.opencv_details);
        String link2[] = {getString(R.string.ppgi_howto_reference_1), getString(R.string.opencv_links)};
        mPagesContainerList.add(new PagesContainer(R.drawable.opencv_750x400, "OpenCV", text, additionalText, link2));

        text = getString(R.string.green_channels);
        additionalText = getString(R.string.green_details);
        String link[] = {getString(R.string.ppgi_link1), getString(R.string.ppgi_link1)};
        mPagesContainerList.add(new PagesContainer(R.drawable.green_750x400, "Green Channel", text, additionalText, links));
    }

}
