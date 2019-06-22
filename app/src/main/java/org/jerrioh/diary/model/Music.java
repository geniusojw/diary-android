package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class Music implements Serializable {

    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "music";
        public static final String COLUMN_NAME_MUSIC_NAME = "music_name";
        public static final String COLUMN_NAME_MUSIC_DATA = "music_data";
    }

    private String musicName;
    private String musicData;

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicData() {
        return musicData;
    }

    public void setMusicData(String musicData) {
        this.musicData = musicData;
    }
}
