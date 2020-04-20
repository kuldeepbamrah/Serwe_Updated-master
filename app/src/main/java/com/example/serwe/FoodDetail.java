package com.example.serwe;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.serwe.Common.Common;
import com.example.serwe.Database.Database;
import com.example.serwe.Model.Food;
import com.example.serwe.Model.Order;
import com.example.serwe.Model.RandomId;
import com.example.serwe.Model.Rating;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart,btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;


    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;

    Food currentFood;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);


        //Firebse
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");
        ratingTbl = database.getReference("Rating");


        //INit view

        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);


        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // new view inflate for selecting nearby places option

                AlertDialog.Builder builder = new AlertDialog.Builder( FoodDetail.this );
                // builder.setTitle( "Edit Employee" );
                LayoutInflater inflater = LayoutInflater.from( FoodDetail.this );
                View v = inflater.inflate( R.layout.custom_food_layout,null );
                builder.setView( v );
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                final Spinner spinnerSpiceLevel = v.findViewById( R.id.spinnerSpicyLevel );
                final EditText comment = v.findViewById(R.id.edtcommentfood);
                Button buttonEdit = v.findViewById( R.id.button_addcart );

                buttonEdit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Database(getBaseContext()).addToCart(new Order(
                                foodId,
                                currentFood.getName(),
                                numberButton.getNumber(),
                                currentFood.getPrice(),
                                currentFood.getDiscount(),
                                spinnerSpiceLevel.getSelectedItem().toString(),
                                comment.getText().toString()

                        ));

                       // Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();

                        alertDialog.dismiss();

                        Snackbar snackbar = Snackbar.make(view,"Added To Cart",Snackbar.LENGTH_SHORT).setAction("Go to Cart", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent cartIntent = new Intent(FoodDetail.this,Cart.class);
                                startActivity(cartIntent);
                            }
                        });
                        snackbar.show();



                    }
                } );
//                new Database(getBaseContext()).addToCart(new Order(
//                        foodId,
//                        currentFood.getName(),
//                        numberButton.getNumber(),
//                        currentFood.getPrice(),
//                        currentFood.getDiscount()

//                ));

                //new Database(getBaseContext()).cleanCart();
                Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        food_description = (TextView) findViewById(R.id.food_description);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);
        food_image = (ImageView) findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()) {
            getDetailFood(foodId);
            getRating(foodId);
        }

    }

    private void getRating(String foodId)
    {
        com.google.firebase.database.Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item = postSnapshot.getValue(Rating.class);
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

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentFood= dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText("$"+currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                .create(FoodDetail.this)
                .show();

    }

    @Override
    public void onNegativeButtonClicked() {

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

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s)

    {
   Rating rating;
//        String tempName="abc";
//        if(Common.googleUser!=null) {
////           rating = new Rating(Common.googleUser.getEmail(), foodId, String.valueOf(i), s);
//            tempName = Common.googleUser.getToken();
//        }
//        else
//        {
//            //rating = new Rating(Common.currentUser.getPhone(), foodId, String.valueOf(i), s);
//            if(Common.currentUser!=null)
//            tempName = Common.currentUser.getPhone();
//        }
        String s1 = getAlphaNumericString(5);
        rating = new Rating(s1, foodId, String.valueOf(i), s);


        ratingTbl.child(s1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.child(s1).exists())
                {
                    ratingTbl.child(s1).removeValue();
                    ratingTbl.child(s1).setValue(rating);
                }
                else
                {
                    ratingTbl.child(s1).setValue(rating);
                }
                Toast.makeText(getApplicationContext(),"Thank you for Feed back",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
