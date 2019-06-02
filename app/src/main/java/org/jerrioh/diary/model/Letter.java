package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class Letter implements Serializable {
    public static class LetterStatus {
        public static final int UNREAD = 0;
        public static final int READ = 1;
    }

    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "letter";
        public static final String COLUMN_NAME_LETTER_ID = "letter_id";
        public static final String COLUMN_NAME_FROM_AUTHOR_ID = "from_author_id";
        public static final String COLUMN_NAME_FROM_AUTHOR_NICKNAME = "from_author_nickname";
        public static final String COLUMN_NAME_TO_AUTHOR_ID = "to_author_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_WRITTEN_TIME = "written_time";
        public static final String COLUMN_NAME_STATUS = "status";
    }

    private String letterId; // ex) author_id + seq
    private String fromAuthorId;
    private String fromAuthorNickname;
    private String toAuthorId;
    private String title;
    private String content;
    private long writtenTime;
    private int status; // ex) 0: unread, 1: read

    public String getLetterId() {
        return letterId;
    }

    public void setLetterId(String letterId) {
        this.letterId = letterId;
    }

    public String getFromAuthorId() {
        return fromAuthorId;
    }

    public void setFromAuthorId(String fromAuthorId) {
        this.fromAuthorId = fromAuthorId;
    }

    public String getFromAuthorNickname() {
        return fromAuthorNickname;
    }

    public void setFromAuthorNickname(String fromAuthorNickname) {
        this.fromAuthorNickname = fromAuthorNickname;
    }

    public String getToAuthorId() {
        return toAuthorId;
    }

    public void setToAuthorId(String toAuthorId) {
        this.toAuthorId = toAuthorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getWrittenTime() {
        return writtenTime;
    }

    public void setWrittenTime(long writtenTime) {
        this.writtenTime = writtenTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
