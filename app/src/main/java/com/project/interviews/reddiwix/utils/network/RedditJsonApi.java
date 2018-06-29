package com.project.interviews.reddiwix.utils.network;

import com.project.interviews.reddiwix.datamodel.DataListing;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RedditJsonApi {
    @Headers({"Content-Type: application/json"})
    @GET("new/.json")
    Call<DataListing> getMostRecentPosts(@Query("limit") int resultLimit);

    @GET(".json")
    Call<DataListing> getPrevPosts(@Query("after") String nextListingName, @Query("limit") int resultLimit);
}
