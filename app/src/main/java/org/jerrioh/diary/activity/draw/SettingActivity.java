package org.jerrioh.diary.activity.draw;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.db.SettingDao;
import org.jerrioh.diary.model.Setting;
import org.jerrioh.diary.util.AuthorUtil;

public class SettingActivity extends CommonActionBarActivity {
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setCommonToolBar("Setting");

        setMemberServices();
    }

    private void setMemberServices() {
        this.setAlias();
        this.setFontSize();
        this.setBackgroundImage();
        this.setScreenLock();
        this.setDiaryAlarm();
        this.setDiaryGroupInvitation();
        this.setDataReset();
    }

    private void setAlias() {
        TextView aliasView = findViewById(R.id.text_view_setting_alias);
        aliasView.setOnClickListener(v -> {
            Toast.makeText(this, "개발이 안된 부분", Toast.LENGTH_SHORT).show();
        });
    }

    private void setFontSize() {
        SettingDao settingDao = new SettingDao(this);
        String fontSize = insertDefaultIfEmptyAndGet(Setting.Key.FONT_SIZE, settingDao);
    }

    private void setBackgroundImage() {
        SettingDao settingDao = new SettingDao(this);
        String fontSize = insertDefaultIfEmptyAndGet(Setting.Key.FONT_SIZE, settingDao);
    }

    private void setScreenLock() {
        SettingDao settingDao = new SettingDao(this);
        String screenLockUse = insertDefaultIfEmptyAndGet(Setting.Key.SCREEN_LOCK_USE, settingDao);
        Switch switchScreenLock = findViewById(R.id.switch_setting_screen_lock);
        switchScreenLock.setChecked(Integer.parseInt(screenLockUse) == 1);
        switchScreenLock.setOnClickListener(v -> {
            String lockUse = insertDefaultIfEmptyAndGet(Setting.Key.SCREEN_LOCK_USE, settingDao);
            if (Integer.parseInt(lockUse) == 1) {
                new SettingDao(this).updateSetting(Setting.Key.SCREEN_LOCK_USE, "0");
                switchScreenLock.setChecked(false);
            } else {
                new SettingDao(this).updateSetting(Setting.Key.SCREEN_LOCK_USE, "1");
                switchScreenLock.setChecked(true);
            }
        });
    }

    private void setDiaryAlarm() {
        SettingDao settingDao = new SettingDao(this);
        String diaryAlarmUse = insertDefaultIfEmptyAndGet(Setting.Key.DIARY_ALARM_USE, settingDao);
        Switch switchDiaryAlarm = findViewById(R.id.switch_setting_diary_alarm);
        switchDiaryAlarm.setChecked(Integer.parseInt(diaryAlarmUse) == 1);
        switchDiaryAlarm.setOnClickListener(v -> {
            String alarmUse = insertDefaultIfEmptyAndGet(Setting.Key.DIARY_ALARM_USE, settingDao);
            if (Integer.parseInt(alarmUse) == 1) {
                new SettingDao(this).updateSetting(Setting.Key.DIARY_ALARM_USE, "0");
                switchDiaryAlarm.setChecked(false);
            } else {
                new SettingDao(this).updateSetting(Setting.Key.DIARY_ALARM_USE, "1");
                switchDiaryAlarm.setChecked(true);
            }
        });
    }

    private void setDiaryGroupInvitation() {
        SettingDao settingDao = new SettingDao(this);
        String groupInvitationUse = insertDefaultIfEmptyAndGet(Setting.Key.GROUP_INVITATION_USE, settingDao);
        Switch switchInvitation = findViewById(R.id.switch_setting_group_invitation);
        switchInvitation.setChecked(Integer.parseInt(groupInvitationUse) == 1);
        switchInvitation.setOnClickListener(v -> {
            String invitationUse = insertDefaultIfEmptyAndGet(Setting.Key.GROUP_INVITATION_USE, settingDao);
            if (Integer.parseInt(invitationUse) == 1) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Disable diary group invitation")
                        .setMessage("You can decline the invitation without disable it.\nDo you really want to disable it?")
                        .setPositiveButton("Disable", (dialog, which) -> {
                            new SettingDao(this).updateSetting(Setting.Key.GROUP_INVITATION_USE, "0");
                            switchInvitation.setChecked(false);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.cancel();
                            switchInvitation.setChecked(true);
                        });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            } else {
                new SettingDao(this).updateSetting(Setting.Key.GROUP_INVITATION_USE, "1");
                switchInvitation.setChecked(true);
            }
        });
    }

    private void setDataReset() {
        LinearLayout dataResetLayout = findViewById(R.id.linear_layout_setting_data_reset);
        dataResetLayout.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("Data Reset")
                    .setMessage("Really? Really? Really?")
                    .setPositiveButton("DELETE ALL", (dialog, which) -> {
                        AuthorUtil.resetAuthorData(this);
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        });
    }

    private String insertDefaultIfEmptyAndGet(Setting.Key settingKey, SettingDao settingDao) {
        String settingValue = settingDao.getSettingValue(settingKey);
        if (settingValue == null) {
            settingDao.insertSetting(settingKey, settingKey.DEFAULT_VALUE);
            settingValue = settingKey.DEFAULT_VALUE;
        }
        return settingValue;
    }
}
