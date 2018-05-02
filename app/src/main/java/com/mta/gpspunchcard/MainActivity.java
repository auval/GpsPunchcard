package com.mta.gpspunchcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mta.gpspunchcard.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // hide the button until we check it
        binding.setIsWorkDefined(true);

        setSupportActionBar(binding.toolbar);

        asyncInit();

    }

    private void asyncInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // check if work is defined
                initWorkButton();

            }
        }).start();
    }

    private void initWorkButton() {
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        String wLat = sharedPref.getString("work-lat", null);
        String wLong = sharedPref.getString("work-long", null);
        String wAddr = sharedPref.getString("work-address", null);

        if (wLat == null) {
            // enable the add work button
            binding.setIsWorkDefined(false);
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

    public void onAddWork(View view) {
//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        startActivity(new Intent(this, SearchActivity.class));

    }
}
