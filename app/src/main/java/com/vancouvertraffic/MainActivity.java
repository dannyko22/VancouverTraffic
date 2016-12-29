package com.vancouvertraffic;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    DatabaseHelper myDbHelper;
    ArrayList<CamData> camList;
    private UiSettings mapUISetting;
    Hashtable<String, Integer> markers;
    public ImageView urlImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!isNetworkAvailable()) {
            showAlertDialog();
        }

        camList = new ArrayList<CamData>();

        myDbHelper = new DatabaseHelper(this);
        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();

        } catch (SQLException sqle) {

            throw sqle;
        }

        camList = myDbHelper.getCamData();

        markers = new Hashtable<String, Integer>();


        setUpMapIfNeeded();



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showAlertDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Sorry... This app requires an Internet connection. :(");
        alertDialogBuilder.setNeutralButton("Exit the app", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        if (mMap != null)
        {
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
            mMap.setOnInfoWindowClickListener(this);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(final Marker mark) {
                    mark.showInfoWindow();
                    return true;


                }
            });
        }

        setUpMap();
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        int count = camList.size();
        String name;
        double latitude;
        double longitude;
        String species;
        LatLng locationLatLngSetup;
        boolean outside_california = false;
        CameraPosition cameraPosition;

        mapUISetting = mMap.getUiSettings();
        mapUISetting.setMyLocationButtonEnabled(true);
        mapUISetting.setTiltGesturesEnabled(false);
        mapUISetting.setRotateGesturesEnabled(false);
        mapUISetting.setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location myLocation = locationManager.getLastKnownLocation(provider);

        if (myLocation != null) {
            if (myLocation.getLatitude() > 49.2 && myLocation.getLatitude() < 49.3 && myLocation.getLongitude() > -123.1 && myLocation.getLongitude() < -122.7) {
                // your current location is within Vancouver
                locationLatLngSetup = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                outside_california = false;
            } else {
                // your current location is outside Vancouver
                locationLatLngSetup = new LatLng(49.277147, -123.124342);
                outside_california = true;
            }
        }
        else
        {
            // your current location is outside california
            locationLatLngSetup = new LatLng(35.948698, -120.30248);
            outside_california = true;
        }

        if (outside_california)
        {
            cameraPosition = new CameraPosition.Builder()
                    .target(locationLatLngSetup) // Sets the center of the map
                    .zoom(6)                   // Sets the zoom
                    .bearing(0) // Sets the orientation of the camera to north
                    .tilt(0)    // Sets the tilt of the camera to 0 degrees
                    .build();    // Creates a CameraPosition from the builder
        }
        else {
            cameraPosition = new CameraPosition.Builder()
                    .target(locationLatLngSetup) // Sets the center of the map
                    .zoom(8)                   // Sets the zoom
                    .bearing(0) // Sets the orientation of the camera to north
                    .tilt(0)    // Sets the tilt of the camera to 0 degrees
                    .build();    // Creates a CameraPosition from the builder
        }

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                cameraPosition));

        while (count != 0) {
            name = camList.get(count-1).getName();
            latitude = Double.parseDouble(camList.get(count-1).getLatitude());
            longitude = Double.parseDouble(camList.get(count-1).getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(name);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
            Marker marker = mMap.addMarker(markerOptions);
            markers.put(marker.getId(), count-1);
            count--;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String url = camList.get(markers.get(marker.getId())).getURL();
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;
        Context mContext;
        Marker previousMarker = null;
        Marker currentMarker = null;
        boolean not_first_time_showing_info_window = false;
        Callback callback = null;

        public CustomInfoWindowAdapter(Context context) {
            view = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContext = context;
        }

        @Override
        public View getInfoContents(Marker marker) {

            previousMarker = currentMarker;
            currentMarker = marker;

            final String title = marker.getTitle();
            final TextView titleUi = ((TextView) view.findViewById(R.id.title));
            // Loader image - will be shown before loading image

            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            final String url = camList.get(markers.get(marker.getId())).getURL();
            urlImageView = ((ImageView) view.findViewById(R.id.trafficimage));


            if (not_first_time_showing_info_window==false & previousMarker==null) {
                // starting up app.  Enter this first if statement on first pass.
                not_first_time_showing_info_window = true;
                callback = new InfoWindowRefresher(marker);
                Picasso.with(MainActivity.this).load(url).into(urlImageView, callback);
            } else if (not_first_time_showing_info_window==true & previousMarker.getId().toString().compareTo(currentMarker.getId().toString()) != 0)
            {
                // enter this if statement when the user quickly clicks on another marker.
                // the user clicks on the first marker and then the url gets fetched.  but in the meantime, the user simultaneously clicks on another marker.
                // by clicking on another marker, the user stops the callback of the previous marker fetch.  so we need to re-initiate/re-start all the fetches.
                // so this is what is needed to quickly go to the next marker and get the url image and properly refreshes the info window.
                not_first_time_showing_info_window=true;
                callback = new InfoWindowRefresher(marker);
                Picasso.with(MainActivity.this).load(url).into(urlImageView,callback);
            } else if (not_first_time_showing_info_window==true) {
                // if the marker is clicked, and the user hasn't clicked on another marker, this is the default statement to go into.
                Picasso.with(MainActivity.this).load(url).into(urlImageView);
            } else if (not_first_time_showing_info_window==false & previousMarker.getId().toString().compareTo(currentMarker.getId().toString()) != 0) {
                // if the user clicks on a marker, and the url from the previous click was fetched properly, and callback was completed
                // and now the user wants to click on another marker, call this function.
                not_first_time_showing_info_window=true;
                callback = new InfoWindowRefresher(marker);
                Picasso.with(MainActivity.this).load(url).into(urlImageView,callback);
            }

            return view;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            return null;
        }

        //call back needed as the info window needs to be refreshed twice.
        private class InfoWindowRefresher implements com.squareup.picasso.Callback {
            private Marker markerToRefresh;

            private InfoWindowRefresher(Marker markerToRefresh) {
                this.markerToRefresh = markerToRefresh;
            }



            @Override
            public void onSuccess() {
                if (markerToRefresh != null  && markerToRefresh.isInfoWindowShown())
                {
                    markerToRefresh.showInfoWindow();
                    not_first_time_showing_info_window=false;
                    callback = null;
                }
            }

            @Override
            public void onError() {
            }
        }
    }
}