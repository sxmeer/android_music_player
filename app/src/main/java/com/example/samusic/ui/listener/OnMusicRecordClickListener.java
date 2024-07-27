package com.example.samusic.ui.listener;

import android.view.View;

import com.example.samusic.entity.MusicRecord;

public interface OnMusicRecordClickListener {
    void onClick(View view, MusicRecord musicRecord, int pos);
}
