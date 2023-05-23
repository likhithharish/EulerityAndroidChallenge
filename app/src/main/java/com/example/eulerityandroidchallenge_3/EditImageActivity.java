package com.example.eulerityandroidchallenge_3;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Context;
import android.content.res.Resources;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eulerityandroidchallenge_3.uploadService.GetUploadURLCallable;
import com.example.eulerityandroidchallenge_3.uploadService.ImageApiService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button addTextButton;
    private Button grayscaleButton;
    private Button doneButton;

    private Button cropButton;

    private String imageUrl;

    private String editedImageUrl;

    private Bitmap originalBitmap;

    private Context context;

    private Resources resources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        context = EditImageActivity.this;
        resources = context.getResources();

        imageView = findViewById(R.id.edit_image_view);
        addTextButton = findViewById(R.id.add_text_button);
        grayscaleButton = findViewById(R.id.convert_to_grayscale_button);
        cropButton = findViewById(R.id.crop_button);
        doneButton = findViewById(R.id.done_button);

        // Get the image URL from the intent
        imageUrl = getIntent().getStringExtra("imageUrl");

        // Display the image using Picasso
        Picasso.get().load(imageUrl)
                .resize(500, 500)
                .centerCrop()
                .into(imageView);

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show a dialog to allow the user to enter the text
                AlertDialog.Builder builder = new AlertDialog.Builder(EditImageActivity.this);
                builder.setTitle("Add Text");
                final EditText editText = new EditText(EditImageActivity.this);
                editText.setHint("Enter text");
                builder.setView(editText);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        addTextToImage(text);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });


        grayscaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement grayscale conversion functionality
                Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                Bitmap grayscaleBitmap = toGrayscale(imageBitmap);
                imageView.setImageBitmap(grayscaleBitmap);
            }
        });


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedImage();
            }
        });

    }





    private Bitmap toGrayscale(Bitmap bitmap) {
        Bitmap grayscaleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayscaleBitmap);
        Paint paint = new Paint();
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return grayscaleBitmap;
    }



    private void addTextToImage(String text) {
        Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(3, 0, 0, Color.BLACK);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = (canvas.getWidth() - bounds.width()) / 2;
        int y = canvas.getHeight() - 50;

        canvas.drawText(text, x, y, paint);

        imageView.setImageBitmap(mutableBitmap);
    }

    private void saveEditedImage() {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap editedBitmap = drawable.getBitmap();

        // Save the edited image locally
        String savedImagePath = saveImageLocally(editedBitmap);
        editedImageUrl = "file://" + savedImagePath;
        getUploadUrl();
    }


    private void getUploadUrl() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> future = executor.submit(new GetUploadURLCallable());

        String url = "";
        try {
            url = future.get();
            uploadImage(url);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void uploadImage(String uploadUrl) {
        File imageFile = new File(editedImageUrl.substring(7)); // Remove the "file://" prefix
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
        RequestBody appidPart = RequestBody.create(MediaType.parse("text/plain"), resources.getString(R.string.app_id));
        RequestBody originalPart = RequestBody.create(MediaType.parse("text/plain"), imageUrl);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(resources.getString(R.string.upload_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ImageApiService apiService = retrofit.create(ImageApiService.class);
        Call<ResponseBody> call = apiService.uploadImage(uploadUrl, appidPart, originalPart, filePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Image upload successful
                    Toast.makeText(EditImageActivity.this, "Image Uploaded Successfully :)", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditImageActivity.this, "Image Upload Failed :(", Toast.LENGTH_SHORT).show();
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle API call failure
            }

        });
    }

    private String saveImageLocally(Bitmap bitmap) {
        String savedImagePath = null;
        String imageFileName = "edited_image.jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File imageFile = new File(storageDir, imageFileName);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            savedImagePath = imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return savedImagePath;
    }



}
