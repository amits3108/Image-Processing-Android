package com.imageprocessing.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.imageprocessing.R;

import java.util.List;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Bitmap> imageBitmaps;
    private OnImageClickListener onImageClickListener;

    public ImageAdapter(Context context, List<Bitmap> imageBitmaps) {
        this.imageBitmaps = imageBitmaps;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.image_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(imageBitmaps.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickListener != null)
                    onImageClickListener.onImageClickListener(imageBitmaps.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageBitmaps == null ? 0 : imageBitmaps.size();
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public interface OnImageClickListener {
        void onImageClickListener(Bitmap image);
    }
}
