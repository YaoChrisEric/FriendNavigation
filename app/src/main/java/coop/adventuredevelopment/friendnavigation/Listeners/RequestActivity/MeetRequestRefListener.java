package coop.adventuredevelopment.friendnavigation.Listeners.RequestActivity;

import android.util.Log;

import coop.adventuredevelopment.friendnavigation.Models.MeetRequestModel;
import coop.adventuredevelopment.friendnavigation.RequestActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by unger on 11/19/2017.
 */

public class MeetRequestRefListener implements ValueEventListener {

    private MeetRequestModel mCurrentMeetRequest;
    private RequestActivity mRequestActivity;

    public MeetRequestRefListener (RequestActivity RequestActivity) {
        mRequestActivity = RequestActivity;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists()) {

            Log.i("position09191", "in reqActivity, dataSnapShot is " + dataSnapshot.toString());

            mCurrentMeetRequest = dataSnapshot.getValue(MeetRequestModel.class);
            String initiatorEmail = mCurrentMeetRequest.getInitiatorEmailAddr();
            String initiatorState = mCurrentMeetRequest.getInitiatorState();
            String responderEmail = mCurrentMeetRequest.getResponderEmailaddr();
            String responderState = mCurrentMeetRequest.getResponderState();

            if (!responderState.equals("true")) {
                return;
            }

            mRequestActivity.navigateToMapActivity();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
