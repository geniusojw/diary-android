package org.jerrioh.diary.activity.draw;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class AccountSignUpActivity extends CommonActionBarActivity {

    public static final String TAG = "AccountSignUpActivity";

    private static final int SCREEN_STATUS_SIGN_UP = 0;
    private static final int SCREEN_STATUS_SIGN_IN = 1;
    private static final int SCREEN_STATUS_FIND_PASSWORD = 2;

    private TextView titleView;
    private TextView decriptionView;

    private TextInputLayout emailTextInput;
    private TextInputEditText emailTextInputEdit;

    private TextInputLayout passwordTextInput;
    private TextInputEditText passwordTextInputEdit;

    private TextInputEditText confirmPasswordTextInputEdit;
    private TextInputLayout confirmPasswordTextInput;

    private Button singUpButton;

    private TextView optionView1;
    private TextView optionView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_sign_up);
        setCommonToolBar("Start a diary");

        titleView = findViewById(R.id.text_view_sign_up_title);
        decriptionView = findViewById(R.id.text_view_sign_up_description);

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
        titleView.setText("Getting Started");
        decriptionView.setText("Additional Member Services!\nEasy Recovery!");

        passwordTextInput.setVisibility(View.VISIBLE);
        confirmPasswordTextInput.setVisibility(View.VISIBLE);

        singUpButton.setText("Sign up for free");
        singUpButton.setOnClickListener(v -> {
            signUp(emailTextInputEdit.getText().toString(), passwordTextInputEdit.getText().toString(), confirmPasswordTextInputEdit.getText().toString());
        });

        optionView1.setText("Already have an account?");
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_SIGN_IN);
        });

        optionView2.setText("Forgot password?");
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_FIND_PASSWORD);
        });
    }

    private void signInStatus() {
        titleView.setText("Sign in");
        decriptionView.setText("Sign in with a member email.");

        passwordTextInput.setVisibility(View.VISIBLE);
        confirmPasswordTextInput.setVisibility(View.GONE);

        singUpButton.setText("Sign in");
        singUpButton.setOnClickListener(v -> {
            signIn(emailTextInputEdit.getText().toString(), passwordTextInputEdit.getText().toString());
        });

        optionView1.setText("Create an account for free.");
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_SIGN_UP);
        });

        optionView2.setText("Forgot password?");
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_FIND_PASSWORD);
        });
    }

    private void findPasswordStatus() {
        titleView.setText("Find password");
        decriptionView.setText("You can get a new password by email.\nEnter your email address.");

        passwordTextInput.setVisibility(View.GONE);
        confirmPasswordTextInput.setVisibility(View.GONE);

        singUpButton.setText("Get new password");
        singUpButton.setOnClickListener(v -> {
            findPassword(emailTextInputEdit.getText().toString());
        });

        optionView1.setText("Create an account for free.");
        optionView1.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_SIGN_UP);
        });

        optionView2.setText("Already have an account?");
        optionView2.setOnClickListener(v -> {
            changeScreenStatus(SCREEN_STATUS_SIGN_IN);
        });
    }

    private void signUp(String email, String password, String passwordConfirm) {
        if (CommonUtil.isAnyEmpty(email, password, passwordConfirm)) {
            Toast.makeText(this, "missing parameter", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(password, passwordConfirm)) {
            Toast.makeText(this, "password != passwordConfirm", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CommonUtil.isEmailPattern(email)) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiCallback callback = new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    signInOrSignUpComplete(email, jsonObject);
                } else {
                    Toast.makeText(AccountSignUpActivity.this, "Email is invalid or already taken", Toast.LENGTH_LONG).show();
                }
            }
        };

        Author author = AuthorUtil.getAuthor(this);
        AccountApis accountApis = new AccountApis(this);
        accountApis.signUp(email, password, author.getAuthorId(), callback);
    }

    private void signIn(String email, String password) {
        if (CommonUtil.isAnyEmpty(email, password)) {
            Toast.makeText(this, "missing parameter", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CommonUtil.isEmailPattern(email)) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiCallback callback = new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    signInOrSignUpComplete(email, jsonObject);
                } else {
                    Toast.makeText(AccountSignUpActivity.this, "Incorrect email or password", Toast.LENGTH_LONG).show();
                }
            }
        };

        Author author = AuthorUtil.getAuthor(this);
        AccountApis accountApis = new AccountApis(this);
        accountApis.signIn(email, password, author.getAuthorId(), callback);
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
                    changeScreenStatus(SCREEN_STATUS_SIGN_IN);
                    Toast.makeText(AccountSignUpActivity.this, "Check your email for a new password.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AccountSignUpActivity.this, "Can't find that email, sorry", Toast.LENGTH_LONG).show();
                }
            }
        };

        Author author = AuthorUtil.getAuthor(this);
        AccountApis accountApis = new AccountApis(this);
        accountApis.findPassword(email, callback);
    }

    private void signInOrSignUpComplete(String email, JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String token = data.getString("token");
        if (TextUtils.isEmpty(token)) {
            Log.e(TAG, "token is empty.");
            return;
        }

        AuthorUtil.accountSignIn(this, email, token);

        super.setResult(RESULT_OK);
        super.finish();
    }
}
