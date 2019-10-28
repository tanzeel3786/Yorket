package com.example.yorket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.LENGTH_LONG;

public class loginPage extends AppCompatActivity implements View.OnClickListener {
    private EditText loginpageditemail,loginpageditpassword;
    private Button pageloginbtn;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mAuth = FirebaseAuth.getInstance();
        loginpageditemail=findViewById(R.id.loginpageditemail);
        loginpageditpassword=findViewById(R.id.loginpageeditpassword);
        pageloginbtn=findViewById(R.id.pageloginbtn);
        pageloginbtn.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.pageloginbtn:if(loginpageditemail.getText().toString().equals("")||loginpageditpassword.getText().toString().equals(""))
                Toast.makeText(loginPage.this, "Please enter the details", LENGTH_LONG).show();
            else
            {progressDialog.setMessage("Logging In....");
            progressDialog.show();
                mAuth.signInWithEmailAndPassword(loginpageditemail.getText().toString(),loginpageditpassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(loginPage.this, task.getResult().getUser().getDisplayName(), LENGTH_LONG).show();
                            transitionToActivity();


                        }
                        else
                            Toast.makeText(loginPage.this, task.getException()+"", LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void transitionToActivity() {
        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuth.getCurrentUser().getUid()).child("usersData").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               String s1=(String) dataSnapshot.child("userType").getValue();
               if(s1.equals("finder"))
               {progressDialog.dismiss();
                   Intent intent=new Intent(loginPage.this,finderMap.class);
                   startActivity(intent);
               }
               else if(s1.equals("target"))
               {progressDialog.dismiss();
                   Intent intent=new Intent(loginPage.this,targetMap.class);
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
