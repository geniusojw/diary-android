package org.jerrioh.diary.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.Theme;
import org.jerrioh.diary.model.db.PropertyDao;
import org.jerrioh.diary.model.db.ThemeDao;

public class ThemeUtil {

    public static BitmapDrawable getBitmapDrawablePattern(AppCompatActivity activity, int patternIndex) {
        return getBitmapDrawablePattern(activity, activity.getResources(), patternIndex);
    }

    public static BitmapDrawable getBitmapDrawablePattern(Fragment fragment, int patternIndex) {
        return getBitmapDrawablePattern(fragment.getActivity(), fragment.getResources(), patternIndex);
    }

    private static BitmapDrawable getBitmapDrawablePattern(Context context, Resources resources, int patternIndex) {
        int drawablePattern;
        switch (patternIndex) {
            case 0: drawablePattern = R.drawable.pattern0; break;
            case 1: drawablePattern = R.drawable.pattern1; break;
            case 2: drawablePattern = R.drawable.pattern2; break;
            case 3: drawablePattern = R.drawable.pattern3; break;
            default: drawablePattern = R.drawable.pattern0; break;
        }
        Bitmap defaultBitmap = BitmapFactory.decodeResource(resources, drawablePattern);

        String themeName = PropertyUtil.getProperty(Property.Key.DIARY_THEME, context);

        if (!Property.Key.DIARY_THEME.DEFAULT_VALUE.equals(themeName)) {
            ThemeDao themeDao = new ThemeDao(context);
            Theme theme = themeDao.getTheme(themeName);

            byte[] data = null;
            if (patternIndex == 0) {
                data = Base64.decode(theme.getPattern0(), Base64.DEFAULT);
            } else if (patternIndex == 1) {
                data = Base64.decode(theme.getPattern1(), Base64.DEFAULT);
            } else if (patternIndex == 2) {
                data = Base64.decode(theme.getPattern2(), Base64.DEFAULT);
            } else if (patternIndex == 3) {
                data = Base64.decode(theme.getPattern3(), Base64.DEFAULT); // logically dead code
            }

            if (data != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap otherThemeBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                defaultBitmap = Bitmap.createScaledBitmap(otherThemeBitmap, defaultBitmap.getWidth(), defaultBitmap.getHeight(), false);
            }
        }

        BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, defaultBitmap);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        return bitmapDrawable;
    }

    public static int getBannerColor(Context context) {
        String themeName = PropertyUtil.getProperty(Property.Key.DIARY_THEME, context);

        if (Property.Key.DIARY_THEME.DEFAULT_VALUE.equals(themeName)) {
            return context.getResources().getColor(R.color.carribeanSky);
        } else {
            ThemeDao themeDao = new ThemeDao(context);
            Theme theme = themeDao.getTheme(themeName);
            String bannerColor = theme.getBannerColor();
            return Color.parseColor(bannerColor);
        }
    }
}
