package com.project.interviews.reddiwix.Utils.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.project.interviews.reddiwix.datamodel.ListingData;
import com.project.interviews.reddiwix.datamodel.T3post;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class NetworkManager {

    //region Public Const's
    public static final int FETCH_RESULTS_DEFAULT_LIMIT = 25;
    public static final String CLOUD_API_BASE_URL = "https://www.reddit.com";
    public static final String PROGRAMMER_HUMOR_CHANNEL_PERMALINK = "/r/ProgrammerHumor/";
    //endregion

    //region Private Const's
    private static final String TAG = NetworkManager.class.getSimpleName();
    //endregion

    //region Custom Interfaces
    public interface NetworkResponse {
        void onResponse(ListingData data);

        void onFailure(Call<JsonObject> call, Throwable t);
    }

    private interface RedditJsonApi {
        @Headers({"Content-Type: application/json"})
        @GET("new/.json")
        Call<JsonObject> getMostRecentPosts(@Query("limit") int resultLimit);

        @GET(".json")
        Call<JsonObject> getPrevPosts(@Query("after") String nextListingName, @Query("limit") int resultLimit);
    }
    //endregion

    //region Singleton
    private static NetworkManager sInstance = null;
    private static Object sMutex = new Object();

    public static NetworkManager getInstance() {
        if (sInstance == null) {
            synchronized (sMutex) {
                if (sInstance == null) {
                    sInstance = new NetworkManager();
                }
            }
        }

        return sInstance;
    }
    //endregion

    //region Data Members
    private Retrofit mRetroClient;
    private RedditJsonApi mApiImplementation;
    //endregion

    //region C'tor
    private NetworkManager() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(CLOUD_API_BASE_URL + PROGRAMMER_HUMOR_CHANNEL_PERMALINK)
                .addConverterFactory(
                        GsonConverterFactory.create());

        mRetroClient = builder.client(httpClient.build()).build();
        mApiImplementation = mRetroClient.create(RedditJsonApi.class);
    }
    //endregion

    //region Public Methods
    public void getMostRecentPosts(NetworkResponse callback) {
        enqueueCall(mApiImplementation.getMostRecentPosts(FETCH_RESULTS_DEFAULT_LIMIT), getAsWrappedCallback(callback));
    }

    public void getPostsAfter(NetworkResponse callback, String nextListingName) {
        enqueueCall(mApiImplementation.getPrevPosts(nextListingName, FETCH_RESULTS_DEFAULT_LIMIT), getAsWrappedCallback(callback));
    }
    //endregion

    //region Private Methods
    private Callback<JsonObject> getAsWrappedCallback(NetworkResponse callback)
    {
        final WeakReference<NetworkResponse> callbackWeakRef = new WeakReference<>(callback);
        Callback<JsonObject> callbackWrapper = new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, response.toString());

                //TODO move work of main thread!!!
                if (callbackWeakRef.get() != null) {
                    Gson converter = new Gson();
                    JsonObject responseData = response.body().get("data").getAsJsonObject();
                    ListingData data = new ListingData();
                    data.setPrevListing(responseData.get("before").isJsonNull() ? "" : responseData.get("before").getAsString());
                    data.setNextListing(responseData.get("after").isJsonNull() ? "" : responseData.get("after").getAsString());
                    ArrayList<T3post> postsList = new ArrayList<>();

                    for (JsonElement obj : responseData.get("children").getAsJsonArray()) {
                        postsList.add(converter.fromJson(obj.getAsJsonObject().get("data").getAsJsonObject(), T3post.class));
                    }

                    data.setPosts(postsList);
                    callbackWeakRef.get().onResponse(data);
                } else {
                    Log.e(TAG, "Missing callback for network response");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, t.getMessage());

                if (callbackWeakRef.get() != null) {
                    callbackWeakRef.get().onFailure(call, t);
                }
            }
        };

        return callbackWrapper;
    }
    private void enqueueCall(Call call, Callback callback) {
        call.enqueue(callback);
    }
    //endregion
}
