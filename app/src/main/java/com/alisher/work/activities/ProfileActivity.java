package com.alisher.work.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.alisher.work.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProfileActivity extends AppCompatActivity {
    TextView fio, email, country, city, street;
    EditText ssn, zip, state, buildNo;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser parseUser = ParseUser.getCurrentUser();
                parseUser.put("ssn", Integer.parseInt(ssn.getText().toString().trim()));
                parseUser.put("state", state.getText().toString().trim());
                parseUser.put("buildingNo", buildNo.getText().toString().trim());
                parseUser.put("zip", zip.getText().toString().trim());
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("SUCCESS", "");
                        } else {
                            Log.d("FAILED", "");
                        }
                    }
                });
            }
        });

        initComponent();
        setTextForET();
    }

    private void setTextForET() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
//                tvClientR.setText("Client rating : " + parseUser.getInt("clientRating"));
//                tvPerfR.setText("Performer rating : " + parseUser.getInt("performerRating"));
                fio.setText(parseUser.getString("firstName") + " " + parseUser.getString("lastName"));
                email.setText(parseUser.getString("username") + "");
                ssn.setText(parseUser.getInt("ssn") + "");
                country.setText(parseUser.getString("country") + "");
                state.setText(parseUser.getString("state") + "");
                city.setText(parseUser.getString("city") + "");
                street.setText(parseUser.getString("street") + "");
                buildNo.setText(parseUser.getString("buildingNo") + "");
                zip.setText(parseUser.getString("zip") + "");
            }
        });
    }

    private void initComponent() {
//        tvClientR = (TextView) findViewById(R.id.client_r_profile);
//        tvPerfR = (TextView) findViewById(R.id.perf_r_profile);
        fio = (TextView) findViewById(R.id.profile_name);
        email = (TextView) findViewById(R.id.profile_email);
        country = (TextView) findViewById(R.id.profile_country);
        city = (TextView) findViewById(R.id.profile_city);
        street = (TextView) findViewById(R.id.profile_street);
        buildNo = (EditText) findViewById(R.id.profile_buildNo);
        zip = (EditText) findViewById(R.id.profile_zip);
        ssn = (EditText) findViewById(R.id.profile_ssn);
        state = (EditText) findViewById(R.id.profile_state);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}