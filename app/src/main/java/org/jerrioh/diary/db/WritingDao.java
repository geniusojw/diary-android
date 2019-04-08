package org.jerrioh.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.dbmodel.Writing;

import java.util.ArrayList;
import java.util.List;

public class WritingDao extends AbstractDao {
    private static final String TAG = "WritingDao";

    private static final String TABLE_NAME = Writing.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Writing.TableDesc.COLUMN_NAME_WRITING_TYPE,
            Writing.TableDesc.COLUMN_NAME_WRITING_DATE,
            Writing.TableDesc.COLUMN_NAME_WRITER,
            Writing.TableDesc.COLUMN_NAME_READER,
            Writing.TableDesc.COLUMN_NAME_TITLE,
            Writing.TableDesc.COLUMN_NAME_CONTENT
    };

    public WritingDao(Context context) {
        super(context);
    }

    public Writing getTodayDiary() {
        String selection = Writing.TableDesc.COLUMN_NAME_WRITING_TYPE + "=?" +
                " AND " + Writing.TableDesc.COLUMN_NAME_WRITING_DATE + "=?" +
                " AND " + Writing.TableDesc.COLUMN_NAME_WRITER + "=?";
        String[] args = { String.valueOf(Writing.WritingType.DIARY), Information.ClientInformation.TODAY, Information.ClientInformation.USER_ID };

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
        return getWritingOnCursor(cursor);
    }

    public List<Writing> getMyTotalDiary() {
        String selection = Writing.TableDesc.COLUMN_NAME_WRITING_TYPE + "=?" +
                " AND " + Writing.TableDesc.COLUMN_NAME_WRITER + "=?";
        String[] args = { String.valueOf(Writing.WritingType.DIARY), Information.ClientInformation.USER_ID };
        String orderBy = Writing.TableDesc.COLUMN_NAME_WRITING_DATE + " DESC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getMyTotalDiary.");
            return null;
        }

        List<Writing> writings = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                writings.add(getWritingOnCursor(cursor));
            } while (cursor.moveToNext());
        }
        return writings;
    }

    public int updateTodayDiary(Writing writing) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Writing.TableDesc.COLUMN_NAME_TITLE, writing.getTitle());
        contentValues.put(Writing.TableDesc.COLUMN_NAME_CONTENT, writing.getContent());

        String selection = Writing.TableDesc.COLUMN_NAME_WRITING_TYPE + "=?" +
                " AND " + Writing.TableDesc.COLUMN_NAME_WRITING_DATE + "=?" +
                " AND " + Writing.TableDesc.COLUMN_NAME_WRITER + "=?";
        String[] args = { String.valueOf(Writing.WritingType.DIARY), Information.ClientInformation.TODAY, Information.ClientInformation.USER_ID };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public long insertWriting(Writing writing) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Writing.TableDesc.COLUMN_NAME_WRITING_TYPE, writing.getWritingType());
        contentValues.put(Writing.TableDesc.COLUMN_NAME_WRITING_DATE, writing.getWritingDate());
        contentValues.put(Writing.TableDesc.COLUMN_NAME_WRITER, writing.getWriter());
        contentValues.put(Writing.TableDesc.COLUMN_NAME_READER, writing.getReader());
        contentValues.put(Writing.TableDesc.COLUMN_NAME_TITLE, writing.getTitle());
        contentValues.put(Writing.TableDesc.COLUMN_NAME_CONTENT, writing.getContent());

        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    private Writing getWritingOnCursor(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        }
        int writingType  = Integer.parseInt(cursor.getString(0));
        String writingDate  = cursor.getString(1);
        String writer  = cursor.getString(2);
        String reader  = cursor.getString(3);
        String title = cursor.getString(4);
        String content  = cursor.getString(5);
        return new Writing(writingType, writingDate, writer, reader, title, content);
    }
}
