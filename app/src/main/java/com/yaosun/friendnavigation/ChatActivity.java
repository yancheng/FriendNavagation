package com.yaosun.friendnavigation;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.yaosun.friendnavigation.Models.MeetRequestModel;
import com.yaosun.friendnavigation.Models.MessageModel;
import com.yaosun.friendnavigation.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaosun.friendnavigation.Utils.FNUtil;

public class ChatActivity extends AppCompatActivity {

    private String basicChatFriend;

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mBasicChatDatabaseRef;

    // for example root/BasicChat/ChatId/MessageIds
    private DatabaseReference mMessageDataBaseReference;

    private DatabaseReference mMeetRequestMessageRef;
    private ValueEventListener mMeetRequestRefListener;


    private ListView mMessageList;

    private FirebaseListAdapter<MessageModel> mMessageListAdapter;

    // the text in the edittext view to be sent to chat
    private TextView mMessageField;

    // mSearchChatIdResult might have some duplication with mChatId, TODO: revisit and combine
    private String mSearchChatIdResult;

    private String mChatId;

    private String mCurrentUserEmail;

    //private ChildEventListener mNavigationRefListener;

    /*private DatabaseReference mCurrentInitiatorEmailAddrRef;
    private ValueEventListener mInitiatorEmailListener;

    private DatabaseReference mCurrentInitiatorStateRef;
    private ValueEventListener mInitiatorStateListener;

    private DatabaseReference mCurrentResponderEmailAddrRef;
    private ValueEventListener mResponderEmailListener;

    private DatabaseReference mCurrentResponderStateRef;
    private ValueEventListener mResponderStateListener;*/



    private MeetRequestModel mCurrentMeetRequest;

    //private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mBasicChatDatabaseRef = mFirebaseDatabase.getReference().child("BasicChat");
        mMessageList = (ListView) findViewById(R.id.messageListView);


        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserEmail = mFirebaseAuth.getCurrentUser().getEmail().trim();
        mSearchChatIdResult = null;
        //mNavigationRefListener = null;

        Intent intent = this.getIntent();

        //instead of getting the basicchatfriend from extra, we will get it from shared preferences



        //basicChatFriend = intent.getStringExtra("friendEmailAddr");

        SharedPreferences friendPref = getSharedPreferences("friendEmailAddr",MODE_PRIVATE);
        basicChatFriend = friendPref.getString("friendEmailAddr","defaultValue");


        mChatId = FNUtil.generateIDWithTwoEmails(mCurrentUserEmail, basicChatFriend );

        mBasicChatDatabaseRef.orderByChild("User2EmailAddr").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot.getValue()){
                    // some chat is already there
                    Log.i("position06", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());
                    mSearchChatIdResult = mChatId;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (null == mSearchChatIdResult) {
            // we didn't find it, we create a chat; TODO: use constants to replace child names here
            mBasicChatDatabaseRef.child(mChatId).child("User1EmailAddr").setValue(mCurrentUserEmail);
            mBasicChatDatabaseRef.child(mChatId).child("User2EmailAddr").setValue(basicChatFriend);
            mBasicChatDatabaseRef.child(mChatId).child("chatId").setValue(mChatId);

            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("initiatorEmailAddr").setValue("");
            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("responderEmailAddr").setValue("");

            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("initiatorState").setValue("false");
            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("responderState").setValue("false");

        }
        // initialize
        mMeetRequestRefListener = null;

       // mCurrentInitiatorEmailAddrRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("initiatorEmailAddr");
       // mCurrentInitiatorStateRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("initiatorState");
       // mCurrentResponderEmailAddrRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("responderEmailAddr");
       // mCurrentResponderStateRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("responderState");

        //mCurrentInitiatorEmailAddrRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest");
        //mCurrentInitiatorStateRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest");
        //mCurrentResponderEmailAddrRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest");
        //mCurrentResponderStateRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest");
        // TODO: verify the  mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child(FNUtil.encodeEmail(basicChatFriend)).
        // already exists, if not, maybe the other user is creating it and we hit a race condition, we just wait until it exists
        // one could do so by add a event listener to mBasicChatDatabaseRef.child(mChatId).child("meetRequest"), and set a value for
        // a while loop on this boolean flag before continuing.

        // attach listener to it to start request activity; if not exists, panic, log.

        mCurrentMeetRequest = new MeetRequestModel();

        mMessageDataBaseReference = mBasicChatDatabaseRef.child(mChatId).child(Constants.BASIC_CHAT_MESSAGE_IDS);
        // TODO:high, create a message adapter attaching to the listview; after done, create showMessages() to display
        // TODO: high attach database listener, onchildadded will add a message to the adapter
        mMessageListAdapter = new FirebaseListAdapter<MessageModel>(this, MessageModel.class, R.layout.message_item, mMessageDataBaseReference) {
            @Override
            protected void populateView(View view, MessageModel model, int position) {
                LinearLayout messageLine = (LinearLayout) view.findViewById(R.id.messageLine);
                TextView messgaeText = (TextView) view.findViewById(R.id.messageTextView);
                TextView senderText = (TextView) view.findViewById(R.id.senderTextView);
                TextView timeText = (TextView) view.findViewById(R.id.timestampTextView);
                LinearLayout individMessageLayout = (LinearLayout) view.findViewById(R.id.individMessageLayout);
                Log.i("positionQ", "in pupulate view Value is" + model.toString());
                messgaeText.setText(model.getMessage());
                senderText.setText(model.getSenderEmail());
                timeText.setText(model.getTimestamp());

                String senderEmail = model.getSenderEmail();

                if (mCurrentUserEmail == senderEmail) {
                    // move message to the right
                    messageLine.setGravity(Gravity.RIGHT);
                } else {
                    messageLine.setGravity(Gravity.LEFT);
                }
                // TODO: add image icons, image, voice; system message handling

            }
        };
        mMessageList.setAdapter(mMessageListAdapter);
        mMeetRequestMessageRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest");
        attachNavigationRefListener();

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

    public void proposeNavigation(View view){
        String initiatorEmail = mCurrentMeetRequest.getInitiatorEmailAddr();
        String initiatorState = mCurrentMeetRequest.getInitiatorState();
        String responderEmail = mCurrentMeetRequest.getResponderEmailaddr();
        String responderState = mCurrentMeetRequest.getResponderState();
        Log.i("proposeNav","in attach listener, initiatorEmail is"+initiatorEmail
                +", initiatorState is "+initiatorState +
                ",responderEmail is "+responderEmail +
                ",responderState is "+responderState);
        detachNavigationRefListener();
        if (initiatorState.equals("false"))
        {
            if (responderState.equals("false"))
            {
                // set our request flag to true (means initiator has agreed)
                mMeetRequestMessageRef.child("initiatorState").setValue("true");
                mMeetRequestMessageRef.child("initiatorEmailAddr").setValue(mCurrentUserEmail);
                mMeetRequestMessageRef.child("responderEmailAddr").setValue(basicChatFriend);

                // start the request activity

                // passing in the fact that we are the initiator, chat id, currentUserEmail
                /*Intent intent = new Intent(view.getContext(),ChatActivity.class);
                // TODO: make a constant for string "friendEmailAddr"
                intent.putExtra("friendEmailAddr",friend.getFriendEmailAddr());
                startActivity(intent);*/
                Intent intent = new Intent(ChatActivity.this,requestActivity.class);
                intent.putExtra("ChatId",mChatId);
                intent.putExtra("isInitiator","true");
                // the requestActivity could figure out currentUserEmail

                startActivity(intent);
                // if we wait for a while and still haven't got frind to true, the request activity should come back and set flag to false
            }
            else
            {
                // friend might already initiated nav request, just wait and do nothing else

                Log.i("position911", "in proposeNavigation, maybe GUI is delaying receiving nav request from friend, wait" );
            }
        }
        else {
            Log.i("position912", "in proposeNavigation, something might be wrong" );
            // TODO: add a panic here to see what happened, maybe we forgot to disable back button in requestActivity
            Log.i("position9121","initiatorState is" + initiatorState);
            //Log.i("position9122","mMeetRequestMessageRef is" + mMeetRequestMessageRef.toString());

        }
        attachNavigationRefListener();

    }


    private void attachNavigationRefListener()
    {

        if(null == mMeetRequestRefListener)
        {
           mMeetRequestRefListener = mMeetRequestMessageRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   if(dataSnapshot.exists())
                   {
                       Log.i("position09171", "in attachNavRef, dataSnapShot is " + dataSnapshot.toString());

                       mCurrentMeetRequest = dataSnapshot.getValue(MeetRequestModel.class);
                       // correction: if the initiator is a friend user email and state is true, and responderstate is false
                       // then we open the requestActivity and pass in isInitiator = false
                       String initiatorEmail = mCurrentMeetRequest.getInitiatorEmailAddr();
                       String initiatorState = mCurrentMeetRequest.getInitiatorState();
                       String responderEmail = mCurrentMeetRequest.getResponderEmailaddr();
                       String responderState = mCurrentMeetRequest.getResponderState();

                       Log.i("ChatAct", "in attach listener, initiatorEmail is" + initiatorEmail
                               + ", initiatorState is " + initiatorState +
                               ",responderEmail is " + responderEmail +
                               ",responderState is " + responderState);

                       if (initiatorEmail.equals(basicChatFriend)) {
                           if (!initiatorState.equals("true") ||
                                   !responderEmail.equals(mCurrentUserEmail) ||
                                   !responderState.equals("false")) {
                               Log.i("ChatAct02", "unexpected string values, something is wrong");
                           }
                           Intent intent = new Intent(ChatActivity.this, requestActivity.class);
                           intent.putExtra("ChatId", mChatId);
                           intent.putExtra("isInitiator", "false");
                           // the requestActivity could figure out currentUserEmail

                           startActivity(intent);

                       } else {
                           // Debug
                           Log.i("ChatAct01", "initiatorEmail is not friend, something is wrong");
                       }
                   }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
        }
        // we attach value event listener to each member of the meetRequest
       /* mInitiatorEmailListener = mCurrentInitiatorEmailAddrRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.i("position09161", "in attachNavRef, dataSnapShot is " + dataSnapshot.toString());
                    RequestInitiatorEmailModel currentInitiatorEmailAddrModel = dataSnapshot.getValue(RequestInitiatorEmailModel.class);
                    String currentInitiatorEmailAddr = currentInitiatorEmailAddrModel.getInitiatorEmailAddr();
                    if((!currentInitiatorEmailAddr.equals("")) && (!currentInitiatorEmailAddr.equals(null)))
                    {
                        mCurrentMeetRequest.setInitiatorEmailAddr(currentInitiatorEmailAddr);
                        Log.i("initEmail", "in attach listener, initiatorEmail is" + mCurrentMeetRequest.getInitiatorEmailAddr()
                                + ", initiatorState is " + mCurrentMeetRequest.getInitiatorState() +
                                ",responderEmail is " + mCurrentMeetRequest.getResponderEmailaddr() +
                                ",responderState is " + mCurrentMeetRequest.getResponderState());
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mInitiatorStateListener = mCurrentInitiatorStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("position09162", "in attachNavRef, dataSnapShot is " + dataSnapshot.toString());
                    //String currentInitiatorState = dataSnapshot.child("initiatorState").toString();

                    RequestInitiatorStateModel currentInitiatorStateModel = dataSnapshot.getValue(RequestInitiatorStateModel.class);

                    String currentInitiatorState = currentInitiatorStateModel.getInitiatorState();
                    Log.i("position091621", "in attachNavRef, currentInitiatorState is " + currentInitiatorState);
                    if (currentInitiatorState.equals("true")) {
                        String initiatorEmail = mCurrentMeetRequest.getInitiatorEmailAddr();
                        String initiatorState = mCurrentMeetRequest.getInitiatorState();
                        //String responderEmail = mCurrentMeetRequest.getResponderEmailaddr();
                        //String responderState = mCurrentMeetRequest.getResponderState();

                        if ((initiatorEmail.equals(basicChatFriend)) &&
                                (initiatorState.equals("false"))){
                            Intent intent = new Intent(ChatActivity.this, requestActivity.class);
                            intent.putExtra("ChatId", mChatId);
                            intent.putExtra("isInitiator", "false");
                            // the requestActivity could figure out currentUserEmail

                            startActivity(intent);
                        }
                        else
                        {
                            Log.i("initState0", "position91401, strange");
                        }

                    }

                    mCurrentMeetRequest.setInitiatorState(currentInitiatorState);
                }
                Log.i("initState", "in attach listener,  initiatorState is " + mCurrentMeetRequest.getInitiatorState() +
                        ",responderState is " + mCurrentMeetRequest.getResponderState());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mResponderEmailListener = mCurrentResponderEmailAddrRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.i("position09163", "in attachNavRef, dataSnapShot is " + dataSnapshot.toString());
                    //String currentResponderEmail = dataSnapshot.child("responderEmailAddr").toString();
                    //mCurrentMeetRequest.setResponderEmailaddr(currentResponderEmail);

                    RequestResponderEmailModel currentResponderEmailModel = dataSnapshot.getValue(RequestResponderEmailModel.class);
                    String currentResponderEmail = currentResponderEmailModel.getResponderEmailAddr();
                    if((!currentResponderEmail.equals("")) && (!currentResponderEmail.equals(null)))
                    {
                        mCurrentMeetRequest.setInitiatorEmailAddr(currentResponderEmail);
                        Log.i("RespEmail", "in attach listener, initiatorEmail is" + mCurrentMeetRequest.getInitiatorEmailAddr()
                                + ", initiatorState is " + mCurrentMeetRequest.getInitiatorState() +
                                ",responderEmail is " + mCurrentMeetRequest.getResponderEmailaddr() +
                                ",responderState is " + mCurrentMeetRequest.getResponderState());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mResponderStateListener = mCurrentResponderStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.i("position09164", "in attachNavRef, dataSnapShot is " + dataSnapshot.toString());
                    RequestResponderStateModel currentResponderStateModel = dataSnapshot.getValue(RequestResponderStateModel.class);
                    String currentResponderState = currentResponderStateModel.getResponderState();
                    mCurrentMeetRequest.setResponderState(currentResponderState);
                }
                Log.i("RespState", "in attach listener,  initiatorState is " + mCurrentMeetRequest.getInitiatorState() +
                        ",responderState is " + mCurrentMeetRequest.getResponderState());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/



    }

    private void detachNavigationRefListener(){
        if (null!= mMeetRequestRefListener)
        {
            mMeetRequestMessageRef.removeEventListener(mMeetRequestRefListener);
            mMeetRequestRefListener = null;
        }

        /*if(null != mInitiatorEmailListener)
        {
            mMeetRequestMessageRef.removeEventListener(mInitiatorEmailListener);
            mInitiatorEmailListener = null;
        }

        if(null != mInitiatorStateListener)
        {
            mMeetRequestMessageRef.removeEventListener(mInitiatorStateListener);
            mInitiatorStateListener = null;
        }

        if (null != mResponderEmailListener)
        {
            mMeetRequestMessageRef.removeEventListener(mResponderEmailListener);
            mResponderStateListener = null;
        }

        if (null != mResponderStateListener){
            mMeetRequestMessageRef.removeEventListener(mResponderStateListener);
            mResponderStateListener = null;
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();

        detachNavigationRefListener();
        mMessageListAdapter.cleanup();

        // TODO: detach db ref listeners either here or in onDestroy (figure out)
    }
}
