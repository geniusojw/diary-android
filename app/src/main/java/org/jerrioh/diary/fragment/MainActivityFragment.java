package org.jerrioh.diary.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import org.jerrioh.diary.R;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.util.DateUtil;

public class MainActivityFragment extends Fragment {

    protected  void setDiaryWriteButton(boolean enable) {
        View writeButton = getActivity().findViewById(R.id.write_diary_button);

        boolean writeButtonEnable = false;
        if (enable) {
            String hhmmss = DateUtil.getHHmmss().substring(0, 6);
            if (Integer.parseInt(hhmmss) >= Constants.DIARY_WRITE_BUTTON_HHMMSS) {
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
