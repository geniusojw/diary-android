package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class Theme implements Serializable {

    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "theme";
        public static final String COLUMN_NAME_THEME_NAME = "theme_name";
        public static final String COLUMN_NAME_PATTERN0 = "pattern0";
        public static final String COLUMN_NAME_PATTERN1 = "pattern1";
        public static final String COLUMN_NAME_PATTERN2 = "pattern2";
        public static final String COLUMN_NAME_PATTERN3 = "pattern3";
        public static final String COLUMN_NAME_BANNER_COLOR = "banner_color";
    }

    private String themeName;
    private String pattern0;
    private String pattern1;
    private String pattern2;
    private String pattern3;
    private String bannerColor;

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getPattern0() {
        return pattern0;
    }

    public void setPattern0(String pattern0) {
        this.pattern0 = pattern0;
    }

    public String getPattern1() {
        return pattern1;
    }

    public void setPattern1(String pattern1) {
        this.pattern1 = pattern1;
    }

    public String getPattern2() {
        return pattern2;
    }

    public void setPattern2(String pattern2) {
        this.pattern2 = pattern2;
    }

    public String getPattern3() {
        return pattern3;
    }

    public void setPattern3(String pattern3) {
        this.pattern3 = pattern3;
    }

    public String getBannerColor() {
        return bannerColor;
    }

    public void setBannerColor(String bannerColor) {
        this.bannerColor = bannerColor;
    }
}
