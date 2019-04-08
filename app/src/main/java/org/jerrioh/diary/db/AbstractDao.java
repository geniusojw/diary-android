package org.jerrioh.diary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class AbstractDao {
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
}
