package com.fortlab.amber;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    private final int PICK_CONTACT = 2015;

    DatabaseHelper databaseHelper;
    ArrayList arrayList;
    ArrayAdapter arrayAdapter;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Contacts");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        databaseHelper = new DatabaseHelper(ContactListActivity.this);

        listView = findViewById(R.id.list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (databaseHelper.removeContact(arrayList.get(i).toString())) {
                    Toast.makeText(ContactListActivity.this, arrayList.get(i).toString() + " removed from list", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ContactListActivity.this, arrayList.get(i).toString() + " unable to remove from list", Toast.LENGTH_LONG).show();
                }
                loadContactList();
            }
        });

        loadContactList();

    }

    public void removeContactButtonClick(View v) {
        Toast.makeText(getApplicationContext(), "Contact removed", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_contact_from_list:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
                break;
                default:
                    break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int contact_id = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

            databaseHelper = new DatabaseHelper(ContactListActivity.this);
            Toast.makeText(getApplicationContext(), "Contact Id: " + cursor.getString(contact_id), Toast.LENGTH_LONG).show();

            if (databaseHelper.addContact(cursor.getString(column), cursor.getInt(contact_id))) {
                Toast.makeText(getApplicationContext(), "Contact Added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to add the contact", Toast.LENGTH_LONG).show();
            }

            loadContactList();
        }
    }

    public void loadContactList() {
        arrayList = databaseHelper.getAllNumbers();
        arrayAdapter = new ArrayAdapter(ContactListActivity.this, R.layout.simple_list_item, R.id.contact_list_view, arrayList);
        listView.setAdapter(arrayAdapter);
    }
}
