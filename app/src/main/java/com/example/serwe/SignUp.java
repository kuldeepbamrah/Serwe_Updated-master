package com.example.serwe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.serwe.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    EditText edtPhone,edtName,edtPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (EditText)findViewById(R.id.edtName);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtPhone = (EditText)findViewById(R.id.edtPhone);

        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        //Init Firebase

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user= database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtPhone.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty() || edtName.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(SignUp.this)
                            .setTitle("Empty Fields")
                            .setMessage("Please Fill all the empty fields")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

                else {
                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please Wait");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //check if already user phone
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                new AlertDialog.Builder(SignUp.this)
                                        .setTitle("Signup Form")
                                        .setMessage("This user is already registered")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                //Toast.makeText(SignUp.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                //Toast.makeText(SignUp.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                new AlertDialog.Builder(SignUp.this)
                                        .setTitle("Signup Form")
                                        .setMessage("Congartulations! You are registered Successfully")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                finish();


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
