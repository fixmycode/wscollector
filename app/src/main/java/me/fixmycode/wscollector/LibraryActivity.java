package me.fixmycode.wscollector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import me.fixmycode.wscollector.adapters.ItemAdapter;
import me.fixmycode.wscollector.wsdb.Card;
import me.fixmycode.wscollector.wsdb.DataBrowser;
import me.fixmycode.wscollector.wsdb.Item;
import me.fixmycode.wscollector.wsdb.Release;
import me.fixmycode.wscollector.wsdb.Serie;


public class LibraryActivity extends BaseActivity implements ItemAdapter.AdapterListener {
    public static final String TAG = "ACT_LIBRARY";
    private static final String PARAM_SERIE = "serie";
    private static final String PARAM_TITLE = "title";

    private Serie serie;
    private ArrayList<CharSequence> titleStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {
            Log.d(TAG, "New Library Activity created");
            loadSeries();
            this.titleStack = new ArrayList<>();
            this.titleStack.add(getString(R.string.library));
        } else {
            setSerie((Serie) savedInstanceState.getParcelable(PARAM_SERIE));
            this.titleStack = savedInstanceState.getCharSequenceArrayList(PARAM_TITLE);
        }
        setToolbarTitle();
        shouldGoBack();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        this.popTitleStack();
        shouldGoBack();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.popTitleStack();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(getSerie() != null){
            outState.putParcelable(PARAM_SERIE, getSerie());
        }
        if(this.titleStack != null){
            outState.putCharSequenceArrayList(PARAM_TITLE, this.titleStack);
        }
        super.onSaveInstanceState(outState);
    }

    protected void openSearch() {
        startActivity(new Intent(this, SearchActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    protected void loadSeries() {
        DataBrowser.getInstance(this).getSeriesAsync(new DataBrowser.ListCallback<Serie>() {
            @Override
            public void onGet(ArrayList<Serie> list) {
                prepareFragment(list, null, false, false);
            }
        });
    }

    protected void loadChildren(final Serie serie) {
        DataBrowser.getInstance(this).getChildrenAsync(serie.getId(), new DataBrowser.ListCallback<Serie>() {
            @Override
            public void onGet(ArrayList<Serie> list) {
                prepareFragment(list, serie.getId());
            }
        });
    }

    protected void loadReleases(final Serie serie) {
        setSerie(serie);
        DataBrowser.getInstance(this).getReleasesAsync(serie.getId(), new DataBrowser.ListCallback<Release>() {
            @Override
            public void onGet(ArrayList<Release> list) {
                prepareFragment(list, serie.getId());
            }
        });
    }

    protected void loadCards(final Release release) {
        final Serie serie = getSerie();
        DataBrowser.getInstance(this).getCardListAsync(serie.getId(), release.getId(), new DataBrowser.ListCallback<Card>() {
            @Override
            public void onGet(ArrayList<Card> list) {
                prepareFragment(list, serie.getId());
            }
        });
    }

    @Override
    public void onItemClick(Item item) {
        Log.d(TAG, String.format("Opening item: %s", item.toString()));
        switch (item.getItemType()) {
            case Item.TYPE_SERIE:
                Serie serie = (Serie) item;
                if (serie.getChildren() > 0) {
                    loadChildren(serie);
                    addToolbarTitle(R.string.collection);
                } else {
                    loadReleases(serie);
                    addToolbarTitle(R.string.releases);
                }
                break;
            case Item.TYPE_RELEASE:
                Release release = (Release) item;
                loadCards(release);
                addToolbarTitle(R.string.cards);
                break;
            case Item.TYPE_CARD:
                loadCard((Card) item);
                addToolbarTitle(getCardTypeRes((Card) item));
                break;
        }
        setToolbarTitle();
    }

    private void addToolbarTitle(int resId) {
        this.titleStack.add(0, getString(resId));
    }

    private void popTitleStack() {
        if(this.titleStack.size() > 1) {
            this.titleStack.remove(0);
            setToolbarTitle();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(this.titleStack.get(0));
    }

    private int getCardTypeRes(Card card) {
        switch (card.getType().toLowerCase()){
            case "character":
                return R.string.type_chara;
            case "event":
                return R.string.type_event;
            case "climax":
                return R.string.type_climax;
            default:
                return R.string.cards;
        }
    }
}
