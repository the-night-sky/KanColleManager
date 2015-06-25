package com.nmt.kancollemanager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    boolean serviceRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button expedition = (Button) findViewById(R.id.btnExpedition);
        expedition.setOnClickListener(this);


        Button overlay = (Button) findViewById(R.id.btnService);
        overlay.setOnClickListener(this);


    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnExpedition) {
            Log.d("onClick", "expedition");
            Intent intent = new Intent(this, ExpeditionActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.btnService) {
            Log.d("onClick", "startService");
            Intent intent = new Intent(this, ExpeditionService.class);
            if (serviceRunning) {
                stopService(intent);
                serviceRunning = false;
            } else {
                startService(intent);
                serviceRunning = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
