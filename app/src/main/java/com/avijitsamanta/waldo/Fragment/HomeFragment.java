package com.avijitsamanta.waldo.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avijitsamanta.waldo.Adopter.CategoryAdopter;
import com.avijitsamanta.waldo.Adopter.WallpaperAdopter;
import com.avijitsamanta.waldo.ImageActivity;
import com.avijitsamanta.waldo.Modal.Category;
import com.avijitsamanta.waldo.Modal.WallpaperItemClick;
import com.avijitsamanta.waldo.Modal.Wallpaper;
import com.avijitsamanta.waldo.R;
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
import java.util.Objects;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdopter adopter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        recyclerView = view.findViewById(R.id.recycler_view_home_fragment);
        progressBar = view.findViewById(R.id.progress_bar_home_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        DatabaseReference dbRefWallpaper = FirebaseDatabase.getInstance().getReference("category");

        dbRefWallpaper.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot category : dataSnapshot.getChildren()) {
                        String key = category.getKey();
                        String title = category.child("desc").getValue(String.class);
                        String url = category.child("url").getValue(String.class);
                        Category c = new Category(key, url, title);
                        categoryList.add(c);
                    }
                    adopter = new CategoryAdopter(categoryList, getActivity());
                    recyclerView.setAdapter(adopter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
