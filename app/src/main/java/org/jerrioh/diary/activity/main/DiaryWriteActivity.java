package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Music;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.db.MusicDao;
import org.jerrioh.diary.model.db.PropertyDao;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.PropertyUtil;
import org.jerrioh.diary.util.ReceiverUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiaryWriteActivity extends AppCompatActivity {
    private static final String TAG = "DiaryWriteActivity";

    private Diary todayDiary;

    private TextView diaryDate;
    private EditText titleText;
    private EditText contentText;

    private TextView musicText;
    private ImageView musicImage;

    private MediaPlayer mediaPlayer;
    private boolean musicOn;

    private String originalTitle;
    private String originalContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_diary);

        diaryDate = findViewById(R.id.text_view_detail_date);
        titleText = findViewById(R.id.edit_text_detail_title);
        titleText.setFocusableInTouchMode(true);

        contentText = findViewById(R.id.edit_text_detail_content);
        contentText.setFocusableInTouchMode(true);

        musicText = findViewById(R.id.text_view_detail_music);
        musicImage = findViewById(R.id.image_view_detail_music);

        FloatingActionButton backButton = findViewById(R.id.floating_back_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        TextView emptySpaceView = findViewById(R.id.text_view_detail_empty_space);
        emptySpaceView.setOnClickListener(v -> {
            contentText.requestFocus();
        });

        musicText.setVisibility(View.VISIBLE);
        musicImage.setVisibility(View.VISIBLE);
        View.OnClickListener musicClick = v -> {
            if (musicOn) {
                musicOff();

            } else {
                musicOn();
            }
        };
        musicText.setOnClickListener(musicClick);
        musicImage.setOnClickListener(musicClick);

        // 오늘의 일기 생성
        DiaryDao diaryDao = new DiaryDao(this);
        String today_yyyyMMdd = DateUtil.getyyyyMMdd();
        todayDiary = diaryDao.getDiary(today_yyyyMMdd);
        if (todayDiary == null) {
            Intent intent = getIntent();
            String title = intent.getStringExtra("title");

            todayDiary = new Diary();
            todayDiary.setDiaryDate(today_yyyyMMdd);
            todayDiary.setTitle(!TextUtils.isEmpty(title) ? title : "");
            todayDiary.setContent("");
            todayDiary.setAuthorDiaryStatus(Diary.DiaryStatus.UNSAVED);
            todayDiary.setAccountDiaryStatus(Diary.DiaryStatus.UNSAVED);
            diaryDao.insertDiary(todayDiary);

            originalTitle = "";
            originalContent = "";
        } else {
            originalTitle = todayDiary.getTitle();
            originalContent = todayDiary.getContent();
        }
        String diaryDateString = DateUtil.getDateStringSkipTime();

        diaryDate.setText(diaryDateString);
        titleText.setText(todayDiary.getTitle());
        contentText.setText(todayDiary.getContent());

        contentText.requestFocus();
    }

    private void musicOn() {
        musicOn = true;

        musicText.setText("MUSIC OFF");
        musicText.setTextColor(getResources().getColor(R.color.colorAccent));

        PropertyDao propertyDao = new PropertyDao(this);
        String selectMusic = PropertyUtil.getProperty(Property.Key.DIARY_WRITE_MUSIC, propertyDao);

        if (Property.Key.DIARY_WRITE_MUSIC.DEFAULT_VALUE.equals(selectMusic)) {
            mediaPlayer = MediaPlayer.create(this,R.raw.find_her);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } else {
            MusicDao musicDao = new MusicDao(this);
            Music music = musicDao.getMusic(selectMusic);
            byte[] decoded = Base64.decode(music.getMusicData(), Base64.DEFAULT);

            try {
                // create temp file that will hold byte array
                File tempMp3 = File.createTempFile("temp_music", "mp3", getCacheDir());
                tempMp3.deleteOnExit();
                FileOutputStream fos = new FileOutputStream(tempMp3);
                fos.write(decoded);
                fos.close();

                FileInputStream fis = new FileInputStream(tempMp3);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                Toast.makeText(this, "music on error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void musicOff() {
        musicOn = false;

        musicText.setText("MUSIC ON");
        musicText.setTextColor(getResources().getColor(R.color.colorPrimary));

        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }

    @Override
    public void onBackPressed() {
        if (musicOn) {
            musicOff();
        }

        if (!titleText.getText().toString().equals(originalTitle) || !contentText.getText().toString().equals(originalContent)) {
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
            saveDiary();
        }

        // receiver 등록
        PropertyDao propertyDao = new PropertyDao(this);
        String value = PropertyUtil.getProperty(Property.Key.YESTERDAY_RECEIVER_ON, propertyDao);
        if (Integer.parseInt(value) == 0) {
            ReceiverUtil.setDiaryReceiverOn(this);
            PropertyUtil.setProperty(Property.Key.YESTERDAY_RECEIVER_ON, "1", propertyDao);
        }

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (musicOn) {
            musicOff();
        }
        super.onPause();
    }

    private void saveDiary() {
        todayDiary.setTitle(titleText.getText().toString());
        todayDiary.setContent(contentText.getText().toString());

        DiaryDao diaryDao = new DiaryDao(this);
        diaryDao.updateDiaryContent(todayDiary.getDiaryDate(), todayDiary.getTitle(), todayDiary.getContent());
    }
}
