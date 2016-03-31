package com.alisher.work.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alisher.work.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alisher on 3/2/2016.
 */
public class RegisterActivity  extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private EditText inputCountry;
    private EditText inputStreet;
    private EditText inputCity;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createLocationRequest();
        buildGoogleApiClient();

        inputFirstName = (EditText) findViewById(R.id.first_name);
        inputLastName = (EditText) findViewById(R.id.last_name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String first_name = inputFirstName.getText().toString().trim();
                String last_name = inputLastName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!first_name.isEmpty() && !last_name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(first_name, last_name, email, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void registerUser(String firstName, String lastName, String email, String password) {
        pDialog.setMessage("Registering ...");
        showDialog();
        final ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("clientRating", 0);
        user.put("performerRating", 0);
        user.put("country", " ");
        user.put("zip", " ");
        user.put("street", " ");
        user.put("state", " ");
        user.put("city", " ");
        Bitmap icon = BitmapFactory.decodeResource(RegisterActivity.this.getResources(),R.drawable.ava);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        ParseFile file = new ParseFile("avatar.png", data);
        file.saveInBackground();
        user.put("photo", file);
        user.put("lat", new ParseGeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //hideDialog();
                    ParseObject achieve = new ParseObject("Achievement");
                    achieve.put("userId", user.getObjectId());
                    achieve.put("performerRating", 0);
                    achieve.put("count", 0);
                    achieve.put("sum", 0);
                    achieve.put("balance", 1000);
                    achieve.put("frozenBalance", 1000);
                    achieve.saveInBackground();

                    Toast.makeText(RegisterActivity.this, "User Saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    //hideDialog();
                    Toast.makeText(RegisterActivity.this, "Saving user failed.", Toast.LENGTH_SHORT).show();
                    Log.w("", "Error : " + e.getMessage() + ":::" + e.getCode());
                    if (e.getCode() == 202) {
                        Toast.makeText(RegisterActivity.this,"Username already taken. \n Please choose another username.",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        hideDialog();
        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        Log.d("RESUME", "ASd");
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.d("LOCATION", mLastLocation.getLatitude()+"");
    }
}
