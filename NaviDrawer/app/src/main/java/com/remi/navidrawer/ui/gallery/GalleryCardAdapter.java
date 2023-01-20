package com.remi.navidrawer.ui.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.remi.navidrawer.Cards;
import com.remi.navidrawer.R;


public class GalleryCardAdapter extends RecyclerView.Adapter<GalleryCardAdapter.Itemholder> {
    ArrayList<Cards> userArraylist;
    Context mContext;
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

        File file = mCard.getFile();
        File thumbnailsFolder = mContext.getCacheDir();
        File folder = new File(thumbnailsFolder, "/thumbnails/");
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.e("ERROR", "Cannot create a directory!");
            } else {
                folder.mkdirs();
            }
        }
        String picPath = mContext.getCacheDir().getAbsolutePath()+ "/thumbnails" +'/' +file.getName().substring(0,file.getName().length()-4) + ".jpg";

        File picFile = new File(picPath);
        Bitmap bitmap;
        if (!picFile.exists()) {
            bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            OutputStream stream = null;
            try {
                stream = new FileOutputStream(picFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            try {
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            bitmap = BitmapFactory.decodeFile(picFile.getAbsolutePath());
            Log.d("pics founded", picFile.getAbsolutePath());
        }
        mItemHolder.getPic().setImageBitmap( bitmap);
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

    public void setContext(Context context) {
        mContext = context;
    }
}
