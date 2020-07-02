package com.avijitsamanta.waldo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.avijitsamanta.waldo.Adopter.WallpaperAdopter;
import com.avijitsamanta.waldo.ImageActivity;
import com.avijitsamanta.waldo.Modal.Wallpaper;
import com.avijitsamanta.waldo.Modal.WallpaperItemClick;
import com.avijitsamanta.waldo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.avijitsamanta.waldo.WallpaperActivity.PARCELABLE_WALLPAPER;

public class FavFragment extends Fragment implements WallpaperItemClick {

    private RecyclerView recyclerView;
    private WallpaperAdopter adopter;
    private ProgressBar progressBar;
    private LottieAnimationView lottie;
    private DatabaseReference dbFavRef, dbCategory;
    private List<Wallpaper> fabList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        lottie = view.findViewById(R.id.lottie_fav_fragment);
        progressBar = view.findViewById(R.id.progress_bar_fav_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_fav_fragment);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setHasFixedSize(true);

        if (user != null) {
            lottie.setVisibility(View.GONE);
            String uid = user.getUid();
            dbFavRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("favourites");
            dbCategory = FirebaseDatabase.getInstance().getReference("users").child(uid).child("favourites");
            adopter = new WallpaperAdopter(fabList, getActivity(), FavFragment.this, "fav");
            getCategory();
            //getFavWallpaper("nature");
        } else {
            progressBar.setVisibility(View.GONE);
            lottie.setVisibility(View.VISIBLE);
        }

    }

    /**
     * get the all category
     */
    private void getCategory() {
        dbFavRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot category : snapshot.getChildren()) {
                        getFavWallpaper(category.getKey());
                    }
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setAdapter(adopter);
                } else {
                    lottie.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * get the all fav wallpaper by category
     *
     * @param category key
     */
    private void getFavWallpaper(final String category) {
        dbCategory.child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : snapshot.getChildren()) {
                        String id = wallpaperSnapshot.getKey();
                        String res = wallpaperSnapshot.child("res").getValue(String.class);
                        String size = wallpaperSnapshot.child("size").getValue(String.class);
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);
                        Wallpaper w = new Wallpaper(id, res, size, title, url, category);

                        w.isFavourite = true;
                        fabList.add(w);
                    }
                    adopter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onWallpaperClick(Wallpaper w, ImageView iv) {
        Intent imageIntent = new Intent(getActivity(), ImageActivity.class);
        imageIntent.putExtra(PARCELABLE_WALLPAPER, w);
        startActivity(imageIntent);
    }
}
