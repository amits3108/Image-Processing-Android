package com.imageprocessing.appInterface;

import com.imageprocessing.widget.EditType;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public interface OnImageProcessingListener {
    void onStartViewChangeListener(EditType editType);

    void onStopViewChangeListener(EditType editType);

    void onAddViewListener(EditType editType, int numberOfAddedViews);

    void onEditTextChangeListener(String text, int colorCode);

    void onRemoveViewListener(int numberOfAddedViews);
}
