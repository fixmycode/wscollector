package me.fixmycode.wscollector.wsdb;

import android.database.Cursor;

import java.io.Serializable;


public class Serie implements Item, Serializable {
    private long id;
    private String title;
    private String side;
    private Long parent;
    private int children;

    public static final String SIDE_WEISS = "Weiss";
    public static final String SIDE_SCHWARZ = "Schwarz";

    private Serie(long id, String title, String side, Long parent, int children) {
        this.id = id;
        this.title = title;
        this.side = side;
        this.children = children;
        this.parent = parent;
    }

    public static Serie fromCursor(Cursor c)  {
        Long parent = null;
        if(!c.isNull(3))
            parent = c.getLong(3);
        return new Serie(c.getInt(0), c.getString(1), c.getString(2), parent, c.getInt(4));
    }

    public long getId() {
        return id;
    }

    @Override
    public int getItemType() {
        return Item.TYPE_SERIE;
    }

    public String getTitle() {
        return title;
    }

    public String getSide() {
        return side;
    }

    public Long getParent() {
        return parent;
    }

    public int getChildren() {
        return children;
    }
}
