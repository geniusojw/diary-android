package org.jerrioh.diary.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.jerrioh.diary.model.Theme;

import java.util.ArrayList;
import java.util.List;

public class ThemeDao extends AbstractDao {
    private static final String TAG = "ThemeDao";

    private static final String TABLE_NAME = Theme.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Theme.TableDesc.COLUMN_NAME_THEME_NAME,
            Theme.TableDesc.COLUMN_NAME_PATTERN0,
            Theme.TableDesc.COLUMN_NAME_PATTERN1,
            Theme.TableDesc.COLUMN_NAME_PATTERN2,
            Theme.TableDesc.COLUMN_NAME_PATTERN3
    };

    public ThemeDao(Context context) {
        super(context);
    }

    public List<String> getAllThemeNames() {
        String orderBy = Theme.TableDesc.COLUMN_NAME_THEME_NAME + " DESC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, "1=1", new String[]{}, null, null, orderBy);
        List<String> themeNames = new ArrayList<>();
        if (cursorIsNotNull(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    Theme theme = getThemeOnCursor(cursor);
                    themeNames.add(theme.getThemeName());
                } while (cursor.moveToNext());
            }
        }
        return themeNames;
    }

    public Theme getTheme(String themeName) {
        String selection = Theme.TableDesc.COLUMN_NAME_THEME_NAME + "=?";
        String[] args = { themeName };

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null, "1");
        if (cursorHasJustOne(cursor)) {
            cursor.moveToFirst();
            return getThemeOnCursor(cursor);
        } else {
            return null;
        }
    }

    public long insertTheme(Theme theme) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Theme.TableDesc.COLUMN_NAME_THEME_NAME, theme.getThemeName());
        contentValues.put(Theme.TableDesc.COLUMN_NAME_PATTERN0, theme.getPattern0());
        contentValues.put(Theme.TableDesc.COLUMN_NAME_PATTERN1, theme.getPattern1());
        contentValues.put(Theme.TableDesc.COLUMN_NAME_PATTERN2, theme.getPattern2());
        contentValues.put(Theme.TableDesc.COLUMN_NAME_PATTERN3, theme.getPattern3());

        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public long deleteTheme(String themeName) {
        String selection = Theme.TableDesc.COLUMN_NAME_THEME_NAME + "=?";
        String[] args = { themeName };
        return writableDb().delete(TABLE_NAME, selection, args);
    }

    public long deleteAllTheme() {
        return writableDb().delete(TABLE_NAME, "1=1", new String[]{});
    }

    private Theme getThemeOnCursor(Cursor cursor) {
        Theme theme = new Theme();
        theme.setThemeName(cursor.getString(0));
        theme.setPattern0(cursor.getString(1));
        theme.setPattern1(cursor.getString(2));
        theme.setPattern2(cursor.getString(3));
        theme.setPattern3(cursor.getString(4));
        return theme;
    }
}
