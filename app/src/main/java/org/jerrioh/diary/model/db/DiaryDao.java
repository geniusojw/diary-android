package org.jerrioh.diary.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.jerrioh.diary.model.Diary;

import java.util.ArrayList;
import java.util.List;

public class DiaryDao extends AbstractDao {
    private static final String TAG = "DiaryDao";

    private static final String TABLE_NAME = Diary.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Diary.TableDesc.COLUMN_NAME_DIARY_DATE,
            Diary.TableDesc.COLUMN_NAME_TITLE,
            Diary.TableDesc.COLUMN_NAME_CONTENT,
            Diary.TableDesc.COLUMN_NAME_AUTHOR_DIARY_STATUS,
            Diary.TableDesc.COLUMN_NAME_ACCOUNT_DIARY_STATUS
    };

    public DiaryDao(Context context) {
        super(context);
    }

    public Diary getDiary(String diaryDate) {
        String selection = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + "=?";
        String[] args = { diaryDate };

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null, "1");
        if (cursorHasJustOne(cursor)) {
            cursor.moveToFirst();
            return getDiaryOnCursor(cursor);
        } else {
            return null;
        }
    }

    public Diary getOneDiary(boolean descend) {
        String orderBy = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + (descend ? " DSC" : " ASC");

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, "1=1", new String[]{}, null, null, orderBy, "1");
        if (cursorHasJustOne(cursor)) {
            cursor.moveToFirst();
            return getDiaryOnCursor(cursor);
        } else {
            return null;
        }
    }

    public List<Diary> getMonthDiariesBeforeToday(String diaryDate_yyyyMM, String todayDate) {
        String selection = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + " LIKE ?"
                + " AND " + Diary.TableDesc.COLUMN_NAME_DIARY_DATE + " < ?";

        String[] args = { diaryDate_yyyyMM + "%", todayDate };
        String orderBy = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + " ASC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        List<Diary> diaries = new ArrayList<>();
        if (cursorIsNotNull(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    diaries.add(getDiaryOnCursor(cursor));
                } while (cursor.moveToNext());
            }
        }
        return diaries;
    }

    public List<Diary> getAllDiariesBeforeToday(String todayDate) {
        String selection = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + " < ?";
        String[] args = { todayDate };
        String orderBy = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + " ASC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        List<Diary> diaries = new ArrayList<>();
        if (cursorIsNotNull(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    diaries.add(getDiaryOnCursor(cursor));
                } while (cursor.moveToNext());
            }
        }
        return diaries;
    }

    public List<Diary> getAllDiaries() {
        String orderBy = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + " ASC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, "1=1", new String[]{}, null, null, orderBy);
        List<Diary> diaries = new ArrayList<>();
        if (cursorIsNotNull(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    diaries.add(getDiaryOnCursor(cursor));
                } while (cursor.moveToNext());
            }
        }
        return diaries;
    }

    public int updateDiaryContent(String diaryDate, String title, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Diary.TableDesc.COLUMN_NAME_TITLE, title);
        contentValues.put(Diary.TableDesc.COLUMN_NAME_CONTENT, content);

        String selection = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + "=?";
        String[] args = { diaryDate };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public int updateDiaryAuthorStatus(String diaryDate, int accountStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Diary.TableDesc.COLUMN_NAME_AUTHOR_DIARY_STATUS, accountStatus);

        String selection = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + "=?";
        String[] args = { diaryDate };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public int updateDiaryAccountStatus(String diaryDate, int accountStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Diary.TableDesc.COLUMN_NAME_ACCOUNT_DIARY_STATUS, accountStatus);

        String selection = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + "=?";
        String[] args = { diaryDate };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public int updateAllDiaryAccountStatus(int accountStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Diary.TableDesc.COLUMN_NAME_ACCOUNT_DIARY_STATUS, accountStatus);

        return writableDb().update(TABLE_NAME, contentValues, "1=1", new String[]{});
    }

    public long insertDiary(Diary diary) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Diary.TableDesc.COLUMN_NAME_DIARY_DATE, diary.getDiaryDate());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_TITLE, diary.getTitle());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_CONTENT, diary.getContent());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_AUTHOR_DIARY_STATUS, diary.getAuthorDiaryStatus());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_ACCOUNT_DIARY_STATUS, diary.getAccountDiaryStatus());

        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public long deleteDiary(String diaryDate) {
        String selection = Diary.TableDesc.COLUMN_NAME_DIARY_DATE + "=?";
        String[] args = { diaryDate };
        return writableDb().delete(TABLE_NAME, selection, args);
    }

    public long deleteAllDiaries() {
        return writableDb().delete(TABLE_NAME, "1=1", new String[]{});
    }

    private Diary getDiaryOnCursor(Cursor cursor) {
        Diary diary = new Diary();
        diary.setDiaryDate(cursor.getString(0));
        diary.setTitle(cursor.getString(1));
        diary.setContent(cursor.getString(2));
        diary.setAuthorDiaryStatus(cursor.getInt(3));
        diary.setAccountDiaryStatus(cursor.getInt(4));
        return diary;
    }
}
