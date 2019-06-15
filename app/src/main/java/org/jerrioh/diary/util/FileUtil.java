package org.jerrioh.diary.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import org.jerrioh.diary.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    public static final String IMAGE_FOLDER = "images";
    public static final String MUSIC_FOLDER = "musics";

    private static final String TAG = "FileUtil";

    // internal storage 에 저장한다.
    public static boolean saveToStorage(String folderName, String fileName, byte[] fileBytes, Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(folderName, Context.MODE_PRIVATE);

        // /data/user/0/org.jerrioh.diary/app_images/
        File file = new File(directory, fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(fileBytes);
        } catch (Exception e) {
            Log.e(TAG, "saveToInternalStorage failed");
            return false;
        }
        return true;
    }

    public static Bitmap getBitmapFromStorage(String folderName, String fileName, Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(folderName, Context.MODE_PRIVATE);

        File file = new File(directory, fileName);
        //return BitmapFactory.decodeStream(new FileInputStream(file));
        BitmapFactory.Options options = new BitmapFactory.Options();

        return BitmapFactory.decodeFile(file.getPath(), options);
    }
}
