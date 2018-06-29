package com.project.interviews.reddiwix.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.project.interviews.reddiwix.datamodel.T3post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavoritesManager {

    //region Const's
    private static final String FAV_FILE_NAME = "com.project.interviews.reddiwix.favpostfile";
    //endregion

    //region Singleton
    private static FavoritesManager sInstance = null;
    private static final Object sMutex = new Object();

    public static FavoritesManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (sMutex) {
                if (sInstance == null) {
                    sInstance = new FavoritesManager(context);
                }
            }
        }

        return sInstance;
    }
    //endregion

    //region Data Members
    private SharedPreferences mSharedPrefs;
    private HashMap<String, T3post> mCachedPosts;

    private Gson mGsonConverter = new Gson();
    //endregion

    //region C'tor
    private FavoritesManager(Context context) {
        mSharedPrefs = context.getSharedPreferences(FAV_FILE_NAME, Context.MODE_PRIVATE);
        mCachedPosts = new HashMap<>();
        retrieveFavPostArrayFromSharedPrefs();
    }
    //endregion

    //region Public Methods
    public void insertNewFavPost(T3post favPost) {
        if (!isFavPost(favPost.getId())) {
            mCachedPosts.put(favPost.getId(), favPost);
            mSharedPrefs.edit().putString(favPost.getId(), mGsonConverter.toJson(favPost)).apply();
        }
    }

    public ArrayList<T3post> getFavPosts() {
        return new ArrayList<>(mCachedPosts.values());
    }

    public void removeFavPost(String postId) {
        if (mCachedPosts.containsKey(postId)) {
            mCachedPosts.remove(postId);
            mSharedPrefs.edit().remove(postId).apply();
        }
    }

    public boolean isFavPost(String postId) {
        return mCachedPosts.containsKey(postId);
    }
    //endregion

    //region Private Methods
    private void retrieveFavPostArrayFromSharedPrefs() {
        @SuppressWarnings("unchecked") final Map<String, String> storedPosts = new HashMap<>((Map<String, String>) mSharedPrefs.getAll());

        //populate cachedPosts map with saved posts from SharedPrefs
        for (Map.Entry<String, String> entry : storedPosts.entrySet()) {
            mCachedPosts.put(entry.getKey(), mGsonConverter.fromJson(entry.getValue(), T3post.class));
        }
    }
    //endregion
}
