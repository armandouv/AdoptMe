package com.adoptme;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import java.util.concurrent.Semaphore;

import okhttp3.Headers;

/**
 * An AsyncHttpClient to query the PetFinder API. Handles automatically authentication.
 */
public class PetFinderClient extends AsyncHttpClient {
    public static final String REST_URL = "https://api.petfinder.com/v2";
    private static final String TAG = PetFinderClient.class.getSimpleName();
    // Ensures the token arrives before any query is done.
    private final Semaphore mSemaphore = new Semaphore(1);
    private String mBearerToken;

    public PetFinderClient() {
        super();
        authenticate();
    }

    private void authenticate() {
        try {
            mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                            // Allow queries to be done only after the token has arrived.
                            mSemaphore.release();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Could not parse bearer token");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Could not get bearer token");
                    }
                });
    }

    public void getPets(int page, int pageSize, String locationStr, String distance, JsonHttpResponseHandler handler) {
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + getToken());

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
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + getToken());

        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("limit", pageSize);

        String apiUrl = REST_URL + "/animals";
        get(apiUrl, headers, params, handler);
    }

    private String getToken() {
        // Ensure the token has arrived
        try {
            mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mBearerToken == null)
            throw new RuntimeException("Token is null");

        String token = mBearerToken;
        mSemaphore.release();

        return token;
    }
}
