package com.remi.navidrawer.ui.contact;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.remi.navidrawer.Cards;
import com.remi.navidrawer.MainActivity;
import com.remi.navidrawer.databinding.FragmentContactBinding;

import java.util.ArrayList;

public class ContactFragment extends Fragment {

    private FragmentContactBinding binding;
    private ContactCardAdapter mContactCardAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactViewModel contactViewModel =
                new ViewModelProvider(this).get(ContactViewModel.class);
        binding = FragmentContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        RecyclerView contactRecyclerView = binding.contactRecyclerView;
        mContactCardAdapter = new ContactCardAdapter();
        contactRecyclerView.setAdapter(mContactCardAdapter);
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        contactViewModel.getContactCardsLiveData().observe(getViewLifecycleOwner(),contactCardsUpdateObserver);


        return root;

    }

    Observer<ArrayList<ContactCards>> contactCardsUpdateObserver = new Observer<ArrayList<ContactCards>>() {
        @Override
        public void onChanged(@NonNull ArrayList<ContactCards> cards) {
            mContactCardAdapter.updateCardsList(cards);
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