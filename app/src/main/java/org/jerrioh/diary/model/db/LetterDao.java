package org.jerrioh.diary.model.db;

import android.content.Context;
import android.database.Cursor;

import org.jerrioh.diary.model.Letter;

import java.util.ArrayList;
import java.util.List;

public class LetterDao extends AbstractDao {
    private static final String TAG = "LetterDao";

    private static final String TABLE_NAME = Letter.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Letter.TableDesc.COLUMN_NAME_LETTER_ID,
            Letter.TableDesc.COLUMN_NAME_FROM_AUTHOR_ID,
            Letter.TableDesc.COLUMN_NAME_TO_AUTHOR_ID,
            Letter.TableDesc.COLUMN_NAME_TITLE,
            Letter.TableDesc.COLUMN_NAME_CONTENT,
            Letter.TableDesc.COLUMN_NAME_WRITTEN_TIME,
            Letter.TableDesc.COLUMN_NAME_STATUS
    };

    public LetterDao(Context context) {
        super(context);
    }

    public List<Letter> getLettersToMe(String authorId) {
        String selection = Letter.TableDesc.COLUMN_NAME_TO_AUTHOR_ID + "=?";
        String[] args = { authorId };
        String orderBy = Letter.TableDesc.COLUMN_NAME_WRITTEN_TIME + " DESC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        List<Letter> letters = new ArrayList<>();
        if (cursorIsNotNull(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    letters.add(getLetterOnCursor(cursor));
                } while (cursor.moveToNext());
            }
        }
        return letters;
    }

    public List<Letter> getLettersToOthers(String authorId) {
        String selection = Letter.TableDesc.COLUMN_NAME_TO_AUTHOR_ID + "!=?";
        String[] args = { authorId };
        String orderBy = Letter.TableDesc.COLUMN_NAME_WRITTEN_TIME + " DESC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, orderBy);
        List<Letter> letters = new ArrayList<>();
        if (cursorIsNotNull(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    letters.add(getLetterOnCursor(cursor));
                } while (cursor.moveToNext());
            }
        }
        return letters;
    }

    public long deleteAllLetters() {
        return writableDb().delete(TABLE_NAME, "1=1", new String[]{});
    }

    private Letter getLetterOnCursor(Cursor cursor) {
        Letter letter = new Letter();
        letter.setLetter_id(cursor.getString(0));
        letter.setFrom_author_id(cursor.getString(1));
        letter.setTo_author_id(cursor.getString(2));
        letter.setTitle(cursor.getString(3));
        letter.setContent(cursor.getString(4));
        letter.setWritten_time(cursor.getString(5));
        letter.setStatus(cursor.getInt(6));
        return letter;
    }
}
