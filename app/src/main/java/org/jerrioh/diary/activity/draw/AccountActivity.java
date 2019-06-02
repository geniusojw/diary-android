package org.jerrioh.diary.activity.draw;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.model.Author;

public class AccountActivity extends CommonActionBarActivity {
    private static final String TAG = "AccountActivity";

    private static final int REQUEST_ACCOUNT_SIGN_IN_ACTIVITY = 1;
    private static final int REQUEST_ACCOUNT_CHANGE_PASSWORD_ACTIVITY = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCOUNT_SIGN_IN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK); // 회원가입, 또는 로그인 성공
            }
            finish(); // 로그인 안된 경우 AccountActivity 에서는 처리할 내용이 없으므로 바로 종료한다.

        } else if (requestCode == REQUEST_ACCOUNT_CHANGE_PASSWORD_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK); // 회원삭제
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Author author = AuthorUtil.getAuthor(this);

        if (!TextUtils.isEmpty(author.getAccountEmail())) { // 로그인 상태
            setCommonToolBar(author.getAccountEmail());
            setAccountInterfaces();

        } else {
            Intent intent = new Intent(this, AccountSignUpActivity.class);
            startActivityForResult(intent, REQUEST_ACCOUNT_SIGN_IN_ACTIVITY);
        }
    }

    private void setAccountInterfaces() {
        this.accountDiarySync();
        this.exportDiary();
        this.startActivityChangePasswordAndDeleteAccount();
        this.signOut();
    }

    private void accountDiarySync() {
        LinearLayout syncLayout = findViewById(R.id.linear_layout_account_sync);
        syncLayout.setOnClickListener(v -> {
            ProgressBar progressBar = findViewById(R.id.progress_bar_account);
            if (progressBar.getVisibility() != View.VISIBLE) {
                progressBar.setVisibility(View.VISIBLE);
                AuthorUtil.syncDiaries(this, progressBar);
            }
        });
    }

    private void exportDiary() {
        LinearLayout exportLayout = findViewById(R.id.linear_layout_account_export);
        exportLayout.setOnClickListener(v -> {
            Toast.makeText(this, "아직 개발되지 않았다.", Toast.LENGTH_SHORT);
        });
    }

    private void startActivityChangePasswordAndDeleteAccount() {
        LinearLayout changePasswordAndDeleteAccountLayout = findViewById(R.id.linear_layout_account_change_password);
        changePasswordAndDeleteAccountLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountChangePwActivity.class);
            startActivityForResult(intent, REQUEST_ACCOUNT_CHANGE_PASSWORD_ACTIVITY);
        });
    }

    private void signOut() {
        LinearLayout signOutLayout = findViewById(R.id.linear_layout_account_sign_out);
        signOutLayout.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("Sign out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        AuthorUtil.accountSignOut(this);
                        setResult(RESULT_OK);
                        finish();

                        Toast.makeText(this, "You have been signed out.", Toast.LENGTH_SHORT);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();;
        });
    }
}
