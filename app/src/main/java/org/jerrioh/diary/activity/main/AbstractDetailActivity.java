package org.jerrioh.diary.activity.main;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.pop.FontSizePopActivity;
import org.jerrioh.diary.activity.pop.PostWritePopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.account.AccountDiaryApis;
import org.jerrioh.diary.api.author.AuthorDiaryApis;
import org.jerrioh.diary.api.author.AuthorLetterApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.Music;
import org.jerrioh.diary.model.Post;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.model.db.MusicDao;
import org.jerrioh.diary.model.db.PostDao;
import org.jerrioh.diary.model.db.PropertyDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.PropertyUtil;
import org.jerrioh.diary.util.SoftKeyboard;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public abstract class AbstractDetailActivity extends AbstractDiaryActivity {

    private static final String TAG = "AbstractDetailActivity";

    private TextView contentText;

    private MediaPlayer mediaPlayer;
    private boolean musicOn;

//    protected SoftKeyboard softKeyboard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (softKeyboard != null) {
//            softKeyboard.unRegisterSoftKeyboardCallback(); // Prevent memory leaks
//        }
    }

    @Override
    protected void onResume() {
        setContentFontSize();
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        musicOff();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        musicOff();
        super.onPause();
    }

//    protected void setUpSoftKeyboard(int mainLayoutId, List<FloatingActionButton> floatingButtons) {
//        RelativeLayout mainLayout = findViewById(mainLayoutId);
//
//        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
//        softKeyboard = new SoftKeyboard(mainLayout, controlManager);
//        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
//            @Override
//            public void onSoftKeyboardHide() {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (FloatingActionButton floatingButton : floatingButtons) {
//                            floatingButton.setAlpha(1.0f);
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onSoftKeyboardShow() {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (FloatingActionButton floatingButton : floatingButtons) {
//                            floatingButton.setAlpha(0.25f);
//                        }
//                    }
//                });
//            }
//        });
//    }

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

    protected void setUpMoreOptionsPost(TextView contentText, Post post, boolean isWrite) {
        this.contentText = contentText;
        this.setContentFontSize();

        boolean isPrivate = post.getChocolates() == -1;

        ImageView moreLayout = findViewById(R.id.image_view_more);
        moreLayout.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.detail_option_font:
                            Intent fontIntent = new Intent(AbstractDetailActivity.this, FontSizePopActivity.class);
                            startActivity(fontIntent);
                            return true;

                        case R.id.detail_option_update:
                            Intent writeIntent = new Intent(AbstractDetailActivity.this, PostWritePopActivity.class);
                            writeIntent.putExtra("post", post);
                            startActivity(writeIntent);
                            finish();
                            return true;

                        case R.id.detail_option_delete:
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AbstractDetailActivity.this);
                            alertBuilder.setTitle(getResources().getString(R.string.post_delete))
                                    .setMessage(getResources().getString(R.string.delete_confirm))
                                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                                        new PostDao(AbstractDetailActivity.this).deletePost(post.getPostId());
                                        Toast.makeText(AbstractDetailActivity.this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                                        dialog.cancel();
                                    });
                            AlertDialog alertDialog = alertBuilder.create();
                            alertDialog.show();
                            return true;

                        default:
                            return false;
                    }
                }
            });

            MenuInflater inflater = popup.getMenuInflater();
            if (isPrivate && !isWrite) {
                inflater.inflate(R.menu.detail_menu_post_private_read, popup.getMenu());
            } else {
                inflater.inflate(R.menu.detail_menu_post_read_write, popup.getMenu());
            }
            popup.show();
        });
    }

    protected void setUpMoreOptionsPost(TextView contentText, boolean isDiary, boolean isWrite, String key) {
        this.contentText = contentText;
        this.setContentFontSize();

        ImageView moreLayout = findViewById(R.id.image_view_more);
        moreLayout.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.detail_option_font:
                            Intent intent = new Intent(AbstractDetailActivity.this, FontSizePopActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.detail_option_music:
                            if (musicOn) {
                                musicOff();
                            } else {
                                musicOn();
                            }
                            return true;
                        case R.id.detail_option_delete:
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AbstractDetailActivity.this);
                            alertBuilder.setTitle(isDiary? getResources().getString(R.string.diary_delete) : getResources().getString(R.string.letter_delete))
                                    .setMessage(getResources().getString(R.string.delete_confirm))
                                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                                        if (key != null) {
                                            if (isDiary) {
                                                Author author = AuthorUtil.getAuthor(AbstractDetailActivity.this);
                                                new AuthorDiaryApis(AbstractDetailActivity.this).deleteDiary(key, new ApiCallback() {
                                                    @Override protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {}
                                                });

                                                DiaryDao diaryDao = new DiaryDao(AbstractDetailActivity.this);
                                                if (!TextUtils.isEmpty(author.getAccountEmail())) {
                                                    Diary diary = diaryDao.getDiary(key);
                                                    if (diary.getAccountDiaryStatus() == Diary.DiaryStatus.SAVED) {
                                                        new AccountDiaryApis(AbstractDetailActivity.this).deleteDiary(key, new ApiCallback() {
                                                            @Override protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                                                if (httpStatus == 200 || httpStatus == 404) {
                                                                    diaryDao.deleteDiary(key);
                                                                    Toast.makeText(AbstractDetailActivity.this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(AbstractDetailActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    diaryDao.deleteDiary(key);
                                                    Toast.makeText(AbstractDetailActivity.this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                new AuthorLetterApis(AbstractDetailActivity.this).deleteLetter(key, new ApiCallback() {
                                                    @Override protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                                        if (httpStatus == 200 || httpStatus == 404) {
                                                            new LetterDao(AbstractDetailActivity.this).deleteLetter(key);
                                                            Toast.makeText(AbstractDetailActivity.this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(AbstractDetailActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        finish();
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                                        dialog.cancel();
                                    });
                            AlertDialog alertDialog = alertBuilder.create();
                            alertDialog.show();
                            return true;
                        default:
                            return false;
                    }
                }
            });

            MenuInflater inflater = popup.getMenuInflater();
            if (isDiary) {
                if (isWrite) {
                    inflater.inflate(R.menu.detail_menu_diary_write, popup.getMenu());
                } else {
                    inflater.inflate(R.menu.detail_menu_diary_read, popup.getMenu());
                }
            } else {
                if (isWrite) {
                    inflater.inflate(R.menu.detail_menu_letter_write, popup.getMenu());
                } else {
                    inflater.inflate(R.menu.detail_menu_letter_read, popup.getMenu());
                }
            }
            popup.show();
        });
    }

    private void setContentFontSize() {
        if (contentText != null) {
            int fontSizeProgress = Integer.parseInt(PropertyUtil.getProperty(Property.Key.FONT_SIZE, this));
            int fontSize = (fontSizeProgress * 2) + Property.Config.FONT_SIZE_OFFSET;

            contentText.setTextSize(fontSize);
        }
    }

    public void musicOn() {

        musicOn = true;

        String selectMusic = PropertyUtil.getProperty(Property.Key.DIARY_WRITE_MUSIC, this);

        if (!Property.Key.DIARY_WRITE_MUSIC.DEFAULT_VALUE.equals(selectMusic)) {
            MusicDao musicDao = new MusicDao(this);
            Music music = musicDao.getMusic(selectMusic);
            if (!TextUtils.isEmpty(music.getMusicData())) {
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
                    return;
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(this, "music on error", Toast.LENGTH_LONG).show();
                }
            }
        }

        mediaPlayer = MediaPlayer.create(this,R.raw.find_her);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void musicOff() {
        if (!musicOn) {
            return;
        }

        musicOn = false;

        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }


    protected void setWindowAttribute(float widthRatio, float heightRatio) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int)(width * widthRatio), (int)(height * heightRatio));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        params.dimAmount = 0.6f;

        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
