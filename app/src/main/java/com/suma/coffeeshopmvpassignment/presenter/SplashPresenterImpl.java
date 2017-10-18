package com.suma.coffeeshopmvpassignment.presenter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.suma.coffeeshopmvpassignment.R;
import com.suma.coffeeshopmvpassignment.views.SplashView;

/**
 * Created by suma on 05/10/17.
 */

public class SplashPresenterImpl implements SplashPresenter {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private SplashView splashView;
    private Context context;
    private AlertDialog alertDialogPermissions, alertDialogWithPermissionsDenied, locationSettingsDialog;

    public SplashPresenterImpl(SplashView splashView, Context context) {
        this.splashView = splashView;
        this.context = context;
    }


    // Request permissions for location
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(splashView.getViewActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(splashView.getViewActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(splashView.getViewActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(splashView.getViewActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            // If permissions are granted pass to view
            splashView.onPermissionsGranted();
        }

    }


    // handle permissions result
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPermissionsResult(int requestCode,
                                    String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        //dismiss permissions dialog if showing
                        hidePermissionsDialog();
                        //dismiss never ask permissions dialog if showing
                        hideNeverAskPermissionsDialog();
                        // pass result to vieww
                        splashView.onPermissionsGranted();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(splashView.getViewActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //pass denied result to view
                            splashView.onPermissionsDenied();
                        } else {
                            //pass PermissionsDeniedWithNeverAskAgain result to view
                            hidePermissionsDialog();
                            splashView.onPermissionsDeniedWithNeverAskAgain();
                        }
                    }
                }
            }

        }
    }

    //create location permissions dialog
    @Override
    public void createPermissionDialog(String message) {
        if (alertDialogPermissions == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(splashView.getViewActivity());
            alertDialogBuilder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.dialog_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    splashView.onPermissionsSettings();
                                }
                            });

            alertDialogPermissions = alertDialogBuilder.create();
            alertDialogPermissions.show();
        }
    }

    //create dialog if permisisons denied with nver ask again check box
    @Override
    public void createPermissionDeniedForeverDialog(String message) {
        hidePermissionsDialog();
        if (alertDialogWithPermissionsDenied == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(splashView.getViewActivity());
            alertDialogBuilder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.go_to_permissions_screen),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    splashView.onPermissionsSettings();
                                }
                            });
            alertDialogBuilder.setNegativeButton(context.getString(R.string.no_thanks),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            splashView.getViewActivity().finish();
                        }
                    });
            alertDialogWithPermissionsDenied = alertDialogBuilder.create();
            alertDialogWithPermissionsDenied.show();
        }
    }

    //create location setting dialog if gps not accessible
    @Override
    public void createLocationSettingsDialog(String message) {
        try {
            if (locationSettingsDialog == null) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(splashView.getViewActivity());
                alertDialogBuilder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.dialog_ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        splashView.openLocationSettingsWindowOkButtonClick();
                                    }
                                });
                alertDialogBuilder.setNegativeButton(context.getString(R.string.dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                splashView.getViewActivity().finish();
                            }
                        });
                locationSettingsDialog = alertDialogBuilder.create();
            }
            locationSettingsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //check Gps is accessible or not and return value
    @Override
    public boolean checkGpsAccessible() {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        boolean isGPSOn = false;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isGPSOn = true;
            hideGpsCheckDialog();
        }
        return isGPSOn;
    }

    @Override
    public void onDestroy() {
        alertDialogPermissions = null;
        alertDialogWithPermissionsDenied = null;
        locationSettingsDialog = null;
    }

    // hide location permissions dialog
    private void hidePermissionsDialog() {
        if (alertDialogPermissions != null) {
            if (alertDialogPermissions.isShowing()) {
                alertDialogPermissions.dismiss();
            }
        }
    }

    //hide nver ask permissions dialog
    private void hideNeverAskPermissionsDialog() {
        if (alertDialogWithPermissionsDenied != null) {
            if (alertDialogWithPermissionsDenied.isShowing()) {
                alertDialogWithPermissionsDenied.dismiss();
            }
        }
    }

    //hide gps check dialog
    private void hideGpsCheckDialog() {
        if (locationSettingsDialog != null) {
            if (locationSettingsDialog.isShowing()) {
                locationSettingsDialog.dismiss();
            }
        }
    }

}