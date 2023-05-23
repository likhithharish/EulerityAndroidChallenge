
package com.example.eulerityandroidchallenge_3;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eulerityandroidchallenge_3.models.ImageModel;
import com.example.eulerityandroidchallenge_3.uploadService.ImageApiService;
import com.example.eulerityandroidchallenge_3.retrofitHTTP.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;

    private static final int EDIT_IMAGE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.linear_layout);

        ImageApiService apiService = RetrofitClient.getClient().create(ImageApiService.class);
        Call<List<ImageModel>> call = apiService.getImages();
        call.enqueue(new Callback<List<ImageModel>>() {
            @Override
            public void onResponse(Call<List<ImageModel>> call, Response<List<ImageModel>> response) {
                if (response.isSuccessful()) {
                    List<ImageModel> imageModels = response.body();
                    if (imageModels != null) {
                        for (ImageModel imageModel : imageModels) {
                            LinearLayout itemLayout = new LinearLayout(MainActivity.this);
                            LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            itemLayout.setLayoutParams(itemLayoutParams);
                            itemLayout.setOrientation(LinearLayout.VERTICAL);
                            itemLayout.setGravity(Gravity.CENTER_HORIZONTAL);

                            ImageView imageView = new ImageView(MainActivity.this);
                            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            imageView.setLayoutParams(imageLayoutParams);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageView.setAdjustViewBounds(true);
                            imageView.setPadding(0, 0, 0, 10);
                            Picasso.get().load(imageModel.getUrl())
                                    .resize(500, 500) // Set the desired dimension
                                    .centerCrop()
                                    .into(imageView);
                            itemLayout.addView(imageView);

                            LinearLayout buttonLayout = new LinearLayout(MainActivity.this);
                            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            buttonLayout.setLayoutParams(buttonLayoutParams);
                            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                            buttonLayout.setGravity(Gravity.CENTER);

                            Button editButton = new Button(MainActivity.this);
                            LinearLayout.LayoutParams editButtonLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            editButtonLayoutParams.setMargins(0, 16, 8, 0);
                            editButton.setLayoutParams(editButtonLayoutParams);
                            editButton.setText("Edit & Upload Image");
                            editButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, EditImageActivity.class);
                                    intent.putExtra("imageUrl", imageModel.getUrl());
                                    startActivityForResult(intent, EDIT_IMAGE_REQUEST_CODE);
                                }
                            });
                            buttonLayout.addView(editButton);

                            itemLayout.addView(buttonLayout);

                            linearLayout.addView(itemLayout);
                        }
                    }
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
            updateImageView(editedImageUrl);
        }
    }

    private void updateImageView(String editedImageUrl) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View childView = linearLayout.getChildAt(i);
            if (childView instanceof LinearLayout) {
                LinearLayout itemLayout = (LinearLayout) childView;
                ImageView imageView = (ImageView) itemLayout.getChildAt(0);
                if (imageView != null && imageView.getTag() != null && imageView.getTag().equals(editedImageUrl)) {
                    Picasso.get().load(editedImageUrl)
                            .resize(500, 500)
                            .centerCrop()
                            .into(imageView);
                    break;
                }
            }
        }
    }
}
