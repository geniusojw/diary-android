package org.jerrioh.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.dbmodel.Write;

import java.util.ArrayList;
import java.util.List;

public class WriteDao extends AbstractDao {
    private static final String TAG = "WriteDao";

    private static final String TABLE_NAME = Write.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Write.TableDesc.COLUMN_NAME_WRITE_TYPE,
            Write.TableDesc.COLUMN_NAME_WRITE_DAY,
            Write.TableDesc.COLUMN_NAME_WRITE_USER_ID,
            Write.TableDesc.COLUMN_NAME_READ_USER_ID,
            Write.TableDesc.COLUMN_NAME_TITLE,
            Write.TableDesc.COLUMN_NAME_CONTENT
    };

    public WriteDao(Context context) {
        super(context);
    }

    public Write getTodayDiary(String today_yyyyMMdd) {
        String selection = Write.TableDesc.COLUMN_NAME_WRITE_TYPE + "=?" +
                " AND " + Write.TableDesc.COLUMN_NAME_WRITE_DAY + "=?" +
                " AND " + Write.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = { String.valueOf(Write.WriteType.DIARY), today_yyyyMMdd, Information.account.getUserId() };

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

    public List<Write> getMyPeriodDiary(String month_yyyyMM, String max_yyyyMMdd) {
        String selection = Write.TableDesc.COLUMN_NAME_WRITE_TYPE + "=?" +
                " AND " + Write.TableDesc.COLUMN_NAME_WRITE_DAY + " LIKE ?" +
                " AND " + Write.TableDesc.COLUMN_NAME_WRITE_DAY + " <=?" +
                " AND " + Write.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = {String.valueOf(Write.WriteType.DIARY), month_yyyyMM + "%", max_yyyyMMdd, Information.account.getUserId() };
        String orderBy = Write.TableDesc.COLUMN_NAME_WRITE_DAY + " DESC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getMyTotalDiary.");
            return null;
        }

        List<Write> writes = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                writes.add(getWriteOnCursor(cursor));
            } while (cursor.moveToNext());
        }
        return writes;
    }

    public List<Write> getMyTotalDiary() {
        String selection = Write.TableDesc.COLUMN_NAME_WRITE_TYPE + "=?" +
                " AND " + Write.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = {String.valueOf(Write.WriteType.DIARY), Information.account.getUserId() };
        String orderBy = Write.TableDesc.COLUMN_NAME_WRITE_DAY + " DESC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getMyTotalDiary.");
            return null;
        }

        List<Write> writes = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                writes.add(getWriteOnCursor(cursor));
            } while (cursor.moveToNext());
        }
        return writes;
    }

    public int updateTodayDiary(Write write) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Write.TableDesc.COLUMN_NAME_TITLE, write.getTitle());
        contentValues.put(Write.TableDesc.COLUMN_NAME_CONTENT, write.getContent());

        String selection = Write.TableDesc.COLUMN_NAME_WRITE_TYPE + "=?" +
                " AND " + Write.TableDesc.COLUMN_NAME_WRITE_DAY + "=?" +
                " AND " + Write.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = { String.valueOf(Write.WriteType.DIARY), write.getWriteDay(), Information.account.getUserId() };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public long insertWrite(Write write) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Write.TableDesc.COLUMN_NAME_WRITE_TYPE, write.getWriteType());
        contentValues.put(Write.TableDesc.COLUMN_NAME_WRITE_DAY, write.getWriteDay());
        contentValues.put(Write.TableDesc.COLUMN_NAME_WRITE_USER_ID, write.getWriteUserId());
        contentValues.put(Write.TableDesc.COLUMN_NAME_READ_USER_ID, write.getReadUserId());
        contentValues.put(Write.TableDesc.COLUMN_NAME_TITLE, write.getTitle());
        contentValues.put(Write.TableDesc.COLUMN_NAME_CONTENT, write.getContent());

        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    private Write getWriteOnCursor(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        }
        int writeType  = Integer.parseInt(cursor.getString(0));
        String writeDay  = cursor.getString(1);
        String writeUserId  = cursor.getString(2);
        String readUserId  = cursor.getString(3);
        String title = cursor.getString(4);
        String content  = cursor.getString(5);
        return new Write(writeType, writeDay, writeUserId, readUserId, title, content);
    }
}
