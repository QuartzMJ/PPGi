package com.remi.navidrawer.ui.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.remi.navidrawer.Cards;
import com.remi.navidrawer.R;
import com.remi.navidrawer.ui.home.HomeCardAdapter;

public class ContactCardAdapter extends RecyclerView.Adapter<ContactCardAdapter.Itemholder> {
    ArrayList<ContactCards> userArraylist;
    public ContactCardAdapter() {
        this.userArraylist = new ArrayList<ContactCards>();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    private HomeCardAdapter.OnItemClickListener mOnItemClickListener;

    @NonNull
    @Override
    public Itemholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_cards, parent, false);
        return new Itemholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Itemholder holder, int position) {
        ContactCards mContactCard = userArraylist.get(position);
        Itemholder mItemHolder = (Itemholder) holder;
        if (mContactCard.getIsBitmap() == true) {
            mItemHolder.getPic().setImageBitmap(mContactCard.getBitmap());
        } else {
            mItemHolder.getPic().setImageResource(mContactCard.getPic());
        }
        mItemHolder.getTextView().setText(mContactCard.getText());
        mItemHolder.getTextView2().setText(mContactCard.getText2());
        mItemHolder.getTextView3().setText(mContactCard.getText3());
        CardView mCardView = mItemHolder.getCardView();
        if(mOnItemClickListener != null) {
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(mItemHolder.itemView, mItemHolder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userArraylist.size();
    }


    public void updateCardsList(ArrayList<ContactCards> cardArrayList) {
        this.userArraylist.clear();
        this.userArraylist = cardArrayList;
        notifyDataSetChanged();
    }

    public static class Itemholder extends RecyclerView.ViewHolder {
        private final TextView mTextView;
        private final TextView mTextView2;
        private final TextView mTextView3;
        private final ImageView mPic;
        private final CardView mCardView;

        public Itemholder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.contactText);
            mTextView2 = (TextView) view.findViewById(R.id.contactText2);
            mTextView3 = (TextView) view.findViewById(R.id.contactText3);
            mPic = (ImageView) view.findViewById(R.id.contactImage);
            mCardView = (CardView) view.findViewById(R.id.contactCard);
        }

        public ImageView getPic() {
            return mPic;
        }

        public TextView getTextView() {
            return mTextView;
        }

        public TextView getTextView2() {
            return mTextView2;
        }

        public TextView getTextView3() {
            return mTextView3;
        }

        public CardView getCardView() {return mCardView;}
    }

    public void setOnItemClickListener(HomeCardAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
