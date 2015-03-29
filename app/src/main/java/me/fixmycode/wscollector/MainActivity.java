package me.fixmycode.wscollector;

import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            Intent libraryIntent = new Intent(this, LibraryActivity.class);
            startActivity(libraryIntent);
            finish();
        }
    }
}
