package com.nmt.kancollemanager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * ExpeditionDataAdapter
 */
public class ExpeditionDataAdapter extends ArrayAdapter<ExpeditionData> {
    private LayoutInflater inflater;

    public ExpeditionDataAdapter(Context context, int resource, List<ExpeditionData> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expedition_line, null);
        }

        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView condition = (TextView) convertView.findViewById(R.id.condition);
        TextView level = (TextView) convertView.findViewById(R.id.level);
        TextView shipLv = (TextView) convertView.findViewById(R.id.shipLv);
        TextView flagLv = (TextView) convertView.findViewById(R.id.flagLv);
        TextView exp = (TextView) convertView.findViewById(R.id.exp);
        TextView fuel = (TextView) convertView.findViewById(R.id.fuel);
        TextView ammo = (TextView) convertView.findViewById(R.id.ammo);
        TextView steel = (TextView) convertView.findViewById(R.id.steel);
        TextView bauxite = (TextView) convertView.findViewById(R.id.bauxite);
        TextView bonus = (TextView) convertView.findViewById(R.id.bonus);

        ExpeditionData data = getItem(position);

        id.setText(String.valueOf(data.getId()));
        name.setText(data.getName());
        time.setText(String.valueOf(data.getFormatTime()));
        condition.setText(data.getCondition());
        level.setText(data.getLevel());
        shipLv.setText(String.valueOf(data.getShipLv()));
        flagLv.setText(String.valueOf(data.getFlagLv()));
        exp.setText(String.valueOf(data.getExp()));
        fuel.setText(String.valueOf(data.getFuel()));
        ammo.setText(String.valueOf(data.getAmmo()));
        steel.setText(String.valueOf(data.getSteel()));
        bauxite.setText(String.valueOf(data.getBauxite()));
        bonus.setText(data.getBonus());

        return convertView;
    }
}
