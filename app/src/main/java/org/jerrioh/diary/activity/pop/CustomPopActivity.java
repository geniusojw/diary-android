package org.jerrioh.diary.activity.pop;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

public abstract class CustomPopActivity extends Activity {

    protected void setWindowAttribute(float widthRatio, float heightRatio, int xOffset, int yOffset) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int)(width * widthRatio), (int)(height * heightRatio));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = xOffset;
        params.y = yOffset;
        params.dimAmount = 0.6f;

        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    protected void setWindowAttribute(float widthRatio, float heightRatio) {
        setWindowAttribute(widthRatio, heightRatio, 0, -20);
    }
}
