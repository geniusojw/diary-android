package org.jerrioh.diary.activity.draw;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.account.AccountDiaryApis;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.PropertyUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountActivity extends AbstractDiaryToolbarActivity {
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
        String lastSyncTimeString = PropertyUtil.getProperty(Property.Key.SYNC_ACCOUNT_DIARY_API_REQUEST_TIME, this);
        long lastSyncTime = Long.parseLong(lastSyncTimeString);

        TextView syncText = findViewById(R.id.text_view_account_sync);
        syncText.setText(getResources().getString(R.string.last_sync_time) + ": " + DateUtil.getDateStringFull(lastSyncTime));

        LinearLayout syncLayout = findViewById(R.id.linear_layout_account_sync);
        syncLayout.setOnClickListener(v -> {
            syncStart();
        });
    }

    private void syncStart() {
        TextView syncText = findViewById(R.id.text_view_account_sync);
        ProgressBar progressBar = findViewById(R.id.progress_bar_account);

        if (progressBar.getVisibility() != View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
            AuthorUtil.syncAccountDiaries(this, progressBar, syncText);
            AuthorUtil.refreshAccountToken(this);
        }
    }

    private void exportDiary() {
        LinearLayout exportLayout = findViewById(R.id.linear_layout_account_export);
        exportLayout.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(getResources().getString(R.string.email_export))
                    .setMessage(getResources().getString(R.string.email_export_confirm))
                    .setPositiveButton(getResources().getString(R.string.email_export_ok), (dialog, which) -> {
                        AccountDiaryApis accountDiaryApis = new AccountDiaryApis(this);
                        accountDiaryApis.exportDiaries(new ApiCallback() {
                            @Override
                            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                if (httpStatus == 200) {
                                    Toast.makeText(AccountActivity.this, getResources().getString(R.string.email_export_success), Toast.LENGTH_SHORT);
                                } else {
                                    Toast.makeText(AccountActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();;
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
            alertBuilder.setTitle(getResources().getString(R.string.sign_out))
                    .setMessage(getResources().getString(R.string.sign_out_confirm))
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                        AuthorUtil.accountSignOut(this);
                        setResult(RESULT_OK);
                        finish();

                        Toast.makeText(this, getResources().getString(R.string.sign_out_success), Toast.LENGTH_SHORT);
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();;
        });
    }
}
