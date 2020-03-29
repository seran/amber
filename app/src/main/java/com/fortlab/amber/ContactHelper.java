package com.fortlab.amber;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;


public class ContactHelper {

    public static final String contactStoredNumberByPhoneNumber(Context context, String phoneNumber) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.NUMBER}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactNumberFound = null;
        String contactNumber = "";
        if(cursor.moveToFirst()) {
            contactNumberFound = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.NUMBER));
        }
        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactNumberFound;

    }
}
