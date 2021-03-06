package org.jerrioh.diary.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.account.AccountApis;
import org.jerrioh.diary.api.account.AccountDiaryApis;
import org.jerrioh.diary.api.author.AuthorApis;
import org.jerrioh.diary.api.author.AuthorDiaryApis;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.AuthorDao;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.model.db.MusicDao;
import org.jerrioh.diary.model.db.PostDao;
import org.jerrioh.diary.model.db.PropertyDao;
import org.jerrioh.diary.model.db.ThemeDao;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
                        String nickname = data.getString("nickname");
                        String description = data.getString("description");

                        AuthorDao authorDao = new AuthorDao(context);
                        authorDao.updateAuthorCode(authorCode);
                        authorDao.updateNickname(nickname);
                        authorDao.updateDescription(description);
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

        // diary 삭제
        DiaryDao diaryDao = new DiaryDao(context);
        diaryDao.deleteAllDiaries();

        // letter 삭제
        LetterDao letterDao = new LetterDao(context);
        letterDao.deleteAllLetters();

        // group 삭제
        DiaryGroupDao diaryGroupDao = new DiaryGroupDao(context);
        diaryGroupDao.deleteDiaryGroup();

        // post(private) 삭제
        PostDao postDao = new PostDao(context);
        postDao.deleteAllPosts();

        // theme 삭제
        ThemeDao themeDao = new ThemeDao(context);
        themeDao.deleteAllTheme();

        // music 삭제
        MusicDao musicDao = new MusicDao(context);
        musicDao.deleteAllMusic();

        // setting 삭제
        PropertyDao propertyDao = new PropertyDao(context);
        propertyDao.deleteAllProperties();

        // TODO alias 삭제

        author = generateNewAuthor();
        authorDao.insertAuthor(author);
    }

    public static void syncAuthorDiaryGroupData(Context context) {
        DiaryGroupApis diaryGroupApis = new DiaryGroupApis(context);
        diaryGroupApis.getDiaryGroup(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    saveDiaryGroup(data, context);

                } else if (httpStatus == 404) {
                    DiaryGroupDao diaryGroupDao = new DiaryGroupDao(context);
                    diaryGroupDao.deleteDiaryGroup();
                }
            }
        });
    }

    public static void uploadAuthorDiary(Context context) {
        String yesterday_yyyyMMdd = DateUtil.getyyyyMMdd(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1));

        DiaryDao diaryDao = new DiaryDao(context);
        Diary diary = diaryDao.getDiary(yesterday_yyyyMMdd);

        if (diary == null) {
            return;
        }
        if (diary.getAuthorDiaryStatus() == Diary.DiaryStatus.SAVED) {
            return;
        }

        if (diary.getAuthorDiaryStatus() != Diary.DiaryStatus.SAVED) {
            AuthorDiaryApis authorDiaryApis = new AuthorDiaryApis(context);
            authorDiaryApis.write(diary.getDiaryDate(), diary.getTitle(), diary.getContent(), new ApiCallback() {
                @Override
                protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                    Log.d(TAG, "result = " + httpStatus);
                    if (httpStatus == 200 || httpStatus == 409) {
                        diaryDao.updateDiaryAuthorStatus(yesterday_yyyyMMdd, Diary.DiaryStatus.SAVED);
                    }
                }
            });
        }
    }

    public static void saveDiaryGroup(JSONObject data, Context context) throws JSONException {
        DiaryGroup diaryGroup = new DiaryGroup();
        diaryGroup.setDiaryGroupId(JsonUtil.getLong("diaryGroupId", data));
        diaryGroup.setDiaryGroupName(JsonUtil.getString("diaryGroupName", data));
        diaryGroup.setHostAuthorId(JsonUtil.getString("hostAuthorId", data));
        diaryGroup.setKeyword(JsonUtil.getString("keyword", data));
        diaryGroup.setCurrentAuthorCount(JsonUtil.getInt("currentAuthorCount", data));
        diaryGroup.setMaxAuthorCount(JsonUtil.getInt("maxAuthorCount", data));
        diaryGroup.setCountry(JsonUtil.getString("country", data));
        diaryGroup.setLanguage(JsonUtil.getString("language", data));
        diaryGroup.setTimeZoneId(JsonUtil.getString("timeZoneId", data));
        diaryGroup.setStartTime(JsonUtil.getLong("startTime", data));
        diaryGroup.setEndTime(JsonUtil.getLong("endTime", data));

        DiaryGroupDao diaryGroupDao = new DiaryGroupDao(context);
        if (diaryGroupDao.getDiaryGroup() == null) {
            diaryGroupDao.insertDiaryGroup(diaryGroup);
        } else {
            diaryGroupDao.updateDiaryGroup(diaryGroup);
        }
    }

    public static void accountSignIn(Context context, String accountEmail, String accountToken) {
        // account Email, Token 삭제
        AuthorDao authorDao = new AuthorDao(context);
        authorDao.updateAccountEmailAndToken(accountEmail, accountToken);

        // sync diaries
        syncAccountDiaries(context, null, null);
    }

    public static void accountSignOut(Context context) {
        // account Email, Token 삭제
        AuthorDao authorDao = new AuthorDao(context);
        authorDao.updateAccountEmailAndToken("", "");

        //일기동기화 상태 0(UNSAVED)로 업데이트
        DiaryDao diaryDao = new DiaryDao(context);
        diaryDao.updateAllDiaryAccountStatus(Diary.DiaryStatus.UNSAVED);

        // 화면잠금 해제
        PropertyDao propertyDao = new PropertyDao(context);
        propertyDao.deleteProperty(Property.Key.SCREEN_LOCK_USE);
        propertyDao.deleteProperty(Property.Key.SCREEN_LOCK_4DIGIT);
    }

    public static void refreshAccountToken(Context context) {
        Author author = AuthorUtil.getAuthor(context);
        if (TextUtils.isEmpty(author.getAccountEmail())) {
            return;
        }

        AccountApis accountApis = new AccountApis(context);
        accountApis.refreshToken(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    AuthorDao authorDao = new AuthorDao(context);
                    JSONObject json = jsonObject.getJSONObject("data");
                    String token = json.getString("token");
                    authorDao.updateAccountEmailAndToken(author.getAccountEmail(), token);
                }
            }
        });
    }

    public static void syncAccountDiaries(Context context, ProgressBar progressBar, TextView syncText) {
        DiaryDao diaryDao = new DiaryDao(context);
        List<Diary> diaries = diaryDao.getAllDiariesBeforeToday(DateUtil.getyyyyMMdd());

        AccountDiaryApis accountDiaryApis = new AccountDiaryApis(context);
        accountDiaryApis.synchronize(diaries, new ApiCallback() {
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

                            if (diaryDao.getDiary(diaryDate) == null) {
                                diaryDao.insertDiary(diary);
                            }
                        } else {
                            if (TextUtils.equals(title, diary.getTitle()) && TextUtils.equals(content, diary.getContent())) {
                                diaryDao.updateDiaryAccountStatus(diaryDate, Diary.DiaryStatus.SAVED);
                            } else {
                                diaryDao.updateDiaryAccountStatus(diaryDate, Diary.DiaryStatus.UNSAVED_CONFLICT);
                            }
                        }
                    }

                    long lastSyncTime = System.currentTimeMillis();
                    PropertyUtil.setProperty(Property.Key.SYNC_ACCOUNT_DIARY_API_REQUEST_TIME, String.valueOf(lastSyncTime), context);
                    if (syncText != null) {
                        String lastSyncDescription = "마지막 동기화 시간: " + DateUtil.getDateStringFull(lastSyncTime);
                        syncText.setText(lastSyncDescription);
                    }
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public static void syncAuthorInfo(Context context) {
        AuthorApis authorApis = new AuthorApis(context);
        authorApis.authorInfo(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    String nickname = data.getString("nickname");
                    String description = data.getString("description");

                    AuthorDao authorDao = new AuthorDao(context);
                    authorDao.updateNickname(nickname);
                    authorDao.updateDescription(description);
                }
            }
        });
    }

    private static Author generateNewAuthor() {
        Author author = new Author();
//        author.setAuthorId("92f44a4e-09ea-4fa5-ab54-df3c10a46812");
//        author.setAuthorCode("RcDHKCZRhQXLe3Cj");

        author.setAuthorId(generateAuthorId());
        author.setAuthorCode("");
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
        return "누군가";
    }

    private static String generateDescription() {
        return "일기를 쓰기로 결심한 사람";
    }
}
