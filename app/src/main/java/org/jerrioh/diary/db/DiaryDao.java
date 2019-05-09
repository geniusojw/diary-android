package org.jerrioh.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.jerrioh.diary.util.CurrentAccountUtil;
import org.jerrioh.diary.dbmodel.Diary;

import java.util.ArrayList;
import java.util.List;

public class DiaryDao extends AbstractDao {
    private static final String TAG = "DiaryDao";

    private static final String TABLE_NAME = Diary.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Diary.TableDesc.COLUMN_NAME_WRITE_DAY,
            Diary.TableDesc.COLUMN_NAME_WRITE_USER_ID,
            Diary.TableDesc.COLUMN_NAME_TITLE,
            Diary.TableDesc.COLUMN_NAME_CONTENT,
            Diary.TableDesc.COLUMN_NAME_SERVER_SAVED
    };

    public DiaryDao(Context context) {
        super(context);
    }

    public Diary getTodayDiary(String today_yyyyMMdd) {
        String selection = Diary.TableDesc.COLUMN_NAME_WRITE_DAY + "=?" +
                " AND " + Diary.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = { today_yyyyMMdd, CurrentAccountUtil.getAccount().getUserId() };

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getTodayDiary.");
            return null;
        }
        if (cursor.getCount() > 1) {
            Log.e(TAG, "Too many result. sql=getTodayDiary. cursor.getCount()=" + cursor.getCount());
            return null;
        }
        cursor.moveToFirst();
        return getWriteOnCursor(cursor);
    }

    public Diary getFirstDiary() {
        String selection = "1 = 1";
        String[] args = {};
        String orderBy = Diary.TableDesc.COLUMN_NAME_WRITE_DAY + " ASC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy, "1");
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getFirstDiary.");
            return null;
        }
        if (cursor.getCount() > 1) {
            Log.e(TAG, "Too many result. sql=getFirstDiary. cursor.getCount()=" + cursor.getCount());
            return null;
        }
        cursor.moveToFirst();
        return getWriteOnCursor(cursor);
    }

    public List<Diary> getMyPeriodDiary(String month_yyyyMM, String max_yyyyMMdd) {
        String selection = Diary.TableDesc.COLUMN_NAME_WRITE_DAY + " LIKE ?" +
                " AND " + Diary.TableDesc.COLUMN_NAME_WRITE_DAY + " <=?" +
                " AND " + Diary.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = { month_yyyyMM + "%", max_yyyyMMdd, CurrentAccountUtil.getAccount().getUserId() };
        String orderBy = Diary.TableDesc.COLUMN_NAME_WRITE_DAY + " ASC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getMyTotalDiary.");
            return null;
        }

        List<Diary> diaries = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                diaries.add(getWriteOnCursor(cursor));
            } while (cursor.moveToNext());
        }
        return diaries;
    }

    public List<Diary> getMyTotalDiary() {
        String selection = Diary.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = { CurrentAccountUtil.getAccount().getUserId() };
        String orderBy = Diary.TableDesc.COLUMN_NAME_WRITE_DAY + " ASC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getMyTotalDiary.");
            return null;
        }

        List<Diary> diaries = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                diaries.add(getWriteOnCursor(cursor));
            } while (cursor.moveToNext());
        }
        return diaries;
    }

    public int updateTodayDiary(Diary diary) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Diary.TableDesc.COLUMN_NAME_TITLE, diary.getTitle());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_CONTENT, diary.getContent());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_SERVER_SAVED, diary.getServerSaved());

        String selection = Diary.TableDesc.COLUMN_NAME_WRITE_DAY + "=?" +
                " AND " + Diary.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = { diary.getWriteDay(), CurrentAccountUtil.getAccount().getUserId() };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public long insertWrite(Diary diary) {
        if (getTodayDiary(diary.getWriteDay()) != null) {
            Log.d(TAG, "Diary already exists");
            return 0L;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Diary.TableDesc.COLUMN_NAME_WRITE_DAY, diary.getWriteDay());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_WRITE_USER_ID, diary.getWriteUserId());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_TITLE, diary.getTitle());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_CONTENT, diary.getContent());
        contentValues.put(Diary.TableDesc.COLUMN_NAME_SERVER_SAVED, diary.getServerSaved());

        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public long removeMyDiary(String writeday) {
        String selection = Diary.TableDesc.COLUMN_NAME_WRITE_DAY + "=?" +
                " AND " + Diary.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = { writeday, CurrentAccountUtil.getAccount().getUserId() };

        return writableDb().delete(TABLE_NAME, selection, args);
    }

    public long removeAllDiary() {
        String selection = "1 = 1";
        String[] args = { };

        return writableDb().delete(TABLE_NAME, selection, args);
    }

    private Diary getWriteOnCursor(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        }
        String writeDay  = cursor.getString(0);
        String writeUserId  = cursor.getString(1);
        String title = cursor.getString(2);
        String content  = cursor.getString(3);
        int serverSaved = cursor.getInt(4);
        return new Diary(writeDay, writeUserId, title, content, serverSaved);
    }
}
