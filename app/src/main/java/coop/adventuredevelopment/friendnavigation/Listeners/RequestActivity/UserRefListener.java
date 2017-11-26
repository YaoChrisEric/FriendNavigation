package coop.adventuredevelopment.friendnavigation.Listeners.RequestActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import coop.adventuredevelopment.friendnavigation.Models.UserModel;
import coop.adventuredevelopment.friendnavigation.Utils.FNUtil;
import coop.adventuredevelopment.friendnavigation.RequestActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by unger on 11/19/2017.
 */

public class UserRefListener implements ValueEventListener{

    private FirebaseDatabase mFirebaseDatabase;
    private RequestActivity mRequestActivity;
    private String mCurrentUserEmail;

    public UserRefListener(FirebaseDatabase firebaseDatabase, RequestActivity RequestActivity, String currentUserEmail){
        mFirebaseDatabase = firebaseDatabase;
        mRequestActivity = RequestActivity;
        mCurrentUserEmail = currentUserEmail;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() == null) {
            return;
        }

        Log.i("position1010", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());

        UserModel user = dataSnapshot.child(FNUtil.encodeEmail(mCurrentUserEmail)).getValue(UserModel.class);
        if (user == null) {
            return;
        }

        String basicChatFriend = user.getCurrentChatFriend();
        Log.i("position1002", "basicChatFriend is" + basicChatFriend);
        String chatId = FNUtil.generateIDWithTwoEmails(mCurrentUserEmail, basicChatFriend);
        DatabaseReference basicChatRef = mFirebaseDatabase.getReference().child("BasicChat").child(chatId);
        DatabaseReference meetRequestReference = basicChatRef.child("meetRequest");
        mRequestActivity.updateMeetRequestReference(user, meetRequestReference, basicChatFriend, chatId);

        String receivingMeetRequest = user.getReceivingMapRequest();

        if (receivingMeetRequest.equals("false")) {
            mRequestActivity.SetupAsInitiator();
        } else {
            mRequestActivity.SetupAsReceiver();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}
