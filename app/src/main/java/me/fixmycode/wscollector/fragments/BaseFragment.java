package me.fixmycode.wscollector.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.fixmycode.wscollector.BaseActivity;
import me.fixmycode.wscollector.R;


public abstract class BaseFragment extends Fragment {

    private RecyclerView recyclerView;
    private View curtain;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        curtain = layout.findViewById(R.id.courtain);
        return layout;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void showRecyclerView(Boolean visible, CharSequence message) {
        if (recyclerView != null && curtain != null) {
            if (visible) {
                recyclerView.setVisibility(View.VISIBLE);
                curtain.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                if (message != null) {
                    curtain.setVisibility(View.VISIBLE);
                    TextView messageText = (TextView) curtain.findViewById(R.id.message_text);
                    messageText.setText(message);
                }
            }
        }
    }

    public CharSequence getRecyclerMessage() {
        TextView messageText = (TextView) curtain.findViewById(R.id.message_text);
        return messageText.getText();
    }

    public void showRecyclerView(Boolean visible) {
        showRecyclerView(visible, null);
    }

    public void showRecyclerView(Boolean visible, @StringRes int resId) {
        CharSequence sequence = getResources().getString(resId);
        showRecyclerView(visible, sequence);
    }

    final public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
