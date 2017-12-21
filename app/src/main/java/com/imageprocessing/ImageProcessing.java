package com.imageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imageprocessing.appInterface.MultiTouchListener;
import com.imageprocessing.appInterface.OnImageProcessingListener;
import com.imageprocessing.widget.BrushDrawingView;
import com.imageprocessing.widget.EditType;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class ImageProcessing implements MultiTouchListener.OnMultiTouchListener {

    private View deleteView;
    private View addTextRootView;
    private List<View> addedViews;
    private ImageView imageView;
    private RelativeLayout parentView;

    private Context context;
    private LayoutInflater inflater;

    private MultiTouchListener multiTouchListener;
    private BrushDrawingView brushDrawingView;
    private OnImageProcessingListener onImageProcessingListener;

    public ImageProcessing(ImageProcessingBuilder ImageProcessingBuilder) {
        this.context = ImageProcessingBuilder.context;
        this.parentView = ImageProcessingBuilder.parentView;
        this.imageView = ImageProcessingBuilder.imageView;
        this.deleteView = ImageProcessingBuilder.deleteView;
        this.brushDrawingView = ImageProcessingBuilder.brushDrawingView;
        addedViews = new ArrayList<>();
    }

    public void addImage(Bitmap desiredImage) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View imageRootView = inflater.inflate(R.layout.image_processing_imageview, null);

        ImageView imageView = (ImageView) imageRootView
                .findViewById(R.id.image_processing_imageview);
        imageView.setImageBitmap(desiredImage);
        imageView.setLayoutParams(getRelativeLayoutParams());

        multiTouchListener = new MultiTouchListener(deleteView, imageView,
                onImageProcessingListener);
        multiTouchListener.setOnMultiTouchListener(this);

        imageRootView.setOnTouchListener(multiTouchListener);

        RelativeLayout.LayoutParams params = getRelativeLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        parentView.addView(imageRootView, params);
        addedViews.add(imageRootView);

        if (onImageProcessingListener != null) {
            onImageProcessingListener.onAddViewListener(EditType.IMAGE, addedViews.size());
        }
    }

    public void addText(String text, int colorCodeTextView) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addTextRootView = inflater.inflate(R.layout.image_processing_text, null);

        TextView addTextView = (TextView) addTextRootView
                .findViewById(R.id.image_processing_text);
        addTextView.setGravity(Gravity.CENTER);
        addTextView.setText(text);
        if (colorCodeTextView != -1) {
            addTextView.setTextColor(colorCodeTextView);
        }

        multiTouchListener = new MultiTouchListener(deleteView, imageView,
                onImageProcessingListener);
        multiTouchListener.setOnMultiTouchListener(this);

        addTextRootView.setOnTouchListener(multiTouchListener);

        RelativeLayout.LayoutParams params = getRelativeLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        parentView.addView(addTextRootView, params);
        addedViews.add(addTextRootView);

        if (onImageProcessingListener != null) {
            onImageProcessingListener.onAddViewListener(EditType.TEXT, addedViews.size());
        }
    }

    public void addEmoji(String emojiName, Typeface emojiFont) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View emojiView = inflater.inflate(R.layout.image_processing_text, null);

        TextView emojiTextView = (TextView) emojiView
                .findViewById(R.id.image_processing_text);
        emojiTextView.setTypeface(emojiFont);
        emojiTextView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        emojiTextView.setText(convertEmoji(emojiName));

        multiTouchListener = new MultiTouchListener(deleteView, imageView,
                onImageProcessingListener);
        multiTouchListener.setOnMultiTouchListener(this);

        emojiView.setOnTouchListener(multiTouchListener);

        RelativeLayout.LayoutParams params = getRelativeLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        parentView.addView(emojiView, params);
        addedViews.add(emojiView);

        if (onImageProcessingListener != null) {
            onImageProcessingListener.onAddViewListener(EditType.EMOJI, addedViews.size());
        }
    }

    private RelativeLayout.LayoutParams getRelativeLayoutParams() {
        return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    public void setBrushDrawingMode(boolean brushDrawingMode) {
        if (brushDrawingView != null) {
            brushDrawingView.setBrushDrawingMode(brushDrawingMode);
        }
    }

    public void setBrushSize(float size) {
        if (brushDrawingView != null) {
            brushDrawingView.setBrushSize(size);
        }
    }

    public void setBrushColor(@ColorInt int color) {
        if (brushDrawingView != null) {
            brushDrawingView.setBrushColor(color);
        }
    }

    public void setBrushEraserSize(float brushEraserSize) {
        if (brushDrawingView != null) {
            brushDrawingView.setBrushEraserSize(brushEraserSize);
        }
    }

    public void setBrushEraserColor(@ColorInt int color) {
        if (brushDrawingView != null) {
            brushDrawingView.setBrushEraserColor(color);
        }
    }

    public float getEraserSize() {
        if (brushDrawingView != null) {
            return brushDrawingView.getEraserSize();
        }
        return 0;
    }

    public float getBrushSize() {
        if (brushDrawingView != null) {
            return brushDrawingView.getBrushSize();
        }
        return 0;
    }

    public int getBrushColor() {
        if (brushDrawingView != null) {
            return brushDrawingView.getBrushColor();
        }
        return 0;
    }

    public void brushEraser() {
        if (brushDrawingView != null) {
            brushDrawingView.brushEraser();
        }
    }

    public void viewUndo() {
        if (addedViews.size() > 0) {
            parentView.removeView(addedViews.remove(addedViews.size() - 1));
            if (onImageProcessingListener != null) {
                onImageProcessingListener.onRemoveViewListener(addedViews.size());
            }
        }
    }

    private void viewUndo(View removedView) {
        if (addedViews.size() > 0) {
            if (addedViews.contains(removedView)) {
                parentView.removeView(removedView);
                addedViews.remove(removedView);
                if (onImageProcessingListener != null) {
                    onImageProcessingListener.onRemoveViewListener(addedViews.size());
                }
            }
        }
    }

    public void clearBrushAllViews() {
        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }

    public void clearAllViews() {
        for (int i = 0; i < addedViews.size(); i++) {
            parentView.removeView(addedViews.get(i));
        }
        if (brushDrawingView != null) {
            brushDrawingView.clearAll();
        }
    }

    public String saveImage(String imageName) {
        String selectedOutputPath = "";
        if (isSDCARDMounted()) {
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ImageProcessing");
            // Create a storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("ImageProcessing", "Failed to create directory");
                }
            }
            // Create a media file name
            selectedOutputPath = mediaStorageDir.getPath() + File.separator + imageName;
            Log.d("ImageProcessing", "selected camera path " + selectedOutputPath);
            File file = new File(selectedOutputPath);
            try {
                FileOutputStream out = new FileOutputStream(file);
                if (parentView != null) {
                    parentView.setDrawingCacheEnabled(true);
                    parentView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 80, out);
                }
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return selectedOutputPath;
    }

    private boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    private String convertEmoji(String emoji) {
        String returnedEmoji = "";
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public void setOnImageProcessingListener(OnImageProcessingListener onImageProcessingListener) {
        this.onImageProcessingListener = onImageProcessingListener;
        brushDrawingView.setOnImageProcessingListener(onImageProcessingListener);
    }

    @Override
    public void onEditTextClickListener(String text, int colorCode) {
        if (addTextRootView != null) {
            parentView.removeView(addTextRootView);
            addedViews.remove(addTextRootView);
        }
    }

    @Override
    public void onRemoveViewListener(View removedView) {
        viewUndo(removedView);
    }
}
