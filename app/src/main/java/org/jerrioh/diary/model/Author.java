package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class Author implements Serializable {
    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "author";
        public static final String COLUMN_NAME_AUTHOR_ID = "author_id";
        public static final String COLUMN_NAME_AUTHOR_CODE = "author_code";
        public static final String COLUMN_NAME_NICKNAME = "nickname";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_ACCOUNT_EMAIL = "account_email";
        public static final String COLUMN_NAME_ACCOUNT_TOKEN = "account_token";

    }

    private String authorId;
    private String authorCode;
    private String nickname;
    private String description;
    private String accountEmail;
    private String accountToken;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorCode() {
        return authorCode;
    }

    public void setAuthorCode(String authorCode) {
        this.authorCode = authorCode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }
}
