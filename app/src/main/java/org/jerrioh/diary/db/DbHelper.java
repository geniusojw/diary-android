package org.jerrioh.diary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jerrioh.diary.dbmodel.Account;
import org.jerrioh.diary.dbmodel.Write;

public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper instance;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "diary.db";

    public static final String SQL_CREATE_WRITE_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s INTEGER, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s STRING, " +
                            "%s INTEGER" + ")",
                    Write.TableDesc.TABLE_NAME,
                    Write.TableDesc._ID,
                    Write.TableDesc.COLUMN_NAME_WRITE_TYPE,
                    Write.TableDesc.COLUMN_NAME_WRITE_DAY,
                    Write.TableDesc.COLUMN_NAME_WRITE_USER_ID,
                    Write.TableDesc.COLUMN_NAME_READ_USER_ID,
                    Write.TableDesc.COLUMN_NAME_TITLE,
                    Write.TableDesc.COLUMN_NAME_CONTENT,
                    Write.TableDesc.COLUMN_NAME_SERVER_SAVED);

    public static final String SQL_CREATE_ACCOUNT_TABLE =
            String.format("CREATE TABLE IF NOT EXISTS " +
                            "%s (" +
                            "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
                    Account.TableDesc.COLUMN_NAME_UPDATE_TIME,
                    Account.TableDesc.COLUMN_NAME_NEXT_UPDATE_TIME);

    public static final String SQL_DELETE_WRITE_TABLE =
            String.format("DROP TABLE IF EXISTS %s",
                    Write.TableDesc.TABLE_NAME);

    public static final String SQL_DELETE_ACCOUNT_TABLE =
            String.format("DROP TABLE IF EXISTS %s",
                    Account.TableDesc.TABLE_NAME);

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
        db.execSQL(SQL_CREATE_WRITE_TABLE);
        db.execSQL(SQL_CREATE_ACCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_WRITE_TABLE);
        db.execSQL(SQL_DELETE_ACCOUNT_TABLE);
        this.onCreate(db);
    }
}
