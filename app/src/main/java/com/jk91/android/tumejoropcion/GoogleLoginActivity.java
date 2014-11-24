package com.jk91.android.tumejoropcion;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.jk91.android.tumejoropcion.entity.Activity;
import com.jk91.android.tumejoropcion.entity.Friend;
import com.jk91.android.tumejoropcion.entity.RequestAmigos;
import com.jk91.android.tumejoropcion.entity.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class GoogleLoginActivity extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        /*ResultCallback<People.LoadPeopleResult> { */

    private static final int RC_SIGN_IN = 0;
    private final String LOG_TAG = "GooglePlusLogin";
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private boolean signOut;

    private GoogleApiClient mGoogleApiClient;

    private SignInButton btnSignIn;

    private String userId;
    private String userDisplayName;
    private final String ACCOUNT_TYPE = "Google+";
    private static List<String[]> people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        btnSignIn = (SignInButton) findViewById(R.id.login_button);
        btnSignIn.setOnClickListener(this);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null) {
            signOut = bundle.getBoolean("logout");
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        people = new ArrayList<String[]>();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_button) {
            signInGPlus();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if(!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        if(!mIntentInProgress) {
            mConnectionResult = result;

            if(mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
            Intent intent) {
        if(requestCode == RC_SIGN_IN) {
            if(responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if(!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        if (mGoogleApiClient.isConnected() && signOut == true) {
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            finish();
            startActivity(getIntent());
        }
        else {
            Toast.makeText(this, "User is Connected!", Toast.LENGTH_LONG).show();
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                userId = currentPerson.getId();
                userDisplayName = currentPerson.getDisplayName();
                Log.v(LOG_TAG, userId+":"+userDisplayName);
            }
            new ServerAuth().execute();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    //Sign in
    public void signInGPlus() {
        if(!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    //Resolve sign in errors
    private void resolveSignInError() {
        if(mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch(IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void generateFriendsList() {
        Intent intent = new Intent(GoogleLoginActivity.this, FriendsActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("peopleSize", people.size());
        Log.v(LOG_TAG, Integer.toString(people.size()));
        for(int i=0; i<people.size(); i++) {
            intent.putExtra("person"+i, people.get(i)[1]);
            intent.putExtra("id"+i, people.get(i)[0]);
            Log.v(LOG_TAG, "person"+i);
        }

        GoogleLoginActivity.this.startActivity(intent);
    }

    private class ServerAuth extends AsyncTask<Void, Void, Void> implements ResultCallback<People.LoadPeopleResult> {

        public ServerAuth() {}

        @Override
        public Void doInBackground(Void... params) {

            final String LOG_TAG = "Server Authentication";
            final String URL = "http://172.24.98.151:8080/TuMejorOpcion-web/webresources/login/auth";

            try {
                User user = new User();
                user.setId(userId);
                user.setDisplayName(userDisplayName);
                user.setAcountType(ACCOUNT_TYPE);

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<User> entity = new HttpEntity<User>(user, headers);
                ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
                response.getHeaders().getLocation();
                response.getStatusCode();
                Log.v(LOG_TAG, response.getBody());

                if(response.getBody().equals("OUTDATED_FRIENDS")) {
                    Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                            .setResultCallback(this);
                }
                else if(response.getBody().equals("OK")) {

                }
            } catch(Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        public void onResult(People.LoadPeopleResult peopleData) {
            if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                PersonBuffer personBuffer = peopleData.getPersonBuffer();
                String[] person;
                try {
                    int count = personBuffer.getCount();
                    List friends = new ArrayList<Friend>();
                    Friend friend;
                    for (int i = 0; i < count; i++) {
                        Log.d(LOG_TAG, "Display name: " + personBuffer.get(i).getDisplayName());
                        person = new String[] { personBuffer.get(i).getId(), personBuffer.get(i).getDisplayName() };
                        people.add(person);
                        Log.d(LOG_TAG, "Person Id: " +people.get(i)[0]);
                        try{
                            friend = new Friend();
                            friend.setId(person[0]);
                            friend.setDisplayName(person[1]);
                            friend.setActivityFied(new ArrayList<Activity>());
                            friends.add(friend);
                        } catch(Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                    new PostFriends().execute(friends);
                } finally {
                    personBuffer.close();
                    personBuffer.release();
                    generateFriendsList();
                }
            } else {
                Log.e(LOG_TAG, "Error requesting visible circles: " + peopleData.getStatus());
            }
        }
    }

    private class PostFriends extends AsyncTask<List, Void, Void> {

        public PostFriends() {}

        public Void doInBackground(List... params) {
            try {
                final String URL = "http://172.24.98.151:8080/TuMejorOpcion-web/webresources/amigos/postAmigos2";

                RequestAmigos request = new RequestAmigos();
                List friends = params[0];

                request.setGoogleId(userId);
                request.setAcountType(ACCOUNT_TYPE);
                request.setFriends(friends);

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<RequestAmigos> entity = new HttpEntity<RequestAmigos>(request, headers);
                ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
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
