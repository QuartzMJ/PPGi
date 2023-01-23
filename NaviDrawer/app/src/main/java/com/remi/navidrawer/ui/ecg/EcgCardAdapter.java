package com.remi.navidrawer.ui.ecg;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.remi.navidrawer.Cards;
import com.remi.navidrawer.R;
import com.remi.navidrawer.ui.gallery.GalleryCardAdapter;

import java.util.ArrayList;

public class EcgCardAdapter extends RecyclerView.Adapter<EcgCardAdapter.Itemholder> {
    private ArrayList<EcgCards> mEcgCards;

    public EcgCardAdapter() {
        this.mEcgCards = new ArrayList<EcgCards>();
    }

    @NonNull
    @Override
    public EcgCardAdapter.Itemholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bt_cards, parent, false);
        return new EcgCardAdapter.Itemholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EcgCardAdapter.Itemholder holder, int position) {
        EcgCards mContentCard = mEcgCards.get(position);
        if (mContentCard.getDeviceName() ==null ) {
            holder.getDeviceName().setText("Name: Unknown");
        } else {
            holder.getDeviceName().setText("Name: " + mContentCard.getDeviceName());
        }
        holder.getDeviceClass().setText(mContentCard.getText());
        holder.getDeviceAddress().setText("MAC:" + mContentCard.getDeviceAddress());
        holder.getDeviceImage().setImageResource(mContentCard.getPic());

    }

    public void updateEcgCardList(ArrayList<EcgCards> cardArrayList) {

        this.mEcgCards.clear();
        this.mEcgCards.addAll(cardArrayList);

        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return mEcgCards.size();
    }

    public static class Itemholder extends RecyclerView.ViewHolder {
        private final TextView mDeviceClass;
        private final TextView mDeviceName;
        private final TextView mDeviceAddress;
        private final ImageView mDeviceImage;

        public Itemholder(View view) {
            super(view);
            mDeviceName = view.findViewById(R.id.btName);
            mDeviceClass = view.findViewById(R.id.btType);
            mDeviceAddress = view.findViewById(R.id.btMac);
            mDeviceImage = view.findViewById(R.id.btImage);
        }

        public TextView getDeviceName() {
            return mDeviceName;
        }

        public TextView getDeviceClass() {
            return mDeviceClass;
        }

        public TextView getDeviceAddress() {
            return mDeviceAddress;
        }

        public ImageView getDeviceImage() {
            return mDeviceImage;
        }
    }
}
