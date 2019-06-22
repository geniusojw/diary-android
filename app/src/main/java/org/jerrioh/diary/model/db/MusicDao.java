package org.jerrioh.diary.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicDao extends AbstractDao {
    private static final String TAG = "MusicDao";

    private static final String TABLE_NAME = Music.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Music.TableDesc.COLUMN_NAME_MUSIC_NAME,
            Music.TableDesc.COLUMN_NAME_MUSIC_DATA
    };

    public MusicDao(Context context) {
        super(context);
    }

    public List<String> getAllMusicNames() {
        String orderBy = Music.TableDesc.COLUMN_NAME_MUSIC_NAME + " DESC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, "1=1", new String[]{}, null, null, orderBy);
        List<String> musicNames = new ArrayList<>();
        if (cursorIsNotNull(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    Music music = getMusicOnCursor(cursor);
                    musicNames.add(music.getMusicName());
                } while (cursor.moveToNext());
            }
        }
        return musicNames;
    }

    public Music getMusic(String musicName) {
        String selection = Music.TableDesc.COLUMN_NAME_MUSIC_NAME + "=?";
        String[] args = { musicName };

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null, "1");
        if (cursorHasJustOne(cursor)) {
            cursor.moveToFirst();
            return getMusicOnCursor(cursor);
        } else {
            return null;
        }
    }

    public long insertMusic(Music music) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Music.TableDesc.COLUMN_NAME_MUSIC_NAME, music.getMusicName());
        contentValues.put(Music.TableDesc.COLUMN_NAME_MUSIC_DATA, music.getMusicData());

        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public long deleteMusic(String musicName) {
        String selection = Music.TableDesc.COLUMN_NAME_MUSIC_NAME + "=?";
        String[] args = { musicName };
        return writableDb().delete(TABLE_NAME, selection, args);
    }

    public long deleteAllMusic() {
        return writableDb().delete(TABLE_NAME, "1=1", new String[]{});
    }

    private Music getMusicOnCursor(Cursor cursor) {
        Music music = new Music();
        music.setMusicName(cursor.getString(0));
        music.setMusicData(cursor.getString(1));
        return music;
    }
}
