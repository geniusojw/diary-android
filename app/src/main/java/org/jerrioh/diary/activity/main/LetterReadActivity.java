package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Letter;

public class LetterReadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_detail);

        Intent intent = getIntent();
        Letter letter = (Letter) intent.getSerializableExtra("letter");

        TextView date = findViewById(R.id.text_view_detail_date);
        date.setText(letter.getFromAuthorId());

        EditText title = findViewById(R.id.edit_text_detail_title);
        title.setText(letter.getTitle());

        EditText content = findViewById(R.id.edit_text_detail_content);
        content.setText(letter.getContent());

        View backButton = findViewById(R.id.floating_back_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}
