package com.example.mygrocerystore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrocerystore.R;
import com.example.mygrocerystore.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    Context context;
    List<MyCartModel> cartModelList;

    int totalPrice = 0;

    FirebaseFirestore firestore;


    FirebaseAuth auth;



    public MyCartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.cartModelList = list;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(cartModelList.get(position).getProductName());
        holder.price.setText(cartModelList.get(position).getProductPrice());
        holder.date.setText(cartModelList.get(position).getCurrentDate());
        holder.time.setText(cartModelList.get(position).getCurrentTime());
        holder.totalPrice.setText(String.valueOf(cartModelList.get(position).getTotalPrice()));
        holder.quantity.setText(cartModelList.get(position).getTotalQuantity());



        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("AddToCart")
                        .document(auth.getCurrentUser().getUid())
                        .collection("CurrentUser")
                        .document(cartModelList.get(position).getDocumentId())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cartModelList.remove(cartModelList.get(position));
                                    notifyDataSetChanged();


                                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                } else  {
                                    Toast.makeText(context, "Error !"+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.quantity.getText().toString());
                quantity++;
                holder.quantity.setText(String.valueOf(quantity));

                int price = cartModelList.get(position).getTotalPrice() / Integer.parseInt(cartModelList.get(position).getTotalQuantity());
                int totalPrice = price * quantity;
                holder.totalPrice.setText(String.valueOf(totalPrice));

                updateCartItem(cartModelList.get(position).getDocumentId(), quantity, totalPrice);
            }
        });


        holder.diminish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.quantity.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    holder.quantity.setText(String.valueOf(quantity));

                    int price = cartModelList.get(position).getTotalPrice() / Integer.parseInt(cartModelList.get(position).getTotalQuantity());
                    int totalPrice = price * quantity;
                    holder.totalPrice.setText(String.valueOf(totalPrice));
                    updateCartItem(cartModelList.get(position).getDocumentId(), quantity, totalPrice);
                } else {
                    Toast.makeText(context, "You must have at least 1 item in the cart", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }



    private void updateCartItem(String documentId, int quantity, int totalPrice) {
        firestore.collection("AddToCart")
                .document(auth.getCurrentUser().getUid())
                .collection("CurrentUser")
                .document(documentId)
                .update("totalQuantity", String.valueOf(quantity), "totalPrice", totalPrice)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        }
                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,price,date,time,quantity, totalPrice;
        ImageView deleteItem,increase, diminish;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            quantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            deleteItem = itemView.findViewById(R.id.delete);
            increase = itemView.findViewById(R.id.add_item);
            diminish = itemView.findViewById(R.id.remove_item);

        }
    }
}