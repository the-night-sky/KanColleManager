package com.nmt.kancollemanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * ExpeditionService
 */
public class ExpeditionService extends Service {
    WindowManager manager;
    WindowManager.LayoutParams params;
    LinearLayout view;

    Handler handler;
    Runnable runnable;
    boolean isSecondStop;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createView();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        manager.removeView(view);
        manager = null;
        params = null;
        view = null;

        isSecondStop = true;
        handler = null;
        runnable = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createView() {

        LayoutInflater inflater = LayoutInflater.from(this);
        view = (LinearLayout)inflater.inflate(R.layout.expedition_overlay, null);
        TextView text = new TextView(getApplicationContext());
        text.setId(0);
        text.setTextColor(Color.WHITE);
        view.addView(text);

       params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.START;
        params.alpha = 60;


        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        manager.addView(view, params);

        updateTime();

        updateSecond();
    }

    private void updateTime() {
        String time = "";
        SharedPreferences preferences = getSharedPreferences("workingList", Context.MODE_PRIVATE);
        boolean isActive = false;
        for (int i = 0; i < 3; i++) {
            String loadData = preferences.getString("save_no_" + i, null);
            if (loadData == null) {
                continue;
            }
            try {
                JSONObject json = new JSONObject(loadData);

                long lTime = json.getLong("time");
                int iTime = (int) Math.floor((lTime - System.currentTimeMillis()) / 1000);
                iTime = (iTime <= 0) ? 0 : iTime;
                if (iTime <= 0) {
                    continue;
                }
                int hour = (int) Math.floor(iTime / 3600);
                int minutes = (int) Math.floor((iTime % 3600) / 60);
                int second =  (int) Math.floor(iTime % 60);
                String formatTime = String.format("%02d:%02d:%02d", hour, minutes, second);

                time += String.format("%d-%s\n", (i + 1), formatTime);
                isActive = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // 全て終了していたらタイマー停止
        if (!isActive) {
            isSecondStop = true;
        }

        TextView text = (TextView) view.findViewById(0);
        text.setText(time.trim());
        manager.updateViewLayout(view, params);

    }
    private void updateSecond() {
        isSecondStop = false;
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                if (isSecondStop) {
                    return;
                }

                // 時間更新
                updateTime();

                // 1秒毎更新設定
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - Calendar.getInstance().getTimeInMillis() % 1000);
                handler.postAtTime(runnable, next);
            }
        };
        runnable.run();
    }
}
