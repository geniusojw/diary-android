package org.jerrioh.diary.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.DiaryGroup;

import java.util.ArrayList;
import java.util.List;

public class DiaryGroupDao extends AbstractDao {
    private static final String TAG = "DiaryGroupDao";

    private static final String TABLE_NAME = DiaryGroup.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            DiaryGroup.TableDesc.COLUMN_NAME_DIARY_GROUP_ID,
            DiaryGroup.TableDesc.COLUMN_NAME_DIARY_GROUP_NAME,
            DiaryGroup.TableDesc.COLUMN_NAME_HOST_AUTHOR_ID,
            DiaryGroup.TableDesc.COLUMN_NAME_KEYWORD,
            DiaryGroup.TableDesc.COLUMN_NAME_CURRENT_AUTHOR_COUNT,
            DiaryGroup.TableDesc.COLUMN_NAME_MAX_AUTHOR_COUNT,
            DiaryGroup.TableDesc.COLUMN_NAME_COUNTRY,
            DiaryGroup.TableDesc.COLUMN_NAME_LANGUAGE,
            DiaryGroup.TableDesc.COLUMN_NAME_TIME_ZONE_ID,
            DiaryGroup.TableDesc.COLUMN_NAME_START_TIME,
            DiaryGroup.TableDesc.COLUMN_NAME_END_TIME
    };

    public DiaryGroupDao(Context context) {
        super(context);
    }

    public DiaryGroup getDiaryGroup() {
        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, "1=1", new String[]{}, null, null, null);
        if (cursorHasJustOne(cursor)) {
            cursor.moveToFirst();
            return getDiaryGroupOnCursor(cursor);
        } else {
            return null;
        }
    }

    public long insertDiaryGroup(DiaryGroup diaryGroup) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_DIARY_GROUP_ID, diaryGroup.getDiaryGroupId());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_DIARY_GROUP_NAME, diaryGroup.getDiaryGroupName());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_HOST_AUTHOR_ID, diaryGroup.getHostAuthorId());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_KEYWORD, diaryGroup.getKeyword());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_CURRENT_AUTHOR_COUNT, diaryGroup.getCurrentAuthorCount());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_MAX_AUTHOR_COUNT, diaryGroup.getMaxAuthorCount());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_COUNTRY, diaryGroup.getCountry());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_LANGUAGE, diaryGroup.getLanguage());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_TIME_ZONE_ID, diaryGroup.getTimeZoneId());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_START_TIME, diaryGroup.getStartTime());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_END_TIME, diaryGroup.getEndTime());
        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public long updateDiaryGroup(DiaryGroup diaryGroup) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_DIARY_GROUP_ID, diaryGroup.getDiaryGroupId());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_DIARY_GROUP_NAME, diaryGroup.getDiaryGroupName());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_HOST_AUTHOR_ID, diaryGroup.getHostAuthorId());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_KEYWORD, diaryGroup.getKeyword());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_CURRENT_AUTHOR_COUNT, diaryGroup.getCurrentAuthorCount());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_MAX_AUTHOR_COUNT, diaryGroup.getMaxAuthorCount());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_COUNTRY, diaryGroup.getCountry());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_LANGUAGE, diaryGroup.getLanguage());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_TIME_ZONE_ID, diaryGroup.getTimeZoneId());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_START_TIME, diaryGroup.getStartTime());
        contentValues.put(DiaryGroup.TableDesc.COLUMN_NAME_END_TIME, diaryGroup.getEndTime());
        return writableDb().update(TABLE_NAME, contentValues, "1=1", new String[]{});
    }

    public int deleteDiaryGroup() {
        return writableDb().delete(TABLE_NAME, "1=1", new String[]{});
    }

    private DiaryGroup getDiaryGroupOnCursor(Cursor cursor) {
        DiaryGroup diaryGroup = new DiaryGroup();
        diaryGroup.setDiaryGroupId(cursor.getLong(0));
        diaryGroup.setDiaryGroupName(cursor.getString(1));
        diaryGroup.setHostAuthorId(cursor.getString(2));
        diaryGroup.setKeyword(cursor.getString(3));
        diaryGroup.setCurrentAuthorCount(cursor.getInt(4));
        diaryGroup.setMaxAuthorCount(cursor.getInt(5));
        diaryGroup.setCountry(cursor.getString(6));
        diaryGroup.setLanguage(cursor.getString(7));
        diaryGroup.setTimeZoneId(cursor.getString(8));
        diaryGroup.setStartTime(cursor.getLong(9));
        diaryGroup.setEndTime(cursor.getLong(10));
        return diaryGroup;
    }
}
