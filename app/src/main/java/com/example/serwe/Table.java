package com.example.serwe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.serwe.Common.Common;
import com.example.serwe.Interface.ItemClickListener;
import com.example.serwe.Model.Request;
import com.example.serwe.Model.Tablem;
import com.example.serwe.ViewHolder.OrderViewHolder;
import com.example.serwe.ViewHolder.TableViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Table extends AppCompatActivity {

    @BindView(R.id.listtables)
    RecyclerView recyclerView;

    public RecyclerView.LayoutManager layoutManager = null;

    FirebaseDatabase database;
    DatabaseReference tables;

    FirebaseRecyclerAdapter<Tablem, TableViewHolder> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        ButterKnife.bind(this);

        // Firebase
        database = FirebaseDatabase.getInstance();
        tables = database.getReference("Tables");

        // set recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // loading order status
        loadOrders(Common.currentUser.getPhone());
    }

    /**
     * Loading order status
     * @param phone
     */
    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Tablem, TableViewHolder>(
                Tablem.class,
                R.layout.table_layout,
                TableViewHolder.class,
                tables.orderByChild("phone")
                        .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(TableViewHolder viewHolder, Tablem model, int position) {
                // get key from DatabaseReference
                viewHolder.txtOrderId.setText("Order No. : " + adapter.getRef(position).getKey());
                // set text for order status, address and phone
                viewHolder.txtOrderStatus.setText("Order Status. : " + model.getStatus());
                viewHolder.txtOrAddress.setText("Order Amount. : $" + model.getTotal());
                viewHolder.txtOrderPhone.setText("Confirmation Pin : " + model.getPin());
                viewHolder.txtPayment.setText(model.getPaymentState());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                       // Toast.makeText(Table.this, "" + position, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        // set adapter for recycler view
        recyclerView.setAdapter(adapter);
    }

    /**
     * Converting code to string status
     * @param status
     * @return
     */
    private String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }


}
