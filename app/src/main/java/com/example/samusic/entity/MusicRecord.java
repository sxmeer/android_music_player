package com.example.samusic.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = @Index(value = {"name", "location", "duration"}, unique = true))
public class MusicRecord {
    @PrimaryKey
    int id;
    String name;
    String location;
    long duration;
    String durationString;
    boolean isFavourite = false;

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public static String getTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long seconds = totalSeconds % 60;
        long mins = totalSeconds / 60;
        long hrs = mins / 60;
        if (hrs > 0) {
            mins = mins - hrs * 60;
        }
        String result = (hrs == 0 ? "" : hrs+":") + (mins == 0 ? "00" : String.format("%02d",mins)) +":"+ (seconds == 0 ? "00" : String.format("%02d",seconds));
        return result;
    }

    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(long milliseconds) {
        this.durationString = getTime(milliseconds);
    }
}
