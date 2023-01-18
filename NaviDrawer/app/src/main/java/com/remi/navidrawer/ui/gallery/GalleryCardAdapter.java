package com.remi.navidrawer.ui.gallery;

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

public class GalleryCardAdapter extends RecyclerView.Adapter<GalleryCardAdapter.Itemholder> {
    ArrayList<Cards> userArraylist;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    private GalleryCardAdapter.OnItemClickListener mOnItemClickListener;
    public GalleryCardAdapter() {
        this.userArraylist = new ArrayList<Cards>();
    }

    @NonNull
    @Override
    public Itemholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_cards, parent, false);
        return new Itemholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Itemholder holder, int position) {
        Cards mCard = userArraylist.get(position);
        Itemholder mItemHolder = (Itemholder) holder;
        if (mCard.getIsBitmap() == true) {
            mItemHolder.getPic().setImageBitmap(mCard.getBitmap());
        } else {
            mItemHolder.getPic().setImageResource(mCard.getPic());
        }
        mItemHolder.getTextView().setText(mCard.getText());
        CardView mCardView = (CardView) mItemHolder.getCardView();

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


    public void updateCardsList(ArrayList<Cards> cardArrayList) {
        this.userArraylist.clear();
        this.userArraylist = cardArrayList;
        notifyDataSetChanged();
    }

    public static class Itemholder extends RecyclerView.ViewHolder {
        private final TextView mTextView;
        private final ImageView mPic;
        private final CardView mCardView;

        public Itemholder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.filenames);
            mPic = (ImageView) view.findViewById(R.id.thumbnails);
            mCardView = (CardView) view.findViewById(R.id.galleryCard);
        }

        public ImageView getPic() {
            return mPic;
        }
        public TextView getTextView() {
            return mTextView;
        }
        public CardView getCardView() { return mCardView;}
    }

    public void setOnItemClickListener(GalleryCardAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
