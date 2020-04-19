package com.example.serwe;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serwe.Interface.ItemClickListener;
import com.example.serwe.Model.Category;
import com.example.serwe.Model.Food;
import com.example.serwe.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Category objectRestaurant;
    ImageView imgRestaurant;
    TextView txtRestaurantName, txtRestaurantAddress, txtRestDesc;

    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        objectRestaurant = getIntent().getParcelableExtra("restaurantDetail");

        imgRestaurant = findViewById(R.id.img_restaurantdetail);
        Picasso.with(getBaseContext()).load(objectRestaurant.getImage())

                .into(imgRestaurant);

        txtRestaurantName = findViewById(R.id.restaurant_namedetail);
        txtRestaurantAddress = findViewById(R.id.restaurant_address);
        txtRestDesc = findViewById(R.id.restaurant_description);
        txtRestaurantName.setText(objectRestaurant.getName());
        txtRestaurantAddress.setText(getAddress(new LatLng(objectRestaurant.getLat(), objectRestaurant.getLong())));
        txtRestDesc.setText(objectRestaurant.getDescription());

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");

        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //get intent
        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId != null){
            loadListFood(categoryId);
        }

    }

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,R.layout.food_item,FoodViewHolder.class,foodList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText( "Price: "+ model.getPrice() + " $");
                Picasso.with(getBaseContext()).load(model.getImage()). into(viewHolder.food_image);
                final  Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new activity
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());//send food id to next activity
                        startActivity(foodDetail);
                    }
                });
            }
        };


        //Set Adapter
        Log.d("TAG",""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);
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

                Toast.makeText(this, address, Toast.LENGTH_SHORT).show();


            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return address;

    }
}
