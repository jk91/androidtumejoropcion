package com.jk91.android.tumejoropcion;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jk91 on 14-11-19.
 */
public class FriendsActivity extends ListActivity {

    private List<String> peopleNames;
    private List<String> peopleIds;
    private ArrayAdapter<String> peopleAdapter;
    private String userId;

    private final String LOG_TAG = "FriendsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getString("userId");
        peopleNames = new ArrayList<String>();
        peopleIds = new ArrayList<String>();
        if (bundle != null) {
            peopleNames.clear();
            peopleIds.clear();
            int peopleSize = bundle.getInt("peopleSize");
            for (int i = 0; i < peopleSize; i++) {
                peopleNames.add(bundle.getString("person" + i));
                peopleIds.add(bundle.getString("id" + i));
                Log.v(LOG_TAG, "Name: " + peopleNames.get(i));
                Log.v(LOG_TAG, "Id: " + peopleIds.get(i));
            }
            Log.v(LOG_TAG, "Get Bundle Success!");
        }

        peopleAdapter = new ArrayAdapter<String>(this,
                R.layout.list_item_person, R.id.list_item_person_textview, peopleNames);
        setListAdapter(peopleAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String friend = (String) l.getItemAtPosition(position);
        String friendId = peopleIds.get(peopleNames.indexOf(friend));
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("friendName", friend);
        intent.putExtra("friendId", friendId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.google_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
