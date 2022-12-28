package com.remi.navidrawer.ui;

import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.remi.navidrawer.ui.home.Cards;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.remi.navidrawer.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.Itemholder> {
    private String[] mText;
    private  int[] mPic;
    ArrayList<Cards> userArraylist;

    public CardAdapter() {
       this.userArraylist = new ArrayList<Cards>();
    }

    @NonNull
    @Override
    public Itemholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_cards, parent, false);
        return new Itemholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Itemholder holder, int position) {
        Cards mCard = userArraylist.get(position);
        Itemholder mItemHolder = (Itemholder) holder;
        mItemHolder.getmPic().setImageResource(mCard.getPic());
        mItemHolder.getmTextView().setText(mCard.getDescription());
    }

    @Override
    public int getItemCount() {
        return userArraylist.size();
    }

    public void updateCardsList(ArrayList<Cards> cardArrayList) {
        this.userArraylist.clear();
        this.userArraylist = cardArrayList;
        notifyDataSetChanged();
    }

    public static class Itemholder extends RecyclerView.ViewHolder {
        private final TextView mTextView;
        private final ImageView mPic;
        public Itemholder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.introText);
            mPic = (ImageView) view.findViewById(R.id.introImage);
        }

        public ImageView getmPic() {
            return mPic;
        }
        public TextView getmTextView() {
            return mTextView;
        }
    }

}
