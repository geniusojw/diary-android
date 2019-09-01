package org.jerrioh.diary.activity.draw;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.account.AccountApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.CommonUtil;
import org.json.JSONObject;

public class AccountChangePwActivity extends AbstractDiaryToolbarActivity {
    private static final String TAG = "AccountChangePwActivity";

    private static final int SCREEN_STATUS_CHANGE_PASSWORD = 0;
    private static final int SCREEN_STATUS_FIND_PASSWORD = 1;
    private static final int SCREEN_STATUS_DELETE_ACCOUNT = 2;

    private ImageView imageView;
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

    private boolean waitingResponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change_password);
        setCommonToolBar(getResources().getString(R.string.change_account));

        imageView = findViewById(R.id.image_view_change_password);
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
        imageView.setImageResource(R.drawable.ic_swap_horiz_black_24dp);
        titleView.setText(getResources().getString(R.string.change_password));
        titleView.setTextColor(getResources().getColor(R.color.prussianBlue));

        descriptionView.setText(getResources().getString(R.string.change_password_description));
        descriptionView.setTextColor(getResources().getColor(R.color.cobaltBlue));

        oldPasswordTextInput.setVisibility(View.VISIBLE);
        newPasswordTextInput.setVisibility(View.VISIBLE);
        confirmNewPasswordTextInput.setVisibility(View.VISIBLE);

        changPasswordButton.setText(getResources().getString(R.string.change_password));
        changPasswordButton.setTextColor(getResources().getColor(R.color.colorWhite));
        changPasswordButton.setOnClickListener(v -> {
            changePassword();
        });

        optionView1.setText(getResources().getString(R.string.forgot_password));
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_FIND_PASSWORD);
        });

        optionView2.setText(getResources().getString(R.string.delete_account));
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_DELETE_ACCOUNT);
        });
    }

    private void findPasswordStatus() {
        imageView.setImageResource(R.drawable.ic_mail_outline_black_24dp);
        titleView.setText(getResources().getString(R.string.find_password));
        titleView.setTextColor(getResources().getColor(R.color.prussianBlue));

        descriptionView.setText(getResources().getString(R.string.find_password_description));
        descriptionView.setTextColor(getResources().getColor(R.color.cobaltBlue));

        oldPasswordTextInput.setVisibility(View.GONE);
        newPasswordTextInput.setVisibility(View.GONE);
        confirmNewPasswordTextInput.setVisibility(View.GONE);

        changPasswordButton.setText(getResources().getString(R.string.find_password_button));
        changPasswordButton.setTextColor(getResources().getColor(R.color.colorWhite));
        changPasswordButton.setOnClickListener(v -> {
            findPassword();
        });

        optionView1.setText(getResources().getString(R.string.change_password));
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_CHANGE_PASSWORD);
        });

        optionView2.setText(getResources().getString(R.string.delete_account));
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_DELETE_ACCOUNT);
        });
    }

    private void deleteAccountStatus() {
        imageView.setImageResource(R.drawable.ic_cloud_off_black_24dp);
        titleView.setText(getResources().getString(R.string.delete_account));
        titleView.setTextColor(getResources().getColor(R.color.colorRed));

        descriptionView.setText(getResources().getString(R.string.delete_account_description));
        descriptionView.setTextColor(getResources().getColor(R.color.colorMaroon));

        oldPasswordTextInput.setVisibility(View.VISIBLE);
        newPasswordTextInput.setVisibility(View.GONE);
        confirmNewPasswordTextInput.setVisibility(View.GONE);

        changPasswordButton.setText(getResources().getString(R.string.delete_account_button));
        changPasswordButton.setTextColor(getResources().getColor(R.color.colorRed));
        changPasswordButton.setOnClickListener(v -> {
            deleteAccount();
        });

        optionView1.setText(getResources().getString(R.string.change_password));
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_CHANGE_PASSWORD);
        });

        optionView2.setText(getResources().getString(R.string.forgot_password));
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_FIND_PASSWORD);
        });
    }

    private void changePassword() {
        if (waitingResponse) {
            return;
        }

        String failMessage = null;
        if (TextUtils.isEmpty(emailTextInputEdit.getText())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (TextUtils.isEmpty(oldPasswordTextInputEdit.getText())) {
            oldPasswordTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (TextUtils.isEmpty(newPasswordTextInputEdit.getText())) {
            newPasswordTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (TextUtils.isEmpty(confirmNewPasswordTextInputEdit.getText())) {
            confirmNewPasswordTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (!TextUtils.equals(newPasswordTextInputEdit.getText(), confirmNewPasswordTextInputEdit.getText())) {
            confirmNewPasswordTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.confirm_password_confirm);
        } else if (!CommonUtil.isEmailPattern(emailTextInputEdit.getText().toString())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.not_email_pattern);
        }

        if (failMessage == null) {
            ApiCallback callback = new ApiCallback() {
                @Override
                protected void execute(int httpStatus, JSONObject jsonObject) {
                    waitingResponse = false;
                    if (httpStatus == 200) {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.change_password_success), Toast.LENGTH_LONG).show();
                        finish();
                    } else if (httpStatus == 401) {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.incorrect_password), Toast.LENGTH_LONG).show();
                    } else if (httpStatus == 412) {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.invalid_password), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_LONG).show();
                    }
                }
            };

            waitingResponse = true;
            Author author = AuthorUtil.getAuthor(this);
            AccountApis accountApis = new AccountApis(this);
            accountApis.changePassword(oldPasswordTextInputEdit.getText().toString(), newPasswordTextInputEdit.getText().toString(), callback);

        } else {
            Toast.makeText(this, failMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void findPassword() {
        if (waitingResponse) {
            return;
        }

        String failMessage = null;
        if (TextUtils.isEmpty(emailTextInputEdit.getText())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (!CommonUtil.isEmailPattern(emailTextInputEdit.getText().toString())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.not_email_pattern);
        }

        if (failMessage == null) {
            ApiCallback callback = new ApiCallback() {
                @Override
                protected void execute(int httpStatus, JSONObject jsonObject) {
                    waitingResponse = false;
                    if (httpStatus == 200) {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.find_password_success), Toast.LENGTH_LONG).show();
                    } else if (httpStatus == 404) {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.find_password_fail), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_LONG).show();
                    }
                }
            };

            waitingResponse = true;
            Author author = AuthorUtil.getAuthor(this);
            AccountApis accountApis = new AccountApis(this);
            accountApis.findPassword(emailTextInputEdit.getText().toString(), callback);

        } else {
            Toast.makeText(this, failMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAccount() {
        if (waitingResponse) {
            return;
        }

        String failMessage = null;
        if (TextUtils.isEmpty(emailTextInputEdit.getText())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (TextUtils.isEmpty(oldPasswordTextInputEdit.getText())) {
            oldPasswordTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (!CommonUtil.isEmailPattern(emailTextInputEdit.getText().toString())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.not_email_pattern);
        }

        if (failMessage == null) {
            ApiCallback callback = new ApiCallback() {
                @Override
                protected void execute(int httpStatus, JSONObject jsonObject) {
                    waitingResponse = false;
                    if (httpStatus == 200) {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.delete_account_success), Toast.LENGTH_LONG).show();
                    } else if (httpStatus == 401) {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.incorrect_password), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AccountChangePwActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_LONG).show();
                    }
                }
            };

            waitingResponse = true;
            Author author = AuthorUtil.getAuthor(this);
            AccountApis accountApis = new AccountApis(this);
            accountApis.deleteAccount(author.getAccountEmail(), oldPasswordTextInputEdit.getText().toString(), callback);
        } else {
            Toast.makeText(this, failMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
