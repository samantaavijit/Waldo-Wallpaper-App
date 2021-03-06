package com.avijitsamanta.waldo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.palette.graphics.Palette;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;

import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.avijitsamanta.waldo.Modal.Wallpaper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;


import static com.avijitsamanta.waldo.WallpaperActivity.PARCELABLE_WALLPAPER;

public class ImageActivity extends AppCompatActivity {
    private Wallpaper w;
    private PhotoView mainImage;
    private ImageView imageViewSetWallpaperDefault, imageViewDownloadDefault,
            imageViewSetWallpaper, imageViewDownload, shareDefault, share;
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayoutDefault;
    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch darkMutedSwatch;
    private final int REQUEST_CODE = 121;
    private String type;
    private TextView tv_img_name;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change Status bar and navigation Bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.category_bg));
        window.setNavigationBarColor(getResources().getColor(R.color.category_bg));

        setContentView(R.layout.activity_image);

        View decorView = getWindow().getDecorView();
        // Hide Action bar and Navigation bar
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.hide();

        mainImage = findViewById(R.id.photo_view_image_activity);
        checkBox = findViewById(R.id.fav_wallpaper_image_activity);
        progressBar = findViewById(R.id.progress_bar_image_activity);
        relativeLayoutDefault = findViewById(R.id.open_bottom_sheet);
        imageViewSetWallpaperDefault = findViewById(R.id.set_wallpaper_default);
        imageViewDownloadDefault = findViewById(R.id.download_wallpaper_default);
        imageViewSetWallpaper = findViewById(R.id.set_wallpaper);
        imageViewDownload = findViewById(R.id.download_wallpaper);
        tv_img_name = findViewById(R.id.name);
        TextView textViewSizeDefault = findViewById(R.id.size_wallpaper_default);
        TextView tvResolution = findViewById(R.id.tv_resolution);
        TextView tvSize = findViewById(R.id.tv_size);
        TextView tvType = findViewById(R.id.tv_type);
        final TextView tvName = findViewById(R.id.tv_name);
        TextView title = findViewById(R.id.tv_title);
        shareDefault = findViewById(R.id.share_wallpaper_default);
        share = findViewById(R.id.share_wallpaper);
        View bottomSheet = findViewById(R.id.bottom_sheet);


        // Back to the previous activity
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageActivity.this.finish();
            }
        });

        imageViewSetWallpaperDefault.setVisibility(View.GONE);
        imageViewDownload.setVisibility(View.GONE);
        imageViewSetWallpaper.setVisibility(View.GONE);
        imageViewDownloadDefault.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        shareDefault.setVisibility(View.GONE);
        Intent getIn = getIntent();
        if (getIn != null) {
            w = getIn.getParcelableExtra(PARCELABLE_WALLPAPER);
            if (w != null) {

                Glide.with(this)
                        .asBitmap()
                        .load(w.getUrl())
                        .into(new BitmapImageViewTarget(mainImage) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                super.onResourceReady(resource, transition);
                                progressBar.setVisibility(View.GONE);
                                imageViewSetWallpaperDefault.setVisibility(View.VISIBLE);
                                imageViewDownload.setVisibility(View.VISIBLE);
                                imageViewSetWallpaper.setVisibility(View.VISIBLE);
                                imageViewDownloadDefault.setVisibility(View.VISIBLE);
                                share.setVisibility(View.VISIBLE);
                                shareDefault.setVisibility(View.VISIBLE);

                                Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(@Nullable Palette palette) {
                                        if (palette != null) {
                                            vibrantSwatch = palette.getVibrantSwatch();
                                            lightVibrantSwatch = palette.getLightVibrantSwatch();
                                            darkVibrantSwatch = palette.getDarkVibrantSwatch();
                                            mutedSwatch = palette.getMutedSwatch();
                                            darkMutedSwatch = palette.getDarkMutedSwatch();
                                            // Color 1
                                            if (vibrantSwatch != null) {
                                                int rgb = vibrantSwatch.getRgb();
                                                ImageView c1 = findViewById(R.id.img_color_1);
                                                c1.setColorFilter(rgb);
                                                final String ss = Integer.toHexString(rgb & 0x00ffffff);
                                                TextView t1 = findViewById(R.id.tv_color_1);
                                                t1.setText("#" + ss);
                                                // Copy the color code
                                                findViewById(R.id.ll_color_1).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        copyTheColorCode(ss);
                                                        Toast.makeText(ImageActivity.this, "#" + ss, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                findViewById(R.id.ll_color_1).setVisibility(View.GONE);
                                            }

                                            //Color 2
                                            if (lightVibrantSwatch != null) {
                                                int rgb = lightVibrantSwatch.getRgb();
                                                ImageView c1 = findViewById(R.id.img_color_2);
                                                c1.setColorFilter(rgb);
                                                final String ss = Integer.toHexString(rgb & 0x00ffffff);
                                                TextView t1 = findViewById(R.id.tv_color_2);
                                                t1.setText("#" + ss);
                                                // Copy the color code
                                                findViewById(R.id.ll_color_2).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        copyTheColorCode(ss);
                                                        Toast.makeText(ImageActivity.this, "#" + ss, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                findViewById(R.id.ll_color_2).setVisibility(View.GONE);
                                            }

                                            // color 3
                                            if (darkVibrantSwatch != null) {
                                                int rgb = darkVibrantSwatch.getRgb();
                                                ImageView c1 = findViewById(R.id.img_color_3);
                                                c1.setColorFilter(rgb);
                                                final String ss = Integer.toHexString(rgb & 0x00ffffff);
                                                TextView t1 = findViewById(R.id.tv_color_3);
                                                t1.setText("#" + ss);
                                                // Copy the color code
                                                findViewById(R.id.ll_color_3).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        copyTheColorCode(ss);
                                                        Toast.makeText(ImageActivity.this, "#" + ss, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                findViewById(R.id.ll_color_3).setVisibility(View.GONE);
                                            }
                                            // Color 4
                                            if (mutedSwatch != null) {
                                                int rgb = mutedSwatch.getRgb();
                                                ImageView c1 = findViewById(R.id.img_color_4);
                                                c1.setColorFilter(rgb);
                                                final String ss = Integer.toHexString(rgb & 0x00ffffff);
                                                TextView t1 = findViewById(R.id.tv_color_4);
                                                t1.setText("#" + ss);
                                                // Copy the color code
                                                findViewById(R.id.ll_color_4).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        copyTheColorCode(ss);
                                                        Toast.makeText(ImageActivity.this, "#" + ss, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                findViewById(R.id.ll_color_4).setVisibility(View.GONE);
                                            }

                                            // Color 5
                                            if (darkMutedSwatch != null) {
                                                int rgb = darkMutedSwatch.getRgb();
                                                ImageView c1 = findViewById(R.id.img_color_5);
                                                c1.setColorFilter(rgb);
                                                final String ss = Integer.toHexString(rgb & 0x00ffffff);
                                                TextView t1 = findViewById(R.id.tv_color_5);
                                                t1.setText("#" + ss);
                                                // Copy the color code
                                                findViewById(R.id.ll_color_5).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        copyTheColorCode(ss);
                                                        Toast.makeText(ImageActivity.this, "#" + ss, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                findViewById(R.id.ll_color_5).setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                });
                            }

                        });

                checkBox.setChecked(w.isFavourite);
                tvResolution.setText(w.getRes());
                textViewSizeDefault.setText(w.getSize() + " MB");
                tvSize.setText(w.getSize() + " MB");
                String imgUrl = w.getUrl();
                final String name = imgUrl.substring(imgUrl.lastIndexOf("%") + 1, imgUrl.indexOf("?"));
                tv_img_name.setText(name.substring(0, name.indexOf(".")));
                tvName.setText(tv_img_name.getText().toString());
                type = name.substring(name.lastIndexOf(".") + 1);
                tvType.setText(type.toUpperCase());
                title.setText(w.getTitle());

                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {

                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        if (slideOffset > 0.11)
                            relativeLayoutDefault.setVisibility(View.GONE);
                        else relativeLayoutDefault.setVisibility(View.VISIBLE);
                    }
                });

                // resolution
                findViewById(R.id.linear_layout_resolution).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ImageActivity.this, "Dimensions " + w.getRes() + " pixels", Toast.LENGTH_SHORT).show();
                    }
                });

                // name
                findViewById(R.id.linear_layout_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ImageActivity.this, tvName.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                // Size
                findViewById(R.id.linear_layout_size).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ImageActivity.this, "Size " + w.getSize() + " MB", Toast.LENGTH_SHORT).show();
                    }
                });

                // Format
                findViewById(R.id.linear_layout_type).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ImageActivity.this, "Format " + type.toUpperCase(), Toast.LENGTH_SHORT).show();
                    }
                });
                imageViewSetWallpaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setWallpaper();
                    }
                });
                imageViewSetWallpaperDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setWallpaper();
                    }
                });
                shareDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareWallpaper();
                    }
                });
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareWallpaper();
                    }
                });

                imageViewDownloadDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkPermission();
                    }
                });

                imageViewDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkPermission();
                    }
                });

            }
        }
    }

    /**
     * Download image
     *
     * @return bitmap
     */
    private Bitmap downloadImage() {
        return ((BitmapDrawable) mainImage.getDrawable()).getBitmap();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);

        } else
            saveImage(waterMarkEffect(downloadImage()));

    }

    private void copyTheColorCode(String code) {

    }

    private void saveImage(Bitmap bitmap) {
        if (bitmap == null)
            return;

        try {
            File filePath = Environment.getExternalStorageDirectory();
            File dir = new File(filePath.getAbsolutePath() + "/WALDO/");
            dir.mkdir();
            String name = tv_img_name.getText().toString();
            File file = new File(dir, name + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//
//        try {
//            File fileName = new File(Environment.getExternalStorageDirectory() + "/WALDO/" + name + ".JPEG");
//            FileOutputStream out = new FileOutputStream(fileName);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//
//            out.flush();
//            out.close();
//
//            ContentValues image = new ContentValues();
//            image.put("title", w.getTitle());
//            image.put("_display_name", name);
//            image.put("description", "App Image");
//            image.put("date_added", System.currentTimeMillis());
//            image.put("type", "App Image");
//            image.put("description", "image/jpeg");
//            image.put("orientation", 0);
//
//            File parent = fileName.getParentFile();
//            if (parent == null)
//                return;
//            image.put("bucket_id", parent.toString().toLowerCase().hashCode());
//            image.put("bucket_display_name", parent.getName().toLowerCase());
//            image.put("_size", fileName.length());
//            image.put("_data", fileName.getAbsolutePath());
//            Uri result = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
//            Toast.makeText(this, "Storage location " + fileName, Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            //StyleableToast.makeText(getApplicationContext(),e.toString(),R.style.example_toast).show();
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * It open the system wallpaper intent
     */
    private void setWallpaper() {
        try {
            Bitmap bitmap = downloadImage();
            if (bitmap == null)
                return;
            Uri uri = getLocalBitmapUri(bitmap);
            if (uri == null)
                return;
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            startActivity(intent);
        } catch (Exception e) {
            Log.d("TAG", "setHomeScreenWallPaper: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void shareWallpaper() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            Bitmap bitmap = waterMarkEffect(downloadImage());
            if (bitmap == null)
                return;
            Uri uri = getLocalBitmapUri(bitmap);
            if (uri == null)
                return;
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("text/plan");
            String st = "Avijit Samanta" + "\n";
            intent.putExtra(Intent.EXTRA_TEXT, st);
            startActivity(Intent.createChooser(intent, "Waldo Wallpaper"));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * It convert Bitmap to uri
     *
     * @param bitmap bitmap
     * @return uri
     */
    private Uri getLocalBitmapUri(Bitmap bitmap) {
        Uri uri = null;
        try {
            File file = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)),
                    System.currentTimeMillis() + ".jpeg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            uri = FileProvider.getUriForFile(ImageActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    /**
     * It is Water mark effect of an image
     *
     * @param src bitmap
     * @return bitmap
     */
    private Bitmap waterMarkEffect(Bitmap src) {
        if (src == null)
            return null;
        int w = src.getWidth();
        int h = src.getHeight();
        int x = w / 2;
        int y = h / 2;
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(90);
        paint.setAntiAlias(true);
        paint.setUnderlineText(true);
        canvas.drawText("Waldo", 50, h - 200, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        canvas.drawText("\u00a9Waldo", 50, 100, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        canvas.drawText("\u00a9WALDO", x, y, paint);

        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int res : grantResults) {
                    // permission denied
                    if (res == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // permission Granted

                Toast.makeText(this, "Permission granted....", Toast.LENGTH_SHORT).show();
                saveImage(waterMarkEffect(downloadImage()));
            } else {
                Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * download the original image
     */
//    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
//
//        @Override
//        protected Bitmap doInBackground(String... url) {
//            String urlDisplay = url[0];
//            Bitmap bitmap = null;
//            try {
//                InputStream str = new URL(urlDisplay).openStream();
//                bitmap = BitmapFactory.decodeStream(str);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(ImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                // StyleableToast.makeText(getApplicationContext(),e.toString(),R.style.example_toast).show();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            super.onPostExecute(bitmap);
//            //saveImage(waterMarkEffect(bitmap));
//        }
//    }

}