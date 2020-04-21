package com.example.serwe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.serwe.Common.Common;
import com.example.serwe.Database.Database;
import com.example.serwe.Model.GoogleUser;
import com.example.serwe.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    Button btnSignUp,btnSignIn,button3;
    SignInButton gBtn;
    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient ;
    ImageView imageView;


    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null) {
            Toast.makeText(this, "Hello" + account.getDisplayName(), Toast.LENGTH_LONG).show();
            User user = new User();
            account.getEmail();
            GoogleUser googleUser = new GoogleUser();
            googleUser.setEmail(account.getEmail());
            googleUser.setName(account.getDisplayName());
            //googleUser.setToken(account.getIdToken());


            user.setName(account.getDisplayName());
            Intent homeIntent= new Intent(MainActivity.this,Home.class);
            Common.currentUser=user;
            Common.googleUser=googleUser;
            startActivity(homeIntent);
            //new Database(getBaseContext()).cleanCart();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        //button3 = (Button)findViewById(R.id.button3);
        gBtn = findViewById(R.id.gBtn);
        imageView = findViewById(R.id.mainImage);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        gBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            String name = account.getDisplayName();
            String email = account.getEmail();
            String img_url = account.getPhotoUrl().toString();
            User user = new User();
            GoogleUser googleUser = new GoogleUser();
            googleUser.setEmail(account.getEmail());
            googleUser.setName(account.getDisplayName());
            googleUser.setToken(account.getIdToken());
            googleUser.setImg(account.getPhotoUrl());
            user.setName(account.getDisplayName());
            Intent homeIntent= new Intent(MainActivity.this,Home.class);
            Common.currentUser=user;
            Common.googleUser=googleUser;
            startActivity(homeIntent);
            //finish();
            //Toast.makeText(this, "Welcome,"+ name, Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Error Found")
                    .setMessage("Login Failed")

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
            //Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}
