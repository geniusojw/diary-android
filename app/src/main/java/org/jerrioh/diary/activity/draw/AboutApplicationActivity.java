package org.jerrioh.diary.activity.draw;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jerrioh.diary.R;

public class AboutApplicationActivity extends CommonActionBarActivity {
    private static final String TAG = "AccountActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        setCommonToolBar("Application information");

        TextView currentVersion = findViewById(R.id.application_current_version);
        currentVersion.setText("Current Version : " + getCurrentVersionInfo(this));


        TextView latestVersion = findViewById(R.id.application_latest_version);
        latestVersion.setText("Latest Version : " + getLatestVersionInfo(this));
    }

    private String getCurrentVersionInfo(Context context) {
        String version = "Unknown";
        PackageInfo packageInfo;

        try {
            packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getApplicationContext().getPackageName(), 0 );
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionInfo : " + e.getMessage());
        }
        return version;
    }

    private String getLatestVersionInfo(Context context) {
        String version = "Unknown";
        return version;
    }
}
