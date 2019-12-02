package com.zoctu.abbarcigas.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoctu.abbarcigas.Interface.ItemClickListner;
import com.zoctu.abbarcigas.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListner listner;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView=itemView.findViewById(R.id.product_image);
        txtProductName=itemView.findViewById(R.id.product_name);
        txtProductDescription=itemView.findViewById(R.id.product_description);
        txtProductPrice=itemView.findViewById(R.id.product_price);
    }
    public void setItemClickListner(ItemClickListner listner){
        this.listner=listner;
    }

    @Override
    public void onClick(View v) {
        listner.onClick(v,getAdapterPosition(),false);
    }
}
