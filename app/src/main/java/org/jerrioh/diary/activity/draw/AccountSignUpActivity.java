package org.jerrioh.diary.activity.draw;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.account.AccountApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.CommonUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountSignUpActivity extends AbstractDiaryToolbarActivity {

    public static final String TAG = "AccountSignUpActivity";

    private static final int SCREEN_STATUS_SIGN_UP = 0;
    private static final int SCREEN_STATUS_SIGN_IN = 1;
    private static final int SCREEN_STATUS_FIND_PASSWORD = 2;

    private ImageView imageView;
    private TextView titleView;
    private TextView descriptionView;

    private TextInputLayout emailTextInput;
    private TextInputEditText emailTextInputEdit;

    private TextInputLayout passwordTextInput;
    private TextInputEditText passwordTextInputEdit;

    private TextInputEditText confirmPasswordTextInputEdit;
    private TextInputLayout confirmPasswordTextInput;

    private Button singUpButton;

    private TextView optionView1;
    private TextView optionView2;

    private boolean waitingResponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_sign_up);
        setCommonToolBar(getResources().getString(R.string.start_a_diary));

        imageView = findViewById(R.id.image_view_sign_up);
        titleView = findViewById(R.id.text_view_sign_up_title);
        descriptionView = findViewById(R.id.text_view_sign_up_description);

        emailTextInput = findViewById(R.id.text_input_layout_sign_up_email);
        emailTextInputEdit = findViewById(R.id.text_input_edit_text_sign_up_email);

        passwordTextInput = findViewById(R.id.text_input_layout_sign_up_password);
        passwordTextInputEdit = findViewById(R.id.text_input_edit_text_sign_up_password);

        confirmPasswordTextInputEdit = findViewById(R.id.text_input_edit_text_sign_up_confirm_password);
        confirmPasswordTextInput = findViewById(R.id.text_input_layout_sign_up_confirm_password);

        singUpButton = findViewById(R.id.button_sign_up);

        optionView1 = findViewById(R.id.text_view_sign_up_option1);
        optionView2 = findViewById(R.id.text_view_sign_up_option2);

        changeScreenStatus(SCREEN_STATUS_SIGN_UP);
    }

    private void changeScreenStatus(int screenStatus) {
        if (screenStatus == SCREEN_STATUS_SIGN_UP) {
            signUpStatus();
        } else if (screenStatus == SCREEN_STATUS_SIGN_IN) {
            signInStatus();
        } else if (screenStatus == SCREEN_STATUS_FIND_PASSWORD) {
            findPasswordStatus();
        }
    }

    private void signUpStatus() {
        imageView.setImageResource(R.drawable.ic_cloud_queue_black_24dp);
        titleView.setText(getResources().getString(R.string.free_account));
        descriptionView.setText(getResources().getString(R.string.free_account_description));

        passwordTextInput.setVisibility(View.VISIBLE);
        confirmPasswordTextInput.setVisibility(View.VISIBLE);

        singUpButton.setText(getResources().getString(R.string.free_account_button));
        singUpButton.setOnClickListener(v -> {
            signUp();
        });

        optionView1.setText(getResources().getString(R.string.already_has_account));
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_SIGN_IN);
        });

        optionView2.setText(getResources().getString(R.string.forgot_password));
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_FIND_PASSWORD);
        });
    }

    private void signInStatus() {
        imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
        titleView.setText(getResources().getString(R.string.sign_in));
        descriptionView.setText(getResources().getString(R.string.sign_in_description));

        passwordTextInput.setVisibility(View.VISIBLE);
        confirmPasswordTextInput.setVisibility(View.GONE);

        singUpButton.setText(getResources().getString(R.string.sign_in));
        singUpButton.setOnClickListener(v -> {
            signIn();
        });

        optionView1.setText(getResources().getString(R.string.create_free));
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_SIGN_UP);
        });

        optionView2.setText(getResources().getString(R.string.forgot_password));
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_FIND_PASSWORD);
        });
    }

    private void findPasswordStatus() {
        imageView.setImageResource(R.drawable.ic_mail_outline_black_24dp);
        titleView.setText(getResources().getString(R.string.find_password));
        descriptionView.setText(getResources().getString(R.string.find_password_description));

        passwordTextInput.setVisibility(View.GONE);
        confirmPasswordTextInput.setVisibility(View.GONE);

        singUpButton.setText(getResources().getString(R.string.find_password_button));
        singUpButton.setOnClickListener(v -> {
            findPassword();
        });

        optionView1.setText(getResources().getString(R.string.create_free));
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_SIGN_UP);
        });

        optionView2.setText(getResources().getString(R.string.already_has_account));
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_SIGN_IN);
        });
    }

    private void signUp() {
        if (waitingResponse) {
            return;
        }

        String failMessage = null;
        if (TextUtils.isEmpty(emailTextInputEdit.getText())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (TextUtils.isEmpty(passwordTextInputEdit.getText())) {
            passwordTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (TextUtils.isEmpty(confirmPasswordTextInputEdit.getText())) {
            confirmPasswordTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (!TextUtils.equals(passwordTextInputEdit.getText(), confirmPasswordTextInputEdit.getText())) {
            confirmPasswordTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.confirm_password_confirm);
        } else if (!CommonUtil.isEmailPattern(emailTextInputEdit.getText().toString())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.not_email_pattern);
        }

        if (failMessage == null) {
            String email = emailTextInputEdit.getText().toString();
            String password = passwordTextInputEdit.getText().toString();

            ApiCallback callback = new ApiCallback() {
                @Override
                protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                    waitingResponse = false;
                    if (httpStatus == 200) {
                        signInOrSignUpComplete(email, jsonObject, true);
                    } else if (httpStatus == 409) {
                        Toast.makeText(AccountSignUpActivity.this, getResources().getString(R.string.sign_up_fail), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AccountSignUpActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_LONG).show();
                    }
                }
            };

            waitingResponse = true;
            Author author = AuthorUtil.getAuthor(this);
            AccountApis accountApis = new AccountApis(this);
            accountApis.signUp(email, password, author.getAuthorId(), callback);


        } else {
            Toast.makeText(this, failMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {
        if (waitingResponse) {
            return;
        }

        String failMessage = null;
        if (TextUtils.isEmpty(emailTextInputEdit.getText())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (TextUtils.isEmpty(passwordTextInputEdit.getText())) {
            passwordTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.missing_parameter);
        } else if (!CommonUtil.isEmailPattern(emailTextInputEdit.getText().toString())) {
            emailTextInputEdit.requestFocus();
            failMessage = getResources().getString(R.string.not_email_pattern);
        }

        if (failMessage == null) {
            String email = emailTextInputEdit.getText().toString();
            String password = passwordTextInputEdit.getText().toString();

            ApiCallback callback = new ApiCallback() {
                @Override
                protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                    waitingResponse = false;
                    if (httpStatus == 200) {
                        signInOrSignUpComplete(email, jsonObject, false);
                    } else if (httpStatus == 401) {
                        Toast.makeText(AccountSignUpActivity.this, getResources().getString(R.string.sign_in_fail), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AccountSignUpActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_LONG).show();
                    }
                }
            };

            waitingResponse = true;
            Author author = AuthorUtil.getAuthor(this);
            AccountApis accountApis = new AccountApis(this);
            accountApis.signIn(email, password, author.getAuthorId(), callback);

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
                        changeScreenStatus(SCREEN_STATUS_SIGN_IN);
                        Toast.makeText(AccountSignUpActivity.this, getResources().getString(R.string.find_password_success), Toast.LENGTH_LONG).show();
                    } else if (httpStatus == 404) {
                        Toast.makeText(AccountSignUpActivity.this, getResources().getString(R.string.find_password_fail), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AccountSignUpActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_LONG).show();
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

    private void signInOrSignUpComplete(String email, JSONObject jsonObject, boolean isNew) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String token = data.getString("token");
        if (TextUtils.isEmpty(token)) {
            Log.e(TAG, "TOKEN IS EMPTY.");
            return;
        }

        AuthorUtil.accountSignIn(this, email.toLowerCase(), token);

        if (isNew) {
            Toast.makeText(AccountSignUpActivity.this, getResources().getString(R.string.sign_up_success), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(AccountSignUpActivity.this, getResources().getString(R.string.sign_in_success), Toast.LENGTH_LONG).show();
        }

        super.setResult(RESULT_OK);
        super.finish();
    }
}
