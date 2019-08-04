package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Property implements Serializable {

    public static class Config {
        public static final int FONT_SIZE_OFFSET = 10;
        public static final long SCREEN_LOCK_LOCK_MILLIS = TimeUnit.MINUTES.toMillis(1);
        public static final long GET_DIARY_GROUP_API_RETRY_MILLIS = TimeUnit.MINUTES.toMillis(1);
        public static final long GET_LETTERS_API_RETRY_MILLIS = TimeUnit.MINUTES.toMillis(1);
        public static final long SYNC_DIARIES_API_RETRY_MILLIS = TimeUnit.DAYS.toMillis(1);
        public static final long AUTO_DELETE_MILLIS = TimeUnit.DAYS.toMillis(30);
        public static final long AUTO_DELETE_CAUTION_MILLIS = TimeUnit.HOURS.toMillis(48);
        public static final int MAX_POST_COUNT = 30;
    }

    public enum Key {
        FONT_SIZE("FONT_SIZE", "3"), // 0 ~ 9
        SCREEN_LOCK_USE("SCREEN_LOCK_USE", "0"), // 0 or 1
        SCREEN_LOCK_4DIGIT("SCREEN_LOCK_4DIGIT", ""), // ex: 1234
        SCREEN_LOCK_LAST_USE_TIME("SCREEN_LOCK_LAST_USE_TIME", "0"), // ex: unix milliseconds time
        DIARY_ALARM_USE("DIARY_ALARM_USE", "0"), // 0 or 1
        DIARY_ALARM_TIME("DIARY_ALARM_TIME", "22:00"), // ex: 22:00
        GROUP_INVITATION_USE("GROUP_INVITATION_USE", "1"), // 0 or 1
        AUTO_DELETE_POST_USE("AUTO_DELETE_POST_USE", "1"), // 0 or 1
        AUTO_DELETE_LETTER_USE("AUTO_DELETE_LETTER_USE", "1"), // 0 or 1

        SYNC_ACCOUNT_DIARY_API_REQUEST_TIME("SYNC_ACCOUNT_DIARY_API_REQUEST_TIME", "0"), // unix milliseconds time
        GET_DIARY_GROUP_API_REQUEST_TIME("GET_DIARY_GROUP_API_REQUEST_TIME", "0"), // unix milliseconds time
        GET_LETTERS_API_REQUEST_TIME("GET_LETTERS_API_REQUEST_TIME", "0"), // unix milliseconds time
        SYNC_DIARIES_API_REQUEST_TIME("SYNC_DIARIES_API_REQUEST_TIME", "0"), // unix milliseconds time
        YESTERDAY_RECEIVER_ON("YESTERDAY_RECEIVER_ON", "0"), // 0 or 1

        DIARY_THEME("DIARY_THEME", "default - blue square"), // default or Theme Name
        DIARY_WRITE_MUSIC("DIARY_WRITE_MUSIC", "default - his day") // default or Music Name
        ;

        Key(String key, String defaultValue) {
            this.KEY = key;
            this.DEFAULT_VALUE = defaultValue;
        }

        public String KEY;
        public String DEFAULT_VALUE;
    }

    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "property";
        public static final String COLUMN_NAME_PROPERTY_KEY = "property_key";
        public static final String COLUMN_NAME_PROPERTY_VALUE = "property_value";
    }

    private String propertyKey;
    private String propertyValue;

    public Property(String propertyKey, String propertyValue) {
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
    }

    public String getPropertyKey() {
        return propertyKey;
    }
    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }
    public String getPropertyValue() {
        return propertyValue;
    }
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
