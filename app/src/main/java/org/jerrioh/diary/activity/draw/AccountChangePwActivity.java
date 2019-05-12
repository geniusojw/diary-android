package org.jerrioh.diary.activity.draw;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.account.AccountApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.CommonUtil;
import org.json.JSONObject;

public class AccountChangePwActivity extends CommonActionBarActivity {
    private static final String TAG = "AccountChangePwActivity";

    private static final int SCREEN_STATUS_CHANGE_PASSWORD = 0;
    private static final int SCREEN_STATUS_FIND_PASSWORD = 1;
    private static final int SCREEN_STATUS_DELETE_ACCOUNT = 2;

    private TextView titleView;
    private TextView descriptionView;

    private TextInputLayout emailTextInput;
    private TextInputEditText emailTextInputEdit;

    private TextInputLayout oldPasswordTextInput;
    private TextInputEditText oldPasswordTextInputEdit;

    private TextInputLayout newPasswordTextInput;
    private TextInputEditText newPasswordTextInputEdit;

    private TextInputLayout confirmNewPasswordTextInput;
    private TextInputEditText confirmNewPasswordTextInputEdit;

    private Button changPasswordButton;

    private TextView optionView1;
    private TextView optionView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change_password);
        setCommonToolBar("Change Password & Delete Account");

        titleView = findViewById(R.id.text_view_change_password_title);
        descriptionView = findViewById(R.id.text_view_change_password_description);

        emailTextInput = findViewById(R.id.text_input_layout_change_password_email);
        emailTextInputEdit = findViewById(R.id.text_input_edit_text_change_password_email);

        oldPasswordTextInput = findViewById(R.id.text_input_layout_change_password_old_password);
        oldPasswordTextInputEdit = findViewById(R.id.text_input_edit_text_change_password_old_password);

        newPasswordTextInput = findViewById(R.id.text_input_layout_change_password_new_password);
        newPasswordTextInputEdit = findViewById(R.id.text_input_edit_text_change_password_new_password);

        confirmNewPasswordTextInputEdit = findViewById(R.id.text_input_edit_text_change_password_confirm_new_password);
        confirmNewPasswordTextInput = findViewById(R.id.text_input_layout_change_password_confirm_new_password);

        // 확인버튼
        changPasswordButton = findViewById(R.id.button_change_password);

        // 다른 옵션 선택
        optionView1 = findViewById(R.id.text_view_change_password_option1);
        optionView2 = findViewById(R.id.text_view_change_password_option2);

        // 회원 이메일로 고정
        Author author = AuthorUtil.getAuthor(this);
        emailTextInputEdit.setText(author.getAccountEmail());
        emailTextInput.setEnabled(false);
        emailTextInput.setClickable(false);

        changeScreenStatus(SCREEN_STATUS_CHANGE_PASSWORD);
    }

    private void changeScreenStatus(int screenStatus) {
        if (screenStatus == SCREEN_STATUS_CHANGE_PASSWORD) {
            changePasswordStatus();
        } else if (screenStatus == SCREEN_STATUS_FIND_PASSWORD) {
            findPasswordStatus();
        } else if (screenStatus == SCREEN_STATUS_DELETE_ACCOUNT) {
            deleteAccountStatus();
        }
    }

    private void changePasswordStatus() {
        titleView.setText("Change password");
        titleView.setTextColor(getResources().getColor(R.color.prussianBlue));

        descriptionView.setText("Change your password.");
        descriptionView.setTextColor(getResources().getColor(R.color.cobaltBlue));

        oldPasswordTextInput.setVisibility(View.VISIBLE);
        newPasswordTextInput.setVisibility(View.VISIBLE);
        confirmNewPasswordTextInput.setVisibility(View.VISIBLE);

        changPasswordButton.setText("Change password");
        changPasswordButton.setTextColor(getResources().getColor(R.color.colorWhite));
        changPasswordButton.setOnClickListener(v -> {
            changePassword(emailTextInputEdit.getText().toString(), oldPasswordTextInputEdit.getText().toString(), newPasswordTextInputEdit.getText().toString(), confirmNewPasswordTextInputEdit.getText().toString());
        });

        optionView1.setText("Forgot password?");
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_FIND_PASSWORD);
        });

        optionView2.setText("Delete account.");
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_DELETE_ACCOUNT);
        });
    }

    private void findPasswordStatus() {
        titleView.setText("Find password");
        titleView.setTextColor(getResources().getColor(R.color.prussianBlue));

        descriptionView.setText("You can get a new password by email.");
        descriptionView.setTextColor(getResources().getColor(R.color.cobaltBlue));

        oldPasswordTextInput.setVisibility(View.GONE);
        newPasswordTextInput.setVisibility(View.GONE);
        confirmNewPasswordTextInput.setVisibility(View.GONE);

        changPasswordButton.setText("Get new password");
        changPasswordButton.setTextColor(getResources().getColor(R.color.colorWhite));
        changPasswordButton.setOnClickListener(v -> {
            findPassword(emailTextInputEdit.getText().toString());
        });

        optionView1.setText("Change password.");
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_CHANGE_PASSWORD);
        });

        optionView2.setText("Delete account.");
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_DELETE_ACCOUNT);
        });
    }

    private void deleteAccountStatus() {
        titleView.setText("Delete Author");
        titleView.setTextColor(getResources().getColor(R.color.colorRed));

        descriptionView.setText("Once you delete your account,\nthere is no going back.\nPlease be certain.");
        descriptionView.setTextColor(getResources().getColor(R.color.colorMaroon));

        oldPasswordTextInput.setVisibility(View.VISIBLE);
        newPasswordTextInput.setVisibility(View.GONE);
        confirmNewPasswordTextInput.setVisibility(View.GONE);

        changPasswordButton.setText("Delete your account");
        changPasswordButton.setTextColor(getResources().getColor(R.color.colorRed));
        changPasswordButton.setOnClickListener(v -> {
            deleteAccount(emailTextInputEdit.getText().toString());
        });

        optionView1.setText("Change password.");
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_CHANGE_PASSWORD);
        });

        optionView2.setText("Forgot password?");
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_FIND_PASSWORD);
        });
    }

    private void changePassword(String email, String oldPassword, String newPassword, String newPasswordConfirm) {
        if (CommonUtil.isAnyEmpty(email, oldPassword, newPassword, newPasswordConfirm)) {
            Toast.makeText(this, "missing parameter", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(newPassword, newPasswordConfirm)) {
            Toast.makeText(this, "password != passwordConfirm", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CommonUtil.isEmailPattern(email)) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO API CALL
    }

    private void findPassword(String email) {
        if (CommonUtil.isAnyEmpty(email)) {
            Toast.makeText(this, "missing parameter", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CommonUtil.isEmailPattern(email)) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiCallback callback = new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) {
                if (httpStatus == 200) {
                    Toast.makeText(AccountChangePwActivity.this, "Check your email for a new password", Toast.LENGTH_LONG).show();
                }
            }
        };

        Author author = AuthorUtil.getAuthor(this);
        AccountApis accountApis = new AccountApis(this);
        accountApis.findPassword(email, callback);
    }

    private void deleteAccount(String email) {
        if (CommonUtil.isAnyEmpty(email)) {
            Toast.makeText(this, "missing parameter", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CommonUtil.isEmailPattern(email)) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO API CALL
    }
}
