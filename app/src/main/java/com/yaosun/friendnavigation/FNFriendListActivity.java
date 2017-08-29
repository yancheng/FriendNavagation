package com.yaosun.friendnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yaosun.friendnavigation.UserModel;

public class FNFriendListActivity extends AppCompatActivity {

     private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseUserRef;
    private FirebaseRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnfriend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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


        // this is temporary for testing recycler view
        // eventually we will be displayingFriendList
        RecyclerView friendList = (RecyclerView)findViewById(R.id.friend_list_view);

        friendList.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mDatabaseUserRef = mFirebaseDatabase.getReference().child("Users");

        mAdapter = new FirebaseRecyclerAdapter<UserModel,friendItemViewHolder>(
                UserModel.class,
                R.layout.recyclerview_friendlist_row,
                friendItemViewHolder.class,
                mDatabaseUserRef) {
            @Override
            protected void populateViewHolder(friendItemViewHolder holder, UserModel user, int position) {
                holder.setEmailAddr(user.getEmailAddr().replace(".",","));
                holder.setListItemNumber(Integer.toString(position));
            }
        };

        friendList.setAdapter(mAdapter);

    }

    public static class friendItemViewHolder extends RecyclerView.ViewHolder{
        private TextView mListItemNumberView;
        private TextView mFriendNameView;
        public friendItemViewHolder(View itemView) {
            super(itemView);
            mListItemNumberView = (TextView)itemView.findViewById(R.id.friend_index);
            mFriendNameView = (TextView)itemView.findViewById(R.id.friend_email_addr);
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
