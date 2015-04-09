package me.fixmycode.wscollector.wsdb;

import android.database.Cursor;
import android.os.Parcel;

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

    public static Serie fromParcel(Parcel p) {
        Long id = p.readLong();
        String title = p.readString();
        String side = p.readString();
        Long parent = p.readLong();
        if(parent == -1) parent = null;
        int children = p.readInt();
        return new Serie(id, title, side, parent, children);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.side);
        if(parent != null)
            dest.writeLong(this.parent);
        else dest.writeLong(-1);
        dest.writeInt(this.children);
    }

    public static final Creator<Serie> CREATOR = new Creator<Serie>() {
        @Override
        public Serie createFromParcel(Parcel source) {
            return Serie.fromParcel(source);
        }

        @Override
        public Serie[] newArray(int size) {
            return new Serie[size];
        }
    };

    @Override
    public String toString() {
        if(this.parent == null)
            return String.format("[%d] %s", this.id, this.title);
        else return String.format("[%d > %d] %s", this.parent, this.id, this.title);
    }
}
