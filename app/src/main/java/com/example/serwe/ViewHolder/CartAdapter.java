package com.example.serwe.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.serwe.Database.Database;
import com.example.serwe.Interface.ItemClickListener;
import com.example.serwe.Model.Order;
import com.example.serwe.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


class CartviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txt_cart_name,txt_price, txt_cart_spicy, txt_cart_comment;
    public ImageView img_cart_count;


    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartviewHolder(View itemView) {
        super(itemView);
        txt_cart_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView)itemView.findViewById(R.id.cart_item_price);
        img_cart_count = (ImageView)itemView.findViewById(R.id.cart_item_count);
        txt_cart_spicy = itemView.findViewById(R.id.cart_item_spicelevel);
        txt_cart_comment = itemView.findViewById(R.id.cart_item_comment);
    }

    @Override
    public void onClick(View view) {

    }
}

public class CartAdapter extends RecyclerView.Adapter<CartviewHolder>{


    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CartviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartviewHolder holder, int position) {

        String quantity = listData.get(position).getQuantity();
        String price = listData.get(position).getPrice();
        String name = listData.get(position).getProductName();
        TextDrawable drawable =  TextDrawable.builder().buildRound(""+quantity, Color.RED);
        holder.img_cart_count.setImageDrawable(drawable);

        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        //int totalPrice = (Integer.parseInt(price)) * (Integer.parseInt(quantity));
        //holder.txt_price.setText(fmt.format(totalPrice));

        holder.txt_cart_name.setText("Food Name: " + name);
        holder.txt_cart_spicy.setText("Spicy Level: " + listData.get(position).getSpicy());
        holder.txt_cart_comment.setText("Comment: " + listData.get(position).getComment());
        holder.txt_price.setText("Food Price: " + price + " $");



    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public Context getContext() {
        return context;
    }

    public void deleteItem(int position)
    {


        new Database(getContext()).deleteCart(listData.get(position).getProductId());
        listData.remove(position);
        notifyItemRemoved(position);

    }

}
