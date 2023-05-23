package com.example.eulerityandroidchallenge_3.retrofitHTTP;


import android.util.Log;

import com.example.eulerityandroidchallenge_3.EditImageActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://eulerity-hackathon.appspot.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        Log.d(BASE_URL, "-------- CALLING API FOR IMAGES --------");
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

