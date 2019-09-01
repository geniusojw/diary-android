package org.jerrioh.diary.activity.lock;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.draw.SettingActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.account.AccountApis;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.PropertyUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class UnlockActivity extends AppCompatActivity {
    private static final String TAG = "UnlockActivity";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private TextView mDescriptionView;
    private TextView mResetTextView;
    private ProgressBar mProgressBar;

    private String temporaryPin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        String lockUse = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_USE, this);
        String pin4digit = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_4DIGIT, this);
        temporaryPin = pin4digit;

        if (Integer.parseInt(lockUse) == 0) {
            finish();
            return;
        }

        Intent intent = getIntent();
        String accountEmail = intent.getStringExtra("accountEmail");

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mDescriptionView = (TextView) findViewById(R.id.text_view_lock_description);
        mResetTextView = (TextView) findViewById(R.id.text_view_lock_reset_pin);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_lock);

        mDescriptionView.setText(getResources().getString(R.string.lock_input_password));
        mResetTextView.setText(getResources().getString(R.string.lock_find_password));

        mResetTextView.setVisibility(View.VISIBLE);

        mResetTextView.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(getResources().getString(R.string.lock_find_password))
                    .setMessage(getResources().getString(R.string.lock_find_password_description, accountEmail))
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {

                        Random random = new Random();
                        temporaryPin = "" + random.nextInt(10) + random.nextInt(10) + random.nextInt(10) + random.nextInt(10);

                        mProgressBar.setVisibility(View.VISIBLE);
                        mResetTextView.setClickable(false);
                        AccountApis accountApis = new AccountApis(this);
                        accountApis.findLockPassword(temporaryPin, new ApiCallback() {
                            @Override
                            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                if (httpStatus == 200) {
                                    Toast.makeText(UnlockActivity.this, getResources().getString(R.string.lock_find_password_check), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UnlockActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                                }
                                mProgressBar.setVisibility(View.GONE);
                                mResetTextView.setClickable(true);
                            }
                        });
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        });

        //attach lock view with dot indicator
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLength(4);

        //set listener for lock code change
        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                Log.d(TAG, "lock code: " + pin);
                if (pin.equals(pin4digit) || pin.equals(temporaryPin)) {
                    PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_LAST_USE_TIME, String.valueOf(System.currentTimeMillis()), UnlockActivity.this);
                    finish();

                } else {
                    Toast.makeText(UnlockActivity.this, "Failed code, try again!", Toast.LENGTH_SHORT).show();
                    mPinLockView.resetPinLockView();
                }
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "lock code is empty!");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}