package com.remi.navidrawer.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.remi.navidrawer.Cards;
import com.remi.navidrawer.R;

public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.Itemholder> {
    private String[] mText;
    private  int[] mPic;
    ArrayList<Cards> userArraylist;

    public HomeCardAdapter() {
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
        mItemHolder.getmTextView().setText(mCard.getText());
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
