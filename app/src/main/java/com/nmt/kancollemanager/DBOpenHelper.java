package com.nmt.kancollemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * DBOpenHelper
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_FILE = "kancolle.db";
    private static final String DB_NAME = "kancolle";
    private static final int DB_VERSION = 2;

    private Context context;
    private File dbPath;
    private boolean isCreateDb;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.dbPath = context.getDatabasePath(DB_NAME);
        isCreateDb = false;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        if (isCreateDb) {
            try {
                db = copyDb(db);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        db.setVersion(1);
        return db;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        if (isCreateDb) {
            try {
                db = copyDb(db);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return db;
    }

    /**
     * Copy assets to application
     *
     * @param db SQLiteDatabase
     * @return SQLiteDatabase
     * @throws IOException
     */
    private SQLiteDatabase copyDb(SQLiteDatabase db) throws IOException {
        db.close();

        InputStream input = context.getAssets().open(DB_FILE);
        OutputStream output = new FileOutputStream(dbPath);

        byte[] buffer = new byte[1024];
        int size;
        while (-1 != (size = input.read(buffer))) {
            output.write(buffer, 0, size);
        }

        isCreateDb = false;

        return super.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        isCreateDb = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        isCreateDb = true;
    }
}
