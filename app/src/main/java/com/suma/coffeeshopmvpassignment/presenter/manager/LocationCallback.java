package com.suma.coffeeshopmvpassignment.presenter.manager;

import android.location.Location;

public interface LocationCallback {

    void onLocationApiManagerConnected();

    void onLocationChanged(Location location);
}