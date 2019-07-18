package org.jerrioh.diary.activity.draw;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.etc.EtcApis;
import org.json.JSONException;
import org.json.JSONObject;

public class AboutApplicationActivity extends CommonActionBarActivity {
    private static final String TAG = "AccountActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        setCommonToolBar(getResources().getString(R.string.app_version));


        TextView currentVersionText = findViewById(R.id.application_current_version);
        TextView latestVersionText = findViewById(R.id.application_latest_version);

        String currentVersion = "0.0.1";
        try {
            PackageInfo packageInfo = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0 );
            currentVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionInfo : " + e.getMessage());
        }

        EtcApis etcApis = new EtcApis(this);
        etcApis.version(currentVersion, new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                String currentVersion = "?";
                String latestVersion = "?";

                if (httpStatus == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    currentVersion = data.getString("currentVersion");
                    latestVersion = data.getString("latestVersion");
                }

                currentVersionText.setText(getResources().getString(R.string.version_current, currentVersion));
                latestVersionText.setText(getResources().getString(R.string.version_latest, latestVersion));
            }
        });

    }

    private String getLatestVersionInfo(Context context) {
        return "Unknown";
    }
}
