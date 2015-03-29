package me.fixmycode.wscollector;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;


public abstract class BaseActivity extends ActionBarActivity {

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
}
