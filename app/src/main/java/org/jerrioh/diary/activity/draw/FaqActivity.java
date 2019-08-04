package org.jerrioh.diary.activity.draw;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.jerrioh.diary.R;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AbstractDiaryToolbarActivity {
    private static final String TAG = "FaqActivity";

    private List<Integer> answerIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        setCommonToolBar(getResources().getString(R.string.help));

        answerIds = new ArrayList<>();
        answerIds.add(R.id.answer1);
        answerIds.add(R.id.answer2);
        answerIds.add(R.id.answer3);
        answerIds.add(R.id.answer4);
        answerIds.add(R.id.answer5);
        answerIds.add(R.id.answer6);
        answerIds.add(R.id.answer7);
        answerIds.add(R.id.answer8);
        answerIds.add(R.id.answer9);

        this.allAnswerTextVisibilityGone();

        setQuestion(1, R.id.question1, R.id.answer1, R.string.help_question1, R.string.help_answer1);
        setQuestion(2, R.id.question2, R.id.answer2, R.string.help_question2, R.string.help_answer2);
        setQuestion(3, R.id.question3, R.id.answer3, R.string.help_question3, R.string.help_answer3);
        setQuestion(4, R.id.question4, R.id.answer4, R.string.help_question4, R.string.help_answer4);
        setQuestion(5, R.id.question5, R.id.answer5, R.string.help_question5, R.string.help_answer5);
        setQuestion(6, R.id.question6, R.id.answer6, R.string.help_question6, R.string.help_answer6);
        setQuestion(7, R.id.question7, R.id.answer7, R.string.help_question7, R.string.help_answer7);
        setQuestion(8, R.id.question8, R.id.answer8, R.string.help_question8, R.string.help_answer8);
        setQuestion(9, R.id.question9, R.id.answer9, R.string.help_question9, R.string.help_answer9);
    }

    private void setQuestion(int index, int questionViewId, int answerViewId, int questionStringId, int answerStringId) {
        TextView questionView = findViewById(questionViewId);
        TextView answerView = findViewById(answerViewId);
        String questionText = getResources().getString(R.string.help_question, index) + ") " + getResources().getString(questionStringId);
        String answerText = " > " +  getResources().getString(answerStringId);

        questionView.setText(questionText);
        answerView.setText(answerText);

        questionView.setOnClickListener(v -> {
            boolean displayAnswer = false;
            if (answerView.getVisibility() != View.VISIBLE) {
                displayAnswer = true;
            }
            allAnswerTextVisibilityGone();
            if (displayAnswer) {
                answerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void allAnswerTextVisibilityGone() {
        for (int i = 0; i < answerIds.size(); i++) {
            TextView answer = findViewById(answerIds.get(i));
            answer.setVisibility(View.GONE);
        }
    }
}
