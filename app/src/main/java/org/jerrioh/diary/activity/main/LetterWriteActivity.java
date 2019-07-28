package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.adapter.CustomSpinnerAdapter;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorLetterApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LetterWriteActivity extends AbstractDetailActivity {

    private EditText letterContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_letter);

        letterContent = findViewById(R.id.edit_text_detail_letter_content);
        letterContent.setFocusableInTouchMode(true);

        // 보내기 버튼
        Intent intent = getIntent();
        String letterId = intent.getStringExtra("letterId");
        String authorId = intent.getStringExtra("authorId");
        String nickname = intent.getStringExtra("nickname");

        this.setSendButton(letterId, authorId, nickname);

        // 뒤로가기 버튼
        FloatingActionButton sendButton = findViewById(R.id.floating_action_button_detail_letter_send);
        FloatingActionButton backButton = findViewById(R.id.floating_action_button_detail_letter_back);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(letterContent.getText())) {
                onBackPressed();
                return;
            }
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(getResources().getString(R.string.letter_delete))
                    .setMessage(getResources().getString(R.string.letter_delete_current_letter))
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                        onBackPressed();
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        });

        super.setUpMoreOptionsPost(letterContent, false, true, null);
        super.setUpTransparentFloatingButton(letterContent, Arrays.asList(sendButton, backButton));
//        super.setUpSoftKeyboard(R.id.relative_layout_detail_letter_main, Arrays.asList(sendButton, backButton));
    }

    private void setSendButton(String inputLetterId, String inputAuthorId, String inputNickname) {
        Author author = AuthorUtil.getAuthor(this);

        String toAuthorId;
        String toAuthorNickname;

        if (inputLetterId != null) {
            LetterDao letterDao = new LetterDao(this);
            Letter letter = letterDao.getLetter(inputLetterId);
            toAuthorId = letter.getFromAuthorId();
            toAuthorNickname = letter.getFromAuthorNickname();

        } else {
            toAuthorId = inputAuthorId;
            toAuthorNickname = inputNickname;
        }

        TextView fromAuthorView = findViewById(R.id.text_view_detail_letter_from_author);
        TextView toAuthor = findViewById(R.id.text_view_detail_letter_to_author);
        TextView letterTimeView = findViewById(R.id.text_view_detail_letter_time);

        fromAuthorView.setText("FROM: " + author.getNickname());
        toAuthor.setText("TO: " + toAuthorNickname);
        letterTimeView.setText(getResources().getString(R.string.letter_send_to_who, toAuthorNickname));

        // 보내기 버튼
        FloatingActionButton sendButton = findViewById(R.id.floating_action_button_detail_letter_send);
        sendButton.setImageResource(R.drawable.ic_send_black_24dp);
        ((View) sendButton).setVisibility(View.VISIBLE);

        sendButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(letterContent.getText())) {
                Toast.makeText(LetterWriteActivity.this,getResources().getString(R.string.letter_letter_is_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(getResources().getString(R.string.letter_send_button_confirm_title))
                    .setMessage(getResources().getString(R.string.letter_send_button_confirm_message, toAuthorNickname))
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                        long writtenTime = System.currentTimeMillis();
                        String newLetterId = author.getAuthorId() + "-" + writtenTime;

                        AuthorLetterApis authorLetterApis = new AuthorLetterApis(this);
                        authorLetterApis.send(newLetterId, toAuthorId, toAuthorNickname, letterContent.getText().toString(), new ApiCallback() {
                            @Override
                            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                if (httpStatus == 200) {
                                    LetterDao letterDao = new LetterDao(LetterWriteActivity.this);
                                    if (inputLetterId != null) { // 편지상태 회신완료로 업데이트
                                        letterDao.updateLetterStatus(inputLetterId, Letter.LetterStatus.REPLIED);
                                    }
                                    Letter newLetter = new Letter();
                                    newLetter.setLetterId(newLetterId);
                                    newLetter.setFromAuthorId(author.getAuthorId());
                                    newLetter.setFromAuthorNickname(author.getNickname());
                                    newLetter.setToAuthorId(toAuthorId);
                                    newLetter.setToAuthorNickname(toAuthorNickname);
                                    newLetter.setLetterType(Letter.LetterType.NORMAL);
                                    newLetter.setContent(letterContent.getText().toString());
                                    newLetter.setStatus(Letter.LetterStatus.REPLIED);
                                    newLetter.setWrittenTime(System.currentTimeMillis()); // 서버에 편지발송시간과 약간 차이가 있을 수 있다.
                                    letterDao.insertLetter(newLetter);

                                    Toast.makeText(LetterWriteActivity.this, getResources().getString(R.string.sent), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if (httpStatus == 404) {
                                    Toast.makeText(LetterWriteActivity.this, getResources().getString(R.string.letter_no_author), Toast.LENGTH_SHORT).show();
                                    finish(); // 수신자 탈퇴 or 랜덤 수신자 찾지못함 등의 원인
                                } else {
                                    Toast.makeText(LetterWriteActivity.this, LetterWriteActivity.this.getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
