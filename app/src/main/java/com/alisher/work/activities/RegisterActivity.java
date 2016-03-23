package com.alisher.work.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alisher.work.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Alisher on 3/2/2016.
 */
public class RegisterActivity  extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFirstName = (EditText) findViewById(R.id.first_name);
        inputLastName = (EditText) findViewById(R.id.last_name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputCountry = (EditText) findViewById(R.id.country);
        inputStreet = (EditText) findViewById(R.id.street);
        inputCity = (EditText) findViewById(R.id.city);
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
                String country = inputCountry.getText().toString().trim();
                String city = inputCity.getText().toString().trim();
                String street = inputStreet.getText().toString().trim();

                if (!first_name.isEmpty() && !last_name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !country.isEmpty() && !city.isEmpty() && !street.isEmpty()) {
                    registerUser(first_name, last_name, email, password, country, city, street);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void registerUser(String firstName, String lastName, String email, String password, String country, String city, String street) {
        pDialog.setMessage("Registering ...");
        showDialog();
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("clientRating", 0);
        user.put("performerRating", 0);
        user.put("country", country);
        user.put("city", city);
        user.put("street", street);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //hideDialog();
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

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
