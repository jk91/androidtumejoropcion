package com.jk91.android.tumejoropcion;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jk91.android.tumejoropcion.entity.GPlusPerson;
import com.jk91.android.tumejoropcion.entity.RequestAmigos;
import com.jk91.android.tumejoropcion.entity.Store;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jk91 on 14-11-21.
 */
public class StoreActivity extends ListActivity {

    private final String LOG_TAG = "StoreActivity";

    private String friendId;
    private String friendName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getString("userId");
        friendId = bundle.getString("friendId");
        friendName = bundle.getString("friendName");

        Log.v(LOG_TAG, friendId +" : "+ friendName);

        RestStores getStores = new RestStores();
        getStores.execute();

        final List<Store> stores = getStores.stores;

        ArrayAdapter storeAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, stores) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(stores.get(position).getLogin());
                text2.setText("Menciones: "+stores.get(position).getNumMenciones());
                return view;
            }
        };

        setListAdapter(storeAdapter);
        storeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Store store = (Store) l.getItemAtPosition(position);
        Intent intent = new Intent(this, BuyBonoActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("friendId", friendId);
        intent.putExtra("friendName", friendName);
        intent.putExtra("storeName", store.getLogin());
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
        if (id == R.id.action_logout) {
            Intent intent = new Intent(getListView().getContext(), GoogleLoginActivity.class);
            intent.putExtra("logout", true);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class RestStores extends AsyncTask<Void, Void, List> {

        GPlusPerson[] people;
        List stores;

        public RestStores(){
            stores = new ArrayList<Store>();
        }

        @Override
        public List doInBackground(Void... params) {

            final String LOG_TAG = "RestStores";

            try{
                //REST Request
                final String URL = "http://172.24.98.151:8080/TuMejorOpcion-web/webresources/amigos/getAmigos2";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                RequestAmigos request = new RequestAmigos();
                request.setGoogleId(userId);
                HttpEntity<RequestAmigos> entity = new HttpEntity<RequestAmigos>(request, headers);
                ResponseEntity<GPlusPerson[]> response = restTemplate.exchange(URL, HttpMethod.POST, entity, GPlusPerson[].class);
                //people = restTemplate.getForObject(URL, GPlusPerson[].class);
                people = response.getBody();
                Log.v(LOG_TAG,response.getBody().toString());
                Log.v(LOG_TAG, response.toString());
                for (int i=0; i<people.length; i++) {
                    Log.v(LOG_TAG, "Name: "+people[i].getDisplayName()+", Id: "+people[i].getIdSocial());
                }

                /*//Mock
                ObjectMapper mapper = new ObjectMapper();
                people = mapper.readValue("[{\"idSocial\":\"103790887876684741604\",\"displayName\":\"Andre Öberg\",\"tipoCuenta\":\"Google+\",\"tiendasMencionadas\":[{\"login\":\"asos\",\"numMencionesLink\":0,\"numMenciones\":1},{\"login\":\"dealextreme\",\"numMencionesLink\":0,\"numMenciones\":0}]},{\"idSocial\":\"111012289377872664851\",\"displayName\":\"Axel Ekman\",\"tipoCuenta\":\"Google+\",\"tiendasMencionadas\":[{\"login\":\"asos\",\"numMencionesLink\":0,\"numMenciones\":0},{\"login\":\"dealextreme\",\"numMencionesLink\":0,\"numMenciones\":1}]}]", GPlusPerson[].class);
                */

                Store[] restStores = new Store[] {};
                for(GPlusPerson p : people) {
                    if (p.getIdSocial().equals(friendId)) {
                        restStores = p.getTiendasMencionadas();
                        Log.v(LOG_TAG, p.getDisplayName());
                    }
                }

                for(int i=0; i<restStores.length; i++) {
                    stores.add(restStores[i]);
                    Log.v(LOG_TAG, restStores[i].getLogin());
                }
            } catch(Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return stores;
        }
    }
}
