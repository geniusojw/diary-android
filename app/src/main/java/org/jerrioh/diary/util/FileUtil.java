package org.jerrioh.diary.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {
    public static final String THEME_FOLDER = "themes";
    public static final String MUSIC_FOLDER = "musics";

    private static final String TAG = "FileUtil";

    public static void saveThemeDirectory(String themeName, Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(THEME_FOLDER, Context.MODE_PRIVATE);

        File file = new File(directory, themeName);
        file.mkdir();
    }

    // internal storage 에 저장한다.
    public static boolean saveThemePatternToStorage(String themeName, String fileName, byte[] fileBytes, Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(THEME_FOLDER, Context.MODE_PRIVATE);

        // /data/user/0/org.jerrioh.diary/app_themes/{fileName}
        File file = new File(directory, themeName + "/" + fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(fileBytes);
        } catch (Exception e) {
            Log.e(TAG, "saveToInternalStorage failed");
            return false;
        }
        return true;
    }

    public static Bitmap getBitmapFromStorage(String themeName, String fileName, Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(THEME_FOLDER, Context.MODE_PRIVATE);

        File file = new File(directory, themeName + "/" + fileName);
        //return BitmapFactory.decodeStream(new FileInputStream(file));
        BitmapFactory.Options options = new BitmapFactory.Options();

        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    // TODO 현재 삭제 안됨
    public static boolean deleteStorageFiles(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File imageDirectory = contextWrapper.getDir(THEME_FOLDER, Context.MODE_PRIVATE);
        File musicDirectory = contextWrapper.getDir(MUSIC_FOLDER, Context.MODE_PRIVATE);

        try {
            imageDirectory.delete();
            musicDirectory.delete();
        } catch (Exception e) {
            Log.e(TAG, "deleteStorageFiles failed");
            return false;
        }
        return true;
    }
}
