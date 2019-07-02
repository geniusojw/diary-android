package org.jerrioh.diary.activity.draw;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends CommonActionBarActivity {
    private static final String TAG = "FaqActivity";

    private List<Integer> questionIds;
    private List<Integer> answerIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        setCommonToolBar("FAQ");

        questionIds = new ArrayList<>();
        questionIds.add(R.id.question1);
        questionIds.add(R.id.question2);
        questionIds.add(R.id.question3);

        answerIds = new ArrayList<>();
        answerIds.add(R.id.answer1);
        answerIds.add(R.id.answer2);
        answerIds.add(R.id.answer3);

        this.allAnswerTextVisibilityGone();

        for (int i = 0; i < questionIds.size(); i++) {
            int qId = questionIds.get(i);
            int aId = answerIds.get(i);

            TextView question = findViewById(qId);
            TextView answer = findViewById(aId);

            question.setText("질문 " + i);
            answer.setText("답변 " +  i);

            question.setOnClickListener(v -> {

                boolean displayAnswer = false;
                if (answer.getVisibility() != View.VISIBLE) {
                    displayAnswer = true;
                }

                allAnswerTextVisibilityGone();

                if (displayAnswer) {
                    answer.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void allAnswerTextVisibilityGone() {
        for (int i = 0; i < answerIds.size(); i++) {
            TextView answer = findViewById(answerIds.get(i));
            answer.setVisibility(View.GONE);
        }
    }
}
