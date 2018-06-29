package com.project.interviews.reddiwix.Utils.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.project.interviews.reddiwix.datamodel.DataListing;
import com.project.interviews.reddiwix.datamodel.T3post;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataListingDeserializer implements JsonDeserializer<DataListing> {
    @Override
    public DataListing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson converter = new Gson();
        converter.serializeNulls();
        JsonObject responseData = json.getAsJsonObject().get("data").getAsJsonObject();
        DataListing data = converter.fromJson(responseData, DataListing.class);
        ArrayList<T3post> postsList = new ArrayList<>();

        for (JsonElement obj : responseData.get("children").getAsJsonArray()) {
            postsList.add(converter.fromJson(obj.getAsJsonObject().get("data").getAsJsonObject(), T3post.class));
        }

        data.setPosts(postsList);

        return data;
    }
}
