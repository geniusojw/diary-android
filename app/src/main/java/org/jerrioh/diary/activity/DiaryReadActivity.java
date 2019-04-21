package org.jerrioh.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.jerrioh.diary.dbmodel.Write;
import org.jerrioh.diary.R;

public class DiaryReadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaryread);

        Intent intent = getIntent();
        Write diary = (Write) intent.getSerializableExtra("diary");

        TextView readView = (TextView) findViewById(R.id.diaryread_contents);
        readView.setText(diary.getContent());

        View backButton = findViewById(R.id.back_diary_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(DiaryReadActivity.this, MainActivity.class);
            backIntent.putExtra("initNavId", R.id.nav_diary);
            startActivity(backIntent);
        });
    }
}
