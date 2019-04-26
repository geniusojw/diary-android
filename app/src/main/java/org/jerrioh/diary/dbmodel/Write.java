package org.jerrioh.diary.dbmodel;

import android.provider.BaseColumns;

import org.jerrioh.diary.config.Information;

import java.io.Serializable;

public class Write implements Serializable {
    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "write";
        public static final String COLUMN_NAME_WRITE_TYPE = "write_type";
        public static final String COLUMN_NAME_WRITE_DAY = "write_day";
        public static final String COLUMN_NAME_WRITE_USER_ID = "write_user_id";
        public static final String COLUMN_NAME_READ_USER_ID = "read_user_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_SERVER_SAVED = "server_saved";
    }

    public static class WriteType {
        public static final int DIARY = 1;
        public static final int LETTER = 2;
        public static final int SYSTEM_NOTICE = 9;
    }

    private int writeType;
    private String writeDay; // ex) 20190430
    private String writeUserId;
    private String readUserId;
    private String title;
    private String content;
    private int serverSaved; // ex) 0: not saved, 1: saved

    public Write(int writeType, String writeDay, String writeUserId, String readUserId, String title, String content, int serverSaved) {
        this.writeType = writeType;
        this.writeDay = writeDay;
        this.writeUserId = writeUserId;
        this.readUserId = readUserId;
        this.title = title;
        this.content = content;
        this.serverSaved = serverSaved;
    }

    public int getWriteType() {
        return writeType;
    }
    public String getWriteDay() {
        return writeDay;
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

    // 수정가능 항목
    public void setReadUserId(String readUserId) {
        if (isMyWrite()) this.readUserId = readUserId;
    }
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
        return writeUserId.equals(Information.account.getUserId());
    }
}
