package com.nmt.kancollemanager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class ExpeditionActivity extends ActionBarActivity {
    Runnable testTask;
    ScheduledExecutorService scheduler;
    ScheduledFuture future;

    private List<ExpeditionWorkingData> workingList;
    private int workingNum = 3;

    public ExpeditionActivity() {
        workingList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            workingList.add(new ExpeditionWorkingData());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expedition);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_xpedition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        // データ読込
        load();

        // タイマー実行
        if (future == null) {
            testTask = new WorkingTask();
            scheduler = Executors.newSingleThreadScheduledExecutor();
            future = scheduler.scheduleAtFixedRate(testTask, 0, 1000, TimeUnit.MILLISECONDS);
        }

        super.onResume();
    }

    public void init() {
        List<ExpeditionData> dataList = new ArrayList<>();

        //データ取得
        DBOpenHelper dbHelper = new DBOpenHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from expedition", null);

        // データセット
        boolean isEof = cursor.moveToFirst();
        while (isEof) {
            ExpeditionData data = new ExpeditionData();
            data.setId(cursor.getInt(cursor.getColumnIndex("id")));
            data.setName(cursor.getString(cursor.getColumnIndex("name")));
            data.setTime(cursor.getInt(cursor.getColumnIndex("time")));
            data.setCondition(cursor.getString(cursor.getColumnIndex("condition")));
            data.setLevel(cursor.getString(cursor.getColumnIndex("level")));
            data.setShipLv(cursor.getInt(cursor.getColumnIndex("ship_lv")));
            data.setFlagLv(cursor.getInt(cursor.getColumnIndex("flag_lv")));
            data.setExp(cursor.getInt(cursor.getColumnIndex("exp")));
            data.setFuel(cursor.getInt(cursor.getColumnIndex("fuel")));
            data.setAmmo(cursor.getInt(cursor.getColumnIndex("ammo")));
            data.setSteel(cursor.getInt(cursor.getColumnIndex("steel")));
            data.setBauxite(cursor.getInt(cursor.getColumnIndex("bauxite")));
            data.setBonus(cursor.getString(cursor.getColumnIndex("bonus")));

            dataList.add(data);
            isEof = cursor.moveToNext();
        }
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setFastScrollEnabled(true);
        ExpeditionDataAdapter adapter = new ExpeditionDataAdapter(this, 0, dataList);
        listView.setAdapter(adapter);

        // リスナー登録
        listView.setOnItemClickListener(new SelectExpeditionListener());

        ListView workingView = (ListView) findViewById(R.id.working_list);
        workingView.setOnItemClickListener(new SetTimerListener());

    }

    /**
     * データ保存
     * @param index int
     */
    private void save(int index) {
        ExpeditionWorkingData workingData = workingList.get(index);
        JSONObject saveData = new JSONObject();
        try {
            saveData.put("index", index);
            saveData.put("fleet", workingData.getFleetNo());
            saveData.put("name", workingData.getName());
            saveData.put("time", workingData.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences = getSharedPreferences("workingList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("save_no_" + index, saveData.toString());
        editor.apply();
    }

    /**
     * データ読込
     */
    private void load() {
        SharedPreferences preferences = getSharedPreferences("workingList", Context.MODE_PRIVATE);
        for (int i = 0; i < workingNum; i++) {
            String loadData = preferences.getString("save_no_" + i, null);
            if (loadData == null) {
                continue;
            }
            ExpeditionWorkingData workingData = workingList.get(i);
            try {
                JSONObject json = new JSONObject(loadData);
                // 表示用データ設定
                workingData.setIndex(json.getInt("index"));
                workingData.setFleetNo(json.getInt("fleet"));
                workingData.setName(json.getString("name"));
                workingData.setTime(json.getLong("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * タイマータスク
     */
    public class WorkingTask implements Runnable {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 表示時間更新
                    ExpeditionWorkingAdapter adapter = new ExpeditionWorkingAdapter(getApplicationContext(), 0, workingList);
                    ListView workingView = (ListView) findViewById(R.id.working_list);
                    workingView.setAdapter(adapter);

                    // 全て終了したらタイマー停止
                    boolean stop = true;
                    for (int i = 0; i < workingNum; i++) {
                        if (workingList.get(i).getTime() >= System.currentTimeMillis()) {
                            stop = false;
                        }
                    }
                    if (stop) {
//                        Log.d("Timer", "Stop");
                        if (future != null) {
                            future.cancel(true);
                            future = null;
                        }
                    }

//                    Log.d("test", String.valueOf((new Date()).getTime()));
                }
            });
        }
    }

    /**
     * 遠征選択リスナ
     */
    public class SelectExpeditionListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            ExpeditionData data = (ExpeditionData) listView.getItemAtPosition(position);

            String[] fleets = {"第1艦隊", "第2艦隊", "第3艦隊", "キャンセル"};
            new AlertDialog.Builder(ExpeditionActivity.this)
                    .setTitle("出撃")
                    .setItems(fleets, new SelectFeetListener(data))
                    .show();
        }
    }

    /**
     * 艦隊選択リスナ
     */
    public class SelectFeetListener implements DialogInterface.OnClickListener {
        private ExpeditionData data;

        /**
         * コンストラクタ
         * @param data ExpeditionData
         */
        public SelectFeetListener(ExpeditionData data) {
            this.data = data;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // キャンセル
            if (which == 3) {
                return;
            }

            // 通知時間設定
            Calendar calender = Calendar.getInstance();
            calender.setTimeInMillis(System.currentTimeMillis());
            calender.add(Calendar.SECOND, data.getTime());

            // 表示用データ設定
            ExpeditionWorkingData workingData = workingList.get(which);
            workingData.setIndex(which);
            workingData.setFleetNo(which + 1);
            workingData.setName(data.getName());
            workingData.setTime(calender.getTimeInMillis());

            // データ保存
            save(which);

            // Intent設定
            Intent intent = new Intent(ExpeditionActivity.this, ExpeditionActivity.NoticeReceiver.class);
            intent.putExtra("fleet", which + 1);
            intent.putExtra("name", data.getName());
            PendingIntent pending = PendingIntent.getBroadcast(ExpeditionActivity.this, which, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Alarm設定
            AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarm.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pending);

            // タイマー実行(二重実行はしない)
            if (future == null){
                future = scheduler.scheduleAtFixedRate(testTask, 0, 1000, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * 通知レシーバー
     */
    public final static class NoticeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int fleet = intent.getIntExtra("fleet", 0);
            String name = intent.getStringExtra("name");

            // 通知設定
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.drum_can)
                    .setContentTitle(name + " 完了")
                    .setContentText(String.format("第%s艦隊 帰還", fleet))
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_VIBRATE
                            | Notification.DEFAULT_SOUND
                            | Notification.DEFAULT_LIGHTS);

            // 通知クリック時動作設定
            Intent clickIntent = new Intent(context, ExpeditionActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent,  PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);

            // 通知領域表示
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.notify(0, builder.build());

            // POPUP表示
            String message = String.format("第%s艦隊が%sから帰還しました", fleet, name);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 時間設定画面リスナ
     */
    private class SetTimerListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 初期表示値
            ExpeditionWorkingData data = workingList.get(position);
            long time = (data.getTime() - System.currentTimeMillis()) / 1000;
            int hour = (int) Math.floor(time / 3600);
            hour = (hour <= 0) ? 0 : hour;
            int minutes = (int) Math. floor((time % 3600) / 60);
            minutes = (minutes <= 0) ? 0 : minutes;
            int second = (int) Math.floor((time % 60));
            second = (second <= 0) ? 0 : second;

            // NumberPiker設定
            LayoutInflater inflater = LayoutInflater.from(ExpeditionActivity.this);
            View setTimer= inflater.inflate(R.layout.time_adjust, null);
            NumberPicker pickerHour = (NumberPicker) setTimer.findViewById(R.id.hour);
            pickerHour.setMaxValue(99);
            pickerHour.setMinValue(0);
            pickerHour.setValue(hour);
            final NumberPicker pickerMinutes = (NumberPicker) setTimer.findViewById(R.id.minutes);
            pickerMinutes.setMaxValue(59);
            pickerMinutes.setMinValue(0);
            pickerMinutes.setValue(minutes);
            final NumberPicker pickerSecond = (NumberPicker) setTimer.findViewById(R.id.second);
            pickerSecond.setMaxValue(59);
            pickerSecond.setMinValue(0);
            pickerSecond.setValue(second);

            // 時間設定画面
            new AlertDialog.Builder(ExpeditionActivity.this)
                    .setTitle("時間入力")
                    .setView(setTimer)
                    .setPositiveButton("設定", new PickerListener(
                            position,
                            setTimer))
                    .show();
        }

        /**
         * 時間設定リスナ
         */
        private class PickerListener implements DialogInterface.OnClickListener {
            int position;
            View view;

            /**
             * コンストラクタ
             * @param position int
             * @param view View
             */
            private PickerListener(int position, View view) {
                this.position = position;
                this.view = view;
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 入力時間
                NumberPicker hourPicker = (NumberPicker) view.findViewById(R.id.hour);
                NumberPicker minutesPicker = (NumberPicker) view.findViewById(R.id.minutes);
                NumberPicker secondPicker = (NumberPicker) view.findViewById(R.id.second);
                int time = (hourPicker.getValue() * 3600) + (minutesPicker.getValue() * 60) + secondPicker.getValue();
                // 表示時間
                Calendar calender = Calendar.getInstance();
                calender.setTimeInMillis(System.currentTimeMillis());
                calender.add(Calendar.SECOND, time);
                ExpeditionWorkingData data = workingList.get(position);
                data.setTime(calender.getTimeInMillis());
                workingList.set(position, data);

                // データ保存
                save(position);

                // Intent設定
                Intent intent = new Intent(ExpeditionActivity.this, ExpeditionActivity.NoticeReceiver.class);
                intent.putExtra("fleet", data.getFleetNo());
                intent.putExtra("name", data.getName());
                PendingIntent pending = PendingIntent.getBroadcast(ExpeditionActivity.this, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Alarm設定
                AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarm.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pending);

                // タイマー実行(タイマー設定中に元の全タイマーが完了すると止まってしまうため)
                if (future == null){
                    future = scheduler.scheduleAtFixedRate(testTask, 0, 1000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
