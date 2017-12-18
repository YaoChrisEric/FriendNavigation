package coop.adventuredevelopment.friendnavigation.Listeners;

import android.support.v4.app.NavUtils;
import android.util.Log;

import coop.adventuredevelopment.friendnavigation.MapsActivity;
import coop.adventuredevelopment.friendnavigation.Models.MeetLocationModel;
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
    private boolean mIsCallingActivityInitiator;

    private MapsActivity mMapsActivity;

    public FriendMapLocationListener(
            boolean isCallingActivityInitiator,
            MapsActivity mapsActivity
    ){
        mIsCallingActivityInitiator = isCallingActivityInitiator;
        mMapsActivity = mapsActivity;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(!dataSnapshot.exists()){
            return;
        }

        MeetLocationModel currentFriendsLocation = dataSnapshot.getValue(MeetLocationModel.class);

        if(!currentFriendsLocation.getResponderLatitude().equals("500")) {
            LatLng latLng = CreateOtherPartyLocationPoint(currentFriendsLocation);
            mMapsActivity.updateOtherUserLocation(latLng);
        }
        else {
            // we end the navigation and go to parent activity
            mMapsActivity.endFriendNavigationAndNavigateToChatActivity();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private LatLng CreateOtherPartyLocationPoint(MeetLocationModel locationModel) {
        if (mIsCallingActivityInitiator){
            double otherPartyLatitude = Double.parseDouble(locationModel.getResponderLatitude());
            double otherPartyLongitude = Double.parseDouble(locationModel.getResponderLongitude());
            return new LatLng(otherPartyLatitude,otherPartyLongitude);
        }
        else{
            // we are responder, only need to create a marker for initiator
            double otherPartyLatitude = Double.parseDouble(locationModel.getInitiatorLatitude());
            double otherPartyLongitude = Double.parseDouble(locationModel.getInitiatorLongitude());
            return new LatLng(otherPartyLatitude,otherPartyLongitude);
        }
    }
}
