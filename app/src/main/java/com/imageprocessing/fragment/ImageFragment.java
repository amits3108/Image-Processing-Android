package com.imageprocessing.fragment;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imageprocessing.R;
import com.imageprocessing.activity.ImageProcessingActivity;
import com.imageprocessing.adapter.ImageAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class ImageFragment extends Fragment implements ImageAdapter.OnImageClickListener {

    @BindView(R.id.image_recycler) RecyclerView recyclerView;

    private ArrayList<Bitmap> stickerBitmaps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypedArray images = getResources().obtainTypedArray(R.array.image_photos);
        stickerBitmaps = new ArrayList<>();
        for (int i = 0; i < images.length(); i++) {
            stickerBitmaps.add(decodeSampledBitmapFromResource(getActivity().getResources(),
                    images.getResourceId(i, -1)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        ButterKnife.bind(this, rootView);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        ImageAdapter adapter = new ImageAdapter(getActivity(), stickerBitmaps);
        adapter.setOnImageClickListener(this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, 120, 120);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onImageClickListener(Bitmap image) {
        ImageProcessingActivity  activity = (ImageProcessingActivity) getActivity();
        if (activity != null) activity.addImage(image);
    }
}
