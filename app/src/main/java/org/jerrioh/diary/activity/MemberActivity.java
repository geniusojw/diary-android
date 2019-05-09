package org.jerrioh.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.AccountApis;
import org.jerrioh.diary.api.ApiCaller;
import org.jerrioh.diary.util.CurrentAccountUtil;
import org.jerrioh.diary.db.AccountDao;
import org.jerrioh.diary.db.DiaryDao;
import org.jerrioh.diary.db.LetterDao;
import org.jerrioh.diary.db.SettingDao;
import org.jerrioh.diary.dbmodel.Account;
import org.jerrioh.diary.dbmodel.Setting;

public class MemberActivity extends CommonActionBarActivity {
    private static final String TAG = "MemberActivity";

    private static final int REQUEST_ACCOUNT = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
            }
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Account myAccount = CurrentAccountUtil.getAccount(this);

        if (CurrentAccountUtil.isMember(myAccount.getUserId())) {
            String userId = myAccount.getUserId();
            String nickname = myAccount.getNickname();
            String description = myAccount.getDescription();

            setCommonToolBar(nickname);
            setMemberServices();

        } else {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivityForResult(intent, REQUEST_ACCOUNT);
        }
    }

    private void setMemberServices() {
        View viewIWanna = findViewById(R.id.member_iwannaknow);
        viewIWanna.setOnClickListener(v -> {
            ApiCaller.Callback callback = (jsonObject) -> {
                int code = (int) jsonObject.get("code");
                if (code == 200000) {
                    Toast.makeText(this, "Your are stupid", Toast.LENGTH_LONG).show();
                }
            };
            AccountApis.aboutMe(this, callback);
        });

        View viewAlias = findViewById(R.id.member_alias);
        viewAlias.setOnClickListener(v -> {
            Toast.makeText(this, "기다리세요. 만드는 중입니다.", Toast.LENGTH_SHORT).show();
        });

        View viewChangeNick = findViewById(R.id.member_change_nick);
        viewChangeNick.setOnClickListener(v -> {
            Toast.makeText(this, "기다리세요. 만드는 중입니다.", Toast.LENGTH_SHORT).show();
        });

        View viewAccount = findViewById(R.id.member_account);
        viewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountMemberActivity.class);
            startActivity(intent);
        });

        View viewSignOut = findViewById(R.id.member_signout);
        viewSignOut.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("Sign out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        // diary 삭제, letter 삭제, alias 삭제, 화면잠금 해제, account 삭제
                        new DiaryDao(this).removeAllDiary();
                        new LetterDao(this).removeAllLetter();
                        //new AliasDao(this).removeAllAlias();
                        new SettingDao(this).removeSetting(Setting.Key.SCREEN_LOCK_USE);
                        new SettingDao(this).removeSetting(Setting.Key.SCREEN_LOCK_4DIGIT);

                        CurrentAccountUtil.deleteAccount(this);

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
