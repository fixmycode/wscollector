package me.fixmycode.wscollector.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

import me.fixmycode.wscollector.R;
import me.fixmycode.wscollector.wsdb.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    public static final String TAG = "ITEMADAPTER";

    private ArrayList<Item> data;
    private AdapterListener listener;
    private Long parentId;
    private TextDrawable.IBuilder drawableBuilder;

    public ItemAdapter(ArrayList<Item> data){
        this(data, null);
    }

    public ItemAdapter(ArrayList<Item> data, Long parentId){
        this.data = data;
        this.parentId = parentId;
    }

    public void changeDataSet(ArrayList<Item> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getItemType();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder itemViewHolder;
        switch (viewType){
            case Item.TYPE_RELEASE:
                itemViewHolder = new ReleaseViewHolder(
                        parent.getContext(), this, inflateHolder(parent));
                break;
            case Item.TYPE_CARD:
                itemViewHolder = new CardViewHolder(
                        parent.getContext(), this, inflateHolder(parent));
                break;
            default:
            case Item.TYPE_SERIE:
                itemViewHolder = new SerieViewHolder(
                        parent.getContext(), this, inflateHolder(parent));
                break;
        }
        return itemViewHolder;
    }

    private View inflateHolder(ViewGroup parent){
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bindItem(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    void onItemClick(Item item){
        if(listener != null)
            listener.onItemClick(item);
    }

    public Long getParentId() {
        return parentId;
    }

    public void addListener(AdapterListener listener){
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    public TextDrawable.IBuilder getDrawableBuilder(){
        if(drawableBuilder == null){
            drawableBuilder = TextDrawable.builder()
                    .beginConfig()
                    .fontSize(50)
                    .endConfig()
                    .round();
        }
        return drawableBuilder;
    }

    public interface AdapterListener {
        public void onItemClick(Item item);
    }
}
