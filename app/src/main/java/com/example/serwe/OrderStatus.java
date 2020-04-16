package com.example.serwe;

import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serwe.Common.Common;
import com.example.serwe.Interface.ItemClickListener;
import com.example.serwe.Model.Request;
import com.example.serwe.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderStatus extends AppCompatActivity {
    @BindView(R.id.listOrders)
    RecyclerView recyclerView;

    public RecyclerView.LayoutManager layoutManager = null;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        ButterKnife.bind(this);

        // Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

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
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                        .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                // get key from DatabaseReference
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                // set text for order status, address and phone
                viewHolder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(OrderStatus.this, "" + position, Toast.LENGTH_SHORT).show();
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
