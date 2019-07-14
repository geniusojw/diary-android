package org.jerrioh.diary.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

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

        super.setUpMoreOptionsPost(contentEditView, false, false, letter.getLetterId());
    }

    private void setReplyButton(Letter letter) {
        Author author = AuthorUtil.getAuthor(this);

        if (!author.getAuthorId().equals(letter.getFromAuthorId())) {
            FloatingActionButton replyButton = findViewById(R.id.floating_action_button_detail_letter_send);
            ((View) replyButton).setVisibility(View.VISIBLE);

            if (letter.getStatus() == Letter.LetterStatus.REPLIED) {
                //replyButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.beige)));
                replyButton.setOnClickListener(v -> {
                    Toast.makeText(this, getResources().getString(R.string.letter_replied_letter), Toast.LENGTH_SHORT).show();
                });

            } else {
                if (letter.getLetterType() == Letter.LetterType.INVITATION) {
                    replyButton.setOnClickListener(v -> {
                        DialogInterface.OnClickListener listener = (dialog, which) -> {
                            String invitationResponseType;
                            String successMessage;
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                invitationResponseType = "accept";
                                successMessage = LetterReadActivity.this.getResources().getString(R.string.accepted);
                            } else {
                                invitationResponseType = "refuse";
                                successMessage = LetterReadActivity.this.getResources().getString(R.string.refused);
                            }

                            DiaryGroupApis diaryGroupApis = new DiaryGroupApis(this);
                            diaryGroupApis.respond(invitationResponseType, new ApiCallback() {
                                @Override
                                protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                    if (httpStatus == 200) {
                                        LetterDao letterDao = new LetterDao(LetterReadActivity.this);
                                        letterDao.updateLetterStatus(letter.getLetterId(), Letter.LetterStatus.REPLIED);
                                        Toast.makeText(LetterReadActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LetterReadActivity.this, LetterReadActivity.this.getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        };

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                        alertBuilder.setTitle(LetterReadActivity.this.getResources().getString(R.string.letter_group_letter_respond))
                                .setMessage(LetterReadActivity.this.getResources().getString(R.string.letter_group_letter_respond_description))
                                .setPositiveButton(LetterReadActivity.this.getResources().getString(R.string.accept), listener)
                                .setNegativeButton(LetterReadActivity.this.getResources().getString(R.string.refuse), listener);

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
