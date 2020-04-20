package com.example.serwe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    CheckBox rememberMe;
    String uname,pass;
    private static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        btnLogin =  (Button) findViewById(R.id.btnLogin);
        rememberMe = findViewById(R.id.rememberMeBtn);

        sharedPreferences =this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
       if(sharedPreferences.contains("uname"));
        {
            Toast.makeText(this,"exiists",Toast.LENGTH_SHORT).show();
           uname= loadData(this,"uname");
           pass = loadData(this,"pass");
           edtPhone.setText(uname);
           edtPassword.setText(pass);
           rememberMe.setActivated(true);
        }




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


                                    if(rememberMe.isChecked())
                                    saveData(getApplicationContext(),edtPhone.getText().toString(),edtPassword.getText().toString());
                                    if(!rememberMe.isChecked())
                                        if(sharedPreferences.contains("uname")) {
                                            sharedPreferences.edit().clear().apply();
                                            Toast.makeText(getApplicationContext(),"deleted",Toast.LENGTH_SHORT).show();
                                        }

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

    public static void saveData(Context context,String uname,String pass) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pass", pass);
        editor.putString("uname",uname);
        editor.apply();
    }

    public static String loadData(Context context,String s) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String pass = sharedPreferences.getString(s, "");
        return pass;
    }
}
