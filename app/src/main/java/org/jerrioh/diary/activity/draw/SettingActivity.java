package org.jerrioh.diary.activity.draw;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.adapter.SettingSpinnerAdapter;
import org.jerrioh.diary.activity.main.LetterWriteActivity;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.model.db.MusicDao;
import org.jerrioh.diary.model.db.ThemeDao;
import org.jerrioh.diary.noti.AlarmBroadcastReceiver;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.PropertyDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.PropertyUtil;
import org.jerrioh.diary.util.ReceiverUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SettingActivity extends CommonActionBarActivity {
    private static final String TAG = "SettingActivity";

    private PropertyDao propertyDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setCommonToolBar("Setting");

        propertyDao = new PropertyDao(this);
        setMemberServices();
    }

    private void setMemberServices() {
        this.setAlias();
        this.setDiaryTheme();
        this.setDiaryWriteMusic();
        this.setFontSize();
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

    private void setDiaryTheme() {
        LinearLayout diaryThemeView = findViewById(R.id.linear_layout_setting_diary_theme);
        Spinner themeSpinner = findViewById(R.id.spinner_setting_diary_theme);

        diaryThemeView.setOnClickListener(v -> {
            themeSpinner.performClick();
        });

        ThemeDao themeDao = new ThemeDao(this);

        List<String> themeList = new ArrayList<>();
        themeList.add(Property.Key.DIARY_THEME.DEFAULT_VALUE);
        themeList.addAll(themeDao.getAllThemeNames());

        PropertyDao propertyDao = new PropertyDao(SettingActivity.this);
        String themeName = PropertyUtil.getProperty(Property.Key.DIARY_THEME, propertyDao);
        int selection = themeList.indexOf(themeName);
        themeSpinner.setSelection(selection);

        SettingSpinnerAdapter adapter = new SettingSpinnerAdapter(this, themeList, selection);
        themeSpinner.setAdapter(adapter);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PropertyUtil.setProperty(Property.Key.DIARY_THEME, themeList.get(position), propertyDao);
                Toast.makeText(SettingActivity.this,"선택된 아이템이 있따", Toast.LENGTH_SHORT).show();
                adapter.setSelection(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SettingActivity.this,"선택된 아이템이 없다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDiaryWriteMusic() {
        LinearLayout musicLayout = findViewById(R.id.linear_layout_setting_diary_write_music);
        Spinner musicSpinner = findViewById(R.id.spinner_setting_diary_diary_music);

        musicLayout.setOnClickListener(v -> {
            musicSpinner.performClick();
        });

        MusicDao musicDao = new MusicDao(this);

        List<String> musicList = new ArrayList<>();
        musicList.add(Property.Key.DIARY_WRITE_MUSIC.DEFAULT_VALUE);
        musicList.addAll(musicDao.getAllMusicNames());

        PropertyDao propertyDao = new PropertyDao(SettingActivity.this);
        String musicName = PropertyUtil.getProperty(Property.Key.DIARY_WRITE_MUSIC, propertyDao);
        int selection = musicList.indexOf(musicName);
        musicSpinner.setSelection(selection);

        SettingSpinnerAdapter adapter = new SettingSpinnerAdapter(this, musicList, selection);
        musicSpinner.setAdapter(adapter);
        musicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PropertyDao propertyDao = new PropertyDao(SettingActivity.this);
                PropertyUtil.setProperty(Property.Key.DIARY_WRITE_MUSIC, musicList.get(position), propertyDao);
                adapter.setSelection(position);
                Toast.makeText(SettingActivity.this,"선택된 아이템이 있따", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SettingActivity.this,"선택된 아이템이 없다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFontSize() {
        String fontSize = PropertyUtil.getProperty(Property.Key.FONT_SIZE, propertyDao);
    }

    private void setScreenLock() {
        String screenLockUse = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_USE, propertyDao);
        Switch switchScreenLock = findViewById(R.id.switch_setting_screen_lock);
        switchScreenLock.setChecked(Integer.parseInt(screenLockUse) == 1);
        switchScreenLock.setOnClickListener(v -> {
            String lockUse = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_USE, propertyDao);
            if (Integer.parseInt(lockUse) == 1) {
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_USE, "0", propertyDao);
                switchScreenLock.setChecked(false);
            } else {
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_USE, "1", propertyDao);
                switchScreenLock.setChecked(true);
            }
        });
    }

    private void setDiaryAlarm() {
        Switch switchDiaryAlarm = findViewById(R.id.switch_setting_diary_alarm);
        TextView textDiaryAlarm = findViewById(R.id.text_view_diary_alarm);

        int diaryAlarmUse = Integer.parseInt(PropertyUtil.getProperty(Property.Key.DIARY_ALARM_USE, propertyDao));
        String diaryAlarmTime = PropertyUtil.getProperty(Property.Key.DIARY_ALARM_TIME, propertyDao);

        if (diaryAlarmUse == 1) {
            switchDiaryAlarm.setChecked(true);
            textDiaryAlarm.setText("설정된 알림시간 : " + diaryAlarmTime);
        } else {
            switchDiaryAlarm.setChecked(false);
            textDiaryAlarm.setText("It helps you to remember to write diary");
        }

        switchDiaryAlarm.setOnClickListener(v -> {
            int alarmUse = Integer.parseInt(PropertyUtil.getProperty(Property.Key.DIARY_ALARM_USE, propertyDao));
            if (alarmUse == 1) {
                PropertyUtil.setProperty(Property.Key.DIARY_ALARM_USE, "0", propertyDao);
                switchDiaryAlarm.setChecked(false);
                textDiaryAlarm.setText("It helps you to remember to write diary");
                ReceiverUtil.setAlarmReceiverOff(this);

                Toast.makeText(SettingActivity.this, "이제 일기알림이 동작하지 않습니다.", Toast.LENGTH_SHORT).show();

            } else {
                String alarmTime = PropertyUtil.getProperty(Property.Key.DIARY_ALARM_TIME, propertyDao);
                String[] time = alarmTime.split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);

                TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                        PropertyUtil.setProperty(Property.Key.DIARY_ALARM_USE, "1", propertyDao);
                        PropertyUtil.setProperty(Property.Key.DIARY_ALARM_TIME, time, propertyDao);
                        switchDiaryAlarm.setChecked(true);
                        textDiaryAlarm.setText("설정된 알림시간 : " + time);
                        ReceiverUtil.setAlarmReceiverOn(SettingActivity.this, hourOfDay, minute);

                        Toast.makeText(SettingActivity.this, "일기 쓸 시간에 알려드릴게요:-)", Toast.LENGTH_SHORT).show();
                    }
                }, hour, minute, false);

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            switchDiaryAlarm.setChecked(false);
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void setDiaryGroupInvitation() {
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
                            PropertyUtil.setProperty(Property.Key.GROUP_INVITATION_USE, "0", propertyDao);
                            switchInvitation.setChecked(false);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.cancel();
                            switchInvitation.setChecked(true);
                        });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            } else {
                PropertyUtil.setProperty(Property.Key.GROUP_INVITATION_USE, "1", propertyDao);
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
