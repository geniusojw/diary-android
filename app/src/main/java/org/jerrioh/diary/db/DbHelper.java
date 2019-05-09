package org.jerrioh.diary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jerrioh.diary.dbmodel.Account;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.dbmodel.Letter;
import org.jerrioh.diary.dbmodel.Setting;

public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper instance;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "diary.db";

    public static final String SQL_CREATE_ACCOUNT_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING" + ")",
                    Account.TableDesc.TABLE_NAME,
                    Account.TableDesc._ID,
                    Account.TableDesc.COLUMN_NAME_USER_ID,
                    Account.TableDesc.COLUMN_NAME_TOKEN,
                    Account.TableDesc.COLUMN_NAME_NICKNAME,
                    Account.TableDesc.COLUMN_NAME_DESCRIPTION,
                    Account.TableDesc.COLUMN_NAME_UPDATE_TIME,
                    Account.TableDesc.COLUMN_NAME_NEXT_UPDATE_TIME);

    public static final String SQL_CREATE_DIARY_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s INTEGER" + ")",
                    Diary.TableDesc.TABLE_NAME,
                    Diary.TableDesc._ID,
                    Diary.TableDesc.COLUMN_NAME_WRITE_DAY,
                    Diary.TableDesc.COLUMN_NAME_WRITE_USER_ID,
                    Diary.TableDesc.COLUMN_NAME_TITLE,
                    Diary.TableDesc.COLUMN_NAME_CONTENT,
                    Diary.TableDesc.COLUMN_NAME_SERVER_SAVED);

    public static final String SQL_CREATE_LETTER_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s INTEGER, " +
                            "%s INTEGER" + ")",
                    Letter.TableDesc.TABLE_NAME,
                    Letter.TableDesc._ID,
                    Letter.TableDesc.COLUMN_NAME_WRITE_TIME,
                    Letter.TableDesc.COLUMN_NAME_WRITE_USER_ID,
                    Letter.TableDesc.COLUMN_NAME_READ_USER_ID,
                    Letter.TableDesc.COLUMN_NAME_TITLE,
                    Letter.TableDesc.COLUMN_NAME_CONTENT,
                    Letter.TableDesc.COLUMN_NAME_SERVER_SAVED,
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

    public static final String SQL_DELETE_ACCOUNT_TABLE =
            String.format("DROP TABLE IF EXISTS %s",
                    Account.TableDesc.TABLE_NAME);

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
        db.execSQL(SQL_CREATE_ACCOUNT_TABLE);
        db.execSQL(SQL_CREATE_DIARY_TABLE);
        db.execSQL(SQL_CREATE_LETTER_TABLE);
        db.execSQL(SQL_CREATE_SETTING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ACCOUNT_TABLE);
        db.execSQL(SQL_DELETE_DIARY_TABLE);
        db.execSQL(SQL_DELETE_LETTER_TABLE);
        db.execSQL(SQL_DELETE_SETTING_TABLE);
        this.onCreate(db);
    }
}
