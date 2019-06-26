package org.jerrioh.diary.activity.main;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.pop.FontSizePopActivity;
import org.jerrioh.diary.model.Music;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.MusicDao;
import org.jerrioh.diary.model.db.PropertyDao;
import org.jerrioh.diary.util.PropertyUtil;
import org.jerrioh.diary.util.SoftKeyboard;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public abstract class AbstractDetailActivity extends AppCompatActivity {

    private static final String TAG = "AbstractDetailActivity";

    private TextView contentText;

    private TextView musicText;
    private MediaPlayer mediaPlayer;
    private boolean musicOn;

    protected SoftKeyboard softKeyboard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (softKeyboard != null) {
            softKeyboard.unRegisterSoftKeyboardCallback(); // Prevent memory leaks
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentFontSize();
    }

    protected void setUpSoftKeyboard(int mainLayoutId, List<FloatingActionButton> floatingButtons) {
        RelativeLayout mainLayout = findViewById(mainLayoutId);

        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(mainLayout, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        for (FloatingActionButton floatingButton : floatingButtons) {
                            floatingButton.setAlpha(1.0f);
                        }
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        for (FloatingActionButton floatingButton : floatingButtons) {
                            floatingButton.setAlpha(0.25f);
                        }
                    }
                });
            }
        });
    }

    protected void setUpTransparentFloatingButton(TextView contentText, List<FloatingActionButton> floatingButtons) {
        contentText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                float alpha;
                if (hasFocus) {
                    alpha = 0.25f;
                } else {
                    alpha = 1.0f;
                }
                for (FloatingActionButton floatingButton : floatingButtons) {
                    floatingButton.setAlpha(alpha);
                }
            }
        });
    }

    protected void setUpFontMusicButton(TextView contentText, View fontSizeView, TextView musicText) {
        // 글자 크기 조절을 위한 세팅
        this.contentText = contentText;
        fontSizeView.setOnClickListener(v -> {
            Intent intent = new Intent(this, FontSizePopActivity.class);
            startActivity(intent);
        });
        this.setContentFontSize();

        // 음악 on/off 세팅 (optional)
        this.musicText = musicText;
        if (this.musicText != null) {
            musicText.setOnClickListener(v -> {
                if (musicOn) {
                    musicOff();
                } else {
                    musicOn();
                }
            });
        }
    }

    private void setContentFontSize() {
        if (contentText != null) {
            int fontSizeProgress = Integer.parseInt(PropertyUtil.getProperty(Property.Key.FONT_SIZE, this));
            int fontSize = (fontSizeProgress * 2) + Property.Config.FONT_SIZE_OFFSET;

            contentText.setTextSize(fontSize);
        }
    }

    public void musicOn() {
        if (this.musicText == null) {
            return;
        }

        musicOn = true;

        musicText.setTextColor(getResources().getColor(R.color.colorAccent));

        PropertyDao propertyDao = new PropertyDao(this);
        String selectMusic = PropertyUtil.getProperty(Property.Key.DIARY_WRITE_MUSIC, this);

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

    public void musicOff() {
        if (this.musicText == null) {
            return;
        }

        if (!musicOn) {
            return;
        }

        musicOn = false;

        musicText.setTextColor(getResources().getColor(R.color.colorIndicatorText));

        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }
}
