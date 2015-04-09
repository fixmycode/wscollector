package me.fixmycode.wscollector.wsdb;


import android.os.Parcelable;

public interface Item extends Parcelable {
    public static final int TYPE_SERIE = 0;
    public static final int TYPE_RELEASE = 1;
    public static final int TYPE_CARD = 2;

    public long getId();
    public int getItemType();
}
