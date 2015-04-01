package me.fixmycode.wscollector;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;

import me.fixmycode.wscollector.fragments.CardFragment;
import me.fixmycode.wscollector.fragments.SearchFragment;


public class SearchActivity extends BaseActivity
        implements SearchView.OnQueryTextListener, CardFragment.CardListener,
        FragmentManager.OnBackStackChangedListener {
    public static final String TAG = "ACT_SEARCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, SearchFragment.newInstance(null), TAG)
                    .commit();
            if (getIntent() != null) {
                handleIntent(getIntent());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        if (Build.VERSION.SDK_INT > 10) {
            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        } else {
            searchView = (SearchView) MenuItemCompat
                    .getActionView(menu.findItem(R.id.action_search));
        }
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void handleIntent(Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    private void showResults(String query) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null) {
            ((SearchFragment) fragment).setQuery(query);
        } else {
            Log.e(TAG, "fragment not found");
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        showResults(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onBackStackChanged() {
        shouldGoBack();
    }

    private Boolean shouldGoBack() {
        Boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
        return canGoBack;
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        if (!shouldGoBack()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            finish();
            return false;
        }
        return super.onSupportNavigateUp();
    }
}
