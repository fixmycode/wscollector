package me.fixmycode.wscollector;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import se.emilsjolander.sprinkles.Migration;
import se.emilsjolander.sprinkles.Sprinkles;

public class WSApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Sprinkles sprinkles = Sprinkles.init(getApplicationContext());

        sprinkles.addMigration(new Migration() {
            @Override
            protected void doMigration(SQLiteDatabase db) {
                db.execSQL(
                        "CREATE TABLE IF NOT EXISTS Deck ("+
                                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                "title STRING UNIQUE,"+
                                "image_id STRING"+
                        ");"+
                        "CREATE TABLE IF NOT EXISTS DeckCard ("+
                                "deck_id INTEGER NOT NULL,"+
                                "card_id INTEGER NOT NULL,"+
                                "FOREIGN KEY (deck_id) REFERENCES Deck(id) ON UPDATE CASCADE,"+
                                "PRIMARY KEY (deck_id, card_id)"+
                        ");"
                );
            }
        });
    }
}
