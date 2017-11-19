package com.example.yaohuasun.friendnavigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.yaohuasun.friendnavigation.Listeners.FriendMapLocationListener;
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
import com.example.yaohuasun.friendnavigation.Models.MeetLocationModel;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location lastlocation;
    private LocationRequest locationRequest;
    private Marker currentLocationmMarker;
    private Marker otherPartyLocationMarker;
    double latitude,longitude;

    private ValueEventListener mMeetLocationsRefListener;
    private DatabaseReference mMeetLocationsReference;
    private String mChatId;
    private String mIsCallingActivityInitiator;

    private FirebaseDatabase mFirebaseDatabase;

    private MeetLocationModel mCurrentFriendsLocation;


    // first task, display current location on map
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = this.getIntent();

        // it is ok to use extras here because the request activity is a transient acivity (cannot go back to it)
        // TODO: implement back arrow to the chat activity.
        mChatId = intent.getStringExtra("ChatId");
        mIsCallingActivityInitiator = intent.getStringExtra("isInitiator");

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mMeetLocationsReference = mFirebaseDatabase.getReference().child("BasicChat").child(mChatId).child("MeetLocation");
        //add a listener for the location for basic chat, if changed, then update map markers
        mMeetLocationsReference.addValueEventListener( new FriendMapLocationListener(otherPartyLocationMarker,
                mCurrentFriendsLocation,
                mIsCallingActivityInitiator,
                mMap
        ));


        //basic chat needs new location
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
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            Log.i("Yao1019","location fine permission granted");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
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
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.i("Yao1015","in onLocationChanged lat = "+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // TODO: fix the camera problem
        // TODO: save marker options to global, just update its position everytime

        //mMap.animateCamera(CameraUpdateFactory.zoomBy(5));

        //next step, save the latlang into the realtime db, and display both friends addr there

        // initiatiorLatitude, initiatorLongitude or
        // responderLatitude, responderLongitude

        if(mIsCallingActivityInitiator.equals("true")){
            mMeetLocationsReference.child("InitiatorLatitude").setValue(Double.toString(latitude));
            mMeetLocationsReference.child("InitiatorLongitude").setValue(Double.toString(longitude));
        }
        else
        {
            mMeetLocationsReference.child("ResponderLatitude").setValue(Double.toString(latitude));
            mMeetLocationsReference.child("ResponderLongitude").setValue(Double.toString(longitude));
        }

        if(null != mGoogleApiClient){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    protected synchronized void bulidGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    // TODO: in onDestroy or onStop, set mReceivingMeetRequest = user.getReceivingMapRequest(); to false
}
