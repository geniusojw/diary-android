package org.jerrioh.diary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jerrioh.diary.dbmodel.Writing;

public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper instance;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "diary.db";

    public static final String SQL_CREATE_WRITING_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s INTEGER, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING" + ")",
                    Writing.TableDesc.TABLE_NAME,
                    Writing.TableDesc._ID,
                    Writing.TableDesc.COLUMN_NAME_WRITING_TYPE,
                    Writing.TableDesc.COLUMN_NAME_WRITING_DATE,
                    Writing.TableDesc.COLUMN_NAME_WRITER,
                    Writing.TableDesc.COLUMN_NAME_READER,
                    Writing.TableDesc.COLUMN_NAME_TITLE,
                    Writing.TableDesc.COLUMN_NAME_CONTENT);

    public static final String SQL_DELETE_WRITING_TABLE =
            String.format("DROP TABLE IF EXISTS %s",
                    Writing.TableDesc.TABLE_NAME);

    private DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WRITING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_WRITING_TABLE);
        this.onCreate(db);
    }
}
