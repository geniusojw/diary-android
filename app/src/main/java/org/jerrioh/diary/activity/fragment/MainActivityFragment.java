package org.jerrioh.diary.activity.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import org.jerrioh.diary.R;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.util.DateUtil;

public class MainActivityFragment extends Fragment {

    protected  void setDiaryWriteButton(boolean enable) {
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
        } else {
            writeButton.setVisibility(View.GONE);
            writeButton.setEnabled(false);
            writeButton.setClickable(false);
        }
    }
}
