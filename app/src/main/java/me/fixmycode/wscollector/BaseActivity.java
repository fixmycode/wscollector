package me.fixmycode.wscollector;

import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import me.fixmycode.wscollector.fragments.CardFragment;
import me.fixmycode.wscollector.wsdb.Card;


public abstract class BaseActivity extends ActionBarActivity implements CardFragment.CardListener {

    private Toolbar toolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getToolbar();
    }

    public Toolbar getToolbar(){
        if(toolbar == null){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if(toolbar != null){
                setSupportActionBar(toolbar);
            }
        }
        return toolbar;
    }


    @Override
    public void OnCardDisplayed(Card card) {
        int color = R.color.primary;
        int dark_color = R.color.primary_dark;
        switch (card.getColor().toLowerCase()){
            case "red":
                color = R.color.red;
                dark_color = R.color.dark_red;
                break;
            case "blue":
                color = R.color.blue;
                dark_color = R.color.dark_blue;
                break;
            case "green":
                color = R.color.green;
                dark_color = R.color.dark_green;
                break;
            case "yellow":
                color = R.color.yellow;
                dark_color = R.color.dark_yellow;
        }

        getToolbar().setBackgroundColor(getResources().getColor(color));

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(getResources().getColor(dark_color));
        }

    }

    @Override
    public void OnCardClosed() {
        getToolbar().setBackgroundColor(getResources().getColor(R.color.primary));

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }
    }
}
