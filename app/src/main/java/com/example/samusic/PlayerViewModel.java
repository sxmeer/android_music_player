package com.example.samusic;

import androidx.lifecycle.ViewModel;

import com.example.samusic.entity.MusicRecord;

import java.util.List;

public class PlayerViewModel extends ViewModel {
    private List<MusicRecord> musicRecords;
    private int current = -1;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void setCurrentFromMusicRecord(MusicRecord musicRecord) {
        for (int i = 0; i < musicRecords.size(); i++) {
            if (musicRecords.get(i).getName().equals(musicRecord.getName())) {
                current = i;
                break;
            }
        }
    }

    public MusicRecord getCurrentMusic() {
        return current != -1 ? musicRecords.get(current) : null;
    }

    public List<MusicRecord> getMusicRecords() {
        return musicRecords;
    }

    public void setMusicRecords(List<MusicRecord> musicRecords) {
        this.musicRecords = musicRecords;
    }

    public boolean hasNextMusic() {
        if ((current + 1) < musicRecords.size())
            return true;
        return false;
    }

    public boolean hasPreviousMusic() {
        if ((current - 1) >= 0)
            return true;
        return false;
    }

    public void setPreviousMusic() {
        current -= 1;
    }

    public void setNextMusic() {
        current += 1;
    }
}
