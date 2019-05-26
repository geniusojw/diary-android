package org.jerrioh.diary.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.account.AccountDiaryApis;
import org.jerrioh.diary.api.author.AuthorApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.Setting;
import org.jerrioh.diary.model.db.AuthorDao;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.model.db.SettingDao;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthorUtil {
    private static final String TAG = "AuthorUtil";

    private static Author author;

    public static Author getAuthor() {
        return author;
    }

    public static Author getAuthor(Context context) {
        AuthorDao authorDao = new AuthorDao(context);
        author = authorDao.getAuthor();

        // 새로운 author
        if (author == null) {
            author = generateNewAuthor();
            authorDao.insertAuthor(author);
        }

        // author code 생성
        if (TextUtils.isEmpty(author.getAuthorCode())) {
            AuthorApis authorApis = new AuthorApis(context);
            authorApis.create(author.getAuthorId(), new ApiCallback() {
                @Override
                protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                    if (httpStatus == 200) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String authorCode = data.getString("authorCode");

                        AuthorDao authorDao = new AuthorDao(context);
                        authorDao.updateAuthorCode(authorCode);
                    }
                }
            });
        }

        return author;
    }

    public static void resetAuthorData(Context context) {
        AuthorApis authorApis = new AuthorApis(context);
        authorApis.delete(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                // do nothing
            }
        });

        AuthorDao authorDao = new AuthorDao(context);
        authorDao.deleteAuthor();

        //TODO diary, letter, alias, setting 삭제
        DiaryDao diaryDao = new DiaryDao(context);
        diaryDao.deleteAllDiaries();

        LetterDao letterDao = new LetterDao(context);
        letterDao.deleteAllLetters();

        SettingDao settingDao = new SettingDao(context);
        settingDao.deleteAllSettings();

        author = generateNewAuthor();
        authorDao.insertAuthor(author);
    }

    public static void accountSignIn(Context context, String accountEmail, String accountToken) {
        // account Email, Token 삭제
        AuthorDao authorDao = new AuthorDao(context);
        authorDao.updateAccountEmailAndToken(accountEmail, accountToken);

        // sync diaries
        syncDiaries(context);
    }

    public static void accountSignOut(Context context) {
        // account Email, Token 삭제
        AuthorDao authorDao = new AuthorDao(context);
        authorDao.updateAccountEmailAndToken("", "");

        //일기동기화 상태 0(UNSAVED)로 업데이트
        DiaryDao diaryDao = new DiaryDao(context);
        diaryDao.updateAllDiaryAccountStatus(Diary.DiaryStatus.UNSAVED);

        // 화면잠금 해제
        SettingDao settingDao = new SettingDao(context);
        settingDao.deleteSetting(Setting.Key.SCREEN_LOCK_USE);
        settingDao.deleteSetting(Setting.Key.SCREEN_LOCK_4DIGIT);
    }

    public static void syncDiaries(Context context) {
        DiaryDao diaryDao = new DiaryDao(context);
        List<Diary> diaries = diaryDao.getAllDiariesBeforeToday(DateUtil.getyyyyMMdd());
        ApiCallback callback = new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject)  throws JSONException {
                if (httpStatus == 200) {
                    Map<String, Diary> diaryMap = new HashMap<>();
                    for (Diary diary : diaries) {
                        diaryMap.put(diary.getDiaryDate(), diary);
                    }

                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject json = data.getJSONObject(i);
                        String diaryDate = json.getString("diaryDate");
                        String title = json.getString("title");
                        String content = json.getString("content");

                        Diary diary = diaryMap.get(diaryDate);
                        if (diary == null) {
                            diary = new Diary();
                            diary.setDiaryDate(diaryDate);
                            diary.setTitle(title);
                            diary.setContent(content);
                            diary.setAuthorDiaryStatus(Diary.DiaryStatus.UNSAVED);
                            diary.setAccountDiaryStatus(Diary.DiaryStatus.SAVED);
                            diaryDao.insertDiary(diary);
                        } else {
                            if (TextUtils.equals(title, diary.getTitle()) && TextUtils.equals(content, diary.getContent())) {
                                diaryDao.updateDiaryAccountStatus(diaryDate, Diary.DiaryStatus.SAVED);
                            } else {
                                diaryDao.updateDiaryAccountStatus(diaryDate, Diary.DiaryStatus.UNSAVED_CONFLICT);
                            }
                        }
                    }
                }
            }
        };
        new AccountDiaryApis(context).synchronize(diaries, callback);
    }

    private static Author generateNewAuthor() {
        Author author = new Author();
        author.setAuthorId("92f44a4e-09ea-4fa5-ab54-df3c10a46812");
        author.setAuthorCode("RcDHKCZRhQXLe3Cj");

//        author.setAuthorId(generateAuthorId());
//        author.setAuthorCode("");
        author.setNickname(generateNickname());
        author.setDescription(generateDescription());
        author.setAccountEmail("");
        author.setAccountToken("");
        return author;
    }

    private static String generateAuthorId() {
        return UUID.randomUUID().toString();
    }

    private static String generateNickname() {
        return "Someone";
    }

    private static String generateDescription() {
        return "Person who decided to write a diary";
    }
}
