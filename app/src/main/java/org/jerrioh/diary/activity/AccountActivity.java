package org.jerrioh.diary.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.AccountApis;
import org.jerrioh.diary.api.ApiUtil;
import org.jerrioh.diary.api.DiaryApis;
import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.db.AccountDao;
import org.jerrioh.diary.db.DiaryDao;
import org.jerrioh.diary.db.LetterDao;
import org.jerrioh.diary.dbmodel.Account;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountActivity extends AppCompatActivity {

    public static final String TAG = "AccountActivity";

    private int accountStatus = 0; // 0: signup, 1: signin, 2: findpassword

    private TextView login_text1;
    private TextView login_text2;
    private TextView login_text3;

    private TextInputLayout login_email;
    private TextInputLayout login_password;
    private TextInputLayout login_password_confirm;

    private TextInputEditText login_email_input;
    private TextInputEditText login_password_input;
    private TextInputEditText login_password_confirm_input;
    private Button login_button;

    private TextView login_option1;
    private TextView login_option2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        login_text1 = findViewById(R.id.login_text1);
        login_text2 = findViewById(R.id.login_text2);
        login_text3 = findViewById(R.id.login_text3);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_password_confirm = findViewById(R.id.login_password_confirm);

        login_email_input = findViewById(R.id.login_email_input);
        login_password_input = findViewById(R.id.login_password_input);
        login_password_confirm_input = findViewById(R.id.login_password_confirm_input);

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(v -> {
            try {
                if (accountStatus == 0) {
                    signup(login_email_input.getText().toString(), login_password_input.getText().toString(), login_password_confirm_input.getText().toString());
                } else if (accountStatus == 1) {
                    signin(login_email_input.getText().toString(), login_password_input.getText().toString());
                } else {
                    findpassword(login_email_input.getText().toString());
                }
            } catch (JSONException e) {
                Log.e(TAG, "json exception : " + e);
            }
        });

        login_option1 = findViewById(R.id.login_option1);
        login_option1.setOnClickListener(v -> {
            if (accountStatus == 0) {
                status1_signin();
            } else if (accountStatus == 1) {
                status0_signup();
            } else {
                status0_signup();
            }
        });

        login_option2 = findViewById(R.id.login_option2);
        login_option2.setOnClickListener(v -> {
            if (accountStatus == 0) {
                status2_findpassword();
            } else if (accountStatus == 1) {
                status2_findpassword();
            } else {
                status1_signin();
            }
        });
    }

    private void signup(String email, String password, String passwordConfirm) throws JSONException {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "email is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "password is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(this, "passwordConfirm is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(password, passwordConfirm)) {
            Toast.makeText(this, "password != passwordConfirm", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiUtil.Callback callback = (jsonObject) -> {
            signInOrSignUpComplete(email, jsonObject, "Email is invalid or already taken");
        };

        AccountApis.signinCallback(this, email, password, callback);
    }

    private void signin(String email, String password) throws JSONException {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "email is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "password is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiUtil.Callback callback = (jsonObject) -> {
            signInOrSignUpComplete(email, jsonObject, "Incorrect email or password");
        };

        AccountApis.signinCallback(this, email, password, callback);
    }

    private void signInOrSignUpComplete(String email, JSONObject jsonObject, String failMessage) throws JSONException {
        boolean success = false;
        String token = null;
        int code = jsonObject.getInt("code");
        if (code == 200000) { // 저장성공 or 이미 서버에 저장됨
            JSONObject data = jsonObject.getJSONObject("data");
            token = data.getString("token");
            if (!TextUtils.isEmpty(token)) {
                success = true;
            }
        }

        if (success) {
            AccountDao accountDao = new AccountDao(this);
            accountDao.updateAccount(email, token, "로그인 유저 닉넴", "로그인 된 사람");

            Information.updateMyAccount(this);

            // 현재 클라이언트의 모든 일기를 지우고 서버에서 일기를 읽어온다.
            // TODO 현재 일기(와 편지)를 살릴것인지를 선택할 수 있도록(비교후 일부 일기를 서버로 전송 필요)
            DiaryDao diaryDao = new DiaryDao(this);
            diaryDao.removeAllDiary();
            Log.d(TAG, "remove all diary");

            LetterDao letterDao = new LetterDao(this);
            letterDao.removeAllLetter();
            Log.d(TAG, "remove all letter");

            DiaryApis.getAllDiary(this, token);
            super.finish();
        } else{
            Toast.makeText(this, failMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void findpassword(String email) throws JSONException {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "email is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = false;
        if (success) {
            Toast.makeText(this, "Check your email for a new password.", Toast.LENGTH_LONG).show();
            status1_signin();
        } else {
            Toast.makeText(this, "Can't find that email, sorry", Toast.LENGTH_LONG).show();
        }
    }

    private void status0_signup() {
        accountStatus = 0;
        login_text1.setVisibility(View.VISIBLE);
        login_text2.setVisibility(View.VISIBLE);
        login_text3.setVisibility(View.VISIBLE);

        login_text1.setText("Getting Started");
        login_text2.setText("- Easy Recovery");
        login_text3.setText("- Additional Member Services");

        login_option1.setText("Already have an account?");
        login_option2.setText("Forgot password?");
        login_password.setVisibility(View.VISIBLE);
        login_password_confirm.setVisibility(View.VISIBLE);
        login_button.setText("Sign up for free");
    }

    private void status1_signin() {
        accountStatus = 1;
        login_text1.setVisibility(View.VISIBLE);
        login_text2.setVisibility(View.VISIBLE);
        login_text3.setVisibility(View.GONE);

        login_text1.setText("Sign-In");
        login_text2.setText("- Enter your email address");

        login_option1.setText("Create an account for free.");
        login_option2.setText("Forgot password?");
        login_password.setVisibility(View.VISIBLE);
        login_password_confirm.setVisibility(View.GONE);
        login_button.setText("Sign in");
    }

    private void status2_findpassword() {
        accountStatus = 2;
        login_text1.setVisibility(View.VISIBLE);
        login_text2.setVisibility(View.VISIBLE);
        login_text3.setVisibility(View.VISIBLE);

        login_text1.setText("Find Password");
        login_text2.setText("- Enter your email address.");
        login_text3.setText("  You will get your new password");

        login_option1.setText("Create an account for free.");
        login_option2.setText("Already have an account?");
        login_password.setVisibility(View.GONE);
        login_password_confirm.setVisibility(View.GONE);
        login_button.setText("Get new password");
    }
}
