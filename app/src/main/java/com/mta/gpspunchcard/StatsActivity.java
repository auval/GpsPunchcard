package com.mta.gpspunchcard;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mta.gpspunchcard.model.GeoFencingHelper;
import com.mta.gpspunchcard.model.GeofenceEvent;
import com.mta.gpspunchcard.presenter.Logic;
import com.mta.gpspunchcard.storage.GeofenceLogDb;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {
    private static final String TAG = StatsActivity.class.getSimpleName();
    RecyclerView mRecyclerView;
    StatsAdapter mAdapter;
    TextView mTotalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        ViewGroup header = findViewById(R.id.header);
        header.setBackgroundColor(0xffeeeeee);
        TextView entetance = header.findViewById(R.id.entrance);
        entetance.setText(R.string.entrance);
        TextView exit = header.findViewById(R.id.exit);
        exit.setText(R.string.exit);

        mRecyclerView = findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<GeofenceEvent> hw = new ArrayList<>();


        // specify an adapter (see also next example)
        mAdapter = new StatsAdapter(hw);
        mRecyclerView.setAdapter(mAdapter);

        mTotalTime = findViewById(R.id.total);

        observeDb();
    }

    protected void observeDb() {

        // Create the observer which updates the UI.
        final Observer<List<GeofenceEvent>> logObserver = new Observer<List<GeofenceEvent>>() {

            @Override
            public void onChanged(@Nullable final List<GeofenceEvent> newLog) {
                // todo: calculate total and set result
                Log.i(TAG, "onChanged: in activity");

                Logic logic = new Logic();
                long totalTime = logic.calculateTotalTime(newLog);

                // should display a more flexible conversion then minutes
                mTotalTime.setText("Total time is " + (totalTime / 60000) + " minutes");

                mAdapter.setData(newLog);
            }
        };

        LiveData<List<GeofenceEvent>> GeofenceLogEntityLiveData = GeofenceLogDb.getInstance(this).readGeofenceLog();

        GeofenceLogEntityLiveData.observe(this, logObserver);
    }


    public void onMockExit(View view) {
        GeoFencingHelper.insertEvent(this, -1);
    }

    public void onMockEnter(View view) {
        GeoFencingHelper.insertEvent(this, 1);
    }


}
