package com.imageprocessing.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imageprocessing.ImageProcessing;
import com.imageprocessing.ImageProcessingBuilder;
import com.imageprocessing.R;
import com.imageprocessing.adapter.ColorPickerAdapter;
import com.imageprocessing.adapter.EmojiPagerAdapter;
import com.imageprocessing.appInterface.OnImageProcessingListener;
import com.imageprocessing.widget.BrushDrawingView;
import com.imageprocessing.fragment.EmojiFragment;
import com.imageprocessing.fragment.ImageFragment;
import com.imageprocessing.widget.EditType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class ImageProcessingActivity extends AppCompatActivity implements OnImageProcessingListener {
    private static final String TAG = "ImageProcessingActivity";

    @BindView(R.id.delete_layout) RelativeLayout deleteLayout;
    @BindView(R.id.parent_image) RelativeLayout parentImageLayout;
    @BindView(R.id.drawing_view) BrushDrawingView brushDrawingView;
    @BindView(R.id.color_picker_recycler) RecyclerView colorPickerRecyclerView;
    @BindView(R.id.undo_text) ImageButton undoText_View;
    @BindView(R.id.done_drawing) TextView doneDrawingView;
    @BindView(R.id.erase_drawing) TextView eraseDrawingView;
    @BindView(R.id.photo_edit) ImageView photoEditImageView;
    @BindView(R.id.emoji_view_pager) ViewPager emojiPager;
    @BindView(R.id.emoji_bottom_sheet) RelativeLayout bottomSheetViewgroup;

    private Typeface emojiFont;
    private int colorCodeTextView = -1;
    private ImageProcessing imageProcessing;

    ColorPickerAdapter colorPickerAdapter;
    BottomSheetBehavior bottomSheetBehavior;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        ButterKnife.bind(this);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        setEditingImage();
        setupImageProcessingSDK();
        setupEmojiAdapter();
    }

    private void setEditingImage() {
        String selectedImagePath = getIntent().getExtras().getString("selectedImagePath");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
        photoEditImageView.setImageBitmap(bitmap);
    }

    private void setupImageProcessingSDK() {
        imageProcessing = new ImageProcessingBuilder(this)
                .deleteView(deleteLayout)
                .childView(photoEditImageView)
                .parentView(parentImageLayout)
                .brushDrawingView(brushDrawingView)
                .buildImageProcessing();
        imageProcessing.setOnImageProcessingListener(this);
    }

    private void setupEmojiAdapter() {
        emojiFont = Typeface.createFromAsset(getAssets(), "multicolore.otf");

        final List<Fragment> fragmentsList = new ArrayList<>();
        fragmentsList.add(new ImageFragment());
        fragmentsList.add(new EmojiFragment());

        EmojiPagerAdapter adapter = new EmojiPagerAdapter(getSupportFragmentManager(), fragmentsList);
        emojiPager.setAdapter(adapter);
        emojiPager.setOffscreenPageLimit(5);
    }

    public void addEmoji(String emojiName) {
        imageProcessing.addEmoji(emojiName, emojiFont);
        openCloseEmojiSheet();
    }

    public void addImage(Bitmap image) {
        imageProcessing.addImage(image);
        openCloseEmojiSheet();
    }

    private void addText(String text, int colorCodeTextView) {
        imageProcessing.addText(text, colorCodeTextView);
    }

    private void clearAllViews() {
        imageProcessing.clearAllViews();
    }

    private void undoViews() {
        imageProcessing.viewUndo();
    }

    private void eraseDrawing() {
        imageProcessing.brushEraser();
    }

    private void openAddTextPopupWindow(String text, int colorCode) {
        colorCodeTextView = colorCode;

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addTextLayout = inflater.inflate(R.layout.add_text_popup_window, null);

        final EditText addText = (EditText) addTextLayout.findViewById(R.id.add_text);
        TextView addTextDone = (TextView) addTextLayout.findViewById(R.id.add_text_done);
        RecyclerView recyclerView = (RecyclerView) addTextLayout.findViewById(R.id.add_text_color_recycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        colorPickerAdapter = new ColorPickerAdapter(this);
        recyclerView.setAdapter(colorPickerAdapter);

        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                addText.setTextColor(colorCode);
                colorCodeTextView = colorCode;
            }
        });

        if (text != null) {
            if (!text.isEmpty())
                addText.setText(text);
                addText.setTextColor(colorCode == -1 ? ContextCompat.getColor(this, R.color.white) : colorCode);
        }

        final PopupWindow pop = getAddTextPopupWindow(addTextLayout);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        addTextDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addText(addText.getText().toString(), colorCodeTextView);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                pop.dismiss();
            }
        });
    }

    @NonNull
    private PopupWindow getAddTextPopupWindow(View addTextLayout) {
        final PopupWindow pop = new PopupWindow(addTextLayout,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        pop.setContentView(addTextLayout);
        pop.setFocusable(true);
        pop.setBackgroundDrawable(null);
        pop.showAtLocation(addTextLayout, Gravity.TOP, 0, 0);
        pop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        return pop;
    }

    private void updateBrushDrawingView(boolean brushDrawingMode) {
        imageProcessing.setBrushDrawingMode(brushDrawingMode);
        if (brushDrawingMode) {
            setViewsVisibility(View.VISIBLE);

            colorPickerRecyclerView.setHasFixedSize(true);
            colorPickerRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));

            colorPickerAdapter = new ColorPickerAdapter(this);
            colorPickerRecyclerView.setAdapter(colorPickerAdapter);

            colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
                @Override
                public void onColorPickerClickListener(int colorCode) {
                    imageProcessing.setBrushColor(colorCode);
                }
            });

        } else {
            setViewsVisibility(View.GONE);
        }
    }

    private void setViewsVisibility(int visible) {
        colorPickerRecyclerView.setVisibility(visible);
        doneDrawingView.setVisibility(visible);
        eraseDrawingView.setVisibility(visible);
    }

    private void savedImage() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentImageLayout.setLayoutParams(layoutParams);

        new CountDownTimer(1000, 500) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                String timeStamp = new SimpleDateFormat("yyyymmdd_hhmmss").format(new Date());
                String imageName = "IMG_" + timeStamp + ".jpg";
                Intent returnIntent = new Intent();
                returnIntent.putExtra("imagePath", imageProcessing.saveImage(imageName));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }.start();
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        onBackPressed();
    }

    @OnClick(R.id.add_image_emoji)
    void onAddImageEmojiClick() {
        openCloseEmojiSheet();
    }

    private void openCloseEmojiSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @OnClick(R.id.add_text)
    void onAddTextClick() {
        openAddTextPopupWindow("", -1);
    }

    @OnClick(R.id.add_pencil)
    void onAddPencilClick() {
        updateBrushDrawingView(true);
    }

    @OnClick(R.id.done_drawing)
    void onDoneDrawingClick() {
        updateBrushDrawingView(false);
    }

    @OnClick(R.id.save)
    void onSaveClick() {
        savedImage();
    }

    @OnClick(R.id.clear_all)
    void onClearAllClick() {
        clearAllViews();
    }

    @OnClick({R.id.undo_text})
    void onUndoClick() {
        undoViews();
    }

    @OnClick(R.id.erase_drawing)
    void onEraseDrawingClick() {
        eraseDrawing();
    }

    @Override
    public void onEditTextChangeListener(String text, int colorCode) {
        openAddTextPopupWindow(text, colorCode);
    }

    @Override
    public void onAddViewListener(EditType editType, int numberOfAddedViews) {
        if (numberOfAddedViews > 0) {
            undoText_View.setVisibility(View.VISIBLE);
        }
        switch (editType) {
            case TEXT:
                Log.i("TEXT", "onAddViewListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onAddViewListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onAddViewListener");
                break;
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onAddViewListener");
                break;
        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        Log.i(TAG, "onRemoveViewListener");
        if (numberOfAddedViews == 0) {
            undoText_View.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStartViewChangeListener(EditType editType) {
        switch (editType) {
            case TEXT:
                Log.i("TEXT", "onStartViewChangeListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onStartViewChangeListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onStartViewChangeListener");
                break;
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onStartViewChangeListener");
                break;
        }
    }

    @Override
    public void onStopViewChangeListener(EditType editType) {
        switch (editType) {
            case TEXT:
                Log.i("TEXT", "onStopViewChangeListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onStopViewChangeListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onStopViewChangeListener");
                break;
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onStopViewChangeListener");
                break;
        }
    }

}
