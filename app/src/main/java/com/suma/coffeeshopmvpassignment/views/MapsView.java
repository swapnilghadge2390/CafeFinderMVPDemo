package com.suma.coffeeshopmvpassignment.views;

import android.app.Activity;
import android.location.Location;

import com.suma.coffeeshopmvpassignment.models.NearByApiResponse;

import retrofit2.Response;

public interface MapsView {

    void generateMap();

    void updateLocationOnMap(Location location);

    void getCoffeeShopListSuccess(Response<NearByApiResponse> response, Location location);

    void onFailure(Throwable error);

    Activity getViewActivity();

    void onPermissionsGranted();

    void onPermissionsDenied();
}