package com.suma.coffeeshopmvpassignment.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.suma.coffeeshopmvpassignment.R;
import com.suma.coffeeshopmvpassignment.constants.Constants;
import com.suma.coffeeshopmvpassignment.presenter.SplashPresenterImpl;
import com.suma.coffeeshopmvpassignment.views.SplashView;

public class SplashActivity extends AppCompatActivity implements SplashView {
    private SplashPresenterImpl splashPresenter;

    /**
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //call to splashPresenters constructor
        splashPresenter = new SplashPresenterImpl(this, getApplicationContext());

    }

    /**
     * @return
     */
    @Override
    public Activity getViewActivity() {
        return SplashActivity.this;
    }


    /**
     * //handle on Permissions granted result
     */

    @Override
    public void onPermissionsGranted() {
        if (splashPresenter.checkGpsAccessible()) {
            //applied delay
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer.
             */

                @Override
                public void run() {
                    //call Maps Activity
                    Intent i = new Intent(SplashActivity.this, MapsActivity.class);
                    startActivity(i);
                    finish();
                }
            }, Constants.SPLASH_TIME_OUT_MILLISECONDS);
        } else {
            //call to presenters create location settings dialog
            splashPresenter.createLocationSettingsDialog(getString(R.string.gps_status_disabled_msg));
        }
    }

    /**
     * request permissions to user
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        splashPresenter.requestPermissions();
    }

    /**
     * call to presenters permissions dialog window
     */

    @Override
    public void onPermissionsDenied() {
        splashPresenter.createPermissionDialog(getString(R.string.msg_permission_denied));
    }

    /**
     * call to presenters createPermissionDeniedForeverDialog method
     */

    @Override
    public void onPermissionsDeniedWithNeverAskAgain() {
        splashPresenter.createPermissionDeniedForeverDialog(getString(R.string.msg_permission_denied_forever));
    }

    /**
     * open device permissions window
     */
    @Override
    public void onPermissionsSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * will open device gps setting window
     */
    @Override
    public void openLocationSettingsWindowOkButtonClick() {
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);
    }

    /**
     * get result of permissions
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        splashPresenter.onPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashPresenter.onDestroy();
    }
}
