package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class DiaryGroup implements Serializable {
    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "diary_group";
        public static final String COLUMN_NAME_DIARY_GROUP_ID = "diary_group_id";
        public static final String COLUMN_NAME_DIARY_GROUP_NAME = "diary_group_name";
        public static final String COLUMN_NAME_HOST_AUTHOR_ID = "host_author_id";
        public static final String COLUMN_NAME_KEYWORD = "keyword";
        public static final String COLUMN_NAME_CURRENT_AUTHOR_COUNT = "current_author_count";
        public static final String COLUMN_NAME_MAX_AUTHOR_COUNT = "max_author_count";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_LANGUAGE = "language";
        public static final String COLUMN_NAME_TIME_ZONE_ID = "time_zone_id";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_END_TIME = "end_time";
    }

    private long diaryGroupId;
    private String diaryGroupName;
    private String hostAuthorId;
    private String keyword;
    private int currentAuthorCount;
    private int maxAuthorCount;
    private String country;
    private String language;
    private String timeZoneId;
    private long startTime;
    private long endTime;

    public long getDiaryGroupId() {
        return diaryGroupId;
    }

    public void setDiaryGroupId(long diaryGroupId) {
        this.diaryGroupId = diaryGroupId;
    }

    public String getDiaryGroupName() {
        return diaryGroupName;
    }

    public void setDiaryGroupName(String diaryGroupName) {
        this.diaryGroupName = diaryGroupName;
    }

    public String getHostAuthorId() {
        return hostAuthorId;
    }

    public void setHostAuthorId(String hostAuthorId) {
        this.hostAuthorId = hostAuthorId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getCurrentAuthorCount() {
        return currentAuthorCount;
    }

    public void setCurrentAuthorCount(int currentAuthorCount) {
        this.currentAuthorCount = currentAuthorCount;
    }

    public int getMaxAuthorCount() {
        return maxAuthorCount;
    }

    public void setMaxAuthorCount(int maxAuthorCount) {
        this.maxAuthorCount = maxAuthorCount;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
