package com.example.serwe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.serwe.Common.Common;
import com.example.serwe.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignIn extends AppCompatActivity {

    EditText edtPhone,edtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        btnLogin =  (Button) findViewById(R.id.btnLogin);

        //Init Firebase

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user= database.getReference("User");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please Wait");
                mDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //Chech if user exist
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                //Get User Info
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());//set phone
                                if (user.getPassword().equals(edtPassword.getText().toString())) {

                                    Intent homeIntent= new Intent(SignIn.this,Home.class);
                                    Common.currentUser=user;
                                    startActivity(homeIntent);
                                    finish();

                                }
                                else{
                                    Toast.makeText(SignIn.this, "Sign In Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "User Does not exist! ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }
        });
    }
}
