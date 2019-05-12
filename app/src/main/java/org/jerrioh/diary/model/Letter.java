package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class Letter implements Serializable {
    public static class DiaryStatus {
        public static final int UNREAD = 0;
        public static final int READ = 1;
    }

    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "letter";
        public static final String COLUMN_NAME_LETTER_ID = "letter_id";
        public static final String COLUMN_NAME_FROM_AUTHOR_ID = "from_author_id";
        public static final String COLUMN_NAME_TO_AUTHOR_ID = "to_author_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_WRITTEN_TIME = "written_time";
        public static final String COLUMN_NAME_STATUS = "status";
    }

    private String letter_id; // ex) unix time
    private String from_author_id;
    private String to_author_id;
    private String title;
    private String content;
    private String written_time;
    private int status; // ex) 0: unread, 1: read

    public String getLetter_id() {
        return letter_id;
    }

    public void setLetter_id(String letter_id) {
        this.letter_id = letter_id;
    }

    public String getFrom_author_id() {
        return from_author_id;
    }

    public void setFrom_author_id(String from_author_id) {
        this.from_author_id = from_author_id;
    }

    public String getTo_author_id() {
        return to_author_id;
    }

    public void setTo_author_id(String to_author_id) {
        this.to_author_id = to_author_id;
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

    public String getWritten_time() {
        return written_time;
    }

    public void setWritten_time(String written_time) {
        this.written_time = written_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
