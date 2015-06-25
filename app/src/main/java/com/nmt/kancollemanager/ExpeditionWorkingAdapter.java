package com.nmt.kancollemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * ExpeditionExecuteAdapter
 */
public class ExpeditionWorkingAdapter extends ArrayAdapter<ExpeditionWorkingData> {
    private LayoutInflater inflater;

    public ExpeditionWorkingAdapter(Context context, int resource, List<ExpeditionWorkingData> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expedition_working, null);
        }

        TextView no = (TextView) convertView.findViewById(R.id.working_fleet_no);
        TextView name = (TextView) convertView.findViewById(R.id.working_name);
        TextView time = (TextView) convertView.findViewById(R.id.working_time);

        ExpeditionWorkingData data = getItem(position);

        String fleet = "第" + data.getFleetNo() + "艦隊";
        no.setText(fleet);

        name.setText(data.getName());

        long lTime = data.getTime();
        int iTime = (int) Math.floor((lTime - System.currentTimeMillis()) / 1000);
        iTime = (iTime <= 0) ? 0 : iTime;
        int hour = (int) Math.floor(iTime / 3600);
        int minutes = (int) Math.floor((iTime % 3600) / 60);
        int second =  (int) Math.floor(iTime % 60);
        String formatTime = String.format("%02d:%02d:%02d", hour, minutes, second);
        time.setText(formatTime);

        return convertView;
    }
}
