package org.jerrioh.diary.model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class AbstractDao {
    private static final String TAG = "AbstractDao";

    protected SQLiteOpenHelper dbHelper;

    protected AbstractDao(Context context) {
        this.dbHelper = DbHelper.getInstance(context);
    }

    protected SQLiteDatabase readableDb() {
        return dbHelper.getReadableDatabase();
    }

    protected SQLiteDatabase writableDb() {
        return dbHelper.getWritableDatabase();
    }

    protected boolean cursorHasJustOne(Cursor cursor) {
        if (!cursorIsNotNull(cursor)) {
            return false;
        }
        if (cursor.getCount() != 1) {
            Log.e(TAG, "Should be only one result. cursor.getCount() = " + cursor.getCount());
            return false;
        }
        return true;
    }

    protected boolean cursorIsNotNull(Cursor cursor) {
        if (cursor == null) {
            Log.d(TAG, "Cursor is null.");
            return false;
        }
        return true;
    }
}
