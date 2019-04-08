package org.jerrioh.diary.dbmodel;

import android.provider.BaseColumns;

import org.jerrioh.diary.config.Information;

import java.io.Serializable;

public class Writing implements Serializable {

    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "WRITING";
        public static final String COLUMN_NAME_WRITING_TYPE = "WRITING_TYPE";
        public static final String COLUMN_NAME_WRITING_DATE = "WRITING_DATE";
        public static final String COLUMN_NAME_WRITER = "WRITER";
        public static final String COLUMN_NAME_READER = "READER";
        public static final String COLUMN_NAME_TITLE = "TITLE";
        public static final String COLUMN_NAME_CONTENT = "CONTENT";
    }

    public static class WritingType {
        public static final int DIARY = 1;
        public static final int LETTER = 2;
        public static final int SYSTEM_NOTICE = 9;
    }

    private int writingType;
    private String writingDate;
    private String writer;
    private String reader;
    private String title;
    private String content;

    public Writing(int writingType, String writingDate, String writer, String reader, String title, String content) {
        this.writingType = writingType;
        this.writingDate = writingDate;
        this.writer = writer;
        this.reader = reader;
        this.title = title;
        this.content = content;
    }

    public int getWritingType() {
        return writingType;
    }
    public String getWritingDate() {
        return writingDate;
    }
    public String getWriter() {
        return writer;
    }
    public String getReader() {
        return reader;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }

    // 수정가능 항목
    public void setReader(String reader) {
        if (isMyWriting()) this.reader = reader;
    }
    public void setTitle(String title) {
        if (isMyWriting()) this.title = title;
    }
    public void setContent(String content) {
        if (isMyWriting()) this.content = content;
    }

    private boolean isMyWriting() {
        return writer.equals(Information.ClientInformation.USER_ID);
    }
}
