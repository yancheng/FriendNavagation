package com.yaosun.friendnavigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yaosun.friendnavigation.Utils.FNUtil;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class requestActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    //private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    //private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    /*private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };*/
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    /*private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };*/
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBasicChatRef;
    private DatabaseReference mMeetRequestReference;

    private String mChatId;

    private String mIsCallingActivityInitiator;

    private Button mAcceptBtn;
    private Button mHanghoutBtn;
    // the email address for the chatting friend
    private String mFriendEmailAddr;

    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request);
        // hide the system buttons
        mContentView = findViewById(R.id.fullscreen_content);
        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        Intent intent = this.getIntent();

        mChatId = intent.getStringExtra("ChatId");
        mIsCallingActivityInitiator = intent.getStringExtra("isInitiator");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBasicChatRef = mFirebaseDatabase.getReference().child("BasicChat").child(mChatId);
        mMeetRequestReference = mBasicChatRef.child("meetRequest");
        mAcceptBtn = (Button)findViewById(R.id.accept_button);

        if(mIsCallingActivityInitiator.equals("true")){
            // the caller doesn't need the accept bubton
            mAcceptBtn.setVisibility(View.INVISIBLE);
            // mFriendEmailAddr = mMeetRequestReference.child("responderEmailAddr").toString();

            mMeetRequestReference.child("responderEmailAddr").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("requestAct00a"," dataSnapshot is "+ dataSnapshot.toString());
                    mFriendEmailAddr = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Log.i("requestActivity01"," mIsCallingActivityInitiator is "+ mIsCallingActivityInitiator);
            mAcceptBtn.setVisibility(View.VISIBLE);
            mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // we will modify the responderState to be true
                    mMeetRequestReference.child("responderState").setValue("true");
                    // TODO: pass in ChatId and isinitiator to map activity

                    Intent intent = new Intent(requestActivity.this,MapsActivity.class);
                    intent.putExtra("ChatId",mChatId);
                    intent.putExtra("isInitiator",mIsCallingActivityInitiator);
                    // and start the MapsActivity
                    startActivity(intent);
                    finish();
                }
            });
            //mFriendEmailAddr = mMeetRequestReference.child("initiatorEmailAddr").toString();
            mMeetRequestReference.child("initiatorEmailAddr").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("requestAct00b"," dataSnapshot is "+ dataSnapshot.toString());
                    mFriendEmailAddr = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mHanghoutBtn = (Button)findViewById(R.id.hangout_button);
        mHanghoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMeetRequestReference.child("initiatorState").setValue("false");
                mMeetRequestReference.child("initiatorEmailAddr").setValue("");
                mMeetRequestReference.child("responderEmailAddr").setValue("");
                mMeetRequestReference.child("responderState").setValue("false");

                Intent intent = new Intent(view.getContext(),ChatActivity.class);
                // TODO: make a constant for string "friendEmailAddr"
                // this extra is not the best way to pass in friend name
                // TODO: add a listener here to the basic
                intent.putExtra("friendEmailAddr",mFriendEmailAddr);
                startActivity(intent);
                finish();
                // might instead need to firstly go back to chat activity and then go to map activity
            }
        });



        // TODO we will need to start a timer of 10 sec and beeping and timeout if user doesn't accpet and go back to ChatActivity
        // similar to hangout button handler


        /*mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);*/




    }
/*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }*/

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.

     private void delayedHide(int delayMillis) {
     mHideHandler.removeCallbacks(mHideRunnable);
     mHideHandler.postDelayed(mHideRunnable, delayMillis);
     }*/
}
