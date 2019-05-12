package org.jerrioh.diary.model;

import android.provider.BaseColumns;

import java.io.Serializable;

public class Diary implements Serializable {
    public static class DiaryStatus {
        public static final int UNSAVED = 0;
        public static final int SAVED = 1;
        public static final int UNSAVED_CONFLICT = 2; // accountDiaryStatus에만 사용.
    }

    public static class TableDesc implements BaseColumns {
        public static final String TABLE_NAME = "diary";
        public static final String COLUMN_NAME_DIARY_DATE = "diary_date";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_AUTHOR_DIARY_STATUS = "author_diary_status";
        public static final String COLUMN_NAME_ACCOUNT_DIARY_STATUS = "account_diary_status";
    }

    private String diaryDate; // ex) 20190430
    private String title;
    private String content;
    private int authorDiaryStatus; // 0:unsaved, 1:saved
    private int accountDiaryStatus; // 0:unsaved, 1:saved, 2:unsaved-conflict  이메일 로그인 시에만 유효한 값. 로그아웃시 모두 0으로 초기화

    public String getDiaryDate() {
        return diaryDate;
    }

    public void setDiaryDate(String diaryDate) {
        this.diaryDate = diaryDate;
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

    public int getAuthorDiaryStatus() {
        return authorDiaryStatus;
    }

    public void setAuthorDiaryStatus(int authorDiaryStatus) {
        this.authorDiaryStatus = authorDiaryStatus;
    }

    public int getAccountDiaryStatus() {
        return accountDiaryStatus;
    }

    public void setAccountDiaryStatus(int accountDiaryStatus) {
        this.accountDiaryStatus = accountDiaryStatus;
    }
}
