package me.fixmycode.wscollector.wsdb;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;


public class DataBrowser extends SQLiteAssetHelper {

    public static final String DB_NAME = "ws.db";
    public static final int DB_VERSION = 1;
    private static DataBrowser instance;

    private DataBrowser(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        setForcedUpgrade();
    }

    public static DataBrowser getInstance(Context context){
        if(instance == null){
            instance = new DataBrowser(context.getApplicationContext());
        }
        return instance;
    }

    /* SYNCED DATA ACCESS */

    public ArrayList<Serie> getSeries(){
        ArrayList<Serie> series = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from serie where parent is null order by title", null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            series.add(Serie.fromCursor(c));
            c.moveToNext();
        }
        c.close();
        return series;
    }

    public ArrayList<Serie> getChildren(long serieId){
        ArrayList<Serie> series = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from serie where parent = ? order by title",
                new String[]{String.valueOf(serieId)});
        c.moveToFirst();

        while(!c.isAfterLast()){
            series.add(Serie.fromCursor(c));
            c.moveToNext();
        }
        c.close();
        return series;
    }

    public ArrayList<Release> getReleases(long serieId){
        ArrayList<Release> releases = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select distinct release_type from Card where serie_id = ?;",
                new String[]{String.valueOf(serieId)});
        Cursor d;
        c.moveToFirst();
        while(!c.isAfterLast()){
            d = db.rawQuery("select * from Release where id = ? limit 1",
                    new String[]{c.getString(0)});
            d.moveToFirst();
            releases.add(Release.fromCursor(d));
            d.close();
            c.moveToNext();
        }
        c.close();
        return releases;
    }

    public ArrayList<Release> getReleases(Serie serie){
        return getReleases(serie.getId());
    }

    public ArrayList<Card> getCardList(long serieId, long relId){
        ArrayList<Card> cardList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select id, title, title_jp, code, color, type " +
                "from Card where serie_id = ? and release_type = ?",
                new String[]{String.valueOf(serieId), String.valueOf(relId)});
        c.moveToFirst();
        while (!c.isAfterLast()){
            cardList.add(Card.fromCursor(c, true));
            c.moveToNext();
        }
        c.close();
        return cardList;
    }

    public int getCardCount(long serieId, long relId){
        int cardCount;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select count(*) " +
                        "from Card where serie_id = ? and release_type = ?",
                new String[]{String.valueOf(serieId), String.valueOf(relId)});
        c.moveToFirst();
        cardCount = c.getInt(0);
        c.close();
        return cardCount;
    }

    public ArrayList<Card> getCardsFromList(long[] cardIds){
        ArrayList<Card> cardList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder builder = new StringBuilder("select id, title, title_jp, code, color, type " +
                "from Card where id in (");
        for(int i = 0; i < cardIds.length; i++){
            builder.append(cardIds[i]);
            if(i < cardIds.length-1) builder.append(", ");
        }
        builder.append(")");
        Cursor c = db.rawQuery(builder.toString(), null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            cardList.add(Card.fromCursor(c, true));
            c.moveToNext();
        }
        c.close();
        return cardList;
    }

    public Card getCard(long id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from Card where id = ?", new String[]{String.valueOf(id)});
        c.moveToFirst();
        if(c.isAfterLast()) return null;
        return Card.fromCursor(c, false);
    }

    public ArrayList<Card> getSearchList(String like){
        ArrayList<Card> cardList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        like = like.replaceAll("%", "\\\\%");
        like = like.replaceAll("_", "\\\\_");
        like = like.replaceAll("\\*", "%");
        if(!like.matches("\".*\"")){
            like = "%" + like + "%";
        } else {
            like = like.replaceAll("\"(.*)\"", "$1");
        }
        Log.d("DATA", "like string: "+like);
        Cursor c = db.rawQuery("select id, title, title_jp, code, color, type " +
                "from Card where title like ? or code like ? limit 100", new String[]{like, like});
        c.moveToFirst();
        while(!c.isAfterLast()){
            cardList.add(Card.fromCursor(c, true));
            c.moveToNext();
        }
        c.close();
        return cardList;
    }

    /* ASYNC VERSIONS */

    public void getSeriesAsync(final ListCallback<Serie> callback){
        new AsyncTask<Void, Void, ArrayList<Serie>>(){
            @Override
            protected ArrayList<Serie> doInBackground(Void... params) {
                return getSeries();
            }

            @Override
            protected void onPostExecute(ArrayList<Serie> series) {
                if(callback != null){
                    callback.onGet(series);
                }
            }
        }.execute();
    }

    public void getChildrenAsync(long serieId, final ListCallback<Serie> callback){
        new AsyncTask<Long, Void, ArrayList<Serie>>(){
            @Override
            protected ArrayList<Serie> doInBackground(Long... params) {
                return getChildren(params[0]);
            }

            @Override
            protected void onPostExecute(ArrayList<Serie> series) {
                if(callback != null){
                    callback.onGet(series);
                }
            }
        }.execute(serieId);
    }

    public void getReleasesAsync(long serieId, final ListCallback<Release> callback){
        new AsyncTask<Long, Void, ArrayList<Release>>(){
            @Override
            protected ArrayList<Release> doInBackground(Long... params) {
                return getReleases(params[0]);
            }

            @Override
            protected void onPostExecute(ArrayList<Release> releases) {
                if(callback != null){
                    callback.onGet(releases);
                }
            }
        }.execute(serieId);
    }

    public void getReleasesAsync(Serie serie, final ListCallback<Release> callback){
        getReleasesAsync(serie.getId(), callback);
    }

    public void getCardListAsync(long serieId, long relId, final ListCallback<Card> callback){
        Pair<Long, Long> data = new Pair<>(serieId, relId);

        new AsyncTask<Pair, Void, ArrayList<Card>>(){
            @Override
            protected ArrayList<Card> doInBackground(Pair... params) {
                return getCardList((Long) params[0].first, (Long) params[0].second);
            }

            @Override
            protected void onPostExecute(ArrayList<Card> cards) {
                if(callback != null){
                    callback.onGet(cards);
                }
            }
        }.execute(data);
    }

    public void getCardFromListAsync(long[] cardIds, final ListCallback<Card> callback){
        Long[] ids = new Long[cardIds.length];
        int i = 0;
        for(long id : cardIds){
            ids[i++] = id;
        }

        new AsyncTask<Long, Void, ArrayList<Card>>(){
            @Override
            protected ArrayList<Card> doInBackground(Long... params) {
                long[] ids = new long[params.length];
                int i = 0;
                for(Long id : params){
                    ids[i++] = id;
                }

                return getCardsFromList(ids);
            }

            @Override
            protected void onPostExecute(ArrayList<Card> cards) {
                if(callback != null){
                    callback.onGet(cards);
                }
            }
        }.execute(ids);
    }

    public void getCardAsync(long id, final Callback<Card> callback){
        new AsyncTask<Long, Void, Card>(){
            @Override
            protected Card doInBackground(Long... params) {
                return getCard(params[0]);
            }

            @Override
            protected void onPostExecute(Card card) {
                if(callback != null){
                    callback.onGet(card);
                }
            }
        }.execute(id);
    }

    public void getCardCountAsync(long serieId, long relId, final CountCallback callback){
        Pair<Long, Long> data = new Pair<>(serieId, relId);

        new AsyncTask<Pair, Void, Integer>(){
            @Override
            protected Integer doInBackground(Pair... params) {
                return getCardCount((Long) params[0].first, (Long) params[0].second);
            }

            @Override
            protected void onPostExecute(Integer cards) {
                if(callback != null){
                    callback.onGet(cards);
                }
            }
        }.execute(data);
    }

    public void getSearchListAsync(String like, final ListCallback<Card> callback){
        new AsyncTask<String, Void, ArrayList<Card>>(){
            @Override
            protected ArrayList<Card> doInBackground(String... params) {
                return getSearchList(params[0]);
            }

            @Override
            protected void onPostExecute(ArrayList<Card> cards) {
                if(callback != null){
                    callback.onGet(cards);
                }
            }
        }.execute(like);
    }

    public interface Callback<T> {
        public void onGet(T entity);
    }

    public interface ListCallback<T> {
        public void onGet(ArrayList<T> list);
    }

    public interface CountCallback {
        public void onGet(int count);
    }
}
