package org.jerrioh.diary.activity.draw;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.etc.EtcApis;
import org.json.JSONException;
import org.json.JSONObject;

public class AboutApplicationActivity extends AbstractDiaryToolbarActivity {
    private static final String TAG = "AboutApplicationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        setCommonToolBar(getResources().getString(R.string.app_version));


        ImageView newRelease = findViewById(R.id.application_new_release);
        ImageView logo = findViewById(R.id.application_logo);
        TextView currentVersionText = findViewById(R.id.application_current_version);
        TextView latestVersionText = findViewById(R.id.application_latest_version);

        View.OnClickListener linkPlayStore = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutApplicationActivity.this, "move to play store...", Toast.LENGTH_SHORT).show();
            }
        };

        newRelease.setOnClickListener(linkPlayStore);
        logo.setOnClickListener(linkPlayStore);

        final String current;
        try {
            PackageInfo packageInfo = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0 );
            current = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionInfo : " + e.getMessage());
            return;
        }
        currentVersionText.setText(getResources().getString(R.string.version_current, current));

        EtcApis etcApis = new EtcApis(this);
        etcApis.version(current, new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                String latestVersion;
                if (httpStatus == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    latestVersion = data.getString("latestVersion");
                } else {
                    latestVersion = "?";
                }
                latestVersionText.setText(getResources().getString(R.string.version_latest, latestVersion));
                if (!latestVersion.equals(current)) {
                    newRelease.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private String getLatestVersionInfo(Context context) {
        return "Unknown";
    }
}
