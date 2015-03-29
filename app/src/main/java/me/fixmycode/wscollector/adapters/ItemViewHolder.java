package me.fixmycode.wscollector.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.fixmycode.wscollector.wsdb.Item;


public abstract class ItemViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
    private Context context;
    private ItemAdapter adapter;

    public ItemViewHolder(Context context, ItemAdapter adapter, View itemView) {
        super(itemView);
        this.context = context;
        this.adapter = adapter;
    }

    public Context getContext() {
        return context;
    }

    public ItemAdapter getAdapter() {
        return adapter;
    }

    public abstract void bindItem(Item item);

}
