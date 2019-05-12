package org.jerrioh.diary.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.model.Setting;

public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper instance;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "diary.db";

    public static final String SQL_CREATE_AUTHOR_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING" + ")",
                    Author.TableDesc.TABLE_NAME,
                    Author.TableDesc._ID,
                    Author.TableDesc.COLUMN_NAME_AUTHOR_ID,
                    Author.TableDesc.COLUMN_NAME_AUTHOR_CODE,
                    Author.TableDesc.COLUMN_NAME_NICKNAME,
                    Author.TableDesc.COLUMN_NAME_DESCRIPTION,
                    Author.TableDesc.COLUMN_NAME_ACCOUNT_EMAIL,
                    Author.TableDesc.COLUMN_NAME_ACCOUNT_TOKEN);

    public static final String SQL_CREATE_DIARY_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s INTEGER, " +
                            "%s INTEGER" + ")",
                    Diary.TableDesc.TABLE_NAME,
                    Diary.TableDesc._ID,
                    Diary.TableDesc.COLUMN_NAME_DIARY_DATE,
                    Diary.TableDesc.COLUMN_NAME_TITLE,
                    Diary.TableDesc.COLUMN_NAME_CONTENT,
                    Diary.TableDesc.COLUMN_NAME_AUTHOR_DIARY_STATUS,
                    Diary.TableDesc.COLUMN_NAME_ACCOUNT_DIARY_STATUS);

    public static final String SQL_CREATE_LETTER_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s INTEGER" + ")",
                    Letter.TableDesc.TABLE_NAME,
                    Letter.TableDesc._ID,
                    Letter.TableDesc.COLUMN_NAME_LETTER_ID,
                    Letter.TableDesc.COLUMN_NAME_FROM_AUTHOR_ID,
                    Letter.TableDesc.COLUMN_NAME_TO_AUTHOR_ID,
                    Letter.TableDesc.COLUMN_NAME_TITLE,
                    Letter.TableDesc.COLUMN_NAME_CONTENT,
                    Letter.TableDesc.COLUMN_NAME_WRITTEN_TIME,
                    Letter.TableDesc.COLUMN_NAME_STATUS);

    public static final String SQL_CREATE_SETTING_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s STRING, " +
                            "%s STRING" + ")",
                    Setting.TableDesc.TABLE_NAME,
                    Setting.TableDesc._ID,
                    Setting.TableDesc.COLUMN_NAME_SETTING_KEY,
                    Setting.TableDesc.COLUMN_NAME_SETTING_VALUE);

    public static final String SQL_DELETE_AUTHOR_TABLE =
            String.format("DROP TABLE IF EXISTS %s",
                    Author.TableDesc.TABLE_NAME);

    public static final String SQL_DELETE_DIARY_TABLE =
            String.format("DROP TABLE IF EXISTS %s",
                    Diary.TableDesc.TABLE_NAME);

    public static final String SQL_DELETE_LETTER_TABLE =
            String.format("DROP TABLE IF EXISTS %s",
                    Letter.TableDesc.TABLE_NAME);

    public static final String SQL_DELETE_SETTING_TABLE =
            String.format("DROP TABLE IF EXISTS %s",
                    Setting.TableDesc.TABLE_NAME);

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
        db.execSQL(SQL_CREATE_AUTHOR_TABLE);
        db.execSQL(SQL_CREATE_DIARY_TABLE);
        db.execSQL(SQL_CREATE_LETTER_TABLE);
        db.execSQL(SQL_CREATE_SETTING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_AUTHOR_TABLE);
        db.execSQL(SQL_DELETE_DIARY_TABLE);
        db.execSQL(SQL_DELETE_LETTER_TABLE);
        db.execSQL(SQL_DELETE_SETTING_TABLE);
        this.onCreate(db);
    }
}