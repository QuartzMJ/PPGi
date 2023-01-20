package com.remi.navidrawer.ui.contact;


import android.content.Intent;
import android.net.Uri;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.remi.navidrawer.Cards;
import com.remi.navidrawer.MainActivity;
import com.remi.navidrawer.R;
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

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.contactFab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
                selectorIntent.setData(Uri.parse("mailto:"));

                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mengjun.zeng@rwth-aachen.de"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report to PPG imaging app");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, Mr Zeng...");
                emailIntent.setSelector( selectorIntent );

                getActivity().startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

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
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }
}