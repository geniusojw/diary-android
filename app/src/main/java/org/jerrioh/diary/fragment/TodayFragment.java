package org.jerrioh.diary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.TodayWriteActivity;
import org.jerrioh.diary.config.Config;
import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class TodayFragment extends Fragment {
    private static final String TAG = "TodayFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 쓰기 버튼 활성화
        View todayView =  inflater.inflate(R.layout.fragment_today, container, false);

        View writeButton = todayView.findViewById(R.id.write_diary_button);
        writeButton.setEnabled(true);
        writeButton.setClickable(true);
        writeButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), TodayWriteActivity.class);
            startActivity(intent);
        });

        // 빈번하게 호출할 필요는 없다.
        serverInformation();
        clientInformation();

        Date today = new Date(System.currentTimeMillis());
        String todayString = DateUtil.getTodayDateString(today, Locale.getDefault());

        TextView textView = todayView.findViewById(R.id.display_today);
        textView.setText("TODAY DIARY  " + todayString);

        return todayView;
    }

    private void clientInformation() {
        Date today = new Date(System.currentTimeMillis());
        Information.ClientInformation.TODAY = DateUtil.getSimpleDateString(today, Locale.getDefault());
    }

    private void serverInformation() {
        Log.d(TAG, "serverInformation");
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.get(getActivity(), "api_url") + "information",
                response -> {
                    try {
                        Log.d(TAG, "GET serverInformation success");
                        JSONObject jsonObject = new JSONObject(response);
                        Information.ServerInformation.SERVER_TIME = (String) jsonObject.get("serverTime");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                vollyError -> {
                    Log.d(TAG, "GET serverInformation fail. vollyError=" + vollyError.toString());
                    if (vollyError.networkResponse != null) {
                        Log.d(TAG, "headers=" + vollyError.networkResponse.headers);
                        Log.d(TAG, "data=" + new String(vollyError.networkResponse.data));
                    }
                }
        );
        queue.add(stringRequest);
        queue.start();
    }
}
