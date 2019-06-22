package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.View;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.DiaryWriteStartPopActivity;
import org.jerrioh.diary.activity.main.LetterWriteActivity;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.Theme;
import org.jerrioh.diary.model.db.PropertyDao;
import org.jerrioh.diary.model.db.ThemeDao;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.FileUtil;
import org.jerrioh.diary.util.PropertyUtil;

public class MainActivityFragment extends Fragment {

    public static final int BUTTON_TYPE_WRITE_DIARY = 0;
    public static final int BUTTON_TYPE_WRITE_LETTER = 1;

    protected  void setDiaryWriteButton(boolean enable, int buttonType) {
        View writeButton = getActivity().findViewById(R.id.floating_action_button_write_diary);

        boolean writeButtonEnable = false;
        if (enable) {
            if (Integer.parseInt(DateUtil.getHHmmss()) >= Constants.PROHIBIT_DIARY_WRITE_HHMMSS) {
                writeButtonEnable = true;
            }
        }

        if (writeButtonEnable) {
            writeButton.setVisibility(View.VISIBLE);
            writeButton.setEnabled(true);
            writeButton.setClickable(true);

            if (buttonType == BUTTON_TYPE_WRITE_DIARY) {
                writeButton.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), DiaryWriteStartPopActivity.class);
                    startActivity(intent);
                });
                ((FloatingActionButton) writeButton).setImageResource(R.drawable.ic_edit_black_48dp);

            } else if (buttonType == BUTTON_TYPE_WRITE_LETTER) {
                writeButton.setOnClickListener(view -> {
                    startActivity(new Intent(getActivity(), LetterWriteActivity.class));
                });
                ((FloatingActionButton) writeButton).setImageResource(R.drawable.ic_reply_black_24dp);

            } else {
                throw new RuntimeException("buttonType error");
            }

        } else {
            writeButton.setVisibility(View.GONE);
            writeButton.setEnabled(false);
            writeButton.setClickable(false);
        }
    }
}
