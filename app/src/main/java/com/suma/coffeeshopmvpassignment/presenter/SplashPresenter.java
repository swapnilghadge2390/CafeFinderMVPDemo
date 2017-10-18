package com.suma.coffeeshopmvpassignment.presenter;

/**
 * Created by suma on 05/10/17.
 */

public interface SplashPresenter {
    void requestPermissions();

    void onPermissionsResult(int requestCode,
                             String permissions[], int[] grantResults);

    void createPermissionDialog(String message);

    void createPermissionDeniedForeverDialog(String message);

    void createLocationSettingsDialog(String message);

    boolean checkGpsAccessible();

    void onDestroy();

}
