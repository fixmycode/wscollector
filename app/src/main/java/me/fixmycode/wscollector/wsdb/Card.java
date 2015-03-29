package me.fixmycode.wscollector.wsdb;

import android.database.Cursor;

import java.io.Serializable;


public class Card implements Item, Serializable {

    public static final int CARD_ID = 0;
    public static final int CARD_SERIE = 1;
    public static final int CARD_REL = 2;
    public static final int CARD_CODE = 3;
    public static final int CARD_TITLE = 4;
    public static final int CARD_TITLE_JP = 5;
    public static final int CARD_RARITY = 6;
    public static final int CARD_COLOR = 7;
    public static final int CARD_SIDE = 8;
    public static final int CARD_TYPE = 9;
    public static final int CARD_LEVEL = 10;
    public static final int CARD_POWER = 11;
    public static final int CARD_COST = 12;
    public static final int CARD_SOUL = 13;
    public static final int CARD_KW_ONE = 14;
    public static final int CARD_KW_ONE_JP = 15;
    public static final int CARD_KW_TWO = 16;
    public static final int CARD_KW_TWO_JP = 17;
    public static final int CARD_TRG_ONE = 18;
    public static final int CARD_TRG_TWO = 19;
    public static final int CARD_TEXT = 20;
    public static final int CARD_FLAVOR = 21;

    private final long id;
    private final long serieId;
    private final long relId;
    private final String code;
    private final String title;
    private final String titleJp;
    private final String rarity;
    private final String color;
    private final String side;
    private final String type;
    private final int level;
    private final int power;
    private final int cost;
    private final int soul;
    private final String kwOne;
    private final String kwOneJp;
    private final String kwTwo;
    private final String kwTwoJp;
    private final String trgOne;
    private final String trgTwo;
    private final String cardText;
    private final String flavor;

    private Card(long id, long serieId, long relId, String code, String title, String titleJp,
                String rarity, String color, String side, String type, int level, int power,
                int cost, int soul, String kwOne, String kwOneJp, String kwTwo, String kwTwoJp,
                String trgOne, String trgTwo, String cardText, String flavor) {
        this.id = id;
        this.serieId = serieId;
        this.relId = relId;
        this.code = code;
        this.title = title;
        this.titleJp = titleJp;
        this.rarity = rarity;
        this.color = color;
        this.side = side;
        this.type = type;
        this.level = level;
        this.power = power;
        this.cost = cost;
        this.soul = soul;
        this.kwOne = kwOne;
        this.kwOneJp = kwOneJp;
        this.kwTwo = kwTwo;
        this.kwTwoJp = kwTwoJp;
        this.trgOne = trgOne;
        this.trgTwo = trgTwo;
        this.cardText = cardText;
        this.flavor = flavor;

    }

    private Card(long id, String title, String titleJp, String code, String color, String type) {
        this(id, -1, -1, code, title, titleJp, null, color, null, type, -1, -1, -1, -1, null, null,
                null, null, null, null, null, null);
    }

    public static Card fromCursor(Cursor c, boolean shortForm) {
        if(shortForm){
            return new Card(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getString(4), c.getString(5));
        } else {
            return new Card(c.getInt(CARD_ID), c.getInt(CARD_SERIE), c.getInt(CARD_REL),
                    c.getString(CARD_CODE), c.getString(CARD_TITLE), c.getString(CARD_TITLE_JP),
                    c.getString(CARD_RARITY), c.getString(CARD_COLOR), c.getString(CARD_SIDE),
                    c.getString(CARD_TYPE), c.getInt(CARD_LEVEL), c.getInt(CARD_POWER),
                    c.getInt(CARD_COST), c.getInt(CARD_SOUL), c.getString(CARD_KW_ONE),
                    c.getString(CARD_KW_ONE_JP), c.getString(CARD_KW_TWO),
                    c.getString(CARD_KW_TWO_JP), c.getString(CARD_TRG_ONE),
                    c.getString(CARD_TRG_TWO), c.getString(CARD_TEXT), c.getString(CARD_FLAVOR));
        }
    }

    public long getId() {
        return id;
    }

    @Override
    public int getItemType() {
        return Item.TYPE_CARD;
    }

    public long getSerieId() {
        return serieId;
    }

    public long getRelId() {
        return relId;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleJp() {
        return titleJp;
    }

    public String getRarity() {
        return rarity;
    }

    public String getColor() {
        return color;
    }

    public String getSide() {
        return side;
    }

    public String getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public int getPower() {
        return power;
    }

    public int getCost() {
        return cost;
    }

    public int getSoul() {
        return soul;
    }

    public String getKwOne() {
        return kwOne;
    }

    public String getKwOneJp() {
        return kwOneJp;
    }

    public String getKwTwo() {
        return kwTwo;
    }

    public String getKwTwoJp() {
        return kwTwoJp;
    }

    public String getTrgOne() {
        return trgOne;
    }

    public String getTrgTwo() {
        return trgTwo;
    }

    public String getCardText() {
        return cardText;
    }

    public String getFlavor() {
        return flavor;
    }
}
