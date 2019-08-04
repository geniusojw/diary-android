package org.jerrioh.diary.activity.lock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.PropertyUtil;

public class LockSettingActivity extends AppCompatActivity {
    private static final String TAG = "LockSettingActivity";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private TextView mDescriptionView;

    //private static final int STEP_OLD_PIN = 0;
    private static final int STEP_NEW_PIN = 1;
    private static final int STEP_NEW_PIN_CONFIRM = 2;

    private int currentStep;
    private String newPin = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        currentStep = STEP_NEW_PIN;

        Intent intent = getIntent();

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mDescriptionView = (TextView) findViewById(R.id.text_view_lock_description);

        mDescriptionView.setText(getResources().getString(R.string.lock_input_new_password));

        //attach lock view with dot indicator
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLength(4);

        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                if (currentStep == STEP_NEW_PIN) {
                    newPin = pin;
                    currentStep = STEP_NEW_PIN_CONFIRM;
                    mPinLockView.resetPinLockView();
                    mDescriptionView.setText(getResources().getString(R.string.lock_input_new_password_confirm));

                } else if (currentStep == STEP_NEW_PIN_CONFIRM) {
                    if (pin.equals(newPin)) {
                        PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_USE, "1", LockSettingActivity.this);
                        PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_4DIGIT, newPin, LockSettingActivity.this);
                        PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_LAST_USE_TIME, String.valueOf(System.currentTimeMillis()), LockSettingActivity.this);
                        finish();

                    } else {
                        Toast.makeText(LockSettingActivity.this, getResources().getString(R.string.lock_input_new_password_confirm_fail), Toast.LENGTH_SHORT).show();
                        finish();
                    }
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
}