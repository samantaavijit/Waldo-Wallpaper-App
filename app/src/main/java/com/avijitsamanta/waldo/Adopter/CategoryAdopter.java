package com.avijitsamanta.waldo.Adopter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avijitsamanta.waldo.Modal.Category;
import com.avijitsamanta.waldo.R;
import com.avijitsamanta.waldo.WallpaperActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class CategoryAdopter extends RecyclerView.Adapter<CategoryAdopter.CategoryViewHolder> {
    public static final String CATEGORY_NAME = "category";
    private List<Category> list;
    private Context context;

    public CategoryAdopter(List<Category> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        final Category category = list.get(position);

        Glide.with(context)
                .asBitmap()
                .load(category.getUrl())
                .centerCrop()
                .placeholder(R.drawable.progress_animation)
                .into(holder.imageView);

        holder.textView.setText(category.getCategoryName());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WallpaperActivity.class);
                intent.putExtra(CATEGORY_NAME, category.getCategoryName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_category_item);
            textView = itemView.findViewById(R.id.text_view_category_item);
        }
    }
}
