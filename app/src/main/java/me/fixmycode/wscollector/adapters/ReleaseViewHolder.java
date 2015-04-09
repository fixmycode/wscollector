package me.fixmycode.wscollector.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import me.fixmycode.wscollector.R;
import me.fixmycode.wscollector.wsdb.DataBrowser;
import me.fixmycode.wscollector.wsdb.Item;
import me.fixmycode.wscollector.wsdb.Release;

public class ReleaseViewHolder extends ItemViewHolder {

    public static final String TAG = "VH_RELEASE";
    private ImageView image;
    private TextView title;
    private TextView description;
    private Release release;

    public ReleaseViewHolder(Context context, ItemAdapter adapter, View view) {
        super(context, adapter, view);
        this.image = (ImageView) view.findViewById(R.id.image);
        this.title = (TextView) view.findViewById(R.id.title);
        this.description = (TextView) view.findViewById(R.id.description);
        view.setOnClickListener(this);
    }

    @Override
    public void bindItem(Item item) {
        this.release = (Release) item;
        this.title.setText(release.getTitle());
        this.description.setText(R.string.loading_card_count);
        Long serieId = getAdapter().getParentId();
        if(release.getCount() == null){
            DataBrowser.getInstance(getContext()).getCardCountAsync(serieId, release.getId(), new DataBrowser.CountCallback() {
                @Override
                public void onGet(int count) {
                    release.setCount(count);
                    setDescriptionCount(release.getCount());
                }
            });
        } else {
            setDescriptionCount(release.getCount());
        }
        setImage(release.getTitle());
    }

    private void setDescriptionCount(int count){
        this.description.setText(
                getContext().getResources().getQuantityString(R.plurals.card_count, count, count));
    }

    private void setImage(String title){
        String imageText = "";
        int imageColor = 0;
        switch (title) {
            case "Booster Pack":
                imageText = "BP";
                imageColor = getContext().getResources().getColor(R.color.orange);
                break;
            case "Trial Deck":
                imageText = "TD";
                imageColor = getContext().getResources().getColor(R.color.light_blue);
                break;
            case "Extra Pack":
                imageText = "XP";
                imageColor = getContext().getResources().getColor(R.color.lime);
                break;
            case "Extra Trial":
                imageText = "XT";
                imageColor = getContext().getResources().getColor(R.color.purple);
                break;
            case "Promotional":
                imageText = "PR";
                imageColor = getContext().getResources().getColor(R.color.indigo);
                break;
        }
        TextDrawable drawable = getAdapter().getDrawableBuilder()
                .build(imageText, imageColor);
        image.setImageDrawable(drawable);
    }

    @Override
    public void onClick(View v) {
        getAdapter().onItemClick(release);
    }
}
