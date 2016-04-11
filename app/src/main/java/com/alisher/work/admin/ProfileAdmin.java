package com.alisher.work.admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.alisher.work.R;

/**
 * Created by Alisher Kozhabay on 3/31/2016.
 */
public class ProfileAdmin extends AppCompatActivity {
    private TextView name, email, ssn, country, city, street, state, zip, buildNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        setComponents();
    }

    public void initComponents() {
        name = (TextView) findViewById(R.id.admin_name);
        email = (TextView) findViewById(R.id.admin_email);
        ssn = (TextView) findViewById(R.id.admin_ssn);
        country = (TextView) findViewById(R.id.admin_country);
        city = (TextView) findViewById(R.id.admin_city);
        street = (TextView) findViewById(R.id.admin_street);
        state = (TextView) findViewById(R.id.admin_state);
        zip = (TextView) findViewById(R.id.admin_zip);
        buildNo = (TextView) findViewById(R.id.admin_buildNo);
    }

    public void setComponents() {
        name.setText(getIntent().getStringExtra("name") + " ");
        email.setText(getIntent().getStringExtra("email") + " ");
        country.setText(getIntent().getStringExtra("country") + " ");
        city.setText(getIntent().getStringExtra("city") + " ");
        street.setText(getIntent().getStringExtra("street") + " ");
        state.setText(getIntent().getStringExtra("state") + " ");
        buildNo.setText(getIntent().getStringExtra("buildNo") + " ");
        zip.setText(getIntent().getStringExtra("zip") + " ");
        if (getIntent().getIntExtra("ssn", 0) == 0) {
            ssn.setText(" ");
        } else {
            ssn.setText(getIntent().getIntExtra("ssn", 0) + " ");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}