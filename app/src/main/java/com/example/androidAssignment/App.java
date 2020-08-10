package com.example.androidAssignment;

import android.app.Application;

public class App extends Application {

    public Repository getRepository() {
        return Repository.getInstance(this);
    }
}
