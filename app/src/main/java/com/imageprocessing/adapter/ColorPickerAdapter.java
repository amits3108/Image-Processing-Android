package com.imageprocessing.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.imageprocessing.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Integer> colorPickerColors;
    private OnColorPickerClickListener onColorPickerClickListener;

    public ColorPickerAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        colorPickerColors = new ArrayList<>();
        colorPickerColors.add(ContextCompat.getColor(context, R.color.black));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.white));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.pink));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.purple));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.deep_purple));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.indigo));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.blue));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.light_blue));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.cyan));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.teal));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.green));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.light_green));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.lime));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.abmer));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.orange));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.deep_orange));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.brown));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.grey));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.blue_grey));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.color_picker_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.colorPickerView.setBackgroundColor(colorPickerColors.get(position));
    }

    @Override
    public int getItemCount() {
        return colorPickerColors.size();
    }

    public void setOnColorPickerClickListener(OnColorPickerClickListener onColorPickerClickListener) {
        this.onColorPickerClickListener = onColorPickerClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.color_picker_view) ImageView colorPickerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onColorPickerClickListener != null)
                        onColorPickerClickListener.onColorPickerClickListener(colorPickerColors.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnColorPickerClickListener {
        void onColorPickerClickListener(int colorCode);
    }
}
