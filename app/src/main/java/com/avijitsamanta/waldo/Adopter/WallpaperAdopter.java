package com.avijitsamanta.waldo.Adopter;

import android.annotation.SuppressLint;
import android.content.Context;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;


import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.avijitsamanta.waldo.Modal.WallpaperItemClick;
import com.avijitsamanta.waldo.Modal.Wallpaper;
import com.avijitsamanta.waldo.R;
import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class WallpaperAdopter extends RecyclerView.Adapter<WallpaperAdopter.WallpaperViewHolder> {
    private List<Wallpaper> list;
    private Context context;
    private WallpaperItemClick click;
    private String fragment;

    public WallpaperAdopter(List<Wallpaper> list, Context context, WallpaperItemClick c, String fragment) {
        this.list = list;
        this.context = context;
        this.click = c;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_item, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final WallpaperViewHolder holder, final int position) {
        final Wallpaper wallpaper = list.get(position);
        Glide.with(context)
                .asBitmap()
                .load(wallpaper.getUrl())
                .centerCrop()
                .placeholder(R.drawable.progress_animation)
                .into(holder.imageView);
        holder.textViewResolution.setText(wallpaper.getRes());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.onWallpaperClick(wallpaper, holder.imageView);
            }
        });

        if (wallpaper.isFavourite)
            holder.checkBox.setChecked(true);
        else holder.checkBox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private ImageView imageView;
        private CheckBox checkBox;
        private TextView textViewResolution;


        public WallpaperViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view_wallpaper_item);
            checkBox = itemView.findViewById(R.id.fav_wallpaper_item);
            textViewResolution = itemView.findViewById(R.id.text_view_resolution_wallpaper_item);
            checkBox.setOnCheckedChangeListener(this);

        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(context, "Please login first.....", Toast.LENGTH_SHORT).show();
                compoundButton.setChecked(false);
                return;
            }

            int position = getAdapterPosition();
            Wallpaper w = list.get(position);
            DatabaseReference dbFavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favourites").child(w.category);
            if (b) { // add
                dbFavs.child(w.id).setValue(w);
                list.get(position).isFavourite = true;

            } else {// remove
                dbFavs.child(w.id).setValue(null);
                list.get(position).isFavourite = false;

                if (fragment.equalsIgnoreCase("fav")) {
                    list.remove(position);
                    notifyDataSetChanged();
                }

            }
        }
    }

}
