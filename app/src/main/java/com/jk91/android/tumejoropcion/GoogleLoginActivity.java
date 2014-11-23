package com.jk91.android.tumejoropcion;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

public class GoogleLoginActivity extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult> {

    private static final int RC_SIGN_IN = 0;
    private final String LOG_TAG = "GooglePlusLogin";
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;

    private GoogleApiClient mGoogleApiClient;

    private SignInButton btnSignIn;

    private String userId;
    private static List<String[]> people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        btnSignIn = (SignInButton) findViewById(R.id.login_button);
        btnSignIn.setOnClickListener(this);

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
        Toast.makeText(this, "User is Connected!", Toast.LENGTH_LONG).show();
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            userId = currentPerson.getId();
        }
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(this);
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

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            String[] person;
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    Log.d(LOG_TAG, "Display name: " + personBuffer.get(i).getDisplayName());
                    person = new String[] { personBuffer.get(i).getId(), personBuffer.get(i).getDisplayName() };
                    people.add(person);

                    Log.d(LOG_TAG, "Person Id: " +people.get(i)[0]);
                }
            } finally {
                personBuffer.close();
                personBuffer.release();
                generateFriendsList();
            }
        } else {
            Log.e(LOG_TAG, "Error requesting visible circles: " + peopleData.getStatus());
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
