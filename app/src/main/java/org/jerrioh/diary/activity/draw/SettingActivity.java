package org.jerrioh.diary.activity.draw;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.PropertyDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.PropertyUtil;

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
        PropertyDao propertyDao = new PropertyDao(this);
        String fontSize = PropertyUtil.getProperty(Property.Key.FONT_SIZE, propertyDao);
    }

    private void setBackgroundImage() {
        PropertyDao propertyDao = new PropertyDao(this);
        String fontSize = PropertyUtil.getProperty(Property.Key.FONT_SIZE, propertyDao);
    }

    private void setScreenLock() {
        PropertyDao propertyDao = new PropertyDao(this);
        String screenLockUse = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_USE, propertyDao);
        Switch switchScreenLock = findViewById(R.id.switch_setting_screen_lock);
        switchScreenLock.setChecked(Integer.parseInt(screenLockUse) == 1);
        switchScreenLock.setOnClickListener(v -> {
            String lockUse = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_USE, propertyDao);
            if (Integer.parseInt(lockUse) == 1) {
                new PropertyDao(this).updateProperty(Property.Key.SCREEN_LOCK_USE, "0");
                switchScreenLock.setChecked(false);
            } else {
                new PropertyDao(this).updateProperty(Property.Key.SCREEN_LOCK_USE, "1");
                switchScreenLock.setChecked(true);
            }
        });
    }

    private void setDiaryAlarm() {
        PropertyDao propertyDao = new PropertyDao(this);
        String diaryAlarmUse = PropertyUtil.getProperty(Property.Key.DIARY_ALARM_USE, propertyDao);
        Switch switchDiaryAlarm = findViewById(R.id.switch_setting_diary_alarm);
        switchDiaryAlarm.setChecked(Integer.parseInt(diaryAlarmUse) == 1);
        switchDiaryAlarm.setOnClickListener(v -> {
            String alarmUse = PropertyUtil.getProperty(Property.Key.DIARY_ALARM_USE, propertyDao);
            if (Integer.parseInt(alarmUse) == 1) {
                new PropertyDao(this).updateProperty(Property.Key.DIARY_ALARM_USE, "0");
                switchDiaryAlarm.setChecked(false);
            } else {
                new PropertyDao(this).updateProperty(Property.Key.DIARY_ALARM_USE, "1");
                switchDiaryAlarm.setChecked(true);
            }
        });
    }

    private void setDiaryGroupInvitation() {
        PropertyDao propertyDao = new PropertyDao(this);
        String groupInvitationUse = PropertyUtil.getProperty(Property.Key.GROUP_INVITATION_USE, propertyDao);
        Switch switchInvitation = findViewById(R.id.switch_setting_group_invitation);
        switchInvitation.setChecked(Integer.parseInt(groupInvitationUse) == 1);
        switchInvitation.setOnClickListener(v -> {
            String invitationUse = PropertyUtil.getProperty(Property.Key.GROUP_INVITATION_USE, propertyDao);
            if (Integer.parseInt(invitationUse) == 1) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Disable diary group beInvited")
                        .setMessage("You can decline the beInvited without disable it.\nDo you really want to disable it?")
                        .setPositiveButton("Disable", (dialog, which) -> {
                            new PropertyDao(this).updateProperty(Property.Key.GROUP_INVITATION_USE, "0");
                            switchInvitation.setChecked(false);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.cancel();
                            switchInvitation.setChecked(true);
                        });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            } else {
                new PropertyDao(this).updateProperty(Property.Key.GROUP_INVITATION_USE, "1");
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
}
