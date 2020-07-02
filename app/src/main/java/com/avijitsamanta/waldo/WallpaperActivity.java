package com.avijitsamanta.waldo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avijitsamanta.waldo.Adopter.WallpaperAdopter;
import com.avijitsamanta.waldo.Fragment.HomeFragment;
import com.avijitsamanta.waldo.Modal.Wallpaper;
import com.avijitsamanta.waldo.Modal.WallpaperItemClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.avijitsamanta.waldo.Adopter.CategoryAdopter.CATEGORY_NAME;

public class WallpaperActivity extends AppCompatActivity implements WallpaperItemClick {
    private RecyclerView recyclerView;
    private List<Wallpaper> wallpaperList = new ArrayList<>();
    private WallpaperAdopter adopter;
    private ProgressBar progressBar;
    public static final String PARCELABLE_WALLPAPER = "wal";
    private DatabaseReference dbRefWallpaper, dbFavs;
    private HashMap<String, Integer> hashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Change Status bar and navigation Bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.category_bg));
        window.setNavigationBarColor(getResources().getColor(R.color.category_bg));

        setContentView(R.layout.activity_wallpaper);

        View decorView = getWindow().getDecorView();
        // Hide Action bar and Navigation bar
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        recyclerView = findViewById(R.id.recycler_view_wallpaper_activity);
        progressBar = findViewById(R.id.progress_bar_wallpaper_activity);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        Intent cat = getIntent();
        if (cat != null) {
            String category = cat.getStringExtra(CATEGORY_NAME);
            if (category != null) {
                dbRefWallpaper = FirebaseDatabase.getInstance().getReference("images").child(category);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    dbFavs = FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favourites").child(category);
                    fetchFavWallpapers(category);
                } else fetchWallPapers(category);
            }
        }
    }

    /**
     * get all fav wallpaper by category
     *
     * @param category key
     */
    private void fetchFavWallpapers(final String category) {
        dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {
                        String id = wallpaperSnapshot.getKey();
                        hashMap.put(id, 0);
                    }
                }
                fetchWallPapers(category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WallpaperActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * it check wallpaper is present or not in fav
     *
     * @param w wallpaper
     * @return boolean
     */
    private boolean isFavourite(Wallpaper w) {
        if (hashMap == null)
            return false;
        return hashMap.containsKey(w.id);
    }

    /**
     * get all wallpaper by category
     *
     * @param category key
     */
    private void fetchWallPapers(final String category) {
        dbRefWallpaper.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {
                        String id = wallpaperSnapshot.getKey();
                        String res = wallpaperSnapshot.child("res").getValue(String.class);
                        String size = wallpaperSnapshot.child("size").getValue(String.class);
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);
                        Wallpaper w = new Wallpaper(id, res, size, title, url, category);

                        if (isFavourite(w)) {
                            w.isFavourite = true;
                        }
                        wallpaperList.add(w);
                    }

                    Collections.reverse(wallpaperList);
                    adopter = new WallpaperAdopter(wallpaperList, WallpaperActivity.this, WallpaperActivity.this, "home");
                    recyclerView.setAdapter(adopter);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WallpaperActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onWallpaperClick(Wallpaper w, ImageView iv) {
        Intent imageIntent = new Intent(WallpaperActivity.this, ImageActivity.class);
        imageIntent.putExtra(PARCELABLE_WALLPAPER, w);

        startActivity(imageIntent);
    }
}