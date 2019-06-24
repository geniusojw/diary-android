package org.jerrioh.diary.activity.draw;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.adapter.CustomSpinnerAdapter;
import org.jerrioh.diary.activity.pop.FontSizePopActivity;
import org.jerrioh.diary.model.db.MusicDao;
import org.jerrioh.diary.model.db.ThemeDao;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.PropertyDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.PropertyUtil;
import org.jerrioh.diary.util.ReceiverUtil;

import java.util.ArrayList;
import java.util.List;

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
        this.setDiaryTheme();
        this.setDiaryWriteMusic();
        this.setFontSize();
        this.setAlias();
        this.setScreenLock();
        this.setDiaryAlarm();
        this.setDiaryGroupInvitation();
        this.setDataReset();
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

        String themeName = PropertyUtil.getProperty(Property.Key.DIARY_THEME, this);
        int selection = themeList.indexOf(themeName);

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, themeList, selection);
        themeSpinner.setAdapter(adapter);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PropertyUtil.setProperty(Property.Key.DIARY_THEME, themeList.get(position), SettingActivity.this);
                Toast.makeText(SettingActivity.this,"선택된 아이템이 있따", Toast.LENGTH_SHORT).show();
                adapter.setSelection(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SettingActivity.this,"선택된 아이템이 없다", Toast.LENGTH_SHORT).show();
            }
        });
        themeSpinner.setSelection(selection);
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

        String musicName = PropertyUtil.getProperty(Property.Key.DIARY_WRITE_MUSIC, this);
        int selection = musicList.indexOf(musicName);

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, musicList, selection);
        musicSpinner.setAdapter(adapter);
        musicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PropertyUtil.setProperty(Property.Key.DIARY_WRITE_MUSIC, musicList.get(position), SettingActivity.this);
                adapter.setSelection(position);
                Toast.makeText(SettingActivity.this,"선택된 아이템이 있따", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SettingActivity.this,"선택된 아이템이 없다", Toast.LENGTH_SHORT).show();
            }
        });
        musicSpinner.setSelection(selection);
    }

    private void setFontSize() {
        LinearLayout fontSizeLayout = findViewById(R.id.linear_layout_setting_font_size);

        fontSizeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, FontSizePopActivity.class);
            startActivity(intent);
        });
    }

    private void setAlias() {
        TextView aliasView = findViewById(R.id.text_view_setting_alias);
        aliasView.setOnClickListener(v -> {
            Toast.makeText(this, "개발이 안된 부분", Toast.LENGTH_SHORT).show();
        });
    }

    private void setScreenLock() {
        String screenLockUse = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_USE, this);
        Switch switchScreenLock = findViewById(R.id.switch_setting_screen_lock);
        switchScreenLock.setChecked(Integer.parseInt(screenLockUse) == 1);
        switchScreenLock.setOnClickListener(v -> {
            String lockUse = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_USE, this);
            if (Integer.parseInt(lockUse) == 1) {
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_USE, "0", this);
                switchScreenLock.setChecked(false);
            } else {
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_USE, "1", this);
                switchScreenLock.setChecked(true);
            }
        });
    }

    private void setDiaryAlarm() {
        Switch switchDiaryAlarm = findViewById(R.id.switch_setting_diary_alarm);
        TextView textDiaryAlarm = findViewById(R.id.text_view_diary_alarm);

        int diaryAlarmUse = Integer.parseInt(PropertyUtil.getProperty(Property.Key.DIARY_ALARM_USE, this));
        String diaryAlarmTime = PropertyUtil.getProperty(Property.Key.DIARY_ALARM_TIME, this);

        if (diaryAlarmUse == 1) {
            switchDiaryAlarm.setChecked(true);
            textDiaryAlarm.setText("설정된 알림시간 : " + diaryAlarmTime);
        } else {
            switchDiaryAlarm.setChecked(false);
            textDiaryAlarm.setText("It helps you to remember to write diary");
        }

        switchDiaryAlarm.setOnClickListener(v -> {
            int alarmUse = Integer.parseInt(PropertyUtil.getProperty(Property.Key.DIARY_ALARM_USE, this));
            if (alarmUse == 1) {
                PropertyUtil.setProperty(Property.Key.DIARY_ALARM_USE, "0", this);
                switchDiaryAlarm.setChecked(false);
                textDiaryAlarm.setText("It helps you to remember to write diary");
                ReceiverUtil.setAlarmReceiverOff(this);

                Toast.makeText(SettingActivity.this, "이제 일기알림이 동작하지 않습니다.", Toast.LENGTH_SHORT).show();

            } else {
                String alarmTime = PropertyUtil.getProperty(Property.Key.DIARY_ALARM_TIME, this);
                String[] time = alarmTime.split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);

                TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                        PropertyUtil.setProperty(Property.Key.DIARY_ALARM_USE, "1", SettingActivity.this);
                        PropertyUtil.setProperty(Property.Key.DIARY_ALARM_TIME, time, SettingActivity.this);
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
        String groupInvitationUse = PropertyUtil.getProperty(Property.Key.GROUP_INVITATION_USE, this);
        Switch switchInvitation = findViewById(R.id.switch_setting_group_invitation);
        switchInvitation.setChecked(Integer.parseInt(groupInvitationUse) == 1);
        switchInvitation.setOnClickListener(v -> {
            String invitationUse = PropertyUtil.getProperty(Property.Key.GROUP_INVITATION_USE, SettingActivity.this);
            if (Integer.parseInt(invitationUse) == 1) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Disable diary group beInvited")
                        .setMessage("You can decline the beInvited without disable it.\nDo you really want to disable it?")
                        .setPositiveButton("Disable", (dialog, which) -> {
                            PropertyUtil.setProperty(Property.Key.GROUP_INVITATION_USE, "0", SettingActivity.this);
                            switchInvitation.setChecked(false);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.cancel();
                            switchInvitation.setChecked(true);
                        });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            } else {
                PropertyUtil.setProperty(Property.Key.GROUP_INVITATION_USE, "1", SettingActivity.this);
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
