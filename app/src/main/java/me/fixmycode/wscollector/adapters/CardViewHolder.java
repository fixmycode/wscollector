package me.fixmycode.wscollector.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.fixmycode.wscollector.R;
import me.fixmycode.wscollector.wsdb.Card;
import me.fixmycode.wscollector.wsdb.Item;

public class CardViewHolder extends ItemViewHolder {

    public static final String TAG = "VH_CARD";
    private ImageView image;
    private TextView title;
    private TextView description;
    private Card card;

    public CardViewHolder(Context context, ItemAdapter adapter, View view) {
        super(context, adapter, view);
        this.image = (ImageView) view.findViewById(R.id.image);
        this.title = (TextView) view.findViewById(R.id.title);
        this.description = (TextView) view.findViewById(R.id.description);
        view.setOnClickListener(this);
    }

    @Override
    public void bindItem(Item item) {
        this.card = (Card) item;
        this.title.setText(card.getTitle());
        this.description.setText(card.getCode());
        setImage(card.getType(), card.getColor());
    }

    private void setImage(String type, String color) {
        int typeNum, colorNum;
        int[][] drawables = new int[][]{
            {R.drawable.red_chara, R.drawable.blue_chara,
                    R.drawable.green_chara, R.drawable.yellow_chara},
            {R.drawable.red_event, R.drawable.blue_event,
                    R.drawable.green_event, R.drawable.yellow_event},
            {R.drawable.red_climax, R.drawable.blue_climax,
                    R.drawable.green_climax, R.drawable.yellow_climax}
        };
        switch (type.toLowerCase()) {
            case "character":
                typeNum = 0; break;
            case "event":
                typeNum = 1; break;
            case "climax":
                typeNum = 2; break;
            default:
                typeNum = -1;
        }
        switch (color.toLowerCase()) {
            case "red":
                colorNum = 0; break;
            case "blue":
                colorNum = 1; break;
            case "green":
                colorNum = 2; break;
            case "yellow":
                colorNum = 3; break;
            default:
                colorNum = -1; break;
        }
        try {
            int drawableId = drawables[typeNum][colorNum];
            this.image.setImageResource(drawableId);
        } catch (IndexOutOfBoundsException e) {
            this.image.setImageDrawable(null);
        }
    }

    @Override
    public void onClick(View v) {
        getAdapter().onItemClick(this.card);
    }
}
