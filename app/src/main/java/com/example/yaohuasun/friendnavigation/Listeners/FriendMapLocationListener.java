package com.example.yaohuasun.friendnavigation.Listeners;

import android.support.v4.app.NavUtils;
import android.util.Log;

import com.example.yaohuasun.friendnavigation.MapsActivity;
import com.example.yaohuasun.friendnavigation.Models.MeetLocationModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by yaohuasun on 11/19/17.
 */

public class FriendMapLocationListener implements ValueEventListener{
    //private Marker currentLocationmMarker;
    private Marker otherPartyLocationMarker;
    private MeetLocationModel mCurrentFriendsLocation;
    private String mIsCallingActivityInitiator;
    private GoogleMap mMap;
    private MapsActivity mMapsActivity;
    private FirebaseDatabase mFirebaseDatabase;
    private String mCurrentChatId;
    private DatabaseReference mMeetRequestReference;

    public FriendMapLocationListener(
            Marker friendLocationMarker,
            MeetLocationModel currentFriendsLocation,
            String isCallingActivityInitiator,
            GoogleMap map,
            MapsActivity mapsActivity,
            FirebaseDatabase firebaseDatabase,
            String chatId
    ){
        otherPartyLocationMarker = friendLocationMarker;
        mCurrentFriendsLocation = currentFriendsLocation;
        mIsCallingActivityInitiator = isCallingActivityInitiator;
        mMap = map;
        mMapsActivity = mapsActivity;
        mFirebaseDatabase = firebaseDatabase;
        mCurrentChatId = chatId;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists()){
            Log.i("position1023001", "in MapsActivity, dataSnapShot is " + dataSnapshot.toString());
            // we had some problem retrieving the whole MeetRequestModel, so we use another method
            // to get the mCurrentMeetRequest instead

            boolean shouldEndNavigation = false;
            if(otherPartyLocationMarker != null)
            {
                otherPartyLocationMarker.remove();
            }

            mCurrentFriendsLocation = dataSnapshot.getValue(MeetLocationModel.class);
            String initiatorLatitude = mCurrentFriendsLocation.getInitiatorLatitude();
            String initiatorLongitude = mCurrentFriendsLocation.getInitiatorLongitude();
            String responderLatitude = mCurrentFriendsLocation.getResponderLatitude();
            String responderLongitude = mCurrentFriendsLocation.getResponderLongitude();
            Log.i("position1023002", "in MapsActivity, initiatorLatitude is "+ initiatorLatitude+", initiatorLongitude is "+
                    initiatorLongitude+ ", responderLongitude is "+responderLongitude + ",responder latitude is "+responderLatitude);

            LatLng latLng;
            double otherPartyLatitude;
            double otherPartyLongitude;
            if (mIsCallingActivityInitiator.equals("true")){

                if(responderLatitude.equals("500"))
                {
                    //TODO:ensure responder longitude is also 500

                    // set end nav flag to be true
                    shouldEndNavigation = true;
                }

                // we are initiator, only need to create a marker for responder
                otherPartyLatitude = Double.parseDouble(responderLatitude);
                otherPartyLongitude = Double.parseDouble(responderLongitude);
                latLng = new LatLng(otherPartyLatitude,otherPartyLongitude);
            }
            else{
                if(initiatorLatitude.equals("500"))
                {
                    //TODO:ensure initiator longitude is also 500
                    // set end nav flag to be true
                    shouldEndNavigation = true;
                }

                // we are responder, only need to create a marker for initiator
                otherPartyLatitude = Double.parseDouble(initiatorLatitude);
                otherPartyLongitude = Double.parseDouble(initiatorLongitude);
                latLng = new LatLng(otherPartyLatitude,otherPartyLongitude);
            }
            if(!shouldEndNavigation) {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("other Party Location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));



            otherPartyLocationMarker = mMap.addMarker(markerOptions);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            else {
                // we end the navigation and go to parent activity
                mMeetRequestReference = mFirebaseDatabase.getReference().child("BasicChat").child(mCurrentChatId).child("meetRequest");
                mMeetRequestReference.child("initiatorState").setValue("false");
                mMeetRequestReference.child("initiatorEmailAddr").setValue("");
                mMeetRequestReference.child("responderEmailAddr").setValue("");
                mMeetRequestReference.child("responderState").setValue("false");
                NavUtils.navigateUpFromSameTask(mMapsActivity);
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
