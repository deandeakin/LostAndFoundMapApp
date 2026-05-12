package com.example.lostandfoundapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lostandfoundapp.model.Advert;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lost_found.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ADVERTS = "adverts";
    private static final String COL_ID = "id";
    private static final String COL_POST_TYPE = "post_type";
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phone";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_LOCATION = "location";
    private static final String COL_CATEGORY = "category";
    private static final String COL_IMAGE_URI = "image_uri";
    private static final String COL_DATE_TIME = "date_time";

    public DatabaseHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    // Creates the local SQLite table used for adverts.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createAdvertsTable = "CREATE TABLE " + TABLE_ADVERTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_POST_TYPE + " TEXT NOT NULL, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_PHONE + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT NOT NULL, " +
                COL_LOCATION + " TEXT NOT NULL, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_IMAGE_URI + " TEXT, " +
                COL_DATE_TIME + " TEXT NOT NULL" +
                ")";

        db.execSQL(createAdvertsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADVERTS);
        onCreate(db);
    }

    // Inserts a new record into the adverts table. Also adds a timestamp to the record.
    public boolean insertAdvert(String postType, String name, String phone, String description, String location, String category, String imageUri) {

        SQLiteDatabase db = this.getWritableDatabase();
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        ContentValues cv = new ContentValues();
        cv.put(COL_POST_TYPE, postType);
        cv.put(COL_NAME, name);
        cv.put(COL_PHONE, phone);
        cv.put(COL_DESCRIPTION, description);
        cv.put(COL_LOCATION, location);
        cv.put(COL_CATEGORY, category);
        cv.put(COL_IMAGE_URI, imageUri);
        cv.put(COL_DATE_TIME, dateTime);

        long result = db.insert(TABLE_ADVERTS, null, cv);
        db.close();

        return result != -1;
    }

    // Retrieves an advert from the DB using its ID.
    public Advert getAdvertById(int advertId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ADVERTS + " WHERE " + COL_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(advertId)});
        Advert advert = null;
        if (cursor.moveToFirst()) {
            advert = cursorToAdvert(cursor);
        }
        cursor.close();
        db.close();
        return advert;
    }

    // Searches adverts using text matching and category filtering.
    public ArrayList<Advert> searchAdverts(String searchText, String category) {
        ArrayList<Advert> adverts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String search = "%" + (searchText == null ? "" : searchText.trim()) + "%";

        boolean hasSearchText = searchText != null && !searchText.trim().isEmpty();
        boolean hasCategory = category != null
                && !category.equalsIgnoreCase("All")
                && !category.trim().isEmpty();

        String selection = null;
        String[] selectionArgs = null;

        if (hasSearchText && hasCategory) {
            selection = COL_CATEGORY + " = ? AND (" +
                    COL_POST_TYPE + " LIKE ? OR " +
                    COL_NAME + " LIKE ? OR " +
                    COL_DESCRIPTION + " LIKE ? OR " +
                    COL_LOCATION + " LIKE ? OR " +
                    COL_CATEGORY + " LIKE ?)";

            selectionArgs = new String[]{category, search, search, search, search, search};

        } else if (hasSearchText) {
            selection = COL_POST_TYPE + " LIKE ? OR " +
                    COL_NAME + " LIKE ? OR " +
                    COL_DESCRIPTION + " LIKE ? OR " +
                    COL_LOCATION + " LIKE ? OR " +
                    COL_CATEGORY + " LIKE ?";

            selectionArgs = new String[]{search, search, search, search, search};

        } else if (hasCategory) {
            selection = COL_CATEGORY + " = ?";
            selectionArgs = new String[]{category};
        }

        Cursor cursor = db.query(TABLE_ADVERTS, null, selection, selectionArgs, null, null, COL_DATE_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                adverts.add(cursorToAdvert(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return adverts;
    }

    public boolean deleteAdvert(int advertId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_ADVERTS, COL_ID + " = ?",
                new String[]{String.valueOf(advertId)}
        );
        db.close();
        return result > 0;
    }

    // Converts the current DB row into an advert object.
    private Advert cursorToAdvert(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
        String postType = cursor.getString(cursor.getColumnIndexOrThrow(COL_POST_TYPE));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY));
        String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI));
        String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_TIME));

        return new Advert(id, postType, name, phone, description, location, category, imageUri, dateTime);
    }
}