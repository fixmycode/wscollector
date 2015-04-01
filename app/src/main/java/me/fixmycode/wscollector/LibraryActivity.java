package me.fixmycode.wscollector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import me.fixmycode.wscollector.fragments.LibraryFragment;


public class LibraryActivity extends BaseActivity
        implements FragmentManager.OnBackStackChangedListener {
    public static final String TAG = "ACT_LIBRARY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {
            Log.d(TAG, "New Library Activity created");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LibraryFragment())
                    .commit();
        }
        shouldGoBack();
    }

    @Override
    public void onBackStackChanged() {
        shouldGoBack();
    }

    private void shouldGoBack() {
        Boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        shouldGoBack();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                openSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void openSearch() {
        startActivity(new Intent(this, SearchActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }
}
