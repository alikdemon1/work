package com.alisher.work.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    TextView fio, email;
    EditText ssn, zip, state, buildNo, country, city, street;
    private Bitmap bmp;
    ImageView profileImage;

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
                email.setText(parseUser.getString("username") + " ");
                ssn.setText(parseUser.getInt("ssn") + "");
                country.setText(parseUser.getString("country") + " ");
                state.setText(parseUser.getString("state") + " ");
                city.setText(parseUser.getString("city") + " ");
                street.setText(parseUser.getString("street") + " ");
                buildNo.setText(parseUser.getString("buildingNo") + " ");
                zip.setText(parseUser.getString("zip") + " ");
                ParseFile file = parseUser.getParseFile("photo");
                try {
                    Bitmap photo = BitmapFactory.decodeByteArray(file.getData(), 0, file.getData().length);
                    profileImage.setImageBitmap(photo);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void initComponent() {
//        tvClientR = (TextView) findViewById(R.id.client_r_profile);
//        tvPerfR = (TextView) findViewById(R.id.perf_r_profile);
        fio = (TextView) findViewById(R.id.profile_name);
        email = (TextView) findViewById(R.id.profile_email);
        country = (EditText) findViewById(R.id.profile_country);
        city = (EditText) findViewById(R.id.profile_city);
        street = (EditText) findViewById(R.id.profile_street);
        buildNo = (EditText) findViewById(R.id.profile_buildNo);
        zip = (EditText) findViewById(R.id.profile_zip);
        ssn = (EditText) findViewById(R.id.profile_ssn);
        state = (EditText) findViewById(R.id.profile_state);
        profileImage = (ImageView) findViewById(R.id.imageProfile);
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

    public void setImageProfile(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Choose image")
                .setPositiveButton("From gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, 100);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(ProfileActivity.this, "NO", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (null != data) {
                    Uri imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        profileImage.setImageBitmap(bitmap);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] imageForUpload = stream.toByteArray();
                        ParseFile file = new ParseFile("avatar.png", imageForUpload);
                        file.saveInBackground();
                        ParseUser user = ParseUser.getCurrentUser();
                        user.put("photo", file);
                        user.saveInBackground();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}