package com.example.serwe;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serwe.Common.Common;
import com.example.serwe.Common.Config;
import com.example.serwe.Database.Database;
import com.example.serwe.Model.Order;
import com.example.serwe.Model.Request;
import com.example.serwe.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Cart extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 9999;
    @BindView(R.id.listCart)
    RecyclerView recyclerView;
    @BindView(R.id.total) TextView txtTotalPrice;
    @BindView(R.id.btnPlaceOrder)
    Button btnPlace;

    //paypal payment
    static PayPalConfiguration config = new PayPalConfiguration()

            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId(Config.Paypal_Client_ID).rememberUser(true);
    String address,comment;


    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database = null;
    DatabaseReference requests = null;

    // init cart list and cart adapter
    List<Order> carts = new ArrayList<>();
    CartAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        //Init Paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // setting recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // loading food list
        loadListFood();

        // placing order
        placeOrder();
    }

    /**
     * Clicking Place Order button
     */
    private void placeOrder() {
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });
    }

    /**
     * Showing alert dialog
     */
    private void showAlertDialog() {
        // set title and message for alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address: ");

        // set editText and layout params
        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        // adding editText to layout params
        edtAddress.setLayoutParams(lp);

        // adding editText to alert dialog
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        // set positive button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                //Show PAypal to payment

                //first,get address and comment from alert dialog

                address = edtAddress.getText().toString();

                String formatAmount = txtTotalPrice.getText().toString()
                        .replace("$","")
                        .replace(",","");

                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                        "CAD",
                        "Serwe Order",
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent (getApplicationContext() , PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
                startActivityForResult(intent,PAYPAL_REQUEST_CODE);



            }
        });
        // set Cancel button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
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
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                jsonObject.getJSONObject("response").getString("state"),
                                carts
                        );

                        //Request request1 = new Request(Common.currentUser.getPhone(),Common.currentUser.getName(),address,txtTotalPrice.getText().toString(),"0",);

                        // Submit to Firebase
                        // Using System.CurrentMillis to key
                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);
                        // Deleting cart
                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(Cart.this, " Thank you, Order Place", Toast.LENGTH_SHORT).show();
                        finish();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(this,"went wrong",Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    /**
     * Loading food list
     */
    private void loadListFood() {
        // get cart list
        carts = new Database(this).getCarts();
        // set adapter
        adapter = new CartAdapter(carts, this);
        recyclerView.setAdapter(adapter);

        // Calculate total price
        int total = 0;
        for(Order order : carts) {
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));
    }
}
