package com.example.serwe.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.serwe.Interface.ItemClickListener;
import com.example.serwe.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName, txtMenuAddress;
    public ImageView imageView;
    public Button buttonDirection;


    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView){
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        txtMenuAddress = (TextView)itemView.findViewById(R.id.menu_address);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);
        buttonDirection = itemView.findViewById(R.id.menu_direction);

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);

    }

    public void setItemClickListener(ItemClickListener categoryId) {
        this.itemClickListener = categoryId;
    }
}
