package com.example.eulerityandroidchallenge_3.uploadService;



import com.example.eulerityandroidchallenge_3.models.ImageModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ImageApiService {
    @GET("image")
    Call<List<ImageModel>> getImages();

  @GET
  Call<UploadUrlResponse> getUploadUrl(@Url String url, @Query("appid") String appid, @Query("original") String original);

    @Multipart
    @POST
    Call<ResponseBody> uploadImage(@Url String url, @Part("appid") RequestBody appid, @Part("original") RequestBody original, @Part MultipartBody.Part file);
}








