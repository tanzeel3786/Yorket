package com.example.yorket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class registration extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText registrationpageemail,registrationpagename,registrationpagephone,registrationpagepassword,userFinder;
    private RadioButton driverradiobtn,passengerradiobtn;
    private Button pageregisterbtn ;
    private TextInputLayout userFinderLayout;
    private  ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        registrationpagename=findViewById(R.id.registrationpagename);
        registrationpageemail=findViewById(R.id.registrationpageemail);
        registrationpagephone=findViewById(R.id.registrationpagephone);
        registrationpagepassword=findViewById(R.id.registrationpagepassword);
        userFinder=findViewById(R.id.userFinder);
        userFinderLayout=findViewById(R.id.layoutUserFinder);
        driverradiobtn=findViewById(R.id.driverradiobtn);
        pageregisterbtn=findViewById(R.id.pageregisternbtn);
        passengerradiobtn=findViewById(R.id.passengerradiobtn);
        pageregisterbtn.setOnClickListener(this);
        passengerradiobtn.setOnClickListener(this);
        driverradiobtn.setOnClickListener(this);
         progressDialog=new ProgressDialog(this);

    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {

                case R.id.pageregisternbtn:


                    if (registrationpagename.getText().toString().equals("") || registrationpageemail.getText().toString().equals("") ||
                            registrationpagephone.getText().toString().equals("") || registrationpagepassword.getText().toString().equals("") ||
                            (driverradiobtn.isChecked() == false && passengerradiobtn.isChecked() == false)) {
                        Toast.makeText(registration.this, "Please fill each detail", Toast.LENGTH_SHORT).show();

                    } else {

                        progressDialog.setMessage("Signing up");
                        progressDialog.show();
                        mAuth.createUserWithEmailAndPassword(registrationpageemail.getText().toString(), registrationpagepassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                   Toast.makeText(registration.this, "Succesfully Signed Up", Toast.LENGTH_SHORT).show();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(registrationpagename.getText().toString()).build();

                                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task1) {
                                                    //if (task1.isSuccessful()) {
                                                      //  Toast.makeText(registration.this,"set dispplay name", Toast.LENGTH_LONG).show();
                                                    //}
                                                   // else
                                                     //   Toast.makeText(registration.this, task1.getException()+"", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    HashMap<String,String> dataMap=new HashMap<>();
                                    if(driverradiobtn.isChecked())
                                    {

                                        dataMap.put("name",registrationpagename.getText().toString());
                                        dataMap.put("email",registrationpageemail.getText().toString());
                                        dataMap.put("phone",registrationpagephone.getText().toString());
                                        dataMap.put("userType","finder");
                                    }
                                    else
                                    {
                                        dataMap.put("name",registrationpagename.getText().toString());
                                        dataMap.put("email",registrationpageemail.getText().toString());
                                        dataMap.put("phone",registrationpagephone.getText().toString());
                                        dataMap.put("userType","target");
                                       dataMap.put("userFinder",userFinder.getText().toString());
                                    }

                                      FirebaseDatabase.getInstance().getReference().child("my_users").child(task.getResult().getUser().getUid()).child("usersData").push().setValue(dataMap);
                                   //   FirebaseDatabase.getInstance().getReference().child("my_users").child("userFinder").setValue(userFinder.getText().toString());
                                    transitionToActivity();


                                } else
                                    Toast.makeText(registration.this, task.getException()+"", Toast.LENGTH_SHORT).show();

                            }
                        });



                    }

                    break;
                case R.id.passengerradiobtn:userFinderLayout.setVisibility(View.VISIBLE);
                break;
                case R.id.driverradiobtn:userFinderLayout.setVisibility(View.GONE);
                break;

            }
        }
        catch (Exception e)
        {
            Toast.makeText(registration.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private void transitionToActivity() {
        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuth.getCurrentUser().getUid()).child("usersData").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String s1=(String) dataSnapshot.child("userType").getValue();
                if(s1.equals("finder"))
                {progressDialog.dismiss();
                    Intent intent=new Intent(registration.this,finderMap.class);
                    startActivity(intent);
                }
                else if(s1.equals("target"))
                {progressDialog.dismiss();
                    Intent intent=new Intent(registration.this,targetMap.class);
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
