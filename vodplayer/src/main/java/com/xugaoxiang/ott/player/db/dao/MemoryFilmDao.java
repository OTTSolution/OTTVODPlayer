package com.xugaoxiang.ott.player.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xugaoxiang.ott.player.db.MemoryFilmOpenHelper;

/**
 * Created by user on 2016/10/26.
 */
public class MemoryFilmDao {

    private final MemoryFilmOpenHelper openHelper;

    private Context mContext;

    private static MemoryFilmDao filmDao;

    private MemoryFilmDao(Context context){
        openHelper = new MemoryFilmOpenHelper(context);
        mContext = context;
    }

    public static MemoryFilmDao getInstance(Context context){
        if (filmDao == null){
            synchronized (MemoryFilmDao.class){
                if (filmDao == null){
                    filmDao = new MemoryFilmDao(context);
                }
            }
        }
        return filmDao;
    }

    public void addMemoryFilm(int film_id , int time){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("film_id" , film_id);
        values.put("film_time" , time);
        db.insert("memoryfilm" , null , values);
        db.close();
    }

    public int findFilm(int film_id){
        int film_time = 0;
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.query("memoryfilm", new String[]{"film_time"}, "film_id=?", new String[]{film_id + ""}, null, null, null);
        if (cursor.moveToFirst()){
            film_time = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return film_time;
    }

    public void updata(int film_id , int time){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("film_time" , time);
        db.update("memoryfilm" , values , "film_id=?" , new String[]{film_id+""});
    }

    public void delete(int film_id){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete("memoryfilm" , "film_id=?" , new String[]{film_id+""});
    }
}
