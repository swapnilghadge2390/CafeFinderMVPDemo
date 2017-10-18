package com.suma.coffeeshopmvpassignment.presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suma.coffeeshopmvpassignment.R;
import com.suma.coffeeshopmvpassignment.constants.Constants;
import com.suma.coffeeshopmvpassignment.models.NearByApiResponse;
import com.suma.coffeeshopmvpassignment.models.OpeningHours;
import com.suma.coffeeshopmvpassignment.models.Result;
import com.suma.coffeeshopmvpassignment.networking.RetrofitClient;
import com.suma.coffeeshopmvpassignment.presenter.manager.GoogleLocationApiManager;
import com.suma.coffeeshopmvpassignment.presenter.manager.LocationCallback;
import com.suma.coffeeshopmvpassignment.views.MapsView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsPresenterImpl implements MapsPresenter, LocationCallback {

    private MapsView view;
    private GoogleLocationApiManager googleLocationApiManager;
    private Context context;


    public MapsPresenterImpl(MapsView view, FragmentActivity fragmentActivity, Context context) {
        this.context = context;
        if (view == null) throw new NullPointerException(context.getString(R.string.view_error));
        if (fragmentActivity == null)
            throw new NullPointerException(context.getString(R.string.fragment_error));
        this.view = view;
        //request permissions if build os is M or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }
        this.googleLocationApiManager = new GoogleLocationApiManager(fragmentActivity, context);
        this.googleLocationApiManager.setLocationCallback(this);
        this.view.generateMap();
    }


    @Override
    public void onLocationApiManagerConnected() {

    }


    @Override
    public void onLocationChanged(Location location) {
        view.updateLocationOnMap(location);

    }


    @Override
    public void connectToLocationService() {
        googleLocationApiManager.connect();
    }


    @Override
    public void disconnectFromLocationService() {
        if (googleLocationApiManager.isConnectionEstablished()) {
            googleLocationApiManager.disconnect();
        }
    }

    /**
     * @param placeType
     * @param location
     */
    @Override
    public void getCoffeeShopList(String placeType, final Location location) {
        Call<NearByApiResponse> call = RetrofitClient.getApiService().getNearbyPlaces(placeType, location.getLatitude() + "," + location.getLongitude(), Constants.PROXIMITY_RADIUS_METERS);
        call.enqueue(new Callback<NearByApiResponse>() {
            //handle success
            @Override
            public void onResponse(Call<NearByApiResponse> call, Response<NearByApiResponse> response) {
                view.getCoffeeShopListSuccess(response, location);
            }

            //handle failure
            @Override
            public void onFailure(Call<NearByApiResponse> call, Throwable t) {
                view.onFailure(t);
            }
        });
    }

    @Override
    public MarkerOptions addCurrentLocationOnMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(context.getString(R.string.current_position));
        // Adding colour to the marker
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        return markerOptions;

    }


    @Override
    public void onMapReady() {
        connectToLocationService();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(view.getViewActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(view.getViewActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(view.getViewActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(view.getViewActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            view.onPermissionsGranted();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        {
            switch (requestCode) {
                case Constants.MY_PERMISSIONS_REQUEST_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted. Do the
                        // contacts-related task you need to do.
                        if (view.getViewActivity().checkSelfPermission(
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            view.onPermissionsGranted();
                        }

                    } else {
                        Toast.makeText(context, context.getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
                        view.onPermissionsDenied();
                    }
                }

            }
        }
    }

    /// create CoffeeShop info dialog which shows ratings and whether cafe open or not info
    @Override
    public View createCoffeeShopInfoDialog(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myContentsView = inflater.inflate(R.layout.coffee_shop_info_dialog, null);
        TextView tvTitle = myContentsView.findViewById(R.id.title);
        ImageView cafeImageView = myContentsView.findViewById(R.id.cafeImage);
        TextView tvSnippet = myContentsView.findViewById(R.id.snippet);
        RatingBar ratingbar = myContentsView.findViewById(R.id.ratingBar);
        TextView isOpenTextView = myContentsView.findViewById(R.id.isOpenTextView);
        if (marker.getTitle().equalsIgnoreCase(context.getString(R.string.current_position))) {
            isOpenTextView.setVisibility(View.GONE);
            ratingbar.setVisibility(View.GONE);
            cafeImageView.setVisibility(View.GONE);
        } else {
            isOpenTextView.setVisibility(View.VISIBLE);
            ratingbar.setVisibility(View.VISIBLE);
            cafeImageView.setVisibility(View.VISIBLE);
            Result markerTag = (Result) marker.getTag();
            if (markerTag != null) {
                OpeningHours openingHours = markerTag.getOpeningHours();
                if (openingHours != null) {
                    String openStatus = context.getString(R.string.open_text) + " " + (markerTag.getOpeningHours().getOpenNow() ? context.getString(R.string.open_status_yes) : context.getString(R.string.open_status_no));
                    isOpenTextView.setText(openStatus);
                }
                if (markerTag.getRating() != null) {
                    ratingbar.setRating(markerTag.getRating().floatValue());
                } else {
                    ratingbar.setRating(0.0f);
                }
            }
        }
        tvTitle.setText(marker.getTitle());
        tvSnippet.setText(marker.getSnippet());
        return myContentsView;
    }


}

