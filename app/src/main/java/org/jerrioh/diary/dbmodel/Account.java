package org.jerrioh.diary.dbmodel;

import android.provider.BaseColumns;

import org.jerrioh.diary.config.Information;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {
    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_TOKEN = "token";
        public static final String COLUMN_NAME_NICKNAME = "nickname";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_UPDATE_TIME = "update_time";
        public static final String COLUMN_NAME_NEXT_UPDATE_TIME = "next_update_time";
    }

    public Account(String userId, String token, String nickname, String description, String updateTime, String nextUpdateTime) {
        this.userId = userId;
        this.token = token;
        this.nickname = nickname;
        this.description = description;
        this.updateTime = updateTime;
        this.nextUpdateTime = nextUpdateTime;
    }

    private String userId; // ex) 20190430
    private String token;
    private String nickname;
    private String description;
    private String updateTime;
    private String nextUpdateTime;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
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
    public String getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    public String getNextUpdateTime() {
        return nextUpdateTime;
    }
    public void setNextUpdateTime(String nextUpdateTime) {
        this.nextUpdateTime = nextUpdateTime;
    }
}
