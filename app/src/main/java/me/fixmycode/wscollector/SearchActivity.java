package me.fixmycode.wscollector;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import me.fixmycode.wscollector.adapters.ItemAdapter;
import me.fixmycode.wscollector.fragments.CardFragment;
import me.fixmycode.wscollector.fragments.ItemListFragment;
import me.fixmycode.wscollector.wsdb.Card;
import me.fixmycode.wscollector.wsdb.DataBrowser;
import me.fixmycode.wscollector.wsdb.Item;

public class SearchActivity extends BaseActivity
        implements SearchView.OnQueryTextListener, CardFragment.CardListener,
        ItemAdapter.AdapterListener {
    public static final String TAG = "ACT_SEARCH";
    private static final String PARAM_QUERY = "query";

    private SearchView searchView;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            Log.d(TAG, "New Search Activity created");
            ItemListFragment fragment = prepareFragment(null, null, false, false);
            fragment.setCurtainMessage(getString(R.string.search_code_or_title));
            if (getIntent() != null) {
                handleIntent(getIntent());
            }
        } else {
            this.query = savedInstanceState.getString(PARAM_QUERY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        if(searchView.requestFocus()){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        }
        return true;
    }

    private void handleIntent(Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    private void showResults(final String query) {
        if(this.query == null || !this.query.equals(query)) {
            if(getSupportFragmentManager().findFragmentByTag(CardFragment.TAG) != null){
                onSupportNavigateUp();
            }
            this.query = query;
            DataBrowser.getInstance(this).getSearchListAsync(query, new DataBrowser.ListCallback<Card>() {
                @Override
                public void onGet(ArrayList<Card> list) {
                    ItemListFragment fragment = prepareFragment(list, null, false, false);
                    if (list.size() == 0)
                        fragment.setCurtainMessage(getString(R.string.no_results));
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(this.query != null){
            outState.putString(PARAM_QUERY, this.query);
        }
        super.onSaveInstanceState(outState);
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
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        if (!shouldGoBack()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            finish();
            return false;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public void OnCardDisplayed(Card card) {
        super.OnCardDisplayed(card);
        if(searchView != null){
            searchView.clearFocus();
        }
    }

    @Override
    public void onItemClick(Item item) {
        loadCard((Card) item);
    }
}
