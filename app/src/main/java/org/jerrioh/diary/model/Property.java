package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class Property implements Serializable {

    public enum Key {
        FONT_SIZE("FONT_SIZE", "12"), // integer (sp)
        SCREEN_LOCK_USE("SCREEN_LOCK_USE", "0"), // 0 or 1
        SCREEN_LOCK_4DIGIT("SCREEN_LOCK_4DIGIT", ""), // ex: 1234
        DIARY_ALARM_USE("DIARY_ALARM_USE", "0"), // 0 or 1
        DIARY_ALARM_TIME("DIARY_ALARM_TIME", "22:00"), // ex: 22:00
        GROUP_INVITATION_USE("GROUP_INVITATION_USE", "1"), // 0 or 1

        LATEST_INVITATION_TIME("LATEST_INVITATION_TIME", "0"), // unix milliseconds time
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
