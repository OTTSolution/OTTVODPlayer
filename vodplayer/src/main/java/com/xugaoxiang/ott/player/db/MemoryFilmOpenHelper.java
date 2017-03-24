package com.xugaoxiang.ott.player.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016/10/26.
 */
public class MemoryFilmOpenHelper extends SQLiteOpenHelper{

    public MemoryFilmOpenHelper(Context context) {
        super(context, "memoryfilm.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table memoryfilm (_id integer primary key autoincrement , " +
                "film_id integer," +
                "film_time integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
