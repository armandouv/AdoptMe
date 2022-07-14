package com.adoptme;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import okhttp3.Headers;

/**
 * An AsyncHttpClient to query the PetFinder API. Handles automatically authentication.
 */
public class PetFinderClient extends AsyncHttpClient {
    public static final String REST_URL = "https://api.petfinder.com/v2";
    private static final String TAG = PetFinderClient.class.getSimpleName();
    private static String mBearerToken;

    public PetFinderClient() {
        super();
        if (mBearerToken == null) authenticate();
    }

    private void authenticate() {
        String body = "{\"grant_type\":\"client_credentials\"," +
                "\"client_id\":\"" + BuildConfig.CONSUMER_KEY + "\","
                + "\"client_secret\":\"" + BuildConfig.CONSUMER_SECRET + "\"}";

        post(REST_URL + "/oauth2/token", body,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            // TODO: Refresh token
                            mBearerToken = json.jsonObject.getString("access_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Could not parse bearer token");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, response);
                    }
                });
    }

    public void getPets(int page, int pageSize, String locationStr, String distance, JsonHttpResponseHandler handler) {
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + mBearerToken);

        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("limit", pageSize);

        params.put("location", locationStr);
        params.put("distance", distance);
        params.put("sort", "distance");

        String apiUrl = REST_URL + "/animals";
        get(apiUrl, headers, params, handler);
    }

    public void getPets(int page, int pageSize, JsonHttpResponseHandler handler) {
        // TODO: Wait for token
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + mBearerToken);

        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("limit", pageSize);

        String apiUrl = REST_URL + "/animals";
        get(apiUrl, headers, params, handler);
    }
}
