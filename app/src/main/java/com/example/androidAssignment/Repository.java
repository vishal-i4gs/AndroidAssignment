package com.example.androidAssignment;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidAssignment.Model.ListItem;
import com.example.androidAssignment.UI.MainActivity;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class Repository {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context;
    private static Repository shared;

    private MutableLiveData<List<ListItem>> listMutableLiveData =
            new MutableLiveData<>();

    private Repository(Context context) {
        this.context = context.getApplicationContext();
        String json = loadJSONFromAsset();
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, ListItem.class);
        JsonAdapter<List<ListItem>> adapter = moshi.adapter(type);
        try {
            List<ListItem> list;
            if (json != null) {
                list = adapter.fromJson(json);
                listMutableLiveData.postValue(list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Repository getInstance(final Context context) {
        if (shared == null) {
            synchronized (Repository.class) {
                if (shared == null) {
                    shared = new Repository(context);
                }
            }
        }
        return shared;
    }

    //Getters
    public LiveData<List<ListItem>> getList() {
        return listMutableLiveData;
    }

    //Helpers
    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = context.getAssets().open("list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
