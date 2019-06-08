package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorLetterApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LetterWriteActivity extends AppCompatActivity {
    private static final String SOMEONE_UNKNOWN_ID = "UNKNOWN";
    private static final String SOMEONE_UNKNOWN_NICKNAME = "[Someone Unknown]";

    private static final String SYSTEM_AUTHOR_ID = "475a45d5-d139-4e3a-9828-e00e296c9040";
    private static final String SYSTEM_AUTHOR_NICKNAME = "[Administrator]";

    private int selectPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_letter);

        Author author = AuthorUtil.getAuthor(this);

        LetterDao letterDao = new LetterDao(this);
        List<Letter> lettersToMe = letterDao.getLettersToMe(author.getAuthorId());
        List<Letter> letters = new ArrayList<>();
        for (Letter letter : lettersToMe) {
            if (letter.getStatus() != Letter.LetterStatus.REPLIED) {
                letters.add(letter);
            }
        }

        Intent intent = getIntent();
        String letterId = intent.getStringExtra("letterId");
        selectPosition = letters.size();

        List<String> fromAuthorNicks = new ArrayList<>();
        for (int i = 0; i < letters.size(); i++) {
            Letter letter = letters.get(i);
            if (letter.getLetterId().equals(letterId)) {
                selectPosition = i;
            }
            fromAuthorNicks.add(i, letter.getFromAuthorNickname());
        }
        fromAuthorNicks.add(letters.size(), SOMEONE_UNKNOWN_NICKNAME);
        fromAuthorNicks.add(letters.size() + 1, SYSTEM_AUTHOR_NICKNAME);

        SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, fromAuthorNicks) ;

        Spinner changeReceiver = findViewById(R.id.spinner_detail_letter_receiver);
        changeReceiver.setVisibility(View.VISIBLE);
        changeReceiver.setAdapter(adapter);
        changeReceiver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                if (position < letters.size()) {
                    Letter letter = letters.get(position);
                    Toast.makeText(LetterWriteActivity.this,"선택된 아이템 : "+ letter.getLetterId(), Toast.LENGTH_SHORT).show();

                    TextView toAuthor = findViewById(R.id.text_view_detail_letter_to_author);
                    toAuthor.setText("TO: " + letter.getFromAuthorNickname());

                } else if (position == letters.size()) {
                    TextView toAuthor = findViewById(R.id.text_view_detail_letter_to_author);
                    toAuthor.setText("TO: " + SOMEONE_UNKNOWN_NICKNAME);

                } else if (position == letters.size() + 1) {
                    TextView toAuthor = findViewById(R.id.text_view_detail_letter_to_author);
                    toAuthor.setText("TO: " + SYSTEM_AUTHOR_NICKNAME);

                } else {
                    Toast.makeText(LetterWriteActivity.this,"spinner selection exception", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(LetterWriteActivity.this,"선택된 아이템이 없다", Toast.LENGTH_SHORT).show();
            }
        });
        changeReceiver.setSelection(selectPosition);

        EditText letterContent = findViewById(R.id.edit_text_detail_letter_content);
        letterContent.setFocusableInTouchMode(true);

        // 보내기 버튼
        FloatingActionButton sendButton = findViewById(R.id.floating_action_button_detail_letter_send);
        sendButton.setImageResource(R.drawable.ic_send_black_24dp);
        sendButton.setEnabled(true);
        sendButton.setClickable(true);
        sendButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(letterContent.getText())) {
                Toast.makeText(LetterWriteActivity.this,"편지를 작성하세요", Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("편지 보내기")
                    .setMessage("편지를 보내시겠습니까?\n받는사람: " + changeReceiver.getSelectedItem())
                    .setPositiveButton("OK", (dialog, which) -> {
                        long writtenTime = System.currentTimeMillis();
                        String newLetterId = author.getAuthorId() + "-" + writtenTime;

                        AuthorLetterApis authorLetterApis = new AuthorLetterApis(this);
                        ApiCallback callback = new ApiCallback() {
                            @Override
                            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                if (httpStatus == 200) {
                                    if (selectPosition < letters.size()) { // 편지상태 회신완료로 업데이트
                                        Letter letter = letters.get(selectPosition);
                                        LetterDao letterDao = new LetterDao(LetterWriteActivity.this);
                                        letterDao.updateLetterStatus(letter.getLetterId(), Letter.LetterStatus.REPLIED);
                                    }
                                    finish();
                                } else if (httpStatus == 404) {
                                    Toast.makeText(LetterWriteActivity.this, "편지를 받을 사람이 없습니다.", Toast.LENGTH_SHORT).show();
                                    finish(); // 수신자 탈퇴 or 랜덤 수신자 찾지못함 등의 원인
                                } else {
                                    //실패 설명
                                }
                            }
                        };
                        if (selectPosition < letters.size()) {
                            Letter letter = letters.get(selectPosition);
                            authorLetterApis.send(newLetterId, letter.getFromAuthorId(), letter.getFromAuthorNickname(), letterContent.getText().toString(), callback);

                        } else if (selectPosition == letters.size()) {
                            authorLetterApis.send(newLetterId, null, null, letterContent.getText().toString(), callback);

                        } else if (selectPosition == letters.size() + 1) {
                            authorLetterApis.send(newLetterId, SYSTEM_AUTHOR_ID, SYSTEM_AUTHOR_NICKNAME, letterContent.getText().toString(), callback);

                        } else {
                            Toast.makeText(LetterWriteActivity.this,"spinner selection exception", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        });

        // 뒤로가기 버튼
        FloatingActionButton backButton = findViewById(R.id.floating_action_button_detail_letter_back);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(letterContent.getText())) {
                onBackPressed();
                return;
            }
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("편지 삭제")
                    .setMessage("작성하던 편지를 삭제하시겠습니까?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        onBackPressed();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
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
