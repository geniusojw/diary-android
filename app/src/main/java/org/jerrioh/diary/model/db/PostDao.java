package org.jerrioh.diary.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDao extends AbstractDao {
    private static final String TAG = "PostDao";

    private static final String TABLE_NAME = Post.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Post.TableDesc.COLUMN_NAME_POST_ID,
            Post.TableDesc.COLUMN_AUTHOR_NICKNAME,
            Post.TableDesc.COLUMN_AUTHOR_CHOCOLATES,
            Post.TableDesc.COLUMN_AUTHOR_CONTENT,
            Post.TableDesc.COLUMN_AUTHOR_WRITTEN_TIME
    };

    public PostDao(Context context) {
        super(context);
    }

    public Post getPost(String postId) {

        String selection = Post.TableDesc.COLUMN_NAME_POST_ID + "=?";
        String[] args = { postId };

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null, "1");
        if (cursorHasJustOne(cursor)) {
            cursor.moveToFirst();
            return getPostOnCursor(cursor);
        } else {
            return null;
        }
    }

    public List<Post> getAllPosts() {
        String orderBy = Post.TableDesc.COLUMN_AUTHOR_WRITTEN_TIME + " DESC";

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, "1=1", new String[]{}, null, null, orderBy);
        List<Post> posts = new ArrayList<>();
        if (cursorIsNotNull(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    posts.add(getPostOnCursor(cursor));
                } while (cursor.moveToNext());
            }
        }
        return posts;
    }

    public long insertPost(Post post) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Post.TableDesc.COLUMN_NAME_POST_ID, post.getPostId());
        contentValues.put(Post.TableDesc.COLUMN_AUTHOR_NICKNAME, post.getAuthorNickname());
        contentValues.put(Post.TableDesc.COLUMN_AUTHOR_CHOCOLATES, post.getChocolates());
        contentValues.put(Post.TableDesc.COLUMN_AUTHOR_CONTENT, post.getContent());
        contentValues.put(Post.TableDesc.COLUMN_AUTHOR_WRITTEN_TIME, post.getWrittenTime());

        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public long updatePost(Post post) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Post.TableDesc.COLUMN_AUTHOR_NICKNAME, post.getAuthorNickname());
        contentValues.put(Post.TableDesc.COLUMN_AUTHOR_CHOCOLATES, post.getChocolates());
        contentValues.put(Post.TableDesc.COLUMN_AUTHOR_CONTENT, post.getContent());
        contentValues.put(Post.TableDesc.COLUMN_AUTHOR_WRITTEN_TIME, post.getWrittenTime());

        String selection = Post.TableDesc.COLUMN_NAME_POST_ID + "=?";
        String[] args = { post.getPostId() };

        return writableDb().update(TABLE_NAME, contentValues, selection, args);

    }

    public long deletePost(String postId) {
        String selection = Post.TableDesc.COLUMN_NAME_POST_ID + "=?";
        String[] args = { postId };
        return writableDb().delete(TABLE_NAME, selection, args);
    }

    public long deleteAllPosts() {
        return writableDb().delete(TABLE_NAME, "1=1", new String[]{});
    }

    private Post getPostOnCursor(Cursor cursor) {
        Post post = new Post();
        post.setPostId(cursor.getString(0));
        post.setAuthorNickname(cursor.getString(1));
        post.setChocolates(cursor.getInt(2));
        post.setContent(cursor.getString(3));
        post.setWrittenTime(cursor.getLong(4));
        return post;
    }
}
