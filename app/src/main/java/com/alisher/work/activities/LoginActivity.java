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
import com.alisher.work.admin.AdminActivity;
import com.alisher.work.arbitor.ListArbitorActivity;
import com.alisher.work.chat.BaseActivity;
import com.alisher.work.chat.UserListActivity;
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
    private ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Log.d("USERNAME", currentUser.getUsername());
        }

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
                if (email.equals("arbitor")&&password.equals("arbitor")){
                    startActivity(new Intent(LoginActivity.this,ListArbitorActivity.class));
                } else if (email.equals("admin")&&password.equals("admin")){
                    startActivity(new Intent(LoginActivity.this,AdminActivity.class));
                } else if (!email.isEmpty() && !password.isEmpty() && !email.equals("arbitor")&& !password.equals("arbitor")) {
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

    private void checkLogin(final String email, String password) {
        pDialog.setMessage("Logging in ...");
        showDialog();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    hideDialog();

//                    if (parseUser.getBoolean("emailVerified")){
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        UserListActivity.user = parseUser;
                        startActivity(i);
                        finish();
//                    } else {
//                        Toast.makeText(LoginActivity.this, "Please confirm the e-mail address", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                   hideDialog();
                    Toast.makeText(getApplicationContext(),"Incorrect login or password", Toast.LENGTH_LONG).show();
                }
            }
        });
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
