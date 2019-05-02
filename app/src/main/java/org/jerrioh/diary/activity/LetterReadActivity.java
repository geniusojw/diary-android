package org.jerrioh.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.dbmodel.Letter;
import org.jerrioh.diary.util.DateUtil;

import java.util.Locale;

public class LetterReadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Letter letter = (Letter) intent.getSerializableExtra("letter");

        TextView date = findViewById(R.id.detail_date);
        date.setText(letter.getWriteUserId());

        EditText title = findViewById(R.id.detail_title);
        title.setText(letter.getTitle());

        EditText content = findViewById(R.id.detail_content);
        content.setText(letter.getContent());

        View backButton = findViewById(R.id.floating_back_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(LetterReadActivity.this, MainActivity.class);
            backIntent.putExtra("initNavId", R.id.nav_letter);
            startActivity(backIntent);
        });
    }
}
