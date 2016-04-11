package com.alisher.work.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

public class ProfileActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemSelectedListener {
    private static final Pattern PATTERN_SSN = Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$");
    private static final String PATTERN_CITY_COUNTRY_STATE = "[A-Za-z]+";
    private static final String PATTERN_ZIP = "^[0-9]{1,5}$";
    private static final String PATTERN_BUILD_STREET = "([a-zA-Z0-9]+ ?)+?";
    TextView fio, email;

    //@RegExp(value = PATTERN_SSN, messageId = R.string.valid_ssn)
    EditText ssn;

    //@RegExp(value = PATTERN_ZIP, messageId = R.string.valid_zip)
    EditText zip;

    @RegExp(value = PATTERN_CITY_COUNTRY_STATE, messageId = R.string.valid_state)
    EditText state;

    //@RegExp(value = PATTERN_BUILD_STREET, messageId = R.string.valid_build)
    EditText buildNo;

    Spinner country;

    @NotEmpty(messageId = R.string.validation_empty)
    @RegExp(value = PATTERN_CITY_COUNTRY_STATE, messageId = R.string.valid_city)
    EditText city;

    @NotEmpty(messageId = R.string.validation_empty)
    @RegExp(value = PATTERN_BUILD_STREET, messageId = R.string.valid_street)
    EditText street;

    private Bitmap bmp;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_profile);
        initComponent();
        setTextForET();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    if (country.getSelectedItem().equals("United States")) {
                        if (checkSsn(ssn.getText().toString())) {
                            updateUser();
                        } else {
                            ssn.setError("Enter in format: 111-22-3333");
                        }
                    } else {
                        updateUser();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Please fill the required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUser() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseUser.put("ssn", ssn.getText().toString().trim());
        parseUser.put("state", state.getText().toString().trim());
        parseUser.put("buildingNo", buildNo.getText().toString().trim());
        parseUser.put("zip", zip.getText().toString().trim());
        parseUser.put("country", country.getSelectedItem());
        parseUser.put("countryId", country.getSelectedItemPosition());
        parseUser.put("city", city.getText().toString().trim());
        parseUser.put("street", street.getText().toString().trim());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ProfileActivity.this, "User updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "User update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                if (parseUser.getString("ssn") == null) {
                    ssn.setText("");
                } else {
                    ssn.setText(parseUser.getString("ssn") + "");
                }
//                country.setText((parseUser.getString("country") + "").replace("null", ""));
                country.setSelection(parseUser.getInt("countryId"));
                state.setText((parseUser.getString("state") + "").replace("null", ""));
                city.setText((parseUser.getString("city") + "").replace("null", ""));
                street.setText((parseUser.getString("street") + "").replace("null", ""));
                buildNo.setText((parseUser.getString("buildingNo") + "").replace("null", ""));
                zip.setText((parseUser.getString("zip") + "").replace("null", ""));
                ParseFile file = parseUser.getParseFile("photo");
                try {
                    Bitmap photo = decodeFile(file.getFile());
                    profileImage.setImageBitmap(photo);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public boolean validate() {
        boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(this, true));
        if (isValid) {
            return true;
        } else {
            return false;
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    private void initComponent() {
//        tvClientR = (TextView) findViewById(R.id.client_r_profile);
//        tvPerfR = (TextView) findViewById(R.id.perf_r_profile);
        fio = (TextView) findViewById(R.id.profile_name);
        email = (TextView) findViewById(R.id.profile_email);
        country = (Spinner) findViewById(R.id.profile_country);
        city = (EditText) findViewById(R.id.profile_city);
        street = (EditText) findViewById(R.id.profile_street);
        buildNo = (EditText) findViewById(R.id.profile_buildNo);
        zip = (EditText) findViewById(R.id.profile_zip);
        ssn = (EditText) findViewById(R.id.profile_ssn);
        state = (EditText) findViewById(R.id.profile_state);
        profileImage = (ImageView) findViewById(R.id.imageProfile);

        country.setOnItemSelectedListener(this);

        ArrayList<String> countries = new ArrayList<>();
        Collections.addAll(countries, getResources().getStringArray(R.array.countries));

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        country.setAdapter(dataAdapter);

        city.addTextChangedListener(this);
        street.addTextChangedListener(this);
        zip.addTextChangedListener(this);
        buildNo.addTextChangedListener(this);
        ssn.addTextChangedListener(this);
        state.addTextChangedListener(this);
    }

    private boolean checkSsn(String ssn) {
        return PATTERN_SSN.matcher(ssn).matches();
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

    private String getRealPathFromURI(Uri contentURI) {
        String result = null;

        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (null != data) {
                    Uri imageUri = data.getData();
                    File file = new File(getRealPathFromURI(imageUri));
                    Bitmap bitmap = decodeFile(file);
                    profileImage.setImageBitmap(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                    }
                    byte[] imageForUpload = stream.toByteArray();
                    Log.d("HELLO", imageForUpload.length + "");
                    ParseFile fileP = new ParseFile("avatar.png", imageForUpload);
                    fileP.saveInBackground();
                    ParseUser user = ParseUser.getCurrentUser();
                    user.put("photo", fileP);
                    user.saveInBackground();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        FormValidator.validate(this, new SimpleErrorPopupCallback(this));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}