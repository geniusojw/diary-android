package org.jerrioh.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.jerrioh.diary.dbmodel.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SettingDao extends AbstractDao {
    private static final String TAG = "SettingDao";

    private static final String TABLE_NAME = Setting.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Setting.TableDesc.COLUMN_NAME_SETTING_KEY,
            Setting.TableDesc.COLUMN_NAME_SETTING_VALUE
    };

    public SettingDao(Context context) {
        super(context);
    }

    public String getSettingValue(Setting.Key settingKey) {
        String selection = Setting.TableDesc.COLUMN_NAME_SETTING_KEY + "=?";
        String[] args = { settingKey.KEY };

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getSettingValue.");
            return null;
        }
        if (cursor.getCount() > 1) {
            Log.e(TAG, "Too many result. sql=getSettingValue. cursor.getCount()=" + cursor.getCount());
            return null;
        }
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        }
        return cursor.getString(1);
    }

    public List<Setting> getAllSetting() {
        String selection = "1 = 1";
        String[] args = { };
        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null);
        if (cursor == null) {
            Log.d(TAG, "cursor is null. sql=getAllSetting.");
            return null;
        }

        List<Setting> settings = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {

                String key = cursor.getString(0);
                String value = cursor.getString(1);
                settings.add(new Setting(key, value));
            } while (cursor.moveToNext());
        }
        return settings;
    }


    public long insertSetting(Setting.Key settingKey, String value) {
        Log.d(TAG, "insert setting KEY" + settingKey.KEY + ", value" + value);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Setting.TableDesc.COLUMN_NAME_SETTING_KEY, settingKey.KEY);
        contentValues.put(Setting.TableDesc.COLUMN_NAME_SETTING_VALUE, value);
        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public long updateSetting(Setting.Key settingKey, String value) {
        Log.d(TAG, "update setting KEY" + settingKey.KEY + ", value" + value);

        String selection = Setting.TableDesc.COLUMN_NAME_SETTING_KEY + "=?";
        String[] args = { settingKey.KEY };

        ContentValues contentValues = new ContentValues();
        contentValues.put(Setting.TableDesc.COLUMN_NAME_SETTING_VALUE, value);

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public long removeSetting(Setting.Key settingKey) {
        String selection = Setting.TableDesc.COLUMN_NAME_SETTING_KEY + "=?";
        String[] args = { settingKey.KEY };

        return writableDb().delete(TABLE_NAME, selection, args);
    }

    public long removeAllSetting() {
        String selection = "1 = 1";
        String[] args = { };

        return writableDb().delete(TABLE_NAME, selection, args);
    }
}
