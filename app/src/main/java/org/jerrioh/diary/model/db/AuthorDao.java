package org.jerrioh.diary.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.jerrioh.diary.model.Author;

public class AuthorDao extends AbstractDao {

    private static final String TABLE_NAME = Author.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Author.TableDesc.COLUMN_NAME_AUTHOR_ID,
            Author.TableDesc.COLUMN_NAME_AUTHOR_CODE,
            Author.TableDesc.COLUMN_NAME_NICKNAME,
            Author.TableDesc.COLUMN_NAME_DESCRIPTION,
            Author.TableDesc.COLUMN_NAME_ACCOUNT_EMAIL,
            Author.TableDesc.COLUMN_NAME_ACCOUNT_TOKEN
    };

    public AuthorDao(Context context) {
        super(context);
    }

    public Author getAuthor() {
        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, "1=1", new String[]{}, null, null, null);
        if (cursorHasJustOne(cursor)) {
            cursor.moveToFirst();
            return getAuthorOnCursor(cursor);
        } else {
            return null;
        }
    }

    public int updateAuthorCode(String authorCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Author.TableDesc.COLUMN_NAME_AUTHOR_CODE, authorCode);
        return writableDb().update(TABLE_NAME, contentValues, "1=1", new String[]{});
    }

    public int updateAccountEmailAndToken(String accountEmail, String accountToken) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Author.TableDesc.COLUMN_NAME_ACCOUNT_EMAIL, accountEmail);
        contentValues.put(Author.TableDesc.COLUMN_NAME_ACCOUNT_TOKEN, accountToken);
        return writableDb().update(TABLE_NAME, contentValues, "1=1", new String[]{});
    }

    public long insertAuthor(Author author) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Author.TableDesc.COLUMN_NAME_AUTHOR_ID, author.getAuthorId());
        contentValues.put(Author.TableDesc.COLUMN_NAME_AUTHOR_CODE, author.getAuthorCode());
        contentValues.put(Author.TableDesc.COLUMN_NAME_NICKNAME, author.getNickname());
        contentValues.put(Author.TableDesc.COLUMN_NAME_DESCRIPTION, author.getDescription());
        contentValues.put(Author.TableDesc.COLUMN_NAME_ACCOUNT_EMAIL, author.getAccountEmail());
        contentValues.put(Author.TableDesc.COLUMN_NAME_ACCOUNT_TOKEN, author.getAccountToken());
        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public int deleteAuthor() {
        return writableDb().delete(TABLE_NAME, "1=1", new String[]{});
    }

    private Author getAuthorOnCursor(Cursor cursor) {
        Author author = new Author();
        author.setAuthorId(cursor.getString(0));
        author.setAuthorCode(cursor.getString(1));
        author.setNickname(cursor.getString(2));
        author.setDescription(cursor.getString(3));
        author.setAccountEmail(cursor.getString(4));
        author.setAccountToken(cursor.getString(5));
        return author;
    }
}
