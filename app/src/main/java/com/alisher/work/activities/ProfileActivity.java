package com.alisher.work.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    TextView tvClientR, tvPerfR;
    ImageView ivUser;
    EditText etFname, etLname, etEmail, etPass, etSSN, etCountry, etState, etCity, etStreet, etBuildNo, etZIP;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponent();
        setTextForET();
    }

    private void setTextForET() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                tvClientR.setText("Client rating : " + parseUser.getInt("clientRating"));
                tvPerfR.setText("Performer rating : " + parseUser.getInt("performerRating"));
                etFname.setText(parseUser.getString("firstName") + "");
                etLname.setText(parseUser.getString("lastName") + "");
                etEmail.setText(parseUser.getString("username") + "");
                etSSN.setText(parseUser.getString("ssn") + "");
                etCountry.setText(parseUser.getString("country") + "");
                etState.setText(parseUser.getString("state") + "");
                etCity.setText(parseUser.getString("city") + "");
                etStreet.setText(parseUser.getString("street") + "");
                etBuildNo.setText(parseUser.getString("buildingNo") + "");
                etZIP.setText(parseUser.getString("zip") + "");

                ParseFile image = (ParseFile) parseUser.get("photo");

                try {
                    bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
                    ivUser.setImageBitmap(bmp);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });


    }

    private void initComponent() {
        tvClientR = (TextView) findViewById(R.id.client_r_profile);
        tvPerfR = (TextView) findViewById(R.id.perf_r_profile);
        etFname = (EditText) findViewById(R.id.first_name_profile);
        etLname = (EditText) findViewById(R.id.last_name_profile);
        etEmail = (EditText) findViewById(R.id.email_profile);
        etPass = (EditText) findViewById(R.id.password_profile);
        etSSN = (EditText) findViewById(R.id.ssn_profile);
        etCountry = (EditText) findViewById(R.id.country_profile);
        etState = (EditText) findViewById(R.id.state_profile);
        etCity = (EditText) findViewById(R.id.city_profile);
        etStreet = (EditText) findViewById(R.id.street_profile);
        etBuildNo = (EditText) findViewById(R.id.buildNo_profile);
        etZIP = (EditText) findViewById(R.id.zip_profile);
        ivUser = (ImageView) findViewById(R.id.img_profile);
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

    public void saveClicked(View view) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                    parseUser.put("country", etCountry.getText().toString().trim());
                    parseUser.put("state", etState.getText().toString().trim());
                    parseUser.put("city", etCity.getText().toString().trim());
                    parseUser.put("street", etStreet.getText().toString().trim());
                    parseUser.put("buildingNo", etBuildNo.getText().toString().trim());
                    parseUser.put("zip", etZIP.getText().toString().trim());
                    parseUser.saveEventually();
                }
            
        });
        Toast.makeText(ProfileActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }
}
