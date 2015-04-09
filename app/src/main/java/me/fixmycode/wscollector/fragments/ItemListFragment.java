package me.fixmycode.wscollector.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.fixmycode.wscollector.R;
import me.fixmycode.wscollector.adapters.ItemAdapter;
import me.fixmycode.wscollector.wsdb.Item;

public class ItemListFragment<T extends Item> extends Fragment {
    public static final String TAG = "FRAG_ITEMLIST";
    private static final String PARAM_LIST = "list";
    private static final String PARAM_PARENT = "parent";
    private static final String PARAM_CURTAIN = "curtain";

    private RecyclerView recyclerView;
    private View curtain;
    private CharSequence curtainMessage;
    private ArrayList<T> itemList;
    private Long parentId;

    public static <T extends Item> ItemListFragment<T> newInstance(ArrayList<T> list,
                                                                   Long parentId){
        ItemListFragment<T> fragment = new ItemListFragment<>();
        Bundle args = new Bundle();
        if(list != null) args.putParcelableArrayList(PARAM_LIST, list);
        if(parentId != null) args.putLong(PARAM_PARENT, parentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            itemList = args.getParcelableArrayList(PARAM_LIST);
            parentId = args.getLong(PARAM_PARENT, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        curtain = layout.findViewById(R.id.curtain);
        if(savedInstanceState != null){
            this.setCurtainMessage(savedInstanceState.getCharSequence(PARAM_CURTAIN));
        }
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if(itemList != null && itemList.size() > 0){
            ItemAdapter<T> adapter = new ItemAdapter<>(itemList, parentId);
            adapter.addListener((ItemAdapter.AdapterListener) getActivity());
            recyclerView.setAdapter(adapter);
            showRecyclerView(true);
        } else {
            showRecyclerView(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(this.curtainMessage != null){
            outState.putCharSequence(PARAM_CURTAIN, this.curtainMessage);
        }
    }

    private void showRecyclerView(Boolean visible) {
        if (recyclerView != null && curtain != null) {
            if (visible) {
                recyclerView.setVisibility(View.VISIBLE);
                curtain.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                curtain.setVisibility(View.VISIBLE);
                TextView messageText = (TextView) curtain.findViewById(R.id.message_text);
                messageText.setText(this.curtainMessage);
            }
        }
    }

    public void setCurtainMessage(CharSequence message) {
        this.curtainMessage = message;
        if(this.curtain != null){
            TextView messageText = (TextView) curtain.findViewById(R.id.message_text);
            messageText.setText(message);
        }
    }
}
