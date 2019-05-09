package org.jerrioh.diary.dbmodel;

import android.provider.BaseColumns;

import java.io.Serializable;

public class Setting implements Serializable {

    public enum Key {
        FONT_SIZE("FONT_SIZE", "12"), // integer (sp)
        BACKGROUND_IMAGE("BACKGROUND_IMAGE", "no image path"), // path
        SCREEN_LOCK_USE("SCREEN_LOCK_USE", "0"), // 0 or 1
        SCREEN_LOCK_4DIGIT("SCREEN_LOCK_4DIGIT", ""), // ex: 1234
        DIARY_ALARM_USE("DIARY_ALARM_USE", "0"), // 0 or 1
        DIARY_ALARM_TIME("DIARY_ALARM_TIME", "22:00"), // ex: 22:00
        GROUP_INVITATION_USE("GROUP_INVITATION_USE", "1"); // 0 or 1

        Key(String key, String defaultValue) {
            this.KEY = key;
            this.DEFAULT_VALUE = defaultValue;
        }

        public String KEY;
        public String DEFAULT_VALUE;
    }

    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "setting";
        public static final String COLUMN_NAME_SETTING_KEY = "setting_key";
        public static final String COLUMN_NAME_SETTING_VALUE = "setting_value";
    }

    private String settingKey;
    private String settingValue;

    public Setting(String settingKey, String settingValue) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
    }

    public String getSettingKey() {
        return settingKey;
    }
    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }
    public String getSettingValue() {
        return settingValue;
    }
    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
