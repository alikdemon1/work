package com.alisher.work.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alisher.work.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by Alisher on 3/2/2016.
 */
public class LoginActivity extends AppCompatActivity{
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    checkLogin(email, password);
                } else {

                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void checkLogin(String email, String password) {
        pDialog.setMessage("Logging in ...");
        showDialog();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    hideDialog();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                   hideDialog();
                    Toast.makeText(getApplicationContext(),"Incorrect login or password", Toast.LENGTH_LONG).show();
                }
            }
        });

//        if(email.equals("admin") && password.equals("admin")){
//            hideDialog();
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.putExtra("email", email);
//            intent.putExtra("pass", password);
//            startActivity(intent);
//            finish();
//        } else {
//            hideDialog();
//            Toast.makeText(getApplicationContext(),"Incorrect login or password", Toast.LENGTH_LONG).show();
//        }
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
