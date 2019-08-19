package org.jerrioh.diary.activity.draw;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telecom.Call;
import android.text.TextUtils;
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
import org.jerrioh.diary.activity.lock.LockSettingActivity;
import org.jerrioh.diary.activity.pop.FontSizePopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorStoreApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Music;
import org.jerrioh.diary.model.Theme;
import org.jerrioh.diary.model.db.MusicDao;
import org.jerrioh.diary.model.db.ThemeDao;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.PropertyUtil;
import org.jerrioh.diary.util.ReceiverUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AbstractDiaryToolbarActivity {
    private static final String TAG = "SettingActivity";

    @Override
    protected void onResume() {
        super.onResume();
        this.setMemberServices();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setCommonToolBar(getResources().getString(R.string.setting));
    }

    private void setMemberServices() {
        this.setDiaryTheme();
        this.setDiaryWriteMusic();
        this.setFontSize();
        //this.setAlias();
        this.setScreenLock();
        this.setDiaryAlarm();
        this.setDiaryGroupInvitation();
        this.setAutoDelete();
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
                String themeName = themeList.get(position);
                PropertyUtil.setProperty(Property.Key.DIARY_THEME, themeName, SettingActivity.this);
                adapter.setSelection(position);

                if (!Property.Key.DIARY_THEME.DEFAULT_VALUE.equals(themeName)) {
                    Theme theme = themeDao.getTheme(themeName);
                    if (TextUtils.isEmpty(theme.getPattern0())) {
                        Toast.makeText(SettingActivity.this, getResources().getString(R.string.diary_theme_download), Toast.LENGTH_SHORT).show();

                        AuthorStoreApis authorStoreApis = new AuthorStoreApis(SettingActivity.this);
                        authorStoreApis.downloadTheme(themeName, new ApiCallback() {
                            @Override
                            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                if (httpStatus == 200) {
                                    JSONObject data = jsonObject.getJSONObject("data");

                                    theme.setPattern0(data.getString("pattern0"));
                                    theme.setPattern1(data.getString("pattern1"));
                                    theme.setPattern2(data.getString("pattern2"));
                                    theme.setPattern3(data.getString("pattern3"));
                                    theme.setBannerColor(data.getString("bannerColor"));

                                    themeDao.updateTheme(theme);
                                } else {
                                    Toast.makeText(SettingActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                String musicName = musicList.get(position);
                PropertyUtil.setProperty(Property.Key.DIARY_WRITE_MUSIC, musicName, SettingActivity.this);
                adapter.setSelection(position);

                if (!Property.Key.DIARY_WRITE_MUSIC.DEFAULT_VALUE.equals(musicName)) {
                    Music music = musicDao.getMusic(musicName);
                    if (TextUtils.isEmpty(music.getMusicData())) {
                        Toast.makeText(SettingActivity.this, getResources().getString(R.string.diary_music_download), Toast.LENGTH_SHORT).show();

                        AuthorStoreApis authorStoreApis = new AuthorStoreApis(SettingActivity.this);
                        authorStoreApis.downloadMusic(musicName, new ApiCallback() {
                            @Override
                            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                if (httpStatus == 200) {
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    String musicData = data.getString("musicData");
                                    musicDao.updateMusic(musicName, musicData);
                                } else {
                                    Toast.makeText(SettingActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

//    private void setAlias() {
//        TextView aliasView = findViewById(R.id.text_view_setting_alias);
//        aliasView.setOnClickListener(v -> {
//            Toast.makeText(this, "TBD", Toast.LENGTH_SHORT).show();
//        });
//    }

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
                Author author = AuthorUtil.getAuthor(SettingActivity.this);
                if (TextUtils.isEmpty(author.getAccountEmail())) {
                    Toast.makeText(SettingActivity.this, "앱을 잠그기 위해서는 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    switchScreenLock.setChecked(false);
                    return;
                }

                Intent lockIntent = new Intent(this, LockSettingActivity.class);
                lockIntent.putExtra("accountEmail", author.getAccountEmail());
                startActivity(lockIntent);

//                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_USE, "1", this);
//                switchScreenLock.setChecked(true);
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
            textDiaryAlarm.setText(getResources().getString(R.string.diary_alarm_description_set, diaryAlarmTime));
        } else {
            switchDiaryAlarm.setChecked(false);
            textDiaryAlarm.setText(getResources().getString(R.string.diary_alarm_description));
        }

        switchDiaryAlarm.setOnClickListener(v -> {
            int alarmUse = Integer.parseInt(PropertyUtil.getProperty(Property.Key.DIARY_ALARM_USE, this));
            if (alarmUse == 1) {
                PropertyUtil.setProperty(Property.Key.DIARY_ALARM_USE, "0", this);
                switchDiaryAlarm.setChecked(false);
                textDiaryAlarm.setText(getResources().getString(R.string.diary_alarm_description));
                ReceiverUtil.setAlarmReceiverOff(this);

                Toast.makeText(SettingActivity.this, getResources().getString(R.string.diary_alarm_description_set_message0), Toast.LENGTH_SHORT).show();

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
                        textDiaryAlarm.setText(getResources().getString(R.string.diary_alarm_description_set, time));
                        ReceiverUtil.setAlarmReceiverOn(SettingActivity.this, hourOfDay, minute);

                        Toast.makeText(SettingActivity.this, getResources().getString(R.string.diary_alarm_description_set_message1), Toast.LENGTH_SHORT).show();
                    }
                }, hour, minute, false);

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                alertBuilder.setTitle(getResources().getString(R.string.diary_group_invitation_pop_title))
                        .setMessage(getResources().getString(R.string.diary_group_invitation_pop_description))
                        .setPositiveButton(getResources().getString(R.string.disable), (dialog, which) -> {
                            PropertyUtil.setProperty(Property.Key.GROUP_INVITATION_USE, "0", SettingActivity.this);
                            switchInvitation.setChecked(false);
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
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

    private void setAutoDelete() {
        String autoDeletePostUse = PropertyUtil.getProperty(Property.Key.AUTO_DELETE_POST_USE, this);
        Switch switchPost = findViewById(R.id.switch_setting_auto_delete_post);
        switchPost.setChecked(Integer.parseInt(autoDeletePostUse) == 1);
        switchPost.setOnClickListener(v -> {
            String autoDeleteUse = PropertyUtil.getProperty(Property.Key.AUTO_DELETE_POST_USE, SettingActivity.this);
            String newValue = String.valueOf((Integer.parseInt(autoDeleteUse) + 1) % 2);
            PropertyUtil.setProperty(Property.Key.AUTO_DELETE_POST_USE, newValue, SettingActivity.this);
            switchPost.setChecked(Integer.parseInt(newValue) == 1);
        });

        String autoDeleteLetter = PropertyUtil.getProperty(Property.Key.AUTO_DELETE_LETTER_USE, this);
        Switch switchLetter = findViewById(R.id.switch_setting_auto_delete_letter);
        switchLetter.setChecked(Integer.parseInt(autoDeleteLetter) == 1);
        switchLetter.setOnClickListener(v -> {
            String autoDeleteUse = PropertyUtil.getProperty(Property.Key.AUTO_DELETE_POST_USE, SettingActivity.this);
            String newValue = String.valueOf((Integer.parseInt(autoDeleteUse) + 1) % 2);
            PropertyUtil.setProperty(Property.Key.AUTO_DELETE_POST_USE, newValue, SettingActivity.this);
            switchLetter.setChecked(Integer.parseInt(newValue) == 1);
        });
    }

    private void setDataReset() {
        LinearLayout dataResetLayout = findViewById(R.id.linear_layout_setting_data_reset);
        dataResetLayout.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(getResources().getString(R.string.reset_data))
                    .setMessage(getResources().getString(R.string.reset_data_description))
                    .setPositiveButton(getResources().getString(R.string.delete_all), (dialog, which) -> {
                        AuthorUtil.resetAuthorData(this);
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        });
    }
}
