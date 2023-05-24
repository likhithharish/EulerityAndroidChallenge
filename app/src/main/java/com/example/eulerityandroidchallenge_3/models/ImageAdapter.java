package com.example.eulerityandroidchallenge_3.models;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eulerityandroidchallenge_3.EditImageActivity;
import com.example.eulerityandroidchallenge_3.MainActivity;
import com.example.eulerityandroidchallenge_3.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<ImageModel> imageModels;
    private Context context;

    public ImageAdapter(List<ImageModel> imageModels, Context context) {
        this.imageModels = imageModels;
        this.context = context;
    }

    public void setImageModels(List<ImageModel> imageModels) {
        this.imageModels = imageModels;
        notifyDataSetChanged();
    }

    public void updateImage(String editedImageUrl) {
        if (imageModels != null) {
            for (int i = 0; i < imageModels.size(); i++) {
                ImageModel imageModel = imageModels.get(i);
                if (imageModel.getUrl().equals(editedImageUrl)) {
                    imageModel.setUrl(editedImageUrl);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageModel imageModel = imageModels.get(position);
        Picasso.get().load(imageModel.getUrl())
                .resize(500, 500)
                .centerCrop()
                .into(holder.imageView);

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditImageActivity.class);
            intent.putExtra("imageUrl", imageModel.getUrl());
            ((MainActivity) context).startActivityForResult(intent, MainActivity.EDIT_IMAGE_REQUEST_CODE);
        });
    }

    @Override
    public int getItemCount() {
        return imageModels != null ? imageModels.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
