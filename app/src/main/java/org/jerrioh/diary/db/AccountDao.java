package org.jerrioh.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.jerrioh.diary.dbmodel.Account;

public class AccountDao extends AbstractDao {
    private static final String TAG = "AccountDao";

    private static final String TABLE_NAME = Account.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Account.TableDesc.COLUMN_NAME_USER_ID,
            Account.TableDesc.COLUMN_NAME_TOKEN,
            Account.TableDesc.COLUMN_NAME_NICKNAME,
            Account.TableDesc.COLUMN_NAME_DESCRIPTION,
            Account.TableDesc.COLUMN_NAME_UPDATE_TIME,
            Account.TableDesc.COLUMN_NAME_NEXT_UPDATE_TIME
    };

    public AccountDao(Context context) {
        super(context);
    }

    public Account getMyAccount() {
        String selection = "1 = 1";
        String[] args = { };

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getMyAccount.");
            return null;
        }
        if (cursor.getCount() > 1) {
            Log.e(TAG, "Too many result. sql=getMyAccount. cursor.getCount()=" + cursor.getCount());
            return null;
        }
        cursor.moveToFirst();
        return getAccountOnCursor(cursor);
    }

    public int updateAccount(String email, String token, String nickname, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Account.TableDesc.COLUMN_NAME_USER_ID, email);
        contentValues.put(Account.TableDesc.COLUMN_NAME_TOKEN, token);
        contentValues.put(Account.TableDesc.COLUMN_NAME_NICKNAME, nickname);
        contentValues.put(Account.TableDesc.COLUMN_NAME_DESCRIPTION, description);

        String selection = "1 = 1";
        String[] args = { };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public int updateToken(String token) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Account.TableDesc.COLUMN_NAME_TOKEN, token);

        String selection = "1 = 1";
        String[] args = { };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public int updateNextUpdateTime(String nextUpdateTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Account.TableDesc.COLUMN_NAME_NEXT_UPDATE_TIME, nextUpdateTime);

        String selection = "1 = 1";
        String[] args = { };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public long insertMyAccount(Account account) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Account.TableDesc.COLUMN_NAME_USER_ID, account.getUserId());
        contentValues.put(Account.TableDesc.COLUMN_NAME_TOKEN, account.getToken());
        contentValues.put(Account.TableDesc.COLUMN_NAME_NICKNAME, account.getNickname());
        contentValues.put(Account.TableDesc.COLUMN_NAME_DESCRIPTION, account.getDescription());
        contentValues.put(Account.TableDesc.COLUMN_NAME_UPDATE_TIME, account.getUpdateTime());
        contentValues.put(Account.TableDesc.COLUMN_NAME_NEXT_UPDATE_TIME, account.getNextUpdateTime());

        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    private Account getAccountOnCursor(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        }
        String userId = cursor.getString(0);
        String token  = cursor.getString(1);
        String nickname  = cursor.getString(2);
        String description  = cursor.getString(3);
        String updateTime = cursor.getString(4);
        String nextUpdateTime = cursor.getString(5);
        return new Account(userId, token, nickname, description, updateTime, nextUpdateTime);
    }
}
