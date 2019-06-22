package org.jerrioh.diary.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.jerrioh.diary.R;

import java.util.List;

public class SettingSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context context;
    private final List<String> list;
    private int selection;

    public SettingSpinnerAdapter(Context context, List<String> list, int selection) {
        this.context = context;
        this.list = list;
        this.selection = selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view =  (TextView) View.inflate(context, R.layout.spinner_item, null);

        if (this.selection == position) {
            view.setText(list.get(position) + " *");
            view.setTextColor(Color.RED);
        } else {
            view.setText(list.get(position));
        }

        return view;
    }
}
