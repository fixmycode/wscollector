package me.fixmycode.wscollector.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.fixmycode.wscollector.R;
import me.fixmycode.wscollector.adapters.ItemAdapter;
import me.fixmycode.wscollector.wsdb.Card;
import me.fixmycode.wscollector.wsdb.DataBrowser;
import me.fixmycode.wscollector.wsdb.Item;
import me.fixmycode.wscollector.wsdb.Release;
import me.fixmycode.wscollector.wsdb.Serie;


public class LibraryFragment extends BaseFragment implements ItemAdapter.AdapterListener {

    public static final String PARAM_LEVEL = "level";
    public static final String PARAM_ITEM = "item";
    public static final String PARAM_TYPE = "type";
    public static final String TOOLBAR_TITLE = "TOOLBAR_TITLE";
    public static final String ITEM_LIST = "ITEM_LIST";
    public static final String CARD_SHOWN = "CARD_SHOWN";
    public static final String TAG = "FRAG_LIBRARY";
    public static final int LEVEL_SERIES = 0;
    public static final int LEVEL_CHILDREN = 1;
    public static final int LEVEL_RELEASES = 2;
    public static final int LEVEL_CARDS = 3;

    private Long browsingItem;
    private ArrayList itemList;
    private int toolbarTitle;
    private LibraryListener listener;
    private Card cardShown;

    public static LibraryFragment newInstance(int level, Long itemId, Long itemType){
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        if(level > LEVEL_SERIES && itemId == null){
            throw new IllegalArgumentException("item must be specified");
        }
        args.putInt(PARAM_LEVEL, level);
        args.putLong(PARAM_ITEM, itemId);
        if(itemType != null)
            args.putLong(PARAM_TYPE, itemType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        getRecyclerView().setHasFixedSize(true);
        if(savedInstanceState == null) {
            showRecyclerView(false);
        } else {
            itemList = (ArrayList) savedInstanceState.getSerializable(ITEM_LIST);
            toolbarTitle = savedInstanceState.getInt(TOOLBAR_TITLE);
            cardShown = (Card) savedInstanceState.getSerializable(CARD_SHOWN);
            if(cardShown != null && this.listener != null){
                this.listener.OnCardDisplayed(cardShown);
            }
            setupRecycler(itemList);
            showRecyclerView(true);
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (LibraryListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(this.cardShown != null && this.listener != null){
            this.listener.OnCardClosed();
            this.cardShown = null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getRecyclerView().setLayoutManager(layoutManager);

        if(getArguments() != null){
            Bundle args = getArguments();
            int browsingLevel = args.getInt(PARAM_LEVEL);
            browsingItem = args.getLong(PARAM_ITEM);
            Long browsingType = args.getLong(PARAM_TYPE, -1);
            loadList(browsingLevel, browsingType, browsingItem);
        } else {
            loadList(LEVEL_SERIES, null, browsingItem);
        }
    }

    private void loadList(int level, Long type, Long item){
        Log.d(TAG, "loading list...");
        DataBrowser dataBrowser = DataBrowser.getInstance(getActivity());
        Toolbar toolbar = getBaseActivity().getToolbar();

        switch (level) {
            case LEVEL_SERIES:
                this.toolbarTitle = R.string.library;
                dataBrowser.getSeriesAsync(new DataBrowser.ListCallback<Serie>() {
                    @Override
                    public void onGet(ArrayList<Serie> list) {
                        itemList = list;
                        setupRecycler(list);
                    }
                });
                break;
            case LEVEL_CHILDREN:
                this.toolbarTitle = R.string.collections;
                dataBrowser.getChildrenAsync(item, new DataBrowser.ListCallback<Serie>() {
                    @Override
                    public void onGet(ArrayList<Serie> list) {
                        itemList = list;
                        setupRecycler(list);
                    }
                });
                break;
            case LEVEL_RELEASES:
                this.toolbarTitle = R.string.releases;
                dataBrowser.getReleasesAsync(item, new DataBrowser.ListCallback<Release>() {
                    @Override
                    public void onGet(ArrayList<Release> list) {
                        itemList = list;
                        setupRecycler(list, browsingItem);
                    }
                });
                break;
            case LEVEL_CARDS:
                this.toolbarTitle = R.string.cards;
                dataBrowser.getCardListAsync(item, type, new DataBrowser.ListCallback<Card>() {
                    @Override
                    public void onGet(ArrayList<Card> list) {
                        itemList = list;
                        setupRecycler(list, browsingItem);
                    }
                });
        }
        toolbar.setTitle(this.toolbarTitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ITEM_LIST, this.itemList);
        outState.putInt(TOOLBAR_TITLE, this.toolbarTitle);
        if(this.cardShown != null) {
            outState.putSerializable(CARD_SHOWN, this.cardShown);
        }
    }

    @SuppressWarnings("unchecked")
    private void setupRecycler(ArrayList list, Long parentItem){
        Log.d(TAG, "list loaded!");
        if(list != null && list.size() > 0) {
            ItemAdapter adapter = new ItemAdapter(itemList, parentItem);
            adapter.addListener(this);
            getRecyclerView().setAdapter(adapter);
            showRecyclerView(true);
            getBaseActivity().getToolbar().setTitle(this.toolbarTitle);
        } else {
            showRecyclerView(false, R.string.empty_list);
        }
    }

    private void setupRecycler(ArrayList list){
        this.setupRecycler(list, null);
    }

    @Override
    public void onItemClick(Item item) {
        Log.d(TAG, String.format("Opening item with ID %d", item.getId()));
        switch (item.getItemType()){
            case Item.TYPE_SERIE:
                Serie serie = (Serie) item;
                if(serie.getChildren() > 0){
                    prepareFragment(LEVEL_CHILDREN, serie.getId(), null);
                } else prepareFragment(LEVEL_RELEASES, serie.getId(), null);
                break;
            case Item.TYPE_RELEASE:
                Release release = (Release) item;
                prepareFragment(LEVEL_CARDS, browsingItem, release.getId());
                break;
            case Item.TYPE_CARD:
                showCard((Card) item);
                break;
        }
    }

    private void prepareFragment(Integer level,  Long itemId, @Nullable Long type){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .replace(R.id.container, LibraryFragment.newInstance(level, itemId, type))
                .addToBackStack(null)
                .commit();
    }

    private void showCard(Card card) {
        DataBrowser.getInstance(getActivity()).getCardAsync(card.getId(), new DataBrowser.Callback<Card>() {
            @Override
            public void onGet(Card entity) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .add(R.id.container, CardFragment.newInstance(entity))
                        .addToBackStack(null)
                        .commit();
                cardShown = entity;
                if(listener != null){
                    listener.OnCardDisplayed(entity);
                }
            }
        });
    }

    public interface LibraryListener {
        public void OnCardDisplayed(Card card);
        public void OnCardClosed();
    }
}
