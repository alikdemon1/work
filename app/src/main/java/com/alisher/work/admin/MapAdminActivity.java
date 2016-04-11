package com.alisher.work.admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alisher.work.R;
import com.alisher.work.models.MyMarker;
import com.alisher.work.models.Perform;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alisher Kozhabay on 3/23/2016.
 */
public class MapAdminActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private HashMap<Marker, MyMarker> mMarkersHashMap;
    private ArrayList<MyMarker> mMyMarkersArray = new ArrayList<MyMarker>();
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_map);
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 1 minute, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();

        getPerformers();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            // Blank for a moment...
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
        } else {
            handleNewLocation(mLastLocation);
        }
    }

    private void handleNewLocation(Location mLastLocation) {
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    private void getPerformers() {
        mMarkersHashMap = new HashMap<Marker, MyMarker>();
        ParseQuery<ParseObject> mainQuery = ParseQuery.getQuery("Achievement");
        mainQuery.whereGreaterThan("performerRating", 0);
        mainQuery.orderByDescending("performerRating");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (final ParseObject p : objects) {
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("objectId", p.getString("userId"));
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list, ParseException e) {
                                if (e == null) {
                                    for (ParseUser o : list) {
                                        final double rating = p.getDouble("performerRating");
                                        String fName = o.getString("firstName");
                                        String lName = o.getString("lastName");
                                        double latitude = o.getParseGeoPoint("lat").getLatitude();
                                        double longtitude = o.getParseGeoPoint("lat").getLongitude();
                                        String email = o.getUsername();
                                        ParseFile file = o.getParseFile("photo");
                                        try {
                                            photo = BitmapFactory.decodeByteArray(file.getData(), 0, file.getData().length);
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                        mMyMarkersArray.add(new MyMarker(fName, lName, (float) rating, latitude, longtitude, email, photo));
                                        setUpMap();
                                        plotMarkers(mMyMarkersArray);
                                    }
                                } else {
                                    Toast.makeText(MapAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUpMap() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
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

    private Bitmap writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Roboto", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20);

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

//        //If the text is bigger than the canvas , reduce the font size
//        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
//            paint.setTextSize(convertToPixels(this, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)- 20);

        canvas.drawText(text, xPos, yPos, paint);

        return bm;
    }

    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f);

    }

    private void plotMarkers(ArrayList<MyMarker> markers) {
        if (markers.size() > 0) {
            for (MyMarker myMarker : markers) {

                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getmLatitude(), myMarker.getmLongitude()));
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.marker, myMarker.getMfName())));

                Marker currentMarker = map.addMarker(markerOption);
                mMarkersHashMap.put(currentMarker, myMarker);
                map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            }
        }
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public MarkerInfoWindowAdapter() {
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = getLayoutInflater().inflate(R.layout.infomarker_admin, null);

            final MyMarker myMarker = mMarkersHashMap.get(marker);

            ImageView markerIcon = (ImageView) v.findViewById(R.id.admin_image);
            TextView markerLabel = (TextView) v.findViewById(R.id.admin_title);
            RatingBar ratingbar = (RatingBar) v.findViewById(R.id.admin_rating);
            TextView email = (TextView) v.findViewById(R.id.admin_email);
            ImageView emailImage = (ImageView) v.findViewById(R.id.admin_click_email);
            email.setText(myMarker.getEmail());
            markerIcon.setImageBitmap(myMarker.getImg());
            ratingbar.setRating(myMarker.getRating());
            markerLabel.setText(myMarker.getMfName() + " " + myMarker.getMlName());

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.d("SLICK", "CLICK");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("plain/text");
                    shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{myMarker.getEmail()});
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "XLancer");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(Intent.createChooser(shareIntent, "Sharing something."));
                }
            });

            emailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("SLICK", "CLICK2");

                }
            });

            return v;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}