package com.suma.coffeeshopmvpassignment.presenter;

import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * map presenter interface
 */
public interface MapsPresenter {

    void connectToLocationService();

    void disconnectFromLocationService();

    void getCoffeeShopList(String placeType, Location location);

    MarkerOptions addCurrentLocationOnMap(Location location);

    void onMapReady();

    void requestPermissions();

    void onPermissionsResult(int requestCode,
                             String permissions[], int[] grantResults);

    View createCoffeeShopInfoDialog(Marker marker);

}