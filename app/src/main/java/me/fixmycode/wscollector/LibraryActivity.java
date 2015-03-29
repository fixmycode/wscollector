package me.fixmycode.wscollector;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import me.fixmycode.wscollector.fragments.LibraryFragment;
import me.fixmycode.wscollector.wsdb.Card;


public class LibraryActivity extends BaseActivity
        implements FragmentManager.OnBackStackChangedListener, LibraryFragment.LibraryListener {
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

    private void shouldGoBack(){
        Boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        OnCardClosed();
        shouldGoBack();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OnCardClosed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_search:
                openSearch(); return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void openSearch(){
        startActivity(new Intent(this, SearchActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    @Override
    public void OnCardDisplayed(Card card) {
        int color = R.color.primary;
        int dark_color = R.color.primary_dark;
        switch (card.getColor().toLowerCase()){
            case "red":
                color = R.color.red;
                dark_color = R.color.dark_red;
                break;
            case "blue":
                color = R.color.blue;
                dark_color = R.color.dark_blue;
                break;
            case "green":
                color = R.color.green;
                dark_color = R.color.dark_green;
                break;
            case "yellow":
                color = R.color.yellow;
                dark_color = R.color.dark_yellow;
        }

        int title = R.string.cards;
        switch (card.getType().toLowerCase()){
            case "character":
                title = R.string.type_chara;
                break;
            case "event":
                title = R.string.type_event;
                break;
            case "climax":
                title = R.string.type_climax;
                break;
        }

        getToolbar().setBackgroundColor(getResources().getColor(color));
        getToolbar().setTitle(title);

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(getResources().getColor(dark_color));
        }

    }

    @Override
    public void OnCardClosed() {
        getToolbar().setBackgroundColor(getResources().getColor(R.color.primary));
        getToolbar().setTitle(R.string.cards);

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }
    }
}
