package com.jk91.android.tumejoropcion;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jk91.android.tumejoropcion.entity.Store;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jk91 on 14-11-21.
 */
public class StoreActivity extends ListActivity {

    private final String LOG_TAG = "StoreActivity";

    private Store[] stores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        RestStores getStores = new RestStores();
        getStores.execute();
        stores = getStores.stores;

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

    private class RestStores extends AsyncTask<Void, Void, Store[]> {

        Store[] stores;

        public RestStores(){
            stores = null;
        }

        @Override
        public Store[] doInBackground(Void... params) {

            final String LOG_TAG = "RestStores";

            try{
                final String URL = "";

                RestTemplate restGetStoreTemplate = new RestTemplate();
                restGetStoreTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                stores = restGetStoreTemplate.getForObject(URL, Store[].class);
                return stores;

            } catch(Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return stores;
        }
    }
}
