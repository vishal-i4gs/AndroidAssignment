package com.example.androidAssignment.UI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidAssignment.R;

public class PhotoActivity extends AppCompatActivity {

    public static String PHOTO_ITEM_FIELD = "photoItemField";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImageView photoImageView = findViewById(R.id.photo_image_view);
        String imageUrl = getIntent().getStringExtra(PHOTO_ITEM_FIELD);
        if (imageUrl != null) {
            Bitmap selectedImage = BitmapFactory.decodeFile(
                    imageUrl);
            photoImageView.setImageBitmap(selectedImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
