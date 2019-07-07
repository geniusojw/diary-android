package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class Post implements Serializable {

    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "post";
        public static final String COLUMN_NAME_POST_ID = "post_id";
        public static final String COLUMN_AUTHOR_NICKNAME = "author_nickname";
        public static final String COLUMN_AUTHOR_CHOCOLATES = "chocolates";
        public static final String COLUMN_AUTHOR_CONTENT = "content";
        public static final String COLUMN_AUTHOR_WRITTEN_TIME = "written_time";
    }

    private String postId;
    private String authorNickname;
    private int chocolates;
    private String content;
    private long writtenTime;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public int getChocolates() {
        return chocolates;
    }

    public void setChocolates(int chocolates) {
        this.chocolates = chocolates;
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
}
