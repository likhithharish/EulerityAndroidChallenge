package com.example.eulerityandroidchallenge_3;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.eulerityandroidchallenge_3.models.ImageModel;
//import com.example.eulerityandroidchallenge_3.uploadService.ImageApiService;
//import com.example.eulerityandroidchallenge_3.retrofitHTTP.RetrofitClient;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//
//    public static final int EDIT_IMAGE_REQUEST_CODE = 1;
//    private RecyclerView recyclerView;
//    private ImageAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new ImageAdapter(null, this);
//        recyclerView.setAdapter(adapter);
//
//        ImageApiService apiService = RetrofitClient.getClient().create(ImageApiService.class);
//        Call<List<ImageModel>> call = apiService.getImages();
//        call.enqueue(new Callback<List<ImageModel>>() {
//            @Override
//            public void onResponse(Call<List<ImageModel>> call, Response<List<ImageModel>> response) {
//                if (response.isSuccessful()) {
//                    List<ImageModel> imageModels = response.body();
//                    adapter.setImageModels(imageModels);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<ImageModel>> call, Throwable t) {
//                // Handle API call failure
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == EDIT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            String editedImageUrl = data.getStringExtra("editedImageUrl");
//            adapter.updateImage(editedImageUrl);
//        }
//    }
//}
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eulerityandroidchallenge_3.models.ImageAdapter;
import com.example.eulerityandroidchallenge_3.models.ImageModel;
import com.example.eulerityandroidchallenge_3.uploadService.ImageApiService;
import com.example.eulerityandroidchallenge_3.retrofitHTTP.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    public static final int EDIT_IMAGE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageAdapter(null, this);
        recyclerView.setAdapter(adapter);

        // Make the API call asynchronously
        ImageApiService apiService = RetrofitClient.getClient().create(ImageApiService.class);
        Call<List<ImageModel>> call = apiService.getImages();
        call.enqueue(new Callback<List<ImageModel>>() {
            @Override
            public void onResponse(Call<List<ImageModel>> call, Response<List<ImageModel>> response) {
                if (response.isSuccessful()) {
                    List<ImageModel> imageModels = response.body();
                    adapter.setImageModels(imageModels);
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<ImageModel>> call, Throwable t) {
                // Handle API call failure
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String editedImageUrl = data.getStringExtra("editedImageUrl");
            adapter.updateImage(editedImageUrl);
        }
    }
}
