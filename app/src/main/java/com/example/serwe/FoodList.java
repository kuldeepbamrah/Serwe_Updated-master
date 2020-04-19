package com.example.serwe;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serwe.Common.Common;
import com.example.serwe.Interface.ItemClickListener;
import com.example.serwe.Model.Category;
import com.example.serwe.Model.Food;
import com.example.serwe.Model.Rating;
import com.example.serwe.Model.RestaurantRating;
import com.example.serwe.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FoodList extends AppCompatActivity implements RatingDialogListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Category objectRestaurant;
    ImageView imgRestaurant;
    TextView txtRestaurantName, txtRestaurantAddress, txtRestDesc;
    FloatingActionButton rating;
    RatingBar ratingBar;

    FirebaseDatabase database;
    DatabaseReference foodList,category,RestaurantRating;
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
        rating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);
        txtRestaurantName.setText(objectRestaurant.getName());
        txtRestaurantAddress.setText(getAddress(new LatLng(objectRestaurant.getLat(), objectRestaurant.getLong())));
        txtRestDesc.setText(objectRestaurant.getDescription());

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");
        category = database.getReference("Category");
        RestaurantRating = database.getReference("RestaurantRating");

        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RestaurantRating.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(Common.currentUser.getName()+categoryId).exists())
                            Toast.makeText(getApplicationContext(),"Rating already givm",Toast.LENGTH_SHORT).show();
                        else
                            showRatingDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



        //get intent
        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId != null){
            loadListFood(categoryId);
            getRating(categoryId);
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

    private void showRatingDialog()
    {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Good","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this Food")
                .setDescription("Plase provide a rating and feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodList.this)
                .show();

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {

        Rating rating;

        RestaurantRating restaurantRating = new RestaurantRating(Common.currentUser.getName()+categoryId,categoryId,String.valueOf(i),s);

        RestaurantRating.child(Common.currentUser.getName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RestaurantRating.child(Common.currentUser.getName()+categoryId).setValue(restaurantRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //category.child(categoryId).child("rating").setValue(5);

    }

    private void getRating(String foodId)
    {
        com.google.firebase.database.Query foodRating = RestaurantRating.orderByChild("resId").equalTo(categoryId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    RestaurantRating item = postSnapshot.getValue(RestaurantRating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                    Toast.makeText(getApplicationContext(),"COunt "+count+" Sum "+sum, Toast.LENGTH_LONG).show();
                }
                if(count!=0) {
                    float average = sum/count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
