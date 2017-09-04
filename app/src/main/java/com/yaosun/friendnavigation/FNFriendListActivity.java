package com.yaosun.friendnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yaosun.friendnavigation.Models.FNUtil;
import com.yaosun.friendnavigation.Models.FriendModel;
import com.yaosun.friendnavigation.Models.UserModel;

public class FNFriendListActivity extends AppCompatActivity {

    private Toast mToast;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseFriendMapRef;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference mDatabaseUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnfriend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // we will create a displayUsers() for now to test Recycler view
        // TODO: after displayUsers is done, and AddFriend() is done, remove displayUsers() and replayce with displayFriends()
        displayUserList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == R.id.sign_out)
        {
            AuthUI.getInstance().signOut(this);
            startActivity(new Intent(FNFriendListActivity.this, MainActivity.class));
        }
        return true;
    }

    private void displayUserList(){


        RecyclerView friendList = (RecyclerView)findViewById(R.id.friend_list_view);

        friendList.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        String currentUserEmail1 = mFirebaseAuth.getCurrentUser().getEmail().trim();

        mDatabaseFriendMapRef = mFirebaseDatabase.getReference().child("FriendMap").child(FNUtil.encodeEmail(currentUserEmail1)).child("FriendList");

        Log.i("positionB", "value for ref is " + mDatabaseFriendMapRef.toString());

        mAdapter = new FirebaseRecyclerAdapter<FriendModel,friendItemViewHolder>(
                FriendModel.class,
                R.layout.recyclerview_friendlist_row,
                friendItemViewHolder.class,
                mDatabaseFriendMapRef) {
            @Override
            protected void populateViewHolder(friendItemViewHolder holder, FriendModel friend, final int position) {
                Log.i("positionA","psition is" + position +", friend email is " + friend.getFriendEmailAddr());
                holder.setEmailAddr(friend.getFriendEmailAddr());
                holder.setListItemNumber(Integer.toString(position));

                holder.mView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(FNFriendListActivity.this,"you clicked on item "+ Integer.toString(position), Toast.LENGTH_LONG).show();
                        // TODO: create a basic chat entry and start the chat activity; 'fore creating it, first check whether the other party created it
                        // if so, capture the chat-id and use the same chat
                    }
                });
            }


        };

        friendList.setAdapter(mAdapter);

    }
    // TODO: add auto completion functionality when adding friends
    public void searchAndAddNewFriend(View view){

        EditText mUserInputEmailEdit = (EditText)findViewById(R.id.searchFriendEdit);
        final FirebaseDatabase mFirebaseDatabaseForSearch = FirebaseDatabase.getInstance();

        final String mUserInputEmailString = mUserInputEmailEdit.getText().toString().trim();

        DatabaseReference mDatabaseUserRefForSearch = mFirebaseDatabaseForSearch.getReference().child("Users");

        mDatabaseUserRefForSearch.orderByChild("emailAddr").equalTo(mUserInputEmailString).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // we have found an entry that matches it, use it
                        // TODO: while user is typing, autocomplete from firebase
                        // add this entry to the friend list in corresponding Friend Map Entry


                        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                        Log.i("position4", "in onDataChange, current user email is"+ mFirebaseAuth.getCurrentUser().getEmail());

                        Log.i("position5", "in onDataChange, user input email is"+ mUserInputEmailString.trim());

                       //  Log.i("position6", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());
                        String currentUserEmail = mFirebaseAuth.getCurrentUser().getEmail().trim();
                        DatabaseReference mFriendMapRef = mFirebaseDatabaseForSearch.getReference().child("FriendMap").child(FNUtil.encodeEmail(currentUserEmail));

                        if ((null != mFriendMapRef) && (null!= dataSnapshot.getValue())){
                            Log.i("position6", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());
                            try {
                                mFriendMapRef.child("mainUserEmail").setValue(currentUserEmail);
                            } catch (Exception e) {
                                Log.i("addUser", "GetEmail failed");

                                e.printStackTrace();
                            }


                            DatabaseReference mFriendListRef = mFriendMapRef.child("FriendList").push();

                            UserModel user = dataSnapshot.child(FNUtil.encodeEmail(mUserInputEmailString)).getValue(UserModel.class);
                            if (null != user) {
                                String useremail = user.getEmailAddr();

                                //String useremail = dataSnapshot.child("emailAddr").getValue().toString();
                                //DatabaseReference mSnapShotRef = dataSnapshot.getRef();
                                //String useremail = mSnapShotRef.push().;

                                Log.i("position7", "in OnDataChange, emailAddr Value is" + useremail);
                                try {
                                    //mFriendListRef.child(FNUtil.encodeEmail(mUserInputEmailString)).setValue(useremail);
                                    mFriendListRef.child("friendEmailAddr").setValue(useremail);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                Toast.makeText(FNFriendListActivity.this,"perhaps user doesn't exist!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                                // TODO: do some basic sanity check for user input before querying firebase
                                Toast.makeText(FNFriendListActivity.this,"sure user existed? perhaps not", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(FNFriendListActivity.this,"couldn't find this user!", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public static class friendItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mListItemNumberView;
        private TextView mFriendNameView;
        View mView;
        public friendItemViewHolder(View itemView) {
            super(itemView);
            mListItemNumberView = (TextView)itemView.findViewById(R.id.friend_index);
            mFriendNameView = (TextView)itemView.findViewById(R.id.friend_email_addr);
            mView = itemView;
        }

        public void setEmailAddr(String emailAddr)
        {
            mFriendNameView.setText(emailAddr);
        }

        public void setListItemNumber(String listItemNumber)
        {
            mListItemNumberView.setText(listItemNumber);
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
