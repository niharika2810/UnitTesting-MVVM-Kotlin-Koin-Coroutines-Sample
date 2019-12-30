package com.example.unittestingsample.util;

/*
 * @author Niharika.Arora
 */

import android.content.Context;
import android.content.SharedPreferences;


import com.example.unittestingsample.backend.BaseApplication;

public class UserHeadersStore {
    private static final String HEADERS_STORE = "user_headers";

    private static SharedPreferences getUserPreferences() {
        return BaseApplication.getContext().getSharedPreferences(HEADERS_STORE, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getUsersEditor() {
        return getUserPreferences().edit();
    }


    public static void setClientId(String clientId) {
        getUsersEditor().putString(Constants.CLIENT, clientId).apply();
    }

    public static String getClientId() {
        return getUserPreferences().getString(Constants.CLIENT, "");
    }

    public static void setUserId(String userId) {
        getUsersEditor().putString(Constants.USER_ID, userId).apply();
    }

    public static String getUserId() {
        return getUserPreferences().getString(Constants.USER_ID, "");
    }

    public static void setAccessToken(String accessToken) {
        getUsersEditor().putString(Constants.ACCESS_TOKEN, accessToken).apply();
    }

    public static String getAccessToken() {
        return getUserPreferences().getString(Constants.ACCESS_TOKEN, "");
    }

}
