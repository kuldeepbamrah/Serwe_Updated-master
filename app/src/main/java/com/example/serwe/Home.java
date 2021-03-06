package com.example.serwe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serwe.Common.Common;
import com.example.serwe.Common.Config;
import com.example.serwe.Database.Database;
import com.example.serwe.Interface.ItemClickListener;
import com.example.serwe.Model.Category;

import com.example.serwe.Model.Tablem;
import com.example.serwe.Service.ListenOrder;
import com.example.serwe.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    private static final int PAYPAL_REQUEST_CODE = 9999;
    DatabaseReference category,requests,tables;

    TextView txtFullName;
    ImageView imageView;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    GoogleSignInClient mGoogleSignInClient ;
    String temp;
    long table;


    static PayPalConfiguration config = new PayPalConfiguration()

            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId(Config.Paypal_Client_ID).rememberUser(true);



    private final int REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    private SupportMapFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);


        if(!checkPermission())
        {
            requestPermission();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Init Firebase
        database=FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        requests = database.getReference("Requests");

        tables = database.getReference("Tables");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent cartIntent = new Intent(Home.this,Cart.class);
            startActivity(cartIntent);
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set NAme for User
        View headerView= navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());
        imageView = findViewById(R.id.profileImg);



        //Load menu
        recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        loadMenu();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);


    }

    private Boolean checkPermission()
    {
        int permissionState = ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION );
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE );

    }
    private void loadMenu() {


         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());

                LatLng latLng = new LatLng(model.getLat(), model.getLong());
                String address = getAddress(latLng);
                viewHolder.txtMenuAddress.setText(getAddress(latLng));
                model.setAddress(address);
                Picasso.with(getBaseContext()).load(model.getImage())

                        .into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.buttonDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent foodList = new Intent(Home.this,LocationRestaurantActivity.class);
                        foodList.putExtra("directionobject", model);
                        startActivity(foodList);
                    }
                });
                viewHolder.bookTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        //MaterialDialog(this, BottomSheet()).show
                        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(Home.this);
                       View sheetView = getLayoutInflater().inflate(R.layout.table_bottom_sheet, null);
                        mBottomSheetDialog.setContentView(sheetView);
                        //Activity activity = (Activity)getApplicationContext();

                            mBottomSheetDialog.show();

                        TextView textView = sheetView.findViewById(R.id.avail_tables);
                        textView.setText(String.valueOf(model.getTable()));
                        Button button = sheetView.findViewById(R.id.btnPay);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(model.getTable()>0) {
                                    String formatAmount = "10";

                                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                                            "CAD",
                                            "Serwe Table",
                                            PayPalPayment.PAYMENT_INTENT_SALE);
                                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);

                                    temp = adapter.getRef(position).getKey();
                                    table = model.getTable()-1;
                                    mBottomSheetDialog.dismiss();
                                    //category.child(adapter.getRef(position).getKey()).child("table").setValue(model.getTable() - 1);

                                }
                                else
                                {
                                    button.setEnabled(false);
                                    button.setText("No Tables Available");
                                }
                            }

                        });

                    {
                    }
                    }
                });
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get categoryId and send to new activity
                        Intent foodList = new Intent(Home.this,FoodList.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        foodList.putExtra("restaurantDetail", model);
                        startActivity(foodList);
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    new AlertDialog.Builder(Home.this)
                                            .setTitle("Sign Out")
                                            .setMessage("Are you sure you want to logout the app")

                                            // Specifying a listener allows you to take an action before dismissing the dialog.
                                            // The dialog is automatically dismissed when a dialog button is clicked.
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();

                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();

                                                }
                                            })

                                            // A null listener allows the button to dismiss the dialog and take no further action.
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();

                                }
                            }

                    );
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this,Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this,OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.log_out) {

            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new AlertDialog.Builder(Home.this)
                                    .setTitle("Sign Out")
                                    .setMessage("Are you sure you want to logout the app")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();

                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    }

                    );

        }
        else if (id == R.id.nav_tables)
        {
            Intent tableIntent = new Intent(Home.this,Table.class);
            startActivity(tableIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getAddress(LatLng latLng)
    {
        String address = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );
            if (addresses != null && addresses.size() > 0)
            {
                //Log.i(TAG, "on Location Result:" + addresses.get(0));
                if (addresses.get(0).getThoroughfare() != null)
                {
                    address += addresses.get(0).getThoroughfare();

                }


                if (addresses.get(0).getLocality() != null)
                {
                    address += " " + addresses.get(0).getLocality();

                }

//                if (addresses.get(0).getAdminArea() != null)
//                {
//                    address += " " + addresses.get(0).getAdminArea();
//                }
//
//                if (addresses.get(0).getCountryName() != null)
//                {
//                    address +=  " " + addresses.get(0).getCountryName();
//
//                }
//                if (addresses.get(0).getPostalCode() != null)
//                {
//                    address += " " + addresses.get(0).getPostalCode();
//
//                }



            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return address;

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        // Create new request
                        Tablem request = new Tablem(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                null,
                                "10",
                                "Booked",
                                jsonObject.getJSONObject("response").getString("state"),
                                getAlphaNumericString(4)
                        );

                        //Request request1 = new Request(Common.currentUser.getPhone(),Common.currentUser.getName(),address,txtTotalPrice.getText().toString(),"0",);

                        // Submit to Firebase
                        // Using System.CurrentMillis to key
                        tables.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);
                        // Deleting cart
                        new Database(getBaseContext()).cleanCart();
                        //Toast.makeText(this, " Thank you,Table Booked", Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(Home.this)
                                .setTitle("Table Booking")
                                .setMessage("Your table has been booked")

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
                        //finish();
                        category.child(temp).child("table").setValue(table);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    new AlertDialog.Builder(Home.this)
                            .setTitle("Error")
                            .setMessage("Something went Wrong")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();                }
            }

        }
    }

    public String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
