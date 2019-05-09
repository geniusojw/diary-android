package org.jerrioh.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.jerrioh.diary.util.CurrentAccountUtil;
import org.jerrioh.diary.dbmodel.Letter;

import java.util.ArrayList;
import java.util.List;

public class LetterDao extends AbstractDao {
    private static final String TAG = "LetterDao";

    private static final String TABLE_NAME = Letter.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Letter.TableDesc.COLUMN_NAME_WRITE_TIME,
            Letter.TableDesc.COLUMN_NAME_WRITE_USER_ID,
            Letter.TableDesc.COLUMN_NAME_READ_USER_ID,
            Letter.TableDesc.COLUMN_NAME_TITLE,
            Letter.TableDesc.COLUMN_NAME_CONTENT,
            Letter.TableDesc.COLUMN_NAME_SERVER_SAVED,
            Letter.TableDesc.COLUMN_NAME_STATUS
    };

    public LetterDao(Context context) {
        super(context);
    }

    public Letter getLetter(String writeTime, String writeUserId, String readUserId) {
        String selection = Letter.TableDesc.COLUMN_NAME_WRITE_TIME + "=?" +
                " AND " + Letter.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?" +
                " AND " + Letter.TableDesc.COLUMN_NAME_READ_USER_ID + "=?";
        String[] args = { writeTime, writeUserId, readUserId };

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getLetter.");
            return null;
        }
        if (cursor.getCount() > 1) {
            Log.e(TAG, "Too many result. sql=getLetter. cursor.getCount()=" + cursor.getCount());
            return null;
        }
        cursor.moveToFirst();
        return getLetterOnCursor(cursor);
    }

    public List<Letter> getTotalLetterToMe() {
        String selection = Letter.TableDesc.COLUMN_NAME_READ_USER_ID + "=?";
        String[] args = { CurrentAccountUtil.getAccount().getUserId() };
        String orderBy = Letter.TableDesc.COLUMN_NAME_WRITE_TIME + " DESC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getTotalLetterToMe.");
            return null;
        }

        List<Letter> letters = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                letters.add(getLetterOnCursor(cursor));
            } while (cursor.moveToNext());
        }
        return letters;
    }

    public long insertLetter(Letter letter) {
        if (getLetter(letter.getWriteTime(), letter.getWriteUserId(), letter.getReadUserId()) != null) {
            Log.d(TAG, "Letter already exists");
            return 0L;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Letter.TableDesc.COLUMN_NAME_WRITE_TIME, letter.getWriteTime());
        contentValues.put(Letter.TableDesc.COLUMN_NAME_WRITE_USER_ID, letter.getWriteUserId());
        contentValues.put(Letter.TableDesc.COLUMN_NAME_READ_USER_ID, letter.getReadUserId());
        contentValues.put(Letter.TableDesc.COLUMN_NAME_TITLE, letter.getTitle());
        contentValues.put(Letter.TableDesc.COLUMN_NAME_CONTENT, letter.getContent());
        contentValues.put(Letter.TableDesc.COLUMN_NAME_SERVER_SAVED, letter.getServerSaved());
        contentValues.put(Letter.TableDesc.COLUMN_NAME_STATUS, letter.getStatus());

        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public int updateLetterStatus(Letter letter) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Letter.TableDesc.COLUMN_NAME_SERVER_SAVED, letter.getServerSaved());
        contentValues.put(Letter.TableDesc.COLUMN_NAME_STATUS, letter.getStatus());

        String selection = Letter.TableDesc.COLUMN_NAME_WRITE_TIME + "=?" +
                " AND " + Letter.TableDesc.COLUMN_NAME_WRITE_USER_ID + "=?";
        String[] args = { letter.getWriteTime(), letter.getWriteUserId() };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public long removeAllLetter() {
        String selection = "1 = 1";
        String[] args = { };

        return writableDb().delete(TABLE_NAME, selection, args);
    }

    private Letter getLetterOnCursor(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        }
        String writeTime  = cursor.getString(0);
        String writeUserId  = cursor.getString(1);
        String readUserId  = cursor.getString(2);
        String title = cursor.getString(3);
        String content  = cursor.getString(4);
        int serverSaved = cursor.getInt(5);
        int status = cursor.getInt(6);
        return new Letter(writeTime, writeUserId, readUserId, title, content, serverSaved, status);
    }
}
