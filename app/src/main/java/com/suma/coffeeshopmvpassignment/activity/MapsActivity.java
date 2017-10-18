package com.suma.coffeeshopmvpassignment.activity;

import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suma.coffeeshopmvpassignment.R;
import com.suma.coffeeshopmvpassignment.constants.Constants;
import com.suma.coffeeshopmvpassignment.models.NearByApiResponse;
import com.suma.coffeeshopmvpassignment.models.Result;
import com.suma.coffeeshopmvpassignment.networking.NetworkError;
import com.suma.coffeeshopmvpassignment.presenter.MapsPresenter;
import com.suma.coffeeshopmvpassignment.presenter.MapsPresenterImpl;
import com.suma.coffeeshopmvpassignment.util.Util;
import com.suma.coffeeshopmvpassignment.views.MapsView;

import java.util.List;

import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapsView, GoogleMap.InfoWindowAdapter {
    private GoogleMap mMap;
    private MapsPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //check google play services are installed or not
        if (Util.isGooglePlayServicesAvailable(MapsActivity.this)) {
            presenter = new MapsPresenterImpl(this, this, this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //set info Adapter for showing cafe info dialog once cafe marker clicked
        //pass call to omMapReady() of presenter
        mMap.setInfoWindowAdapter(this);
        presenter.onMapReady();
    }


    /**
     * generate Map
     */
    @Override
    public void generateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * update location on map if there is change in user location
     *
     * @param location
     */
    @Override
    public void updateLocationOnMap(Location location) {
        MarkerOptions markerOptions = presenter.addCurrentLocationOnMap(location);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerOptions.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_LEVEL));
        if (Util.isNetworkAvailable()) {
            presenter.getCoffeeShopList(getString(R.string.type_cafe), location);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection_available), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * get result back from findplaces api and update UI
     *
     * @param response
     * @param location
     */

    @Override
    public void getCoffeeShopListSuccess(Response<NearByApiResponse> response, Location location) {
        mMap.clear();
        MarkerOptions markerOption = presenter.addCurrentLocationOnMap(location);
        mMap.addMarker(markerOption);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerOption.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_LEVEL));
        handleCoffeeShopSuccessResponse(response);
    }

    //handle nearbyPlaces api failure response
    @Override
    public void onFailure(Throwable error) {
        NetworkError networkError = new NetworkError(error);
        Toast.makeText(this, networkError.getAppErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public Activity getViewActivity() {
        return MapsActivity.this;
    }


    @Override
    public void onPermissionsGranted() {

    }

    @Override
    public void onPermissionsDenied() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //disconnect google api client once activity destroyed
        presenter.disconnectFromLocationService();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        presenter.onPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return presenter.createCoffeeShopInfoDialog(marker);
    }

    //handle nearbyplaces api success response depends on status
    private void handleCoffeeShopSuccessResponse(Response<NearByApiResponse> response) {
        String responseStatus = response.body().getStatus();
        switch (responseStatus) {
            case Constants.NEARBY_PLACES_API_OK_STATUS:
                List<Result> resultData = response.body().getResults();
                for (int i = 0; i < resultData.size(); i++) {
                    Result result = resultData.get(i);
                    Double lat = result.getGeometry().getLocation().getLat();
                    Double lng = result.getGeometry().getLocation().getLng();
                    String placeName = result.getName();
                    String vicinity = result.getVicinity();
                    LatLng latLng = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.coffeemug))
                            .title(placeName + " : " + vicinity)
                            .anchor(0.5f, 1))
                            .setTag(result);
                }
                break;
            case Constants.NEARBY_PLACES_API_ZERO_RESULTS_STATUS:
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.there_is_no_coffeeshop_nearby_msg), Toast.LENGTH_LONG).show();
                break;
            case Constants.NEARBY_PLACES_API_OVER_QUERY_LIMIT_STATUS:
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.maximum_query_hit_limit_crossed_msg), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                break;
        }
    }
}
