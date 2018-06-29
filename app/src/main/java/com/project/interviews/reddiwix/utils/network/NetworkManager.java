package com.project.interviews.reddiwix.utils.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.project.interviews.reddiwix.datamodel.DataListing;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    //region Const's
    public static final int FETCH_RESULTS_DEFAULT_LIMIT = 25;
    public static final String CLOUD_API_BASE_URL = "https://www.reddit.com";
    private  static final String PROGRAMMER_HUMOR_CHANNEL_PERMALINK = "/r/ProgrammerHumor/";
    //endregion

    //region Private Const's
    private static final String TAG = NetworkManager.class.getSimpleName();
    //endregion

    //region Custom Interfaces / Enums
    public interface NetworkResponse {
        void onResponse(DataListing data);

        void onFailure(Call<DataListing> call, NetworkError error);
    }

    public enum NetworkError {
        NETWORK_ERROR, SERVER_ERROR, OTHER
    }
    //endregion

    //region Singleton
    private static NetworkManager sInstance = null;
    private static final Object sMutex = new Object();

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
        Gson converter = new GsonBuilder().registerTypeAdapter(DataListing.class, new DataListingDeserializer()).create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(CLOUD_API_BASE_URL + PROGRAMMER_HUMOR_CHANNEL_PERMALINK)
                .addConverterFactory(GsonConverterFactory.create(converter));

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
    private Callback<DataListing> getAsWrappedCallback(NetworkResponse callback) {
        final WeakReference<NetworkResponse> callbackWeakRef = new WeakReference<>(callback);
        Callback<DataListing> callbackWrapper = new Callback<DataListing>() {
            @Override
            public void onResponse(Call<DataListing> call, Response<DataListing> response) {
                if (callbackWeakRef.get() != null) {
                    if (response.isSuccessful()) {

                        callbackWeakRef.get().onResponse(response.body());
                    } else {
                        callbackWeakRef.get().onFailure(call, NetworkError.SERVER_ERROR);

                    }
                } else {
                    Log.e(TAG, "Missing callback for network response");
                }
            }

            @Override
            public void onFailure(Call<DataListing> call, Throwable t) {
                if (callbackWeakRef.get() != null) {
                    NetworkError error;
                    if (t instanceof IOException) {
                        error = NetworkError.NETWORK_ERROR;
                    } else {
                        error = NetworkError.OTHER;
                    }

                    callbackWeakRef.get().onFailure(call, error);
                }
                else
                {
                    Log.e(TAG, "An error occurred on network response with following message \n" + t.getMessage());
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
