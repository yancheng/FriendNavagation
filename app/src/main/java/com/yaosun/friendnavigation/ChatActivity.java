package com.yaosun.friendnavigation;

import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yaosun.friendnavigation.Models.BasicChatModel;
import com.yaosun.friendnavigation.Models.MessageModel;
import com.yaosun.friendnavigation.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatActivity extends AppCompatActivity {

    private String basicChatFriend;

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mBasicChatDatabaseRef;

    // for example root/BasicChat/ChatId/MessageIds
    private DatabaseReference mMessageDataBaseReference;

    private ListView mMessageList;

    private FirebaseListAdapter<MessageModel> mMessageListAdapter;

    // the text in the edittext view to be sent to chat
    private TextView mMessageField;

    // mSearchChatIdResult might have some duplication with mChatId, TODO: revisit and combine
    private String mSearchChatIdResult;

    private String mChatId;

    private String mCurrentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mBasicChatDatabaseRef = mFirebaseDatabase.getReference().child("BasicChat");
        mMessageList = (ListView) findViewById(R.id.messageListView);
        mSearchChatIdResult = null;

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserEmail = mFirebaseAuth.getCurrentUser().getEmail().trim();

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
                            if (mCurrentUserEmail == basicChat.getUser2EmailAddr()) {
                                // we found a match
                                Log.i("position04", "found email2 " + mCurrentUserEmail);
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
                                if (mCurrentUserEmail == basicChat1.getUser1EmailAddr()) {
                                    // we found a match
                                    Log.i("position09", "found email1 " + mCurrentUserEmail);
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
            mBasicChatDatabaseRef.child(mChatId).child("User1EmailAddr").setValue(mCurrentUserEmail);
            mBasicChatDatabaseRef.child(mChatId).child("User2EmailAddr").setValue(basicChatFriend);
            mBasicChatDatabaseRef.child(mChatId).child("chatId").setValue(mChatId);

        }
        else
        {
            mChatId = mSearchChatIdResult;
        }
        mMessageDataBaseReference = mBasicChatDatabaseRef.child(mChatId).child(Constants.BASIC_CHAT_MESSAGE_IDS);
        // TODO:high, create a message adapter attaching to the listview; after done, create showMessages() to display
        // TODO: high attach database listener, onchildadded will add a message to the adapter
        mMessageListAdapter = new FirebaseListAdapter<MessageModel>(this, MessageModel.class,R.layout.message_item, mMessageDataBaseReference){
            @Override
            protected void populateView(View view, MessageModel model, int position) {
                LinearLayout messageLine = (LinearLayout) view.findViewById(R.id.messageLine);
                TextView messgaeText = (TextView) view.findViewById(R.id.messageTextView);
                TextView senderText = (TextView) view.findViewById(R.id.senderTextView);
                TextView timeText = (TextView) view.findViewById(R.id.timestampTextView);
                LinearLayout individMessageLayout = (LinearLayout)view.findViewById(R.id.individMessageLayout);

                messgaeText.setText(model.getMessage());
                senderText.setText(model.getSenderEmail());
                timeText.setText(model.getTimestamp());

                String senderEmail = model.getSenderEmail();
                if (mCurrentUserEmail == senderEmail)
                {
                    // move message to the right
                    messageLine.setGravity(Gravity.RIGHT);
                }else {
                    messageLine.setGravity(Gravity.LEFT);
                }
                // TODO: add image icons, image, voice; system message handling

            }
        };

        mMessageList.setAdapter(mMessageListAdapter);
        // TODO: move above into displaymsgs API


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

    public void sendMessage(View view){
        mMessageField = (TextView)findViewById(R.id.messageToSend);
        final DatabaseReference pushRef = mMessageDataBaseReference.push();
        final String pushKey = pushRef.getKey();

        String messageString = mMessageField.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);

        MessageModel message = new MessageModel(mCurrentUserEmail,messageString,timestamp);
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);
        mMessageDataBaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mMessageField.setText("");
                    }
                });

    }

}
