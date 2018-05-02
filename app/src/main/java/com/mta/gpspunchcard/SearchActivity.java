package com.mta.gpspunchcard;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.mta.gpspunchcard.databinding.ActivitySearchBinding;
import com.mta.gpspunchcard.model.SearchResult;

import java.io.IOException;
import java.util.List;

/**
 * reference:
 * https://developer.android.com/training/location/display-address
 * <p>
 * https://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
 */
public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding searchBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        searchBinding.setLifecycleOwner(this);
        searchBinding.setSearchResult(new SearchResult(null));
        searchBinding.setIsSearching(false);

        searchBinding.editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    onSearchClicked(searchBinding.editText);
                    return true;
                }
                return false;
            }
        });

    }

    public void onSearchClicked(View view) {
        searchBinding.setIsSearching(true);

        // async search the lat/long of the work
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Address> locationFromAddress = getLocationFromAddress(searchBinding.editText.getText().toString());
                searchBinding.setIsSearching(false);
                if (locationFromAddress != null && locationFromAddress.size() > 0) {
                    // I found out that this API always return size==1, so no point in adding a recyclerView here
                    searchBinding.setSearchResult(new SearchResult(locationFromAddress.get(0)));
                } else {
                    searchBinding.setSearchResult(new SearchResult(null));
                }
            }
        }).start();
    }

    public List<Address> getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address = null;

        try {
            address = coder.getFromLocationName(strAddress, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public void onSave(final View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // save address in SharedPreferences
                Address address = searchBinding.getSearchResult().getAddress();

                if (address != null) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("work-lat", Double.toString(address.getLatitude()));
                    editor.putString("work-long", Double.toString(address.getLongitude()));
                    editor.putString("work-address", address.getAddressLine(0));

                    editor.apply();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, getString(R.string.address_saved), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

                finish();

            }
        }).start();
    }

    public void onCancel(View view) {
        finish();
    }
}
