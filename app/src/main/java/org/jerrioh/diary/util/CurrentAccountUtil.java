package org.jerrioh.diary.util;

import android.content.Context;
import android.util.Patterns;

import org.jerrioh.diary.db.AccountDao;
import org.jerrioh.diary.dbmodel.Account;

import java.util.UUID;

public class CurrentAccountUtil {
    private static final String TAG = "CurrentAccountUtil";

    private static Account account;

    public static boolean isMember(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static Account getAccount() {
        return account;
    }

    public static Account getAccount(Context context) {
        if (account == null) {
            updateAccount(context);
        }
        return account;
    }

    public static void updateAccount(Context context) {
        AccountDao accountDao = new AccountDao(context);
        account = accountDao.getMyAccount();

        // DB에 없다면 앱 최초 시작 또는 앱 데이터삭제 등
        if (account == null) {
            account = new Account(UUID.randomUUID().toString(), "", "temporary random nickname", "temporary random description", "","0");
            accountDao.insertMyAccount(account);
        }
    }

    public static void deleteAccount(Context context) {
        AccountDao accountDao = new AccountDao(context);
        accountDao.deleteAccount();

        account = new Account(UUID.randomUUID().toString(), "", "temporary random nickname", "temporary random description", "","0");
        accountDao.insertMyAccount(account);
    }
}
