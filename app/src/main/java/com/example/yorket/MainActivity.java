package com.example.yorket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private Button startpageloginbtn,startpageregisterbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startpageloginbtn=findViewById(R.id.staartpageloginbtn);
        startpageregisterbtn=findViewById(R.id.startpageregisterbtn);
        mAuth = FirebaseAuth.getInstance();
        startpageloginbtn.setOnClickListener(this);
        startpageregisterbtn.setOnClickListener(this);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
            transitionToActivity();
      //  updateUI(currentUser);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.staartpageloginbtn:
                Intent intent1 = new Intent(MainActivity.this, loginPage.class);
                startActivity(intent1);
                Toast.makeText(MainActivity.this, "Login Tapped", Toast.LENGTH_LONG).show();
                break;
            case R.id.startpageregisterbtn:
                Intent intent2 = new Intent(MainActivity.this, registration.class);
                startActivity(intent2);
                break;
        }
    }
    private void transitionToActivity() {
        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuth.getCurrentUser().getUid()).child("usersData").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String s1=(String) dataSnapshot.child("userType").getValue();
                if(s1.equals("finder"))
                {
                    Intent intent=new Intent(MainActivity.this,finderMap.class);
                    startActivity(intent);
                }
                else if(s1.equals("target"))
                {
                    Intent intent=new Intent(MainActivity.this,targetMap.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
