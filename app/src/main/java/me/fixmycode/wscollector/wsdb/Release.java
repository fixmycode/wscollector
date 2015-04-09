package me.fixmycode.wscollector.wsdb;

import android.database.Cursor;
import android.os.Parcel;

import java.io.Serializable;


public class Release implements Item, Serializable {
    private long id;
    private String title;
    private Integer count;

    private Release(long id, String title) {
        this.id = id;
        this.title = title;
    }

    private Release(long id, String title, Integer count) {
        this(id, title);
        this.count = count;
    }

    public static Release fromCursor(Cursor c) {
        return new Release(c.getInt(0), c.getString(1));
    }

    public static Release fromParcel(Parcel p) {
        long id = p.readLong();
        String title = p.readString();
        Integer count = p.readInt();
        if(count == -1) count = null;
        return new Release(id, title, count);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        if(this.count != null)
            dest.writeInt(this.count);
        else dest.writeInt(-1);
    }

    public static final Creator<Release> CREATOR = new Creator<Release>() {
        @Override
        public Release createFromParcel(Parcel source) {
            return Release.fromParcel(source);
        }

        @Override
        public Release[] newArray(int size) {
            return new Release[size];
        }
    };

    @Override
    public String toString() {
        return String.format("%s (%s)", this.title, this.count != null? this.count.toString() : "unknown count");
    }
}
