package com.mta.gpspunchcard.model;

import android.location.Address;
import android.view.View;

public class SearchResult {
    private final Address address;

    public SearchResult(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public int getVisibilityByResult() {
        if (address == null) {
            return View.INVISIBLE;
        }
        return View.VISIBLE;
    }

    public String getResultText() {
        if (address == null) {
            return "nothing found";
        }
        return address.getAddressLine(0);
    }


}
