package com.yaosun.friendnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yaosun.friendnavigation.Models.BasicChatModel;

public class ChatActivity extends AppCompatActivity {

    private String basicChatFriend;

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mBasicChatDatabaseRef;
    private String mSearchChatIdResult;

    private String mChatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mBasicChatDatabaseRef = mFirebaseDatabase.getReference().child("BasicChat");

        mSearchChatIdResult = null;

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        final String currentUserEmail2 = mFirebaseAuth.getCurrentUser().getEmail().trim();

        if (null != mBasicChatDatabaseRef) {
            Intent intent = this.getIntent();
            // TODO: make "friendEmailAddr" into a CONSTANT
            basicChatFriend = intent.getStringExtra("friendEmailAddr");

            // TODO: do a sanity check on basicChatFriend



            mBasicChatDatabaseRef.orderByChild("User1EmailAddr").equalTo(basicChatFriend).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (null != dataSnapshot.getValue())
                    {
                        Log.i("position01", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());
                        String basicChatKey = dataSnapshot.getKey();
                        Log.i("position02", "in OnDataChange, basicChatKey is" + basicChatKey);
                        BasicChatModel basicChat = dataSnapshot.child(basicChatKey).getValue(BasicChatModel.class);
                        if (null != basicChat) {
                            Log.i("position03", "found basic chat is " + basicChat.toString());
                            if (currentUserEmail2 == basicChat.getUser2EmailAddr()) {
                                // we found a match
                                Log.i("position04", "found email2 " + currentUserEmail2);
                                mSearchChatIdResult = basicChat.getChatId();
                            }
                        }
                        else
                        {
                            //debug, TODO: remove this if-else and use try catch
                            Log.i("position05", "basicChat is null");
                        }
                    }
                    // else do nothing, search for user2email below
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (null == mSearchChatIdResult)
            {
                // we didn't find the chat with user1email as the key
                // we need to do this because the chat might have been created by us previously already

                mBasicChatDatabaseRef.orderByChild("User2EmailAddr").equalTo(basicChatFriend).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (null != dataSnapshot.getValue())
                        {
                            Log.i("position06", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());
                            String basicChatKey = dataSnapshot.getKey();
                            Log.i("position07", "in OnDataChange, basicChatKey is" + basicChatKey);
                            BasicChatModel basicChat1 = dataSnapshot.child(basicChatKey).getValue(BasicChatModel.class);
                            if (null != basicChat1) {
                                Log.i("position08", "found basic chat is " + basicChat1.toString());
                                if (currentUserEmail2 == basicChat1.getUser1EmailAddr()) {
                                    // we found a match
                                    Log.i("position09", "found email1 " + currentUserEmail2);
                                    mSearchChatIdResult = basicChat1.getChatId();
                                }
                            }
                            else
                            {
                                //debug, TODO: remove this if-else and use try catch
                                Log.i("position09", "basicChat1 is null");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        } // if null != basicchatref

        if (null == mSearchChatIdResult)
        {
            // we didn't find it, we create a chat
            mChatId = mBasicChatDatabaseRef.push().getKey();
            mBasicChatDatabaseRef.child(mChatId).child("User1EmailAddr").setValue(currentUserEmail2);
            mBasicChatDatabaseRef.child(mChatId).child("User2EmailAddr").setValue(basicChatFriend);
            mBasicChatDatabaseRef.child(mChatId).child("chatId").setValue(mChatId);

        }
        else
        {
            mChatId = mSearchChatIdResult;
        }

        // TODO:high, create a message adapter attaching to the listview
        // TODO: high attach database listener, onchildadded will add a message to the adapter




        // TODO: high, else, create the chat (similar as what needs to be done when not finding the chat(consider API)



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
