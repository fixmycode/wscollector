package me.fixmycode.wscollector.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.io.IOException;
import java.io.InputStream;

import me.fixmycode.wscollector.R;
import me.fixmycode.wscollector.drawables.RoundImageDrawable;
import me.fixmycode.wscollector.wsdb.Item;
import me.fixmycode.wscollector.wsdb.Serie;


public class SerieViewHolder extends ItemViewHolder {

    public static final String TAG = "VH_SERIE";
    private ImageView image;
    private TextView title;
    private TextView description;
    private Serie serie;

    public SerieViewHolder(Context context, ItemAdapter adapter, View view){
        super(context, adapter, view);
        this.image = (ImageView) view.findViewById(R.id.image);
        this.title = (TextView) view.findViewById(R.id.title);
        this.description = (TextView) view.findViewById(R.id.description);
        view.setOnClickListener(this);
    }

    @Override
    public void bindItem(Item item){
        this.serie = (Serie) item;
        this.title.setText(serie.getTitle());
        int children = serie.getChildren();
        if(children > 0) {
            this.description.setText(getContext()
                    .getResources().getQuantityString(R.plurals.collections, children, children));
        } else {
            this.description.setText(R.string.collection);
        }
        getImageAsset(this.serie.getTitle(), this.image);
    }

    private void getImageAsset(final String title, final ImageView image) {
        image.setImageDrawable(null);
        new AsyncTask<Long, Void, Drawable>(){
            @Override
            protected Drawable doInBackground(Long... params) {
                String assetName = "series/"+String.valueOf(params[0])+".png";
                try {
                    InputStream imageStream = getContext().getAssets().open(assetName);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    return new RoundImageDrawable(bitmap);
                } catch (IOException e) {
                    ColorGenerator generator = ColorGenerator.MATERIAL;
                    int color = generator.getColor(title);
                    return getAdapter().getDrawableBuilder()
                            .build(title.substring(0,1).toUpperCase(), color);
                }
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                image.setImageDrawable(drawable);
            }
        }.execute(this.serie.getId());
    }

    @Override
    public void onClick(View v) {
        getAdapter().onItemClick(serie);
    }
}
