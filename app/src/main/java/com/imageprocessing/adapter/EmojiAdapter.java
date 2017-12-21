package com.imageprocessing.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imageprocessing.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

    private Typeface emojiFont;
    private List<String> emojiIds;
    private LayoutInflater inflater;
    private OnEmojiClickListener onEmojiClickListener;

    public EmojiAdapter(Context context, List<String> emojiIds) {
        this.inflater = LayoutInflater.from(context);
        this.emojiIds = emojiIds;
        emojiFont = Typeface.createFromAsset(context.getAssets(), "multicolore.otf");
    }

    public void setOnEmojiClickListener(OnEmojiClickListener onEmojiClickListener) {
        this.onEmojiClickListener = onEmojiClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.emoji_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.emojiTextView.setText(convertEmoji(emojiIds.get(position)));
        holder.emojiTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEmojiClickListener != null)
                    onEmojiClickListener.onEmojiClickListener(emojiIds.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return emojiIds == null ? 0 : emojiIds.size();
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

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.emoji) TextView emojiTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            emojiTextView.setTypeface(emojiFont);
        }
    }

    public interface OnEmojiClickListener {
        void onEmojiClickListener(String emojiName);
    }
}
