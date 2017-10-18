package com.suma.coffeeshopmvpassignment.networking;

import com.suma.coffeeshopmvpassignment.R;
import com.suma.coffeeshopmvpassignment.application.CoffeeShopApp;

import java.io.IOException;

//handle network related errors
public class NetworkError extends Throwable {
    private Throwable error;

    public NetworkError(Throwable error) {
        this.error = error;
    }

    /**
     * return error msg
     *
     * @return
     */
    public String getAppErrorMessage() {
        if (error instanceof IOException) {
            return CoffeeShopApp.getAppContext().getString(R.string.network_or_conversion_error_happened);
        } else {
            return CoffeeShopApp.getAppContext().getString(R.string.something_went_wrong);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkError that = (NetworkError) o;

        return error != null ? error.equals(that.error) : that.error == null;

    }

    @Override
    public int hashCode() {
        return error != null ? error.hashCode() : 0;
    }
}
