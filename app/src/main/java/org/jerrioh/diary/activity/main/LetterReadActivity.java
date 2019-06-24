package org.jerrioh.diary.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorLetterApis;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LetterReadActivity extends AbstractDetailActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_letter);

        Intent intent = getIntent();
        Letter letter = (Letter) intent.getSerializableExtra("letter");

        TextView letterTimeView = findViewById(R.id.text_view_detail_letter_time);
        letterTimeView.setText("Created on " + DateUtil.getDateStringFull(letter.getWrittenTime()));

        TextView toAuthorView = findViewById(R.id.text_view_detail_letter_to_author);
        toAuthorView.setText("TO: " + letter.getToAuthorNickname());

        EditText contentEditView = findViewById(R.id.edit_text_detail_letter_content);
        contentEditView.setText(letter.getContent());

        TextView fromAuthorView = findViewById(R.id.text_view_detail_letter_from_author);
        fromAuthorView.setText("FROM: " + letter.getFromAuthorNickname());

        if (letter.getStatus() == Letter.LetterStatus.UNREAD) {
            LetterDao letterDao = new LetterDao(this);
            letterDao.updateLetterStatus(letter.getLetterId(), Letter.LetterStatus.READ);
        }

        // 답장하기 버튼 세팅
        setReplyButton(letter);

        // 뒤로가기 버튼
        View backButton = findViewById(R.id.floating_action_button_detail_letter_back);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            finish();
        });

        RelativeLayout fontSizeLayout = findViewById(R.id.relative_layout_detail_letter_font_size);
        fontSizeLayout.setVisibility(View.VISIBLE);

        TextView adjustText = findViewById(R.id.text_view_detail_letter_font_size_adjust);
        super.setUpFontMusicButton(contentEditView, adjustText, null);
    }

    private void setReplyButton(Letter letter) {
        Author author = AuthorUtil.getAuthor(this);

        if (!author.getAuthorId().equals(letter.getFromAuthorId())) {
            FloatingActionButton replyButton = findViewById(R.id.floating_action_button_detail_letter_send);
            ((View) replyButton).setVisibility(View.VISIBLE);

            if (letter.getStatus() == Letter.LetterStatus.REPLIED) {
                //replyButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.beige)));
                replyButton.setOnClickListener(v -> {
                    Toast.makeText(this, "이미 회신된 편지입니다.", Toast.LENGTH_SHORT).show();
                });

            } else {
                if (letter.getLetterType() == Letter.LetterType.INVITATION) {
                    replyButton.setOnClickListener(v -> {
                        DialogInterface.OnClickListener listener = (dialog, which) -> {
                            String invitationResponseType;
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                invitationResponseType = "accept";
                            } else {
                                invitationResponseType = "refuse";
                            }

                            DiaryGroupApis diaryGroupApis = new DiaryGroupApis(this);
                            diaryGroupApis.respond(invitationResponseType, new ApiCallback() {
                                @Override
                                protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                    if (httpStatus == 200) {
                                        LetterDao letterDao = new LetterDao(LetterReadActivity.this);
                                        letterDao.updateLetterStatus(letter.getLetterId(), Letter.LetterStatus.REPLIED);
                                        Toast.makeText(LetterReadActivity.this, "[debug] 성공", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LetterReadActivity.this, "[debug] 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        };

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                        alertBuilder.setTitle("초대 응답하기")
                                .setMessage("일기모임에 참여 하시겠습니까?\n당신의 일기는 몇일동안 익명의 누구에게 공개되며 blah blah...")
                                .setPositiveButton("수락", listener)
                                .setNegativeButton("거절", listener);

                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    });

                } else if (letter.getLetterType() == Letter.LetterType.NORMAL) {
                    replyButton.setOnClickListener(v -> {
                        Intent replyIntent = new Intent(this, LetterWriteActivity.class);
                        replyIntent.putExtra("letterId", letter.getLetterId());
                        startActivity(replyIntent);
                        finish();
                    });
                }
            }
        }
    }
}
