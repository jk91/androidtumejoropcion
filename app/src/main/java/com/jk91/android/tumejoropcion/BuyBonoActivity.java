package com.jk91.android.tumejoropcion;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jk91 on 14-11-21.
 */
public class BuyBonoActivity extends Activity implements View.OnClickListener {

    private String friendName;
    private String friendId;
    private String storeName;
    private String userId;

    private Button buyBonoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buybono);

        Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getString("userId");
        friendId = bundle.getString("friendId");
        friendName = bundle.getString("friendName");
        storeName = bundle.getString("storeName");

        TextView storeText = (TextView) findViewById(R.id.store_name);
        storeText.setText(storeName);
        TextView friendText = (TextView) findViewById(R.id.friend_name);
        friendText.setText(friendName);

        buyBonoButton = (Button) findViewById(R.id.buy_bono_button);
        buyBonoButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buy_bono_button) {
            new BuyBono().execute();
            Toast.makeText(this, "Bono comprado con éxito!", Toast.LENGTH_LONG).show();
        }
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

    private class BuyBono extends AsyncTask<Void, Void, Void> {

        public BuyBono() {}

        @Override
        public Void doInBackground(Void... params) {

            final String LOG_TAG = "BuyBono";

            JSONObject bono = new JSONObject();
            EditText bonoValorInput = (EditText) findViewById(R.id.value_bono);
            long bonoValor = Long.valueOf(bonoValorInput.getText().toString()).longValue();

            try{
                bono.put("valor", bonoValor);
                bono.put("loginTienda", storeName);
                bono.put("idCuentaUsuario", userId);
                bono.put("tipoUsuario", "GOOGLE_PLUS");
                bono.put("idAmigo", friendId);
                bono.put("tipoAmigo", "GOOGLE_PLUS");

                Log.v(LOG_TAG, bono.toString());

                final String URL = "http://172.24.98.151/:8080/TuMejorOpcion-web/webresources/bonos/comprarBono";

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> entity = new HttpEntity<String>(bono.toString(), headers);
                ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.PUT, entity, String.class);
                response.getHeaders().getLocation();
                response.getStatusCode();

                Log.v(LOG_TAG, response.getBody());

            } catch(Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }
    }
}
