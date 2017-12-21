package com.imageprocessing.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imageprocessing.R;
import com.imageprocessing.activity.ImageProcessingActivity;
import com.imageprocessing.adapter.EmojiAdapter;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class EmojiFragment extends Fragment implements EmojiAdapter.OnEmojiClickListener {

    @BindView(R.id.emoji_recycler) RecyclerView emojiRecyclerView;

    private String[] emojis;
    private ArrayList<String> emojiIds;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emojis = getActivity().getResources().getStringArray(R.array.photo_emoji);
        emojiIds = new ArrayList<>();
        Collections.addAll(emojiIds, emojis);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emoji, container, false);
        ButterKnife.bind(this, rootView);

        emojiRecyclerView.setHasFixedSize(true);
        emojiRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        EmojiAdapter adapter = new EmojiAdapter(getActivity(), emojiIds);
        emojiRecyclerView.setAdapter(adapter);
        adapter.setOnEmojiClickListener(this);

        return rootView;
    }

    @Override
    public void onEmojiClickListener(String emojiName) {
        ImageProcessingActivity activity = (ImageProcessingActivity) getActivity();
        if (activity != null) activity.addEmoji(emojiName);
    }
}
