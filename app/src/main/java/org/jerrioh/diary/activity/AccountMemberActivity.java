package org.jerrioh.diary.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.util.CurrentAccountUtil;
import org.json.JSONException;

public class AccountMemberActivity extends CommonActionBarActivity {

    public static final String TAG = "AccountMemberActivity";

    private int accountStatus = 0; // 0: export diary, 1: change password, 2: delete, 3: find password

    private TextView member_text1;
    private TextView member_text2;

    private TextInputLayout member_email;
    private TextInputLayout member_old_password;
    private TextInputLayout member_password;
    private TextInputLayout member_password_confirm;

    private TextInputEditText member_email_input;
    private TextInputEditText member_old_password_input;
    private TextInputEditText member_password_input;
    private TextInputEditText member_password_confirm_input;

    private Button member_button;

    private TextView member_option0_5;
    private TextView member_option1;
    private TextView member_option2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_member);
        setCommonToolBar("Account");

        member_text1 = findViewById(R.id.member_text1);
        member_text2 = findViewById(R.id.member_text2);

        member_email = findViewById(R.id.member_email);
        member_old_password = findViewById(R.id.member_old_password);
        member_password = findViewById(R.id.member_password);
        member_password_confirm = findViewById(R.id.member_password_confirm);

        member_email_input = findViewById(R.id.member_email_input);
        member_old_password_input = findViewById(R.id.member_old_password_input);
        member_password_input = findViewById(R.id.member_password_input);
        member_password_confirm_input = findViewById(R.id.member_password_confirm_input);

        // 확인버튼
        member_button = findViewById(R.id.member_button);

        // 다른 옵션 선택
        member_option0_5 = findViewById(R.id.member_option0_5);
        member_option1 = findViewById(R.id.member_option1);
        member_option2 = findViewById(R.id.member_option2);

        member_option0_5.setText("Forgot password?");
        member_option0_5.setOnClickListener(v -> {
            status3_findpassword();
        });

        // 회원 이메일로 고정
        member_email_input.setText(CurrentAccountUtil.getAccount(this).getUserId());
        member_email.setEnabled(false);
        member_email.setClickable(false);

        status0_exportDiary();
    }

    private void status0_exportDiary() {
        accountStatus = 0;

        member_text1.setText("Export diary");
        member_text2.setText("You can get a diary by email.");
        member_text1.setTextColor(getResources().getColor(R.color.prussianBlue));
        member_text2.setTextColor(getResources().getColor(R.color.cobaltBlue));

        member_old_password.setVisibility(View.GONE);
        member_password.setVisibility(View.GONE);
        member_password_confirm.setVisibility(View.GONE);

        member_button.setText("Get a diary");
        member_button.setTextColor(getResources().getColor(R.color.colorWhite));
        member_button.setOnClickListener(v -> {
            try {
                exportDiary(member_email_input.getText().toString());
            } catch (JSONException e) {
                Log.e(TAG, "json exception : " + e);
            }
        });

        member_option0_5.setVisibility(View.GONE);
        member_option1.setText("Change password.");
        member_option2.setText("Delete account.");

        member_option1.setOnClickListener(v -> {
            status1_changePassword();
        });
        member_option2.setOnClickListener(v -> {
            status2_deleteAccount();
        });
    }

    private void status1_changePassword() {
        accountStatus = 1;

        member_text1.setText("Change password");
        member_text2.setText("Change your password.");
        member_text1.setTextColor(getResources().getColor(R.color.prussianBlue));
        member_text2.setTextColor(getResources().getColor(R.color.cobaltBlue));

        member_old_password.setVisibility(View.VISIBLE);
        member_password.setVisibility(View.VISIBLE);
        member_password_confirm.setVisibility(View.VISIBLE);

        member_button.setText("Change password");
        member_button.setTextColor(getResources().getColor(R.color.colorWhite));
        member_button.setOnClickListener(v -> {
            try {
                changePassword(member_email_input.getText().toString(), member_old_password_input.getText().toString(), member_password_input.getText().toString(), member_password_confirm_input.getText().toString());
            } catch (JSONException e) {
                Log.e(TAG, "json exception : " + e);
            }
        });

        member_option0_5.setVisibility(View.VISIBLE);
        member_option1.setText("Export diary.");
        member_option2.setText("Delete account.");

        member_option1.setOnClickListener(v -> {
            status0_exportDiary();
        });
        member_option2.setOnClickListener(v -> {
            status2_deleteAccount();
        });
    }

    private void status2_deleteAccount() {
        accountStatus = 2;

        member_text1.setText("Delete Account");
        member_text2.setText("Once you delete your account,\nthere is no going back.\nPlease be certain.");
        member_text1.setTextColor(getResources().getColor(R.color.colorRed));
        member_text2.setTextColor(getResources().getColor(R.color.colorMaroon));

        member_old_password.setVisibility(View.GONE);
        member_password.setVisibility(View.GONE);
        member_password_confirm.setVisibility(View.GONE);

        member_button.setText("Delete your account");
        member_button.setTextColor(getResources().getColor(R.color.colorRed));
        member_button.setOnClickListener(v -> {
            try {
                deleteAccount(member_email_input.getText().toString());
            } catch (JSONException e) {
                Log.e(TAG, "json exception : " + e);
            }
        });

        member_option0_5.setVisibility(View.GONE);
        member_option1.setText("Export diary.");
        member_option2.setText("Change password.");

        member_option1.setOnClickListener(v -> {
            status0_exportDiary();
        });
        member_option2.setOnClickListener(v -> {
            status1_changePassword();
        });
    }

    private void status3_findpassword() {
        accountStatus = 3;

        member_text1.setText("Find password");
        member_text2.setText("You can get a new password by email.");
        member_text1.setTextColor(getResources().getColor(R.color.prussianBlue));
        member_text2.setTextColor(getResources().getColor(R.color.cobaltBlue));

        member_old_password.setVisibility(View.GONE);
        member_password.setVisibility(View.GONE);
        member_password_confirm.setVisibility(View.GONE);

        member_button.setText("Get new password");
        member_button.setTextColor(getResources().getColor(R.color.colorWhite));
        member_button.setOnClickListener(v -> {
            try {
                deleteAccount(member_email_input.getText().toString());
            } catch (JSONException e) {
                Log.e(TAG, "json exception : " + e);
            }
        });

        member_option0_5.setVisibility(View.GONE);
        member_option1.setText("Export diary.");
        member_option2.setText("Delete account.");

        member_option1.setOnClickListener(v -> {
            status0_exportDiary();
        });
        member_option2.setOnClickListener(v -> {
            status2_deleteAccount();
        });
    }

    private void exportDiary(String email) throws JSONException {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "email is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO API CALL
    }

    private void changePassword(String email, String oldPassword, String password, String passwordConfirm) throws JSONException {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "email is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(this, "oldPassword is empty", Toast.LENGTH_SHORT).show();
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
        //TODO API CALL
    }

    private void deleteAccount(String email) throws JSONException {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "email is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "check email address", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO API CALL
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
        //TODO API CALL
    }
}
