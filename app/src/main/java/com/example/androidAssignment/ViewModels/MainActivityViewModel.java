package com.example.androidAssignment.ViewModels;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidAssignment.App;
import com.example.androidAssignment.Model.ListItem;
import com.example.androidAssignment.Model.Type;
import com.example.androidAssignment.Repository;
import com.example.androidAssignment.SingleLiveEvent;
import com.example.androidAssignment.UI.ViewHolder.PhotoAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    private Context context;

    private SingleLiveEvent<Boolean> shouldShowCameraActivity =
            new SingleLiveEvent<>();

    private SingleLiveEvent<String> showPhotoActivity =
            new SingleLiveEvent<>();

    private SingleLiveEvent<Integer> shouldReloadItemAtIndex =
            new SingleLiveEvent<>();

    private LiveData<List<ListItem>> listLiveData;

    private int currentItemIndex;
    private Uri photoUri;
    private String defaultPhotoPath;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        context = application;
        Repository mRepository = ((App) application).getRepository();
        listLiveData = mRepository.getList();
    }

    public LiveData<List<ListItem>> getListLiveData() {
        return listLiveData;
    }

    public LiveData<Boolean> getShouldShowCameraActivity() {
        return shouldShowCameraActivity;
    }

    public MutableLiveData<Integer> getShouldReloadItemAtIndex() {
        return shouldReloadItemAtIndex;
    }

    public LiveData<String> getShowPhotoActivity() {
        return showPhotoActivity;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void handlePhotoListItemAction(PhotoAction photoAction,
                                          int index) {
        if (listLiveData.getValue() == null) {
            return;
        }
        currentItemIndex = index;
        ListItem listItem = listLiveData.getValue().get(index);
        switch (photoAction) {
            case CLICK:
                if (listItem.dataMap.currentInput == null) {
                    handlePhotoSelectionEvent();
                } else {
                    showPhotoActivity.postValue(
                            listItem.dataMap.currentInput);
                }
                break;
            case REMOVE:
                listItem.dataMap.currentInput = null;
                shouldReloadItemAtIndex.postValue(index);
                break;
        }
    }

    public void handlePhotoSelectionCallback() {
        Bitmap selectedImage = BitmapFactory.decodeFile(
                defaultPhotoPath);
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(defaultPhotoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(selectedImage, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(selectedImage, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(selectedImage, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = selectedImage;
            }
            if (listLiveData.getValue() == null) {
                return;
            }
            ListItem listItem = listLiveData.getValue().get(currentItemIndex);
            listItem.dataMap.currentInput =
                    compressImageFile(compressImage(rotatedBitmap));
            shouldReloadItemAtIndex.postValue(currentItemIndex);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSubmitButtonCallback() {
        if (listLiveData.getValue() == null) {
            return;
        }
        ArrayList<HashMap<String, String>> logArray = new ArrayList<>();
        for (ListItem listItem : listLiveData.getValue()) {
            HashMap<String, String> listItemLog = new HashMap<>();
            if (listItem.type.equals(Type.SINGLE_CHOICE)) {
                listItemLog.put("id", listItem.id);
                if (listItem.dataMap.currentInput != null) {
                    listItemLog.put("currentInput",
                            listItem.dataMap.options.get(
                                    Integer.parseInt(
                                            listItem.dataMap.currentInput)));
                } else {
                    listItemLog.put("currentInput", null);
                }
            } else {
                listItemLog.put("id", listItem.id);
                listItemLog.put("currentInput", listItem.dataMap.currentInput);
            }
            logArray.add(listItemLog);
        }
        Log.d(TAG, String.valueOf(logArray));
    }

    //Helper functions
    private void handlePhotoSelectionEvent() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(context,
                    "com.example.androidAssignment.fileprovider",
                    photoFile);
            shouldShowCameraActivity.postValue(true);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        defaultPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String compressImageFile(Bitmap bitmap) throws IOException {
        FileOutputStream stream =
                new FileOutputStream(defaultPhotoPath);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        stream.close();
        return defaultPhotoPath;
    }

    private Bitmap compressImage(Bitmap bmp) {
        Bitmap scaledBitmap = null;
        int actualHeight = bmp.getHeight();
        int actualWidth = bmp.getWidth();
        float maxHeight = 800.f;
        float maxWidth = 800.f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
        try {
            scaledBitmap = Bitmap.createScaledBitmap(
                    bmp, actualWidth, actualHeight, true);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        return scaledBitmap;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0,
                source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
