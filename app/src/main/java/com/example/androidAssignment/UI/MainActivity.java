package com.example.androidAssignment.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidAssignment.Model.ListItem;
import com.example.androidAssignment.R;
import com.example.androidAssignment.ViewModels.MainActivityViewModel;

import java.util.List;

import static com.example.androidAssignment.UI.PhotoActivity.PHOTO_ITEM_FIELD;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MainActivityViewModel mainActivityViewModel;
    private ListAdapter listAdapter;

    private static final int CAMERA_PERMISSION = 909;
    private static final int CAMERA_REQUEST = 908;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView listItemView = findViewById(R.id.list_item_view);
        mainActivityViewModel = new ViewModelProvider(this).get(
                MainActivityViewModel.class);
        mainActivityViewModel.getListLiveData().observe(this,
                listItems -> {
                    Log.d(TAG, String.valueOf(listItems));
                    listAdapter.setList(listItems);
                });
        mainActivityViewModel.getShowPhotoActivity().observe(this,
                url -> {
                    if (url != null) {
                        handlePhotoIntent(url);
                    }
                });
        mainActivityViewModel.getShouldShowCameraActivity().observe(this,
                value -> {
                    if (value != null && value) {
                        handleCameraIntent();
                    }
                });
        mainActivityViewModel.getShouldReloadItemAtIndex().observe(
                this, integer -> {
                    if (integer != null) {
                        listAdapter.reloadPosition(integer);
                    }
                });
        listAdapter = new ListAdapter(mainActivityViewModel);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);
        listItemView.setLayoutManager(layoutManager);
        listItemView.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit_button) {
            mainActivityViewModel.handleSubmitButtonCallback();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handlePhotoIntent(String url) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(PHOTO_ITEM_FIELD, url);
        startActivity(intent);
    }

    private void handleCameraIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION);
        } else {
            moveToCameraActivity(mainActivityViewModel.getPhotoUri());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                moveToCameraActivity(mainActivityViewModel.getPhotoUri());
            } else {
                Toast.makeText(this, "camera permission denied",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            mainActivityViewModel.handlePhotoSelectionCallback();
        }
    }

    private void moveToCameraActivity(Uri photoUri) {
        Intent takePictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }

}
