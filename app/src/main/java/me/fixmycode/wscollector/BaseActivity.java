package me.fixmycode.wscollector;

import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import me.fixmycode.wscollector.fragments.CardFragment;
import me.fixmycode.wscollector.fragments.ItemListFragment;
import me.fixmycode.wscollector.wsdb.Card;
import me.fixmycode.wscollector.wsdb.DataBrowser;
import me.fixmycode.wscollector.wsdb.Item;

public abstract class BaseActivity extends ActionBarActivity implements CardFragment.CardListener,
        FragmentManager.OnBackStackChangedListener {
    private Toolbar toolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getToolbar();
    }

    protected Toolbar getToolbar(){
        if(toolbar == null){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if(toolbar != null){
                setSupportActionBar(toolbar);
            }
        }
        return toolbar;
    }

    @Override
    public void onBackStackChanged() {
        shouldGoBack();
    }

    protected Boolean shouldGoBack() {
        Boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
        return canGoBack;
    }

    @Override
    public void OnCardDisplayed(Card card) {
        Pair<Integer, Integer> colors = getCardColors(card);
        getToolbar().setBackgroundColor(getResources().getColor(colors.first));
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(getResources().getColor(colors.second));
        }
    }

    @Override
    public void OnCardClosed() {
        getToolbar().setBackgroundColor(getResources().getColor(R.color.primary));

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }
    }

    private Pair<Integer, Integer> getCardColors(Card card) {
        switch (card.getColor().toLowerCase()){
            case "red":
                return new Pair<>(R.color.red, R.color.dark_red);
            case "blue":
                return new Pair<>(R.color.blue, R.color.dark_blue);
            case "green":
                return new Pair<>(R.color.green, R.color.dark_green);
            case "yellow":
                return new Pair<>(R.color.yellow, R.color.dark_yellow);
            default:
                return new Pair<>(R.color.primary, R.color.primary_dark);
        }
    }

    protected void loadCard(Card card) {
        DataBrowser.getInstance(this).getCardAsync(card.getId(), new DataBrowser.Callback<Card>() {
            @Override
            public void onGet(Card entity) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .add(R.id.container, CardFragment.newInstance(entity), CardFragment.TAG)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    protected <T extends Item> ItemListFragment<T> prepareFragment(ArrayList<T> itemList, Long parentId, Boolean backStack, Boolean animation) {
        ItemListFragment<T> fragment = ItemListFragment.newInstance(itemList, parentId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(animation) transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.container, fragment);
        if(backStack) transaction.addToBackStack(null);
        transaction.commit();
        return fragment;
    }

    protected <T extends Item> ItemListFragment<T> prepareFragment(ArrayList<T> itemList, Long parentId) {
        return prepareFragment(itemList, parentId, true, true);
    }
}
