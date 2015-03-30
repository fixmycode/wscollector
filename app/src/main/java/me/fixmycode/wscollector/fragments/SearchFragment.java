package me.fixmycode.wscollector.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
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

public class SearchFragment extends BaseFragment implements ItemAdapter.AdapterListener {
    public static final String TAG = "FRAG_SEARCH";
    public static final String PARAM_QUERY = "QUERY";
    public static final String RECYCLER_VISIBLE = "RECYCLER_VISIBLE";
    public static final String RECYCLER_MESSAGE = "RECYCLER_MESSAGE";
    public static final String CARD_LIST = "CARD_LIST";

    private ArrayList cardList;

    public static SearchFragment newInstance(@Nullable String query){
        SearchFragment fragment = new SearchFragment();
        if(query != null){
            Bundle args = new Bundle();
            args.putString(PARAM_QUERY, query);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        getRecyclerView().setHasFixedSize(true);
        if(savedInstanceState == null) {
            Log.d(TAG, "New Search Fragment created");
            showRecyclerView(false, R.string.search_code_or_title);
        } else {
            cardList = (ArrayList) savedInstanceState.getSerializable(CARD_LIST);
            setupRecycler(cardList);
            showRecyclerView(savedInstanceState.getBoolean(RECYCLER_VISIBLE),
                    savedInstanceState.getCharSequence(RECYCLER_MESSAGE));
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RECYCLER_VISIBLE, getRecyclerView().getVisibility() == View.VISIBLE);
        outState.putCharSequence(RECYCLER_MESSAGE, getRecyclerMessage());
        outState.putSerializable(CARD_LIST, cardList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getRecyclerView().setLayoutManager(layoutManager);

        if(getArguments() != null){
            setQuery(getArguments().getString(PARAM_QUERY));
        }
    }

    public void setQuery(String like){
        DataBrowser dataBrowser = DataBrowser.getInstance(getActivity());
        dataBrowser.getSearchListAsync(like, new DataBrowser.ListCallback<Card>() {
            @Override
            public void onGet(ArrayList<Card> list) {
                cardList = list;
                setupRecycler(cardList);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void setupRecycler(ArrayList list){
        if(list != null && list.size() > 0) {
            ItemAdapter adapter = new ItemAdapter(list, null);
            adapter.addListener(this);
            getRecyclerView().setAdapter(adapter);
            showRecyclerView(true);
        } else {
            showRecyclerView(false, R.string.no_results);
        }
    }

    @Override
    public void onItemClick(Item item) {
        showCard((Card) item);
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
            }
        });
    }
}
