package me.fixmycode.wscollector.wsdb;

import android.database.Cursor;

import java.io.Serializable;


public class Release implements Item, Serializable {
    private long id;
    private String title;
    private Integer count;

    private Release(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Release fromCursor(Cursor c) {
        return new Release(c.getInt(0), c.getString(1));
    }

    public long getId() {
        return id;
    }

    @Override
    public int getItemType() {
        return Item.TYPE_RELEASE;
    }

    public String getTitle() {
        return title;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
