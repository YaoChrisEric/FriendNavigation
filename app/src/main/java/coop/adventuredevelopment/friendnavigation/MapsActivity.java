package coop.adventuredevelopment.friendnavigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import coop.adventuredevelopment.friendnavigation.Listeners.FriendMapLocationListener;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import coop.adventuredevelopment.friendnavigation.Models.MeetLocationModel;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker mCurrentLocationmMarker;
    private Marker mOtherPartyLocationMarker;
    private DatabaseReference mMeetLocationsReference;
    private String mChatId;
    private boolean mIsCallingActivityInitiator;
    private FirebaseDatabase mFirebaseDatabase;
    private boolean mEndNavigation;
    private DatabaseReference mMeetRequestReference;

    public static final int REQUEST_LOCATION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = this.getIntent();

        mChatId = intent.getStringExtra("ChatId");
        mIsCallingActivityInitiator = intent.getBooleanExtra("isInitiator", false);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mMeetLocationsReference = mFirebaseDatabase.getReference().child("BasicChat").child(mChatId).child("MeetLocation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEndNavigation = false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endNavigationAndMarkAsEnded();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMeetLocationsReference.addValueEventListener( new FriendMapLocationListener(
                mIsCallingActivityInitiator,
                this
        ));

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            Log.i("Yao1019","location fine permission granted");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else
        {
            Log.i("Yao1019","permission denied");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (mEndNavigation) {
            return;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        updateCurrentUserMapMarker(new LatLng(latitude, longitude));

        if(mIsCallingActivityInitiator){
            mMeetLocationsReference.child("InitiatorLatitude").setValue(Double.toString(latitude));
            mMeetLocationsReference.child("InitiatorLongitude").setValue(Double.toString(longitude));
        }
        else
        {
            mMeetLocationsReference.child("ResponderLatitude").setValue(Double.toString(latitude));
            mMeetLocationsReference.child("ResponderLongitude").setValue(Double.toString(longitude));
        }
    }


    protected synchronized void bulidGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    public boolean checkLocationPermission()
    {
        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // respond to up/home button to go back to parent activity
            case android.R.id.home:
                endNavigationAndMarkAsEnded();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void endFriendNavigationAndNavigateToChatActivity() {
        mMeetRequestReference = mFirebaseDatabase.getReference().child("BasicChat").child(mChatId).child("meetRequest");
        mMeetRequestReference.child("initiatorState").setValue("false");
        mMeetRequestReference.child("initiatorEmailAddr").setValue("");
        mMeetRequestReference.child("responderEmailAddr").setValue("");
        mMeetRequestReference.child("responderState").setValue("false");

        NavUtils.navigateUpFromSameTask(this);
    }

    public void updateOtherUserLocation(LatLng location) {
        if (mOtherPartyLocationMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Other Party Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            markerOptions.position(location);
            mOtherPartyLocationMarker = mMap.addMarker(markerOptions);
        }
        else {
            mOtherPartyLocationMarker.setPosition(location);
        }
    }

    private void endNavigationAndMarkAsEnded() {
        mEndNavigation = true;
        if(mIsCallingActivityInitiator){
            mMeetLocationsReference.child("InitiatorLatitude").setValue("500");
            mMeetLocationsReference.child("InitiatorLongitude").setValue("500");
        }
        else
        {
            mMeetLocationsReference.child("ResponderLatitude").setValue("500");
            mMeetLocationsReference.child("ResponderLongitude").setValue("500");
        }
        endFriendNavigationAndNavigateToChatActivity();
    }

    private void updateCurrentUserMapMarker(LatLng location) {
        if (mCurrentLocationmMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Current Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.position(location);
            mCurrentLocationmMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
        else {
            mCurrentLocationmMarker.setPosition(location);
        }
    }
}
