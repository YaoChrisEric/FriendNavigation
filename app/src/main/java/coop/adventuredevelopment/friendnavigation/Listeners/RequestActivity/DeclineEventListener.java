package coop.adventuredevelopment.friendnavigation.Listeners.RequestActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import coop.adventuredevelopment.friendnavigation.Models.MeetRequestModel;
import coop.adventuredevelopment.friendnavigation.Models.UserModel;
import coop.adventuredevelopment.friendnavigation.RequestActivity;
import coop.adventuredevelopment.friendnavigation.Utils.FNUtil;

/**
 * Created by unger on 12/17/2017.
 * Listens to MeetRequest to see if the partner's decline button was pressed.
 */

public class DeclineEventListener implements ValueEventListener{
    private RequestActivity mRequestActivity;
    private String mCurrentUserEmail;

    public DeclineEventListener(RequestActivity requestActivity, String currentUserEmail){
        mRequestActivity = requestActivity;
        mCurrentUserEmail = currentUserEmail;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

        MeetRequestModel meetRequestModel = dataSnapshot.getValue(MeetRequestModel.class);
        if ((meetRequestModel.getInitiatorEmailAddr().isEmpty())&&(meetRequestModel.getResponderEmailaddr().isEmpty())){
            mRequestActivity.navigateToFriendListActivity();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
