package com.suma.coffeeshopmvpassignment.networking;

import com.suma.coffeeshopmvpassignment.constants.Constants;
import com.suma.coffeeshopmvpassignment.models.NearByApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * retrofit response Interface for calling nearbysearch api
 */
public interface RequestInterface {

        @GET("api/place/nearbysearch/"+ Constants.API_RESPONSE_FORMAT+"?sensor=true&key="+Constants.MAP_KEY)
        Call<NearByApiResponse> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);
    }