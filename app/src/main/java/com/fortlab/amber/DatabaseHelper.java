package com.fortlab.amber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "amber_alerts";
    private static final String TABLE_NAME = "selected_contacts";

    private Context thisContext;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,1);
        this.thisContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create table
        String createTable = "create table " + TABLE_NAME + "(id INTEGER PRIMARY KEY, contact_id TEXT, contact TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addContact(String number, int contact_id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("contact", number);
        contentValues.put("contact_id", contact_id);

        SQLiteDatabase sqLiteDatabase1 = this.getReadableDatabase();

        if (sqLiteDatabase1.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE contact = '" + number + "'", null).getCount() > 0) {
            Toast.makeText(thisContext, "Contact already exists", Toast.LENGTH_LONG).show();
        } else {
            sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        }


        return true;

    }

    public boolean removeContact(String number) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, "contact" + " = '" + number + "'", null);
        return true;
    }

    public ArrayList getAllNumbers() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(cursor.getString(cursor.getColumnIndex("contact")));
            cursor.moveToNext();
        }
        return arrayList;
    }

    public boolean checkContactExists(String contact) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        if (sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE contact = '" + contact + "'", null).getCount() > 0) {
            return true;
        }

        return false;
    }
}
