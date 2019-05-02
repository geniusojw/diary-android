package org.jerrioh.diary.dbmodel;

import android.provider.BaseColumns;

import org.jerrioh.diary.config.Information;

import java.io.Serializable;

public class Diary implements Serializable {
    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "diary";
        public static final String COLUMN_NAME_WRITE_DAY = "write_day";
        public static final String COLUMN_NAME_WRITE_USER_ID = "write_user_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_SERVER_SAVED = "server_saved";
    }

    private String writeDay; // ex) 20190430
    private String writeUserId;
    private String title;
    private String content;
    private int serverSaved; // ex) 0: not saved, 1: saved

    public Diary(String writeDay, String writeUserId, String title, String content, int serverSaved) {
        this.writeDay = writeDay;
        this.writeUserId = writeUserId;
        this.title = title;
        this.content = content;
        this.serverSaved = serverSaved;
    }

    public String getWriteDay() {
        return writeDay;
    }
    public String getWriteUserId() {
        return writeUserId;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public int getServerSaved() {
        return serverSaved;
    }

    // 수정가능 항목
    public void setTitle(String title) {
        if (isMyWrite()) this.title = title;
    }
    public void setContent(String content) {
        if (isMyWrite()) this.content = content;
    }
    public void setServerSaved(int serverSaved) {
        if (isMyWrite()) this.serverSaved = serverSaved;
    }

    private boolean isMyWrite() {
        return writeUserId.equals(Information.getAccount().getUserId());
    }
}
