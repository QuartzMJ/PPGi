package com.remi.navidrawer.ui.contact;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.remi.navidrawer.ui.home.HomeCardAdapter;

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

        mContactCardAdapter.setOnItemClickListener(new HomeCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView tv  =(TextView) view.findViewById(R.id.contactText);
                String type  = tv.getText().toString();

                if (type == "Mail")
                {
                    TextView tv2 = (TextView) view.findViewById(R.id.contactText2);
                    String emailAddress = tv2.getText().toString();
                    Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
                    selectorIntent.setData(Uri.parse("mailto:"));

                    Log.d("Output email", emailAddress);
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{emailAddress});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report to PPG imaging app");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello...");
                    emailIntent.setSelector( selectorIntent );

                    getActivity().startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }else if (type == "Phone"){
                    try{
                    TextView tv2 = (TextView) view.findViewById(R.id.contactText2);
                    String phoneNumber = tv2.getText().toString();

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:"+Uri.encode(phoneNumber.trim())));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                    }catch (Exception e){
                        if (e instanceof ActivityNotFoundException){
                            Toast.makeText(getActivity(),
                                    "No application found to make phone call", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    TextView tv2 = (TextView) view.findViewById(R.id.contactText2);
                    String site = tv2.getText().toString();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(site)));
                }
            }
        });

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