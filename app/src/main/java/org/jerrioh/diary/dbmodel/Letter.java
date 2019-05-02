package org.jerrioh.diary.dbmodel;

import android.provider.BaseColumns;

import org.jerrioh.diary.config.Information;

import java.io.Serializable;

public class Letter implements Serializable {
    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "letter";
        public static final String COLUMN_NAME_WRITE_TIME = "write_time";
        public static final String COLUMN_NAME_WRITE_USER_ID = "write_user_id";
        public static final String COLUMN_NAME_READ_USER_ID = "read_user_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_SERVER_SAVED = "server_saved";
        public static final String COLUMN_NAME_STATUS = "status";
    }

    private String writeTime; // ex) unix time
    private String writeUserId;
    private String readUserId;
    private String title;
    private String content;
    private int serverSaved; // ex) 0: not saved, 1: saved
    private int status; // ex) 0: unread, 1: read, 2: reply

    public Letter(String writeTime, String writeUserId, String readUserId, String title, String content, int serverSaved, int status) {
        this.writeTime = writeTime;
        this.writeUserId = writeUserId;
        this.readUserId = readUserId;
        this.title = title;
        this.content = content;
        this.serverSaved = serverSaved;
        this.status = status;
    }

    public String getWriteTime() {
        return writeTime;
    }
    public String getWriteUserId() {
        return writeUserId;
    }
    public String getReadUserId() {
        return readUserId;
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
    public int getStatus() {
        return status;
    }
}
