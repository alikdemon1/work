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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {
    TextView tvClientR,tvPerfR;
    ImageView ivUser;
    EditText etFname,etLname,etEmail,etPass,etSSN,etCountry,etState,etCity,etStreet,etBuildNo,etZIP;
    private Bitmap bmp;
    private ParseUser user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponent();
        setTextForET();
    }

    private void setTextForET()  {
//        ParseQuery<ParseUser> queryParseQuery = ParseUser.getQuery();
//        queryParseQuery.whereEqualTo("objectId", user.get)
        tvClientR.setText("Client rating : "+user.getInt("clientRating"));
        tvPerfR.setText("Performer rating : " + user.getInt("performerRating"));
        etFname.setText(user.getString("firstName")+"");
        etLname.setText(user.getString("lastName")+"");
        etEmail.setText(user.getString("username")+"");
        etSSN.setText(user.getString("ssn")+"");
        etCountry.setText(user.getString("country")+"");
        etState.setText(user.getString("state")+"");
        etCity.setText(user.getString("city")+"");
        etStreet.setText(user.getString("street")+"");
        etBuildNo.setText(user.getString("buildingNo")+"");
        etZIP.setText(user.getString("zip")+"");

        ParseFile image = (ParseFile) user.get("photo");
        try {
            bmp = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ivUser.setImageBitmap(bmp);

    }

    private void initComponent() {
        tvClientR=(TextView)findViewById(R.id.client_r_profile);
        tvPerfR=(TextView)findViewById(R.id.perf_r_profile);
        etFname=(EditText)findViewById(R.id.first_name_profile);
        etLname=(EditText)findViewById(R.id.last_name_profile);
        etEmail=(EditText)findViewById(R.id.email_profile);
        etPass=(EditText)findViewById(R.id.password_profile);
        etSSN=(EditText)findViewById(R.id.ssn_profile);
        etCountry=(EditText)findViewById(R.id.country_profile);
        etState=(EditText)findViewById(R.id.state_profile);
        etCity=(EditText)findViewById(R.id.city_profile);
        etStreet=(EditText)findViewById(R.id.street_profile);
        etBuildNo=(EditText)findViewById(R.id.buildNo_profile);
        etZIP=(EditText)findViewById(R.id.zip_profile);
        ivUser=(ImageView)findViewById(R.id.img_profile);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveClicked(View view) {
        user.setUsername(etEmail.getText().toString().trim());
        if(!etPass.getText().toString().isEmpty())
            user.setPassword(etPass.getText().toString().trim());
        user.put("firstName",etFname.getText().toString().trim() );
        user.put("lastName", etLname.getText().toString().trim());
        user.put("ssn", etSSN.getText().toString().trim());
        user.put("country", etCountry.getText().toString().trim());
        user.put("state", etState.getText().toString().trim());
        user.put("city", etCity.getText().toString().trim());
        user.put("street", etStreet.getText().toString().trim());
        user.put("buildingNo", etBuildNo.getText().toString().trim());
        user.put("zip", etZIP.getText().toString().trim());

        user.saveEventually();
        Toast.makeText(ProfileActivity.this,"Changes saved",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
    }
}
