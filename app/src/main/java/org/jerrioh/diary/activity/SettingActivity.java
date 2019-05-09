package org.jerrioh.diary.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Switch;

import org.jerrioh.diary.R;
import org.jerrioh.diary.db.SettingDao;
import org.jerrioh.diary.dbmodel.Setting;

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
        SettingDao settingDao = new SettingDao(this);
        String fontSize = insertDefaultIfEmptyAndGet(Setting.Key.FONT_SIZE, settingDao);
        String backgroundImage = insertDefaultIfEmptyAndGet(Setting.Key.BACKGROUND_IMAGE, settingDao);

        // screen lock
        String screenLockUse = insertDefaultIfEmptyAndGet(Setting.Key.SCREEN_LOCK_USE, settingDao);
        Switch switchScreenLock = findViewById(R.id.switch_screenlock);
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

        // diary alarm
        String diaryAlarmUse = insertDefaultIfEmptyAndGet(Setting.Key.DIARY_ALARM_USE, settingDao);
        Switch switchDiaryAlarm = findViewById(R.id.switch_diary_alarm);
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

        // diary group invitation
        String groupInvitationUse = insertDefaultIfEmptyAndGet(Setting.Key.GROUP_INVITATION_USE, settingDao);
        Switch switchInvitation = findViewById(R.id.switch_group_invitation);
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

    private String insertDefaultIfEmptyAndGet(Setting.Key settingKey, SettingDao settingDao) {
        String settingValue = settingDao.getSettingValue(settingKey);
        if (settingValue == null) {
            settingDao.insertSetting(settingKey, settingKey.DEFAULT_VALUE);
            settingValue = settingKey.DEFAULT_VALUE;
        }
        return settingValue;
    }
}
