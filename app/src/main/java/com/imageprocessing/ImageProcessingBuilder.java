package com.imageprocessing;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.imageprocessing.widget.BrushDrawingView;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class ImageProcessingBuilder {

    Context context;
    View deleteView;
    ImageView imageView;
    RelativeLayout parentView;
    BrushDrawingView brushDrawingView;

    public ImageProcessingBuilder(Context context) {
        this.context = context;
    }

    public ImageProcessingBuilder deleteView(View deleteView) {
        this.deleteView = deleteView;
        return this;
    }

    public ImageProcessingBuilder childView(ImageView imageView) {
        this.imageView = imageView;
        return this;
    }

    public ImageProcessingBuilder parentView(RelativeLayout parentView) {
        this.parentView = parentView;
        return this;
    }

    public ImageProcessingBuilder brushDrawingView(BrushDrawingView brushDrawingView) {
        this.brushDrawingView = brushDrawingView;
        return this;
    }

    public ImageProcessing buildImageProcessing() {
        return new ImageProcessing(this);
    }
}